/*
 * net.kevinboone.androidstart.SearchActivity
 * (c)2014 Kevin Boone
 */
package net.kevinboone.androidstart;

import net.kevinboone.androidutils.MyPackageManager;
import net.kevinboone.androidutils.MyPackageInfo;
import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Window;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Button;
import android.widget.CheckBox;
import java.util.HashMap; 
import java.util.List; 
import java.util.ArrayList; 
import java.util.Collections; 
import java.util.Comparator; 
import android.content.Context;
import android.view.MenuInflater;
import android.view.Menu;
import android.widget.PopupMenu;
import android.view.MenuItem;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.Intent;
import android.net.Uri;
import android.database.Cursor;
import android.provider.Browser;
import java.io.File; 
import android.os.AsyncTask;

public class SearchActivity extends Activity
  {
  private SearchTask searchTask = null;

  @Override
  public void onStop()
    {
    super.onStop();
    stopSearchTask();
    }

  public void onSearchTaskComplete()
    {
    // TODU UI change
    searchTask = null;
    setProgress ("");
    disableStopButton();
    }

  public void stopSearchTask()
    {
    if (searchTask != null)
      {
      searchTask.cancel(true);
      }
    }

  public void enableStopButton()
    {
    Button stopButton = 
     (Button) findViewById(R.id.stop_button);
    stopButton.setEnabled (true);
    }

  public void disableStopButton()
    {
    Button stopButton = 
     (Button) findViewById(R.id.stop_button);
    stopButton.setEnabled (false);
    }

  @Override
  public void onCreate(Bundle savedInstanceState)
    {
    super.onCreate(savedInstanceState);

    MainActivity.setActivityThemeByName (this, MainActivity.getThemeName());
/*
    Window window = this.getWindow();
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
      WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
    window.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
      WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
*/
    LayoutInflater inflater = (LayoutInflater) 
      getSystemService(Context.LAYOUT_INFLATER_SERVICE);
/*
    WindowManager.LayoutParams lp = getWindow().getAttributes();
    lp.x = 0;
    lp.y = 0;
    lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
    //lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
    lp.dimAmount = 0;
    lp.gravity=Gravity.BOTTOM | Gravity.LEFT;
*/
    View view = inflater.inflate(R.layout.search, null);
    setContentView(view);

    //setContentView(view, lp);
    ListView searchResultList = (ListView) findViewById(R.id.searchresultlist);
    TextView searchStringView = (TextView) findViewById(R.id.search_string);

    CheckBox searchAppsCheckBox = 
     (CheckBox) findViewById(R.id.checkbox_search_apps);
    searchAppsCheckBox.setChecked 
      (MainActivity.getBoolPreference ("search_apps", true));

    CheckBox searchBookmarksCheckBox = 
     (CheckBox) findViewById(R.id.checkbox_search_bookmarks);
    searchBookmarksCheckBox.setChecked 
      (MainActivity.getBoolPreference ("search_bookmarks", true));

    CheckBox searchFilesCheckBox = 
     (CheckBox) findViewById(R.id.checkbox_search_files);
    searchFilesCheckBox.setChecked 
      (MainActivity.getBoolPreference ("search_files", true));

    searchStringView.setOnEditorActionListener 
        (new TextView.OnEditorActionListener()
      {
      @Override
      public boolean onEditorAction (TextView view, int actionId, 
            KeyEvent event) 
        {
        if (event==null) 
          {
          doCompleteSearch ();
          return true;
          }
        return false;
        }
      });
    disableStopButton();
    }


  public void onSearchClick (View view)
    {
    doCompleteSearch ();
    }


  public void onClearClick (View view)
    {
    doClearSearch ();
    }

  public void onStopClick (View view)
    {
    stopSearchTask ();
    }


  public void setProgress (String message)
    {
    TextView messageTextView = 
     (TextView) findViewById(R.id.search_progress);
    messageTextView.setText (message);
    }

  /** Clear the search results and search text box. */ 
  public void doClearSearch ()
    {
    stopSearchTask();
    ListView searchResultList = 
         (ListView) findViewById(R.id.searchresultlist);
    searchResultList.setAdapter(null);
    TextView searchStringView = (TextView) findViewById(R.id.search_string);
    searchStringView.setText ("");
    }

  /** Get the search text from the edit control and go for it */
  public void doCompleteSearch ()
    {
    saveCheckboxes ();
    TextView searchStringView = (TextView) findViewById(R.id.search_string);
    String searchString = searchStringView.getText().toString();
    if (searchString.length() > 0)
      {
      setProgress ("");
      doSearch (searchString);
      }
    else
      MainActivity.showErrorMessage 
        (MainActivity.getStringResource (R.string.empty_search));
    }


  /** Get the checkbox settings and write them to shared prefs. */
  public void saveCheckboxes ()
    {
    CheckBox searchAppsCheckBox = 
     (CheckBox) findViewById(R.id.checkbox_search_apps);
    MainActivity.setBoolPreference 
      ("search_apps", searchAppsCheckBox.isChecked());

    CheckBox searchBookmarksCheckBox = 
     (CheckBox) findViewById(R.id.checkbox_search_bookmarks);
    MainActivity.setBoolPreference 
      ("search_bookmarks", searchBookmarksCheckBox.isChecked());

    CheckBox searchFilesCheckBox = 
     (CheckBox) findViewById(R.id.checkbox_search_files);
    MainActivity.setBoolPreference 
      ("search_files", searchFilesCheckBox.isChecked());
    }


  /** Returns true if the candidate string is considered a strong enough
      match to include in the search results */
  public boolean includeInResults (String searchString, String candidate)
    {
    // Note that we don't want to match in the "http" or "https" of
    //  browser bookmarks
    String lcCandidate = candidate.toLowerCase();
    if (candidate.indexOf ("https") == 0)
      {
      if (lcCandidate.indexOf (searchString.toLowerCase()) >= 5) 
        return true;
      }
    if (candidate.indexOf ("http") == 0)
      {
      if (lcCandidate.indexOf (searchString.toLowerCase()) >= 4) 
        return true;
      } 
    else
      {
      if (lcCandidate.indexOf (searchString.toLowerCase()) >= 0) 
        return true;
      }
    return false;
    }

 
  boolean isFileInStartTree (StartTree st, File path)
    {
    for (StartTreeNode node : st.getNodes())
      {
      if (node instanceof FileNode)
        {
        File candidate = ((FileNode) node).getPath();
        if (candidate.getName().equals(path.getName()))
          {
          if (candidate.length() == path.length())
            return true;
          }
        }
      }
    return false; 
    }


  public void doSearch (String searchString)
    {
    //int limit = 200; //TODO -- from settings

    boolean searchBookmarks = MainActivity.getBoolPreference    
       ("search_bookmarks", true);
    boolean searchFiles = MainActivity.getBoolPreference    
       ("search_files", true);
    boolean searchApps = MainActivity.getBoolPreference    
       ("search_apps", true);

    if (!searchBookmarks && !searchFiles && !searchApps)
      {
      MainActivity.showErrorMessage (this, 
        MainActivity.getStringResource (R.string.nothing_to_search));
      return;
      }

    StartTree st = new ProgramGroup ("Results");

    searchTask = new SearchTask();
    SearchTaskParams stp = new SearchTaskParams();
    stp.searchBookmarks = searchBookmarks;
    stp.searchApps = searchApps;
    stp.searchFiles = searchFiles;
    stp.searchString = searchString;
    stp.activity = this;

    // DO the search in a background thread
    enableStopButton();
    searchTask.execute (stp);
    }


  public void updateResults (StartTree st)
    {
    ListView searchResultList = 
         (ListView) findViewById(R.id.searchresultlist);
    ArrayAdapter adapter = new StartTreeArrayAdapter 
          (this, R.layout.app_list_item, st.getNodes());
    searchResultList.setAdapter(adapter);

    MainListClickListener listener =
          new MainListClickListener (this, st);
    searchResultList.setOnItemClickListener (listener);
    searchResultList.setOnItemLongClickListener (listener);
    }
}



/** Class to do the search in a background threa. */
class SearchTask extends AsyncTask<SearchTaskParams, String, StartTree> 
  {
  private SearchActivity activity = null;

  protected StartTree doInBackground(SearchTaskParams... stps) 
    {
    int limit = 200; //TODO -- from settings

    StartTree st = new ProgramGroup ("Results");
    SearchTaskParams stp = stps[0];
    String searchString = stp.searchString;
    SearchActivity activity = stp.activity;
    this.activity = activity;

    // Apps
    if (stp.searchApps && !isCancelled())
      {
      publishProgress ("Searching apps"); 
      ArrayList<MyPackageInfo> packageList = 
        MyPackageManager.getInstalledApps (MainActivity.getContext(), false);
      Collections.sort (packageList, new Comparator<MyPackageInfo>()
        {
        public int compare (MyPackageInfo t1, MyPackageInfo t2)
          {
          return t1.getAppName().toUpperCase().compareTo 
            (t2.getAppName().toUpperCase());
          }
          });

      for (MyPackageInfo info : packageList)
        {
        // TODO limit ?
        if (activity.includeInResults (searchString, info.getAppName()))
          {
          StartTreeNode node = new LauncherNode (info.getPackageName());
          st.addNode (node);
          }
        }
      }

    // Bookmarks
    if (stp.searchBookmarks && !isCancelled())
      {
      publishProgress ("Searching bookmarks"); 
      // Ugh. This same code is also in BookmarksNode, and really it should
      //  be refactored
      Uri uri = Browser.BOOKMARKS_URI;

      String[] proj = new String[] 
        { Browser.BookmarkColumns.TITLE,Browser.BookmarkColumns.URL };
      String sel = Browser.BookmarkColumns.BOOKMARK + " = 1";
      Cursor cursor = MainActivity.getContext().getContentResolver().query 
        (uri, proj, sel, null, null);
      if (cursor != null)
        {
        if (cursor.moveToFirst() && cursor.getCount() > 0) 
         {
         while (cursor.isAfterLast() == false) 
           {
           String title = cursor.getString
             (cursor.getColumnIndex(Browser.BookmarkColumns.TITLE));
           String url = cursor.getString
             (cursor.getColumnIndex(Browser.BookmarkColumns.URL));
  
           if (activity.includeInResults (searchString, title))
             {
             st.addNode (new URLLauncherNode (title, url));
             }
           else if (activity.includeInResults (searchString, url))
              {
              st.addNode (new URLLauncherNode (title, url));
              }

           cursor.moveToNext();
           }
         }
        } 
      }

    // Files
    if (stp.searchFiles && !isCancelled())
      {
      publishProgress ("Searching files"); 
      searchDirectory ("/storage", st, true, limit, searchString);
      }

    return st;
    }

  protected void onProgressUpdate(String... progress) 
     {
     activity.setProgress (progress[0]);
     }

  protected void onCancelled (StartTree st) 
    {
    // We can still show any results gathered so far. But don't
    //  warn if there are none.
    activity.updateResults (st);
    activity.onSearchTaskComplete();
    }

  protected void onPostExecute (StartTree st) 
    {
    if (st.getNodes().size() == 0)
      {
      MainActivity.showErrorMessage 
        (activity, MainActivity.getStringResource (R.string.no_search_matches));
      }
     else
      {
      activity.updateResults (st);
      }
    activity.onSearchTaskComplete();
    }

  void searchDirectory (String root, StartTree st, boolean recurse, 
      int limit, String searchString)
    {
    if (st.getNodes().size() >= limit) return;
    File file = new File (root);
    publishProgress (file.getName()); 
    String[] names = file.list();
    if (names != null)
      {
      for (String name : names)
        {
        String pathName = root + "/" + name;
        File path = new File (pathName);
        // TODO hidden files 
        if (path.isDirectory() && recurse && !isCancelled())
          {
          searchDirectory (path.toString(), st, recurse, limit, searchString);
          }
        else if (path.isFile())
          {
          if (activity.includeInResults (searchString, name))
            {
            if (st.getNodes().size() < limit)
              {
              if (!activity.isFileInStartTree (st, path))
                st.addNode (new FileNode (path));
              }
            } 
          }
        }
      }
    }
  }

class SearchTaskParams
  {
  public String searchString;
  public SearchActivity activity;
  public boolean searchApps;
  public boolean searchBookmarks;
  public boolean searchFiles;
  }






