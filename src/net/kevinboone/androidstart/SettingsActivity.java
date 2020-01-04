/*
 * net.kevinboone.androidstart.SettingsActivity
 * (c)2014 Kevin Boone
 */
package net.kevinboone.androidstart;

import net.kevinboone.androidutils.MyPackageManager;
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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity
    implements SharedPreferences.OnSharedPreferenceChangeListener
  {
  @Override
  public void onCreate(Bundle savedInstanceState)
    {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.preferences);
    getPreferenceScreen().getSharedPreferences()
            .registerOnSharedPreferenceChangeListener (this);

    }

  public void onSharedPreferenceChanged (SharedPreferences sharedPreferences,
        String key) 
    {
    if (key.equals("theme")) 
      {
      String theme = sharedPreferences.getString (key, "dark");
      MainActivity.getMainActivity().setThemeByName (theme); 
      MainActivity.getMainActivity().recreate();
      }
    } 

  }

