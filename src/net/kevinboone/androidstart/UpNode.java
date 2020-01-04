/*
 * net.kevinboone.androidstart.UpNode
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

/** Represents the "up directory" node in a file list. Essentially
    the same as DirNode, but with a different icon and few menu commands */
public class UpNode extends DirNode implements Serializable 
  {
  private static final long serialVersionUID = 1L;

  public UpNode (File path)
    {
    super (path);
    }

  public void contextMenu (MainActivity main, View view, 
      StartTree parentStartTree)
    {
    final MainActivity _main = main;
    final StartTreeNode node = this;
    final StartTree _parentStartTree = parentStartTree;
    PopupMenu pm = new PopupMenu (main, view);
    pm.getMenuInflater().inflate (R.menu.up_context_menu, 
      pm.getMenu());
    pm.setOnMenuItemClickListener (new PopupMenu.OnMenuItemClickListener () 
      {
      public boolean onMenuItemClick(MenuItem mi) 
        {
        if (mi.getItemId() == R.id.open)
          {
          invoke (_main);
          }
        return true;
        }
      });
    pm.show();
    }


  public Drawable getDrawable ()
    {
    return MainActivity.getContext()
      .getResources().getDrawable (R.drawable.ic_up);
    }
  }


