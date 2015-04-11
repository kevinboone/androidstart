/*
 * net.kevinboone.androidstart.StartTree
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
import java.util.Collections; 
import java.util.Comparator; 
import java.util.ArrayList; 
import android.content.Context;
import java.io.Serializable;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import android.widget.Toast;

public abstract class StartTree extends StartTreeNode implements Serializable 
  {
  private static final long serialVersionUID = 1L;
  protected ArrayList<StartTreeNode> nodes = new ArrayList<StartTreeNode>();

  public StartTree (String title)
    {
    super (title);
    }

  public ArrayList<StartTreeNode> getNodes() 
    {
    return nodes;
    }

  public void addNode (StartTreeNode node)
    {
    nodes.add (node);
    }

  public void removeNode (StartTreeNode node)
    {
    nodes.remove (node);
    }

  public static StartTree makeTestTree ()
    {
    StartTree tree = new ProgramGroup("Top");
    StartTreeNode n1 = new LauncherNode ("com.android.browser");
    StartTreeNode n2 = new LauncherNode ("com.android.settings");
    tree.addNode (n1);
    tree.addNode (n2);
    StartTree group = new ProgramGroup("Test group");
    StartTreeNode n3 = new LauncherNode ("com.android.settings");
    group.addNode (n3);
    tree.addNode (group);
    StartTree allApps = new AllAppsNode();
    tree.addNode (allApps);
    return tree;
    }

  public static StartTree makeBaseTree ()
    {
    StartTree tree = new ProgramGroup("Top");
    StartTree files = new FilesNode("/");
    tree.addNode (files);
    StartTree bookmarks = new BookmarksNode();
    tree.addNode (bookmarks);
    StartTree recent = new RecentNode();
    tree.addNode (recent);
    StartTree runningApps = new RunningAppsNode();
    tree.addNode (runningApps);
    StartTree allApps = new AllAppsNode();
    tree.addNode (allApps);
    return tree;
    }

  public void store (Context context)
    {
    try
      {
      FileOutputStream fos = context.openFileOutput ("StartTree", 0);
      ObjectOutputStream oos = new ObjectOutputStream (fos); 
      oos.writeObject (this);
      oos.close();
      fos.close();
      }
    catch (Exception e)
      {
      Toast.makeText(context,
        e.toString(), Toast.LENGTH_LONG)
      .show();
      }
    }

  public static StartTree load (Context context)
      throws Exception
    {
    try
      {
      FileInputStream fis = context.openFileInput ("StartTree");
      ObjectInputStream ois = new ObjectInputStream (fis);
      Object o = ois.readObject();
      ois.close();
      fis.close();
      return (StartTree) o;
      }
    catch (Exception e)
      {
      // It is normal for this operation to fail with a file-not-found.
      // TODO: Consider what to do with other exceptions.
      e.printStackTrace();
      throw e;
      }
    }

  public void unpin (Activity activity, StartTreeNode node)
    {
      Toast.makeText(activity,
       "Can't unpin from this list", Toast.LENGTH_LONG)
        .show();
    }

  public void moveUp (Activity activity, StartTreeNode node)
    {
      Toast.makeText(activity,
       "Can't move items in this list", Toast.LENGTH_LONG)
        .show();
    }

  public void moveDown (Activity activity, StartTreeNode node)
    {
      Toast.makeText(activity,
       "Can't move items in this list", Toast.LENGTH_LONG)
        .show();
    }

  public StartTreeNode findByName (String name)
    {
    int i, l = nodes.size();
    for (i = 0; i < l; i++)
      {
      if (nodes.get(i).getTitle().equals(name))
        return nodes.get(i);
      }
    return null;
    }


  public void addProgramGroup (String name)
      throws Exception
    {
    StartTreeNode existing = findByName (name);
    if (existing != null)
      throw new Exception 
        (MainActivity.getStringResource (R.string.duplicate_group_name));
    StartTree group = new ProgramGroup (name);
    addNode (group);
    }

  /** Find the first child node of the specified class. Used by MainActivity
      to find system groups like "All apps" and "Recent" */
  StartTreeNode findFirstNodeOfClass (Class c)
    {
    for (StartTreeNode node : nodes)
      {
      if (node.getClass() == c) return node;
      }
    return null;
    }

  /** Find the first child node for which the specified comparator's compare()
      method returns true */ 
  StartTreeNode findFirstNodeByComparator (StartTreeNodeComparator comp)
    {
    for (StartTreeNode node : nodes)
      {
      if (comp.compare (node)) return node;
      }
    return null;
    }

  public void clear (StartTreeNode node)
    {
    nodes.clear();
    MainActivity.getMainActivity().refreshMainList();
    MainActivity.getMainActivity().refreshAppList();
    MainActivity.getMainActivity().storeStartTree();
    }

  public void sort()
    {
    final boolean filesFirst = 
        MainActivity.getBoolPreference ("files_first", false); 

    Collections.sort (getNodes(), new Comparator<StartTreeNode>()
      {
      public int compare (StartTreeNode t1, StartTreeNode t2)
        {
        if (!filesFirst)
          {
          // Folders first
          if (t1 instanceof StartTree && !(t2 instanceof StartTree))
            return -1;
          if (t2 instanceof StartTree && !(t1 instanceof StartTree))
            return 1;
          }
        else
          {
          // Files first
          if (t1 instanceof StartTree && !(t2 instanceof StartTree))
            return 1;
          if (t2 instanceof StartTree && !(t1 instanceof StartTree))
            return -1;
          }
        return t1.getTitle().toUpperCase().compareTo(t2.getTitle().toUpperCase());
        }
      });
    }
  }


