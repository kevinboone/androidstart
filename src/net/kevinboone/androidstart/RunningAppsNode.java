/*
 * net.kevinboone.androidstart.RunningAppsNode
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
import android.app.ActivityManager;
import android.content.ComponentName;

public class RunningAppsNode extends StartTree implements Serializable 
  {
  private static final long serialVersionUID = 1L;

  public RunningAppsNode ()
    {
    super (MainActivity.getStringResource (R.string.running_apps));
    }

  public void buildList ()
      throws Exception
    {
    boolean showNoActivityTasks = 
      MainActivity.getBoolPreference ("show_no_activity_tasks", false); 

    nodes.clear();

    ActivityManager am = (ActivityManager) MainActivity.getMainActivity() 
                .getSystemService(Activity.ACTIVITY_SERVICE);

    List<ActivityManager.RunningTaskInfo> tasks = 
      am.getRunningTasks (100);

    for (ActivityManager.RunningTaskInfo task : tasks)
      {
      if (task.numRunning > 0 || showNoActivityTasks)
        {
        ComponentName cn = task.topActivity;
        RunningAppNode n = new RunningAppNode (cn.getPackageName(),
          task.numRunning, task.numActivities);
        nodes.add (n);
        }
      }
    }

  @Override
  public void refresh (MainActivity main)
    {
    try
      {
      buildList ();
      }
    catch (Exception e)
      {
      main.showException (e);
      }
    }

  public void invoke (Activity activity)
    {
    nodes.clear();

    try
      {
      buildList ();
      }
    catch (Exception e)
      {
      MainActivity.showException (activity, e);
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
    pm.getMenuInflater().inflate (R.menu.running_apps_context_menu, 
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

  public String getNodeInfoHtml ()
    {
    return "<b>Running apps</b>:<br/>" + 
      "A list of all tasks currently in Android's task list, " + 
       "most recently-used at the top";
    }


  public Drawable getDrawable ()
    {
    return MainActivity.getContext()
      .getResources().getDrawable (R.drawable.ic_group);
    }
  }


