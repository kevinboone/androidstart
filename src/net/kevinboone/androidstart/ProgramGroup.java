/*
 * net.kevinboone.androidstart.ProgramGroup
 * (c)2014 Kevin Boone
 */
package net.kevinboone.androidstart;

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

public class ProgramGroup extends StartTree implements Serializable 
  {
  private static final long serialVersionUID = 1L;

  public ProgramGroup (String title)
    {
    super (title);
    }

  @Override
  public void invoke (Activity activity)
    {
    MainActivity.getMainActivity().selectAppListStartTree (this);
    }

  @Override
  public Drawable getDrawable ()
    {
    return MainActivity.getContext()
      .getResources().getDrawable (R.drawable.ic_group);
    }

  @Override
  public void unpin (Activity activity, StartTreeNode node)
    {
    nodes.remove (node);
    MainActivity.getMainActivity().refreshMainList();
    MainActivity.getMainActivity().refreshAppList();
    MainActivity.getMainActivity().storeStartTree();
    }

  @Override
  public void moveUp (Activity activity, StartTreeNode node)
    {
    int i, j = -1, l = nodes.size();
    for (i = 0; i < l; i++)
      {
      if (nodes.get(i) == node)
        j = i;
      }
    if (j > 0)
      {
      int k = j - 1;
      if (k < nodes.size())
        {
        Collections.rotate (nodes.subList (k, j+1), -1);
        MainActivity.getMainActivity().refreshMainList();
        MainActivity.getMainActivity().refreshAppList();
        MainActivity.getMainActivity().storeStartTree();
        }
      }
    }

  @Override
  public void moveDown (Activity activity, StartTreeNode node)
    {
    int i, j = -1, l = nodes.size();
    for (i = 0; i < l; i++)
      {
      if (nodes.get(i) == node)
        j = i;
      }
    if (j < l - 1)
      {
      int k = j + 1;
      if (k < nodes.size())
        {
        Collections.rotate (nodes.subList (j, k+1), -1);
        MainActivity.getMainActivity().refreshMainList();
        MainActivity.getMainActivity().refreshAppList();
        MainActivity.getMainActivity().storeStartTree();
        }
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
    pm.getMenuInflater().inflate (R.menu.program_group_context_menu, 
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
        else if (mi.getItemId() == R.id.delete)
          {
          // unpin() is sufficient to delete -- GC takes care of the
          //  rest
          _parentStartTree.unpin (_activity, node);
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
        else if (mi.getItemId() == R.id.sortcontents)
          {
          sort();
          MainActivity.getMainActivity().refreshAppList();
          MainActivity.getMainActivity().storeStartTree();
          }
        return true;
        }
      });
    pm.show();
    }

  @Override
  public String getNodeInfoHtml ()
    {
    int n = nodes.size();
    return "<b>Program group</b> containing " + 
      n + " " +  
      (n == 1 ? "item" : "items");
    }
  }



