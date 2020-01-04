/*
 * net.kevinboone.androidstart.URLLauncherNode
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
import android.content.Context;
import java.io.Serializable;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import android.widget.Toast;
import android.graphics.drawable.Drawable;
import android.widget.PopupMenu;
import net.kevinboone.androidutils.MyPackageManager;
import android.view.MenuItem;
import android.net.Uri;
import android.content.Intent;

public class URLLauncherNode extends StartTreeNode implements Serializable 
  {
  private static final long serialVersionUID = 1L;
  private String url;

  public URLLauncherNode (String title, String url)
    {
    super (title);
    this.url = url;
    }

  public void invoke (Activity activity)
    {
    Intent browserIntent = new Intent (Intent.ACTION_VIEW, 
      Uri.parse(url));
    try
      {
      activity.startActivity (browserIntent);
      MainActivity.getMainActivity().invoked (this);
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
    pm.getMenuInflater().inflate (R.menu.urllauncher_context_menu, 
      pm.getMenu());
    pm.setOnMenuItemClickListener (new PopupMenu.OnMenuItemClickListener () 
      {
      public boolean onMenuItemClick(MenuItem mi) 
        {
        if (mi.getItemId() == R.id.launch)
          {
          invoke (_activity);
          }
        else if (mi.getItemId() == R.id.moveup)
          {
          _parentStartTree.moveUp (MainActivity.getMainActivity(), node);
          }
        else if (mi.getItemId() == R.id.rename)
          {
          MainActivity.getMainActivity().promptRenameNode 
            (_parentStartTree, node);
          }
        else if (mi.getItemId() == R.id.movedown)
          {
          _parentStartTree.moveDown (MainActivity.getMainActivity(), node);
          }
        else if (mi.getItemId() == R.id.pin)
          {
          MainActivity.getMainActivity().pinDialog (node);
          }
        else if (mi.getItemId() == R.id.unpin)
          {
          _parentStartTree.unpin (_activity, node);
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
    return "<b>URL</b>:<br/>" + 
      "Title: " + getTitle() + "<br/>" + 
      "URL: <code>" + url + "</code><br/>";
    }

  public Drawable getDrawable ()
    {
    return MainActivity.getContext()
        .getResources().getDrawable (R.drawable.ic_web);
    }
  }




