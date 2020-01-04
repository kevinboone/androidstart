/*
 * net.kevinboone.androidstart.StartTreeNode
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

public abstract class StartTreeNode implements Serializable 
  {
  private static final long serialVersionUID = 1L;
  protected String title;

  public StartTreeNode (String title)
    {
    this.title = title;
    }

  public String getTitle() 
    {
    return title;
    }

  /** getContextTitle() returns an extended title, for nodes whose
      context may chance at run time, such as a file list */
  public String getContextTitle() 
    {
    return title;
    }

  public void setTitle (String title)
    {
    this.title = title;
    }

  public abstract Drawable getDrawable();

  public abstract void invoke (Activity activity);

  public abstract void contextMenu (Activity activity, View view, 
    StartTree parentStartTree);

  /* Don't make abstract -- most descents will do nothing when refreshed */
  public void refresh (MainActivity main) 
    {
    }

  public void showInfo (Activity activity)
    {
    MessageDialog dialog = new MessageDialog (activity,  
     MainActivity.getStringResource(R.string.info), getTitle(), 
     getNodeInfoHtml(), getDrawable());
    dialog.show();
    }

  public String getNodeInfoHtml ()
    {
    return MainActivity.getStringResource(R.string.no_more_info);
    }
  }



