/*
 * net.kevinboone.androidstart.RunningAppNode
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
import android.app.ActivityManager;
import android.content.ComponentName;

public class RunningAppNode extends LauncherNode implements Serializable 
  {
  private static final long serialVersionUID = 1L;
  transient int numRunning = 0;
  transient int numActivities = 0;

  public RunningAppNode (String packageName, int numActivities, int numRunning)
    {
    super (packageName);
    this.numActivities = numActivities;
    this.numRunning = numRunning;
    }

  public void kill ()
    {
    ActivityManager am = (ActivityManager) MainActivity.getMainActivity() 
                .getSystemService(Activity.ACTIVITY_SERVICE);

    am.killBackgroundProcesses (packageName);
    MainActivity.getMainActivity().refreshAppList();
    }

  @Override
  public void contextMenu (Activity activity, View view, 
      StartTree parentStartTree)
    {
    final Activity _activity = activity;
    final StartTreeNode node = this;
    final StartTree _parentStartTree = parentStartTree;
    PopupMenu pm = new PopupMenu (activity, view);
    pm.getMenuInflater().inflate (R.menu.running_app_context_menu, 
      pm.getMenu());
    pm.setOnMenuItemClickListener (new PopupMenu.OnMenuItemClickListener () 
      {
      public boolean onMenuItemClick(MenuItem mi) 
        {
        if (mi.getItemId() == R.id.launch)
          {
          invoke (_activity);
          }
        else if (mi.getItemId() == R.id.kill)
          {
          kill ();
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
    ActivityManager am = (ActivityManager) MainActivity.getContext() 
                .getSystemService(Activity.ACTIVITY_SERVICE);

    return "<b>Running app</b>:<br/>" + 
      "Package name: <code>" + packageName + "</code><br/>" + 
      "Activities: " + numActivities + "<br/>" + 
      "Running activities: " + numRunning + "<br/>";
    }


  }




