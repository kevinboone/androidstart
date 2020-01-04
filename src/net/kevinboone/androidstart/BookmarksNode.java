/*
 * net.kevinboone.androidstart.BookmarksNode
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import java.util.HashMap; 
import java.util.List; 
import java.util.ArrayList; 
import java.util.Collections; 
import java.util.Comparator; 
import android.content.Context;
import java.io.Serializable;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import android.widget.Toast;
import android.graphics.drawable.Drawable;
import android.widget.PopupMenu;
import android.view.MenuItem;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.net.Uri;
import android.database.Cursor;
import android.provider.Browser;


public class BookmarksNode extends StartTree implements Serializable 
  {
  private static final long serialVersionUID = 1L;

  public BookmarksNode ()
    {
    super (MainActivity.getStringResource (R.string.bookmarks));
    }

  public void invoke (Activity activity)
    {
    nodes.clear();

    Uri uri = Browser.BOOKMARKS_URI;

    String[] proj = new String[] 
      { Browser.BookmarkColumns.TITLE,Browser.BookmarkColumns.URL };
    String sel = Browser.BookmarkColumns.BOOKMARK + " = 1";
    Cursor cursor = activity.getContentResolver().query 
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

         addNode (new URLLauncherNode (title, url));

         cursor.moveToNext();
         }
       } 
      }
    else
      {
      // No matches in the content provider
      }

    MainActivity.getMainActivity().selectAppListStartTree (this);
    }

  public void contextMenu (Activity activity, View view, 
      StartTree parentStartTree)
    {
    final Activity _activity = activity;
    final StartTreeNode node = this;
    final StartTree _parentStartTree = parentStartTree;
    PopupMenu pm = new PopupMenu (activity, view);
    pm.getMenuInflater().inflate (R.menu.bookmarks_context_menu, 
      pm.getMenu());
    pm.setOnMenuItemClickListener (new PopupMenu.OnMenuItemClickListener () 
      {
      public boolean onMenuItemClick(MenuItem mi) 
        {
        if (mi.getItemId() == R.id.moveup)
          {
          _parentStartTree.moveUp (_activity, node);
          }
        else if (mi.getItemId() == R.id.movedown)
          {
          _parentStartTree.moveDown (_activity, node);
          }
        else if (mi.getItemId() == R.id.info)
          {
          showInfo (_activity);
          }
        return true;
        }
      });
    pm.show();
    }

  public String getNodeInfoHtml ()
    {
    return "<b>Bookmarks</b>:<br/>" + 
      "A list of Web browser bookmarks";
    }

  public Drawable getDrawable ()
    {
    return MainActivity.getContext()
      .getResources().getDrawable (R.drawable.ic_web);
    }
  }



