/*
 * net.kevinboone.androidstart.DirNode
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
import java.io.File;
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

/** Stores a file, to be displayed in the app list 2*/
public class DirNode extends StartTree implements Serializable 
  {
  private static final long serialVersionUID = 1L;
  private File path;

  public DirNode (File path)
    {
    super (path.getName());
    this.path = path;
    }

  public void invoke (Activity activity)
    {
    StartTree appListStartTree =  
      MainActivity.getMainActivity().getAppListStartTree();
    if (appListStartTree != null)
      {
      if (appListStartTree instanceof FilesNode)
        {
        FilesNode filesNode = (FilesNode) appListStartTree;
        try
          {
          filesNode.makeFileList (path.toString());
          }
        catch (Exception e)
          {
          MainActivity.showException (e);
          }
        // Ouch! We have to refresh even in the event of an exception, because
        //  the tree might have been modified before the exception was thrown
        MainActivity.getMainActivity().refreshAppList();    
        }
      }
    }

  public void contextMenu (Activity activity, View view, 
      StartTree parentStartTree)
    {
    final Activity _activity = activity;
    final StartTreeNode node = this;
    final StartTree _parentStartTree = parentStartTree;
    PopupMenu pm = new PopupMenu (activity, view);
    pm.getMenuInflater().inflate (R.menu.dir_context_menu, 
      pm.getMenu());
    pm.setOnMenuItemClickListener (new PopupMenu.OnMenuItemClickListener () 
      {
      public boolean onMenuItemClick(MenuItem mi) 
        {
        if (mi.getItemId() == R.id.pin)
          {
          FilesNode filesNode = new FilesNode (path.toString());
          filesNode.setTitle (path.getName());
          MainActivity.getMainActivity().pinDialog (filesNode);
          }
        else if (mi.getItemId() == R.id.open)
          {
          invoke (_activity);
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


  public Drawable getDrawable ()
    {
    return MainActivity.getContext()
      .getResources().getDrawable (R.drawable.ic_folder);
    }

  public String getNodeInfoHtml ()
    {
    return "<b>Directory</b>:<br/>" + 
      "Path: <code>" + path + "</code><br/>" + 
      "Read: " + (path.canRead() ? "yes" : "no") + "<br/>" + 
      "Write: " + (path.canExecute() ? "yes" : "no") + "<br/>"; 
    }
  }




