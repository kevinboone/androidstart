/*
 * net.kevinboone.androidstart.FileNode
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
import android.net.Uri;
import android.content.Intent;
import android.webkit.MimeTypeMap;
import android.content.ActivityNotFoundException;
import java.util.Date; 
import java.text.DateFormat; 

/** Stores a file, to be displayed in the app list 2*/
public class FileNode extends StartTreeNode implements Serializable 
  {
  private static final long serialVersionUID = 1L;
  private File path;
  transient String mimeType = null;
  transient int iconType = FileClassifier.UNKNOWN;

  public FileNode (File path)
    {
    super (path.getName());
    this.path = path;
    }

  private String getExt (String path)
    {
    int p = path.lastIndexOf (".");
    if (p < 0) return "";
    return path.substring (p + 1);
    } 

  public void invoke (Activity activity)
    {
    MimeTypeMap mtm = MimeTypeMap.getSingleton();
    String mimeType = mtm.getMimeTypeFromExtension (getExt (path.toString()));
    Intent intent = new Intent (Intent.ACTION_VIEW);
    intent.setDataAndType (Uri.fromFile (path), mimeType);
      
    try
      {
      MainActivity.getContext().startActivity (intent);
      MainActivity.getMainActivity().invoked (this);
      }
    catch (ActivityNotFoundException e)
      {
      MainActivity.showErrorMessage 
       ("Can't find an activity to handle this file type");
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
    pm.getMenuInflater().inflate (R.menu.file_context_menu, 
      pm.getMenu());
    pm.setOnMenuItemClickListener (new PopupMenu.OnMenuItemClickListener () 
      {
      public boolean onMenuItemClick(MenuItem mi) 
        {
        if (mi.getItemId() == R.id.launch)
          {
          invoke (_activity);
          }
        else if (mi.getItemId() == R.id.pin)
          {
          MainActivity.getMainActivity().pinDialog (node);
          }
        else if (mi.getItemId() == R.id.unpin)
          {
          _parentStartTree.unpin (MainActivity.getMainActivity(), node);
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

  public static String formatFileSize (long size)
    {
    if (size > 1048576)
      return "" + size / 1048576 + " MB"; 
    if (size > 1024)
      return "" + size / 1024 + " kB"; 
    if (size > 0)
      return "" + size + " kB"; 
    return "empty";
    }

  public String getNodeInfoHtml ()
    {
    DateFormat df =  DateFormat.getDateTimeInstance();

    String s = "<b>File</b>:<br/>" + 
      "Path: <code>" + path + "</code><br/>" + 
      "MIME type: <code>" + (mimeType.length() == 0  ? "unknown" : mimeType) 
        + "</code><br/>" + 
      "Read: " + (path.canRead() ? "yes" : "no") + "<br/>" + 
      "Write: " + (path.canExecute() ? "yes" : "no") + "<br/>" +
      "Execute: " + (path.canExecute() ? "yes" : "no") + "<br/>" +
      "Modified: " + 
      ((path.lastModified() == 0) ? "?" :
        df.format (new Date (path.lastModified()))) + "<br/>" +
      "Size: " + formatFileSize (path.length()); 
    return s;
    }


  public Drawable getDrawable ()
    {
    if (mimeType == null)
      {
      MimeTypeMap mtm = MimeTypeMap.getSingleton();
      mimeType = mtm.getMimeTypeFromExtension (getExt (path.toString()));
      if (mimeType == null) 
        mimeType = ""; // Still null, but prevent search for it in future
      iconType = FileClassifier.getTypeFromMime (mimeType);
      //System.out.println ("XXXX mime " + mimeType);
      //System.out.println ("XXXX mime type " + iconType);
      }

    switch (iconType) 
      {
      case FileClassifier.AUDIO:
        return MainActivity.getContext()
          .getResources().getDrawable (R.drawable.ic_mime_audio);

      case FileClassifier.VIDEO:
        return MainActivity.getContext()
          .getResources().getDrawable (R.drawable.ic_mime_video);

      case FileClassifier.TEXT:
        return MainActivity.getContext()
          .getResources().getDrawable (R.drawable.ic_mime_text);

      case FileClassifier.ARCHIVE:
        return MainActivity.getContext()
          .getResources().getDrawable (R.drawable.ic_mime_archive);

      case FileClassifier.IMAGE:
        return MainActivity.getContext()
          .getResources().getDrawable (R.drawable.ic_mime_image);

      default:
        return MainActivity.getContext()
          .getResources().getDrawable (R.drawable.ic_file);
      }
    }

  public File getPath ()
    {
    return path;
    }
  }



