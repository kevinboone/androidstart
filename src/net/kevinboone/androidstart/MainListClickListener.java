/*
 * net.kevinboone.androidstart.MainListClickListener
 * (c)2014 Kevin Boone
 */
package net.kevinboone.androidstart;

import android.widget.AdapterView;
import android.widget.Toast;
import android.view.View;
import android.app.Activity;

/** Responds to clicks on the main list. */
public class MainListClickListener implements AdapterView.OnItemClickListener,
    AdapterView.OnItemLongClickListener
  {
  private Activity activity; 
  private StartTree startTree;

  public MainListClickListener (Activity activity, StartTree startTree)
    {
    this.activity = activity;
    this.startTree = startTree;
    }

  @Override
  public void onItemClick (AdapterView<?> parent, View view,
      int position, long id) 
    {
    StartTreeNode node = (StartTreeNode) 
      parent.getItemAtPosition (position);
    node.invoke (activity);
    }

  @Override
  public boolean onItemLongClick (AdapterView<?> parent, View view,
      int position, long id) 
    {
    StartTreeNode node = (StartTreeNode) 
      parent.getItemAtPosition (position);
    node.contextMenu (activity, view, startTree);
    return true;
    }
  }


