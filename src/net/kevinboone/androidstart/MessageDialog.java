/*
 * net.kevinboone.androidstart.MessageDialog
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
import android.text.Html;
import android.graphics.drawable.Drawable;

public class MessageDialog extends AlertDialog.Builder
  {
  public MessageDialog (final Context context, String title, String subtitle, 
      String message, Drawable drawable)
    {
    super (context);
    //System.out.println ("XXXXXXXX context=" + context);

    LayoutInflater inflater = (LayoutInflater) context
      .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    View messageView = inflater.inflate(R.layout.message, null, false);

    setTitle(title);
    setMessage(subtitle);
    TextView tv = (TextView) messageView.findViewById (R.id.message_text); 
    ImageView iv = (ImageView) messageView.findViewById (R.id.message_icon); 
    tv.setText (Html.fromHtml (message));
    iv.setImageDrawable (drawable);
    setView (messageView);

    setPositiveButton (MainActivity.getStringResource (R.string.ok), 
          new DialogInterface.OnClickListener() 
        {
        public void onClick(DialogInterface dialog, int whichButton) 
          {
          dialog.dismiss();
          }
        });
    }
  }


