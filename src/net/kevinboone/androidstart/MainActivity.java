/*
 * net.kevinboone.androidstart.MainActivity
 * (c)2014 Kevin Boone
 */
package net.kevinboone.androidstart;

import net.kevinboone.androidutils.MyPackageManager;
import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Window;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;
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
import android.preference.PreferenceManager;
import android.content.Intent;

public class MainActivity extends Activity
  {
  public static final int RESULT_SETTINGS = 100;

  private StartTree rootStartTree = null;
  StartTree startTree = null; // Start tree for main list pane
  StartTree appListStartTree = null; // Start tree for app list pane
  private static Context context;
  private static MainActivity mainActivity;

  public StartTree getAppListStartTree ()
    {
    return appListStartTree;
    }

  public static Context getContext()
    {
    return context;
    }

  /** Better hope there's only one instance of this object. */
  public static MainActivity getMainActivity()
    {
    return mainActivity;
    }

  @Override
  public void onCreate(Bundle savedInstanceState)
    {
    super.onCreate(savedInstanceState);
    context = this;
    mainActivity = this;

    setThemeByName (getThemeName());

    Window window = this.getWindow();
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
      WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
    window.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
      WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
    LayoutInflater inflater = (LayoutInflater) 
      getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    WindowManager.LayoutParams lp = getWindow().getAttributes();
    lp.x = 0;
    lp.y = 0;
    lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
    //lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
    lp.dimAmount = 0;
    lp.gravity=Gravity.BOTTOM | Gravity.LEFT;
    View view = inflater.inflate(R.layout.main, null);

    setContentView(view, lp);

    ListView mainList = (ListView) findViewById(R.id.mainlist);

    try
      {
      startTree = StartTree.load (this);
      }
    catch (Exception e)
      {
      startTree = StartTree.makeBaseTree();
      }

    rootStartTree = startTree;

    //addStandardEntriesToRootStartTree ();
    applySettings ();

    refreshMainList();

    setupMenus (view);
    }


  /** Sets the theme on the some activity, given a theme
      name from settings. This is a static function, and can be called
      by any activity, not just MainActivity. */
  public static void setActivityThemeByName (Activity activity, String name)
    {
    if ("light".equals(name))
     activity.setTheme (R.style.light_theme);
    else if ("mid".equals(name))
      activity.setTheme (R.style.mid_theme);
    else
      activity.setTheme (R.style.dark_theme);
    }    


  /** Sets the theme on the main activity, given a theme
      name from settings. */
  public void setThemeByName (String name)
    {
    setActivityThemeByName (this, name);
    }    


  public void setupMenus (View view)
    {
    ImageView menuIcon = (ImageView) findViewById(R.id.menuicon);
    final PopupMenu pm = new PopupMenu (this, menuIcon);
    pm.setOnMenuItemClickListener (new PopupMenu.OnMenuItemClickListener () 
      {
      public boolean onMenuItemClick(MenuItem mi) 
        {
        return onOptionsItemSelected (mi); 
        }
      });
    pm.getMenuInflater().inflate (R.menu.options_menu, pm.getMenu());
    menuIcon.setOnClickListener (new View.OnClickListener(){
      public void onClick(View v) 
         {
         pm.show();
         }
      });
    ImageView searchIcon = (ImageView) findViewById(R.id.searchicon);
    searchIcon.setOnClickListener (new View.OnClickListener(){
      public void onClick(View v) 
         {
         showSearchDialog();
         }
      });
    /*
    ImageView closeIcon = (ImageView) findViewById(R.id.closeicon);
    closeIcon.setOnClickListener (new View.OnClickListener(){
      public void onClick(View v) 
         {
         close();
         }
      });
    */
    }

  /** We need this for backward compatibility -- when new stuff is 
      added, users won't want to have to reset all groups to see it.
      NO LONGER USED, as we can control which standard items are
      displayed using the settings menu */
  public void addStandardEntriesToRootStartTree ()
    {
    StartTreeNode stn = rootStartTree.findFirstNodeOfClass 
      (FilesNode.class);
    if (stn == null)
      rootStartTree.addNode (new FilesNode ("/"));
    stn = rootStartTree.findFirstNodeOfClass 
      (RunningAppsNode.class);
    if (stn == null)
      rootStartTree.addNode (new RunningAppsNode ());
    stn = rootStartTree.findFirstNodeOfClass 
      (AllAppsNode.class);
    if (stn == null)
      rootStartTree.addNode (new AllAppsNode ());
    }

  /** Close the UI window, but don't kill the task (of course, Android
      might kill it) */
  public void close()
    {
    moveTaskToBack(true);
    }

   /** Close the UI window, and allow the task to exit. */
  public void exit()
    {
    finish();
    }

   /** Shows the About... dialog box */ 
  public void about()
    {
    final MainActivity main = this;
    final AboutDialog about = new AboutDialog (main);
    about.show();
    }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) 
    {
    /*
      Toast.makeText(this,
        "click " + item.getItemId(), Toast.LENGTH_LONG)
        .show();
    */
    switch (item.getItemId())
      {
      case R.id.close:
        close();
        break;
      case R.id.newgroup:
        promptNewGroup();
        break;
      case R.id.exit:
        exit();
        break;
      case R.id.about:
        about();
        break;
      case R.id.reset:
        promptResetGroups();
        break;
      case R.id.refresh:
        refreshMainList();
        if (appListStartTree != null)
          {
          appListStartTree.refresh(this);
          refreshAppList();
          }
        break;
      case R.id.settings:
        Intent i = new Intent (this, SettingsActivity.class);
        startActivityForResult(i, RESULT_SETTINGS);
        break;
      case R.id.sortgroups:
        sortGroups();
        break;
      case R.id.search:
        showSearchDialog();
        break;
      }
    return true; 
    }


  public void showSearchDialog ()
    {
    Intent i = new Intent (this, SearchActivity.class);
    startActivity(i);
    }


  /** Sorts the items in the main list. */
  public void sortGroups()
    {
    rootStartTree.sort();
    storeStartTree();
    refreshMainList();
    }

  /** Prompts the user and resets the groups to a default layout. */
  public void promptResetGroups()
    {
    final MainActivity main = this;
    final OKCancelDialog ok = new OKCancelDialog (main, 
          getStringResource (R.string.confirm),
          getStringResource (R.string.reset_groups))
        {
        public void ok()
          {
          try
            {
            TextView tv = (TextView) findViewById (R.id.caption);
            tv.setText (getStringResource (R.string.app_name));
            rootStartTree = ProgramGroup.makeBaseTree();
            startTree = rootStartTree;
            refreshMainList();
            selectAppListStartTree (new ProgramGroup(""));
            appListStartTree = null;
            refreshAppList();
            storeStartTree();
            }
          catch (Exception e)
            {
            showException (e);
            }
          }
        };
    ok.show();
    }

  
  /** Prompt the user for a new program group name, adds it to the StartTree,
      saves the tree, and updates the UI. Note that at present we can
      only create new top-level groups */
  void promptNewGroup()
    {
    final MainActivity main = this;
    final InputTextDialog id = new InputTextDialog (main, 
          getStringResource (R.string.new_program_group),
          getStringResource (R.string.enter_new_group_name),
          getStringResource (R.string.new_group))
        {
        public void ok(String result)
          {
          if (result != null && result.length() > 0)
            {
            try
              {
              createGroup (result); 
              }
            catch (Exception e)
              {
              showException (e);
              }
            }
          else
            {
            Toast.makeText(main,
              result, Toast.LENGTH_LONG)
              .show();
            }
          }
        };
    id.show();
    }


  /** Create a new program group in the current startTree. Refresh
      the UI and store the tree. */
  public void createGroup (String name)
      throws Exception
    {
    startTree.addProgramGroup (name);
    rootStartTree.store (this);
    refreshMainList();
    }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) 
    {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.options_menu, menu);
    return true;
    }

  public void refreshMainList()
    {
    StartTree startTree = rootStartTree;
    ListView mainList = (ListView) findViewById(R.id.mainlist);
    ArrayAdapter adapter = new StartTreeArrayAdapter 
      (this, R.layout.main_list_item, startTree.getNodes());
    mainList.setAdapter(adapter);

    MainListClickListener listener =
      new MainListClickListener (this, startTree);
    mainList.setOnItemClickListener (listener);
    mainList.setOnItemLongClickListener (listener);
    }

  /** Prompt and rename a start tree node. The owner node is also passed,
      so that in principle we can work out what parts of the UI need to
      be redrawn. Current we just redraw everything :/ */
  public void promptRenameNode (StartTree ownerStartTree, 
       final StartTreeNode node)
    {
    final MainActivity main = this;
    final InputTextDialog id = new InputTextDialog (main, 
          getStringResource (R.string.rename_item),
          getStringResource (R.string.enter_new_name),
          node.getTitle())
        {
        public void ok(String result)
          {
          if (result != null && result.length() > 0)
            {
            try
              {
              node.setTitle (result);
              refreshMainList();
              refreshAppList();
              storeStartTree();
              }
            catch (Exception e)
              {
              showException (e);
              }
            }
          else
            {
            Toast.makeText(main,
              result, Toast.LENGTH_LONG)
              .show();
            }
          }
        };
    id.show();
    }

  public void refreshAppList()
    {
    // Refresh simply by selecting the current applist StartTree again
    if (appListStartTree != null)
      {
      appListStartTree.refresh (this);
      selectAppListStartTree (appListStartTree);
      }
    }

  /** onTouchEvent closes the main window if the user clicks outside it */
  @Override
  public boolean onTouchEvent(MotionEvent event)  
    {  
    if (event.getAction() == MotionEvent.ACTION_OUTSIDE)
      {  
      close();  
      }  
    return false;  
    }  

  public void storeStartTree ()
    {
    try
      {
      rootStartTree.store (this);
      }
    catch (Exception e)
      {
      Toast.makeText(this,
        e.toString(), Toast.LENGTH_LONG)
        .show();
      }
    }

  /** Pin the specified start node to the main list. In future, we will
      support program groups, and this method will prompt for a group
      to pin into */
  public void pinDialog (final StartTreeNode node)
    {
    try
      {
      ArrayList<String> list = new ArrayList<String>(); 
      list.add ("[Top]");

      int i, l = startTree.getNodes().size();
      for (i = 0; i < l; i++)
        {
        StartTreeNode n = startTree.getNodes().get(i);
        if (n instanceof ProgramGroup)
          list.add (n.getTitle());
        }

      final SelectListDialog sl = new SelectListDialog (this, 
          getStringResource (R.string.pin_to_group),
          getStringResource (R.string.select_program_group),
          list, 0)
        {
        public void ok (int iResult, String sResult)
          {
          if (sResult != null && sResult.length() > 0)
            {
            try
              {
              String pinGroupName = sResult;
              StartTree pinStartTree = null; 
              if (pinGroupName.equals("[Top]"))
                pinStartTree = rootStartTree;
              else
                pinStartTree = (StartTree) startTree.findByName (pinGroupName);
              if (pinStartTree != null)
                {
                pinStartTree.addNode (node);
                rootStartTree.store (MainActivity.getContext());
                //TODO we probably don't need to refresh the applist if
                // if it is all-apps, since we can't pin anything in the
                // all-apps list
                if (pinStartTree == rootStartTree)
                  refreshMainList();
                else
                  refreshAppList();
                }
              }
            catch (Exception e)
              {
              showException (e);
              }
            }
          }
        };
      sl.show();
      }
    catch (Exception e)
      {
      showException (e);
      }
    }


  public void selectAppListStartTree (StartTree startTree)
    {
    final ListView appList = (ListView) findViewById(R.id.applist);
    MainListClickListener listener = 
      new MainListClickListener (this, startTree);
    appList.setOnItemClickListener (listener);
    appList.setOnItemLongClickListener (listener);
    ArrayAdapter adapter = new StartTreeArrayAdapter 
      (this, R.layout.app_list_item, startTree.getNodes());
    appList.setAdapter(adapter);
    TextView tv = (TextView) findViewById (R.id.caption);
    tv.setText (getStringResource (R.string.app_name) + ": " 
      + startTree.getContextTitle());
    appListStartTree = startTree;
    }

  public static String getStringResource (int id)
    {
    return MainActivity.getContext().getResources().getString (id);
    }

  public static void showErrorMessage (String message)
    {
    showErrorMessage (MainActivity.getMainActivity(), message);
    }

  public static void showErrorMessage (Activity activity, String message)
    {
    Toast.makeText(activity, message, 
        Toast.LENGTH_LONG).show();
    }

  public static void showException (Exception e)
    {
    showException (MainActivity.getMainActivity(), e);
    }

  public static void showException (Activity activity, Exception e)
    {
    String message = e.getMessage();
    if (message == null || message.length() == 0)
      message = e.toString();
    showErrorMessage (activity, message);
    }

  /** Called by any node with an "invoke" operation to indicate that
      the node was successfully invoked (e.g., an app got launched.
      This is used by MainActivity to maintain the Recent list */
  public void invoked (StartTreeNode node)
    {
    // We'd better hope that we get something of the right class, else a
    //  nasty class cast exception 
    RecentNode recent = (RecentNode) 
     rootStartTree.findFirstNodeOfClass (RecentNode.class);
    if (recent != null)
      {
      recent.addNode (node);  
      storeStartTree ();
      if (appListStartTree != null)
        {
        // Only refresh the applist if it is the recent apps list that
        //  is currently showing
        if (appListStartTree instanceof RecentNode)
          refreshAppList ();
        }
      }
    }


  void applySettings ()
    {
    /* Validate the values set in the preferences dialog */
    int recentMax = getIntPreference ("recent_max", 10);

    if (recentMax <= 0) 
      setIntPreference ("recent_max", 10);

    boolean showAllApps = getBoolPreference ("show_all_apps", true);
    showOrHideAllAppsList (showAllApps);

    boolean showFiles = getBoolPreference ("show_files", true);
    showOrHideFilesList (showFiles);

    boolean showBookmarks = getBoolPreference ("show_bookmarks", true);
    showOrHideBookmarksList (showBookmarks);

    boolean showRecent = getBoolPreference ("show_recent", true);
    showOrHideRecentList (showRecent);

    boolean showRunningApps = getBoolPreference ("show_running_apps", true);
    showOrHideRunningAppsList (showRunningApps);
    }


  void showOrHideAllAppsList (boolean show)
    {
    StartTreeNode stn = rootStartTree.findFirstNodeOfClass 
      (AllAppsNode.class);
    if (stn == null && show)
      rootStartTree.addNode (new AllAppsNode ());
    if (stn != null && !show)
       rootStartTree.removeNode (stn);
    }


  void showOrHideBookmarksList (boolean show)
    {
    StartTreeNode stn = rootStartTree.findFirstNodeOfClass 
      (BookmarksNode.class);
    if (stn == null && show)
      rootStartTree.addNode (new BookmarksNode ());
    if (stn != null && !show)
       rootStartTree.removeNode (stn);
    }


  void showOrHideRecentList (boolean show)
    {
    StartTreeNode stn = rootStartTree.findFirstNodeOfClass 
      (RecentNode.class);
    if (stn == null && show)
      rootStartTree.addNode (new RecentNode ());
    if (stn != null && !show)
       rootStartTree.removeNode (stn);
    }


  void showOrHideRunningAppsList (boolean show)
    {
    StartTreeNode stn = rootStartTree.findFirstNodeOfClass 
      (RunningAppsNode.class);
    if (stn == null && show)
      rootStartTree.addNode (new RunningAppsNode ());
    if (stn != null && !show)
       rootStartTree.removeNode (stn);
    }


  void showOrHideFilesList (boolean show)
    {
    StartTreeNode stn = rootStartTree.findFirstNodeByComparator 
      (new StartTreeNodeComparator()
      {
      public boolean compare (StartTreeNode node)
        {
        if (!(node instanceof FilesNode)) return false;
        FilesNode fn = (FilesNode) node;
        if (fn.getPath().equals ("/")) return true;
        return false; 
        }
      }); 
    if (stn == null && show)
      rootStartTree.addNode (new FilesNode ("/"));
    if (stn != null && !show)
       rootStartTree.removeNode (stn);
    }


  @Override
  protected void onActivityResult (int requestCode, 
      int resultCode, Intent data) 
    {
    super.onActivityResult (requestCode, resultCode, data);
    switch (requestCode) 
      {
      case RESULT_SETTINGS:
        applySettings();
        refreshMainList();
        refreshAppList();
        storeStartTree();
      break;
      }
    refreshAppList(); // Because setting change may affect display
    }


  /** Wrapper around Android's brain-dead (non-)handling of integer-valued
      user preferences :/. */
  static int getIntPreference (String name, int deflt)
    {
    SharedPreferences sharedPrefs = 
      PreferenceManager.getDefaultSharedPreferences
        (MainActivity.getContext());

    int value = deflt; 
    try
      {
      value = 
        Integer.parseInt (sharedPrefs.getString (name, "" + deflt));
      }
    catch (Exception e)
      {
      value = deflt; 
      }
    return value;
    } 


  static boolean getBoolPreference (String name, boolean deflt)
    {
    SharedPreferences sharedPrefs = 
      PreferenceManager.getDefaultSharedPreferences
        (MainActivity.getContext());

    boolean value = deflt; 
    try
      {
      value = sharedPrefs.getBoolean(name, deflt);
      }
    catch (Exception e)
      {
      value = deflt; 
      }
    return value;
    } 


  /** Helper function to get the theme name from SharedPrefs */
  static String getThemeName ()
    {
    return getStringPreference ("theme", "dark");
    }

  /** Helper function to get the list text size from SharedPrefs */
  static int getListTextSize ()
    {
    String sListTextSize = getStringPreference ("list_text_size", "8");
    try
      {
      return Integer.parseInt (sListTextSize);
      }
    catch (Exception e) 
      {
      // Should only happen if shared prefs data is corrupt
      return 8;
      }
    }

  /** Helper function to set the theme name into SharedPrefs */
  static void setThemeNameToPrefs (String themeName)
    {
    setStringPreference ("theme", themeName);
    }


  static String getStringPreference (String name, String deflt)
    {
    SharedPreferences sharedPrefs = 
      PreferenceManager.getDefaultSharedPreferences
        (MainActivity.getContext());

    String value = deflt; 
    try
      {
      value = sharedPrefs.getString(name, deflt);
      }
    catch (Exception e)
      {
      value = deflt; 
      }
    return value;
    } 


  /** Wrapper around Android's brain-dead (non-)handling of integer-valued
      user preferences :/. */
  static void setIntPreference (String name, int value)
    {
    SharedPreferences sharedPrefs = 
      PreferenceManager.getDefaultSharedPreferences
        (MainActivity.getContext());

    SharedPreferences.Editor editor = sharedPrefs.edit();
    editor.putString(name, "" + value);
    editor.commit();
    } 


  static void setBoolPreference (String name, boolean value)
    {
    SharedPreferences sharedPrefs = 
      PreferenceManager.getDefaultSharedPreferences
        (MainActivity.getContext());

    SharedPreferences.Editor editor = sharedPrefs.edit();
    editor.putBoolean(name, value);
    editor.commit();
    } 


  static void setStringPreference (String name, String value)
    {
    SharedPreferences sharedPrefs = 
      PreferenceManager.getDefaultSharedPreferences
        (MainActivity.getContext());

    SharedPreferences.Editor editor = sharedPrefs.edit();
    editor.putString(name, value);
    editor.commit();
    } 
  }


