/*
 * net.kevinboone.androidstart.FilesNode
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
import java.util.Arrays; 
import java.util.Comparator; 
import android.content.Context;
import java.io.Serializable;
import java.io.IOException;
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

/** Stores the list of files in a particular directory */
public class FilesNode extends StartTree implements Serializable 
  {
  private static final long serialVersionUID = 1L;
  private String path;
  private transient String workingPath;

  public FilesNode (String path)
    {
    super (MainActivity.getStringResource (R.string.files));
    this.path = path;
    workingPath = path;
    }


  @Override
  public void refresh (MainActivity main)
    {
    try
      {
      makeFileList (workingPath);
      }
    catch (Exception e)
      {
      main.showException (e);
      }
    }


  public void makeFileList (String path)
      throws IOException
    {
    File dir = new File (path);
    if (dir.isDirectory())
      {
      File[] list = dir.listFiles();
      if (list != null)
        {
        boolean showHidden = 
          MainActivity.getBoolPreference ("show_hidden", false); 

        boolean filesFirst = 
          MainActivity.getBoolPreference ("files_first", false); 

        // Don't clear the old list until we're reasonable sure we
        //  can display the new one
        nodes.clear();

        if (!path.equals("/"))
          {
          UpNode up = new UpNode (new File (dir.getParent()));
          addNode (up);
          up.setTitle (MainActivity.getStringResource (R.string.uplink));
          }

        Arrays.sort (list, new Comparator<File>()
          {
          public int compare (File t1, File t2)
            {
            return t1.getName().toUpperCase().compareTo 
              (t2.getName().toUpperCase());
            }
          });

        if (filesFirst)
          {
          for (File file : list)
            {
            if (!file.isHidden() || showHidden)
              if (!file.isDirectory())
                addNode (new FileNode (file));
            }
          for (File file : list)
            {
            if (!file.isHidden() || showHidden)
              if (file.isDirectory())
                  addNode (new DirNode (file));
            }
          }
        else
          {
          for (File file : list)
            {
            if (!file.isHidden() || showHidden)
              if (file.isDirectory())
                  addNode (new DirNode (file));
            }
          for (File file : list)
            {
            if (!file.isHidden() || showHidden)
              if (!file.isDirectory())
                addNode (new FileNode (file));
            }
          }
        workingPath = path;
        }
      else
        {
        throw new IOException 
          (MainActivity.getStringResource (R.string.cannot_list_dir));
        }
      }
    }


  public void invoke (Activity activity)
    {
    try
      {
      makeFileList (path);
      MainActivity.getMainActivity().selectAppListStartTree (this);
      }
    catch (Exception e)
      {
      MainActivity.showException (e);
      }
    }


  public void contextMenu (Activity activity, View view, 
      StartTree parentStartTree)
    {
    final Activity _activity = activity;
    final StartTreeNode node = this;
    final StartTree _parentStartTree = parentStartTree;
    PopupMenu pm = new PopupMenu (activity, view);
    pm.getMenuInflater().inflate (R.menu.files_context_menu, 
      pm.getMenu());
    pm.setOnMenuItemClickListener (new PopupMenu.OnMenuItemClickListener () 
      {
      public boolean onMenuItemClick(MenuItem mi) 
        {
        if (mi.getItemId() == R.id.moveup)
          {
          _parentStartTree.moveUp (MainActivity.getMainActivity(), node);
          }
        else if (mi.getItemId() == R.id.movedown)
          {
          _parentStartTree.moveDown (MainActivity.getMainActivity(), node);
          }
        else if (mi.getItemId() == R.id.clear)
          {
          clear (node);
          }
        else if (mi.getItemId() == R.id.unpin)
          {
          _parentStartTree.unpin (MainActivity.getMainActivity(), node);
          }
        else if (mi.getItemId() == R.id.rename)
          {
          MainActivity.getMainActivity().promptRenameNode 
            (_parentStartTree, node);
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

  @Override
  public String getContextTitle ()
    {
    return getTitle() + ": " + workingPath;
    }

  public String getNodeInfoHtml ()
    {
    return "<b>File list</b>:<br/>" + 
      "Base directory <code>" + 
      path + 
      "</code><br/>" + 
      "Current directory <code>" +  
      (workingPath == null ? path : workingPath) + 
      "</code>"; 
    }

  String getPath ()
    {
    return path;
    }

  }


