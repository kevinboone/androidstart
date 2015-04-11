/*
 * net.kevinboone.androidstart.LauncherNode
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

public class LauncherNode extends StartTreeNode implements Serializable 
  {
  private static final long serialVersionUID = 1L;
  protected String packageName;

  public LauncherNode (String packageName)
    {
    super (packageName);
    this.packageName = packageName;
    try
      {
      setTitle (MyPackageManager.getTitleForPackage 
        (MainActivity.getContext(), packageName));
      }
    catch (Exception e)
      {
      // Perhaps app has been deleted already?
      }
    }

  @Override
  public void invoke (Activity activity)
    {
    if (MyPackageManager.launchPackage (activity, packageName))
      {
      MainActivity.getMainActivity().invoked (this);
      //It looks like we ought to close() here but, in fact, this causes
      // a catastrophic failue in Android 4.4.x, where we can't launch
      // _anything_ after a cold boot.
      //activity.close();
      }
    else
      {
      Toast.makeText(activity,
       "Sorry, can't launch " + packageName, Toast.LENGTH_LONG)
        .show();
      }
    }


  public void showPackageInfo (Activity activity)
    {
    Intent browserIntent = new Intent 
            (android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, 
    Uri.parse("package:" + packageName));
    try
      {
      MainActivity.getMainActivity().startActivity (browserIntent);
      }
    catch (Exception e)
      {
      MainActivity.showException (activity, e);
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
    pm.getMenuInflater().inflate (R.menu.launcher_context_menu, 
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
          _parentStartTree.moveUp (_activity, node);
          }
        else if (mi.getItemId() == R.id.rename)
          {
          MainActivity.getMainActivity().promptRenameNode 
            (_parentStartTree, node);
          }
        else if (mi.getItemId() == R.id.movedown)
          {
          _parentStartTree.moveDown (_activity, node);
          }
        else if (mi.getItemId() == R.id.unpin)
          {
          _parentStartTree.unpin (_activity, node);
          }
        else if (mi.getItemId() == R.id.pin)
          {
          MainActivity.getMainActivity().pinDialog (node);
          }
        else if (mi.getItemId() == R.id.settings)
          {
          showPackageInfo (_activity);
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

  @Override
  public String getNodeInfoHtml ()
    {
    return "<b>App</b>:<br/>" + 
      "Package name: <code>" + packageName + "</code><br/>";
    }


  public Drawable getDrawable ()
    {
    try
      {
      return MyPackageManager.getDrawableForPackage 
        (MainActivity.getContext(), packageName); 
      }
    catch (Exception e)
      {
      // Perhaps app has been deleted already?
      return MainActivity.getContext()
        .getResources().getDrawable (R.drawable.ic_group);
      }
    }
  }



