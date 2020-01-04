/*
 * net.kevinboone.androidstart.RecentNode
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

/** Stores the list of recently-launched items, as they appear on
    the "Recent" start tree item */
public class RecentNode extends StartTree implements Serializable 
  {
  private static final long serialVersionUID = 1L;

  public RecentNode ()
    {
    super (MainActivity.getStringResource (R.string.recent));
    }

  @Override
  public void invoke (Activity activity)
    {
    MainActivity.getMainActivity().selectAppListStartTree (this);
    }

  @Override
  public void addNode (StartTreeNode node)
    {
    // Don't add this node if there is already one of the same name
    StartTreeNode oldNode = findByName (node.getTitle());
    if (oldNode == null)
      {
      if (getNodes().size() >= 
          MainActivity.getIntPreference ("recent_max", 10))
        {
        getNodes().remove (nodes.size() - 1); 
        }

      getNodes().add (0, node);
      }
    }

  @Override
  public void contextMenu (Activity activity, View view, 
      StartTree parentStartTree)
    {
    final Activity _activity = activity;
    final StartTreeNode node = this;
    final StartTree _parentStartTree = parentStartTree;
    PopupMenu pm = new PopupMenu (activity, view);
    pm.getMenuInflater().inflate (R.menu.recent_context_menu, 
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
        else if (mi.getItemId() == R.id.clear)
          {
          clear (node);
          }
        return true;
        }
      });
    pm.show();
    }


  @Override
  public Drawable getDrawable ()
    {
    return MainActivity.getContext()
      .getResources().getDrawable (R.drawable.ic_group);
    }
  }


