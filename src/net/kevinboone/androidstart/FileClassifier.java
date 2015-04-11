/*
 * net.kevinboone.androidstart.FileClassifier
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

public class FileClassifier
  {
  public static final int UNKNOWN = 0;
  public static final int AUDIO = 1;
  public static final int VIDEO = 2;
  public static final int TEXT = 3;
  public static final int ARCHIVE = 4;
  public static final int IMAGE = 5;

  static int getTypeFromMime (String mimeType)
    {
    if (mimeType == null) return UNKNOWN;
    if (mimeType.length() == 0) return UNKNOWN;
    if (mimeType.indexOf ("ogg") >= 0) return AUDIO;
    if (mimeType.indexOf ("audio") >= 0) return AUDIO;
    if (mimeType.indexOf ("video") >= 0) return VIDEO;
    if (mimeType.indexOf ("text") >= 0) return TEXT;
    if (mimeType.indexOf ("xml") >= 0) return TEXT;
    if (mimeType.indexOf ("zip") >= 0) return ARCHIVE;
    if (mimeType.indexOf ("archive") >= 0) return ARCHIVE;
    if (mimeType.indexOf ("image") >= 0) return IMAGE;
    return UNKNOWN;
    }
  }

