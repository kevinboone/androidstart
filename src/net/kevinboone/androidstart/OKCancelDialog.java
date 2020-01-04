/*
 * net.kevinboone.androidstart.OKCancelDialog
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
import android.widget.EditText;
import android.widget.Toast;
import java.util.HashMap; 
import java.util.List; 
import java.util.ArrayList; 
import android.content.Context;
import android.view.MenuInflater;
import android.view.Menu;
import android.widget.PopupMenu;
import android.view.MenuItem;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.DialogInterface;

public abstract class OKCancelDialog extends AlertDialog.Builder
  {
  OKCancelDialog (final Context context, String title, String message)
    {
    super (context);

    setTitle(title);
    setMessage(message);

    setPositiveButton (MainActivity.getStringResource (R.string.ok), 
          new DialogInterface.OnClickListener() 
        {
        public void onClick(DialogInterface dialog, int whichButton) 
          {
          ok();
          dialog.dismiss();
          }
        });

    setNegativeButton (MainActivity.getStringResource (R.string.cancel), 
        new DialogInterface.OnClickListener() 
      {
      public void onClick(DialogInterface dialog, int whichButton) 
        {
        dialog.dismiss();
        }
      });
    }

  public abstract void ok();
  }

