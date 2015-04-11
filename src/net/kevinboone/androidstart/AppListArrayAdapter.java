/*
 * net.kevinboone.androidstart.AppListArrayAdapter
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
import net.kevinboone.androidutils.MyPackageManager; 
import net.kevinboone.androidutils.MyPackageInfo; 

public class AppListArrayAdapter extends ArrayAdapter<MyPackageInfo>
  {
  private Context context;
  private ArrayList<MyPackageInfo> packageList;

  public AppListArrayAdapter 
      (Context context, ArrayList<MyPackageInfo> packageList)
    {
    super(context, R.layout.app_list_item, packageList);
    this.context = context;
    this.packageList = packageList;
    }

  @Override
  public View getView (int position, View convertView, ViewGroup parent)
    {
    LayoutInflater inflater = (LayoutInflater) context
      .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    View rowView = inflater.inflate(R.layout.app_list_item, parent, false);
    TextView textView = (TextView) rowView.findViewById(R.id.label);
    ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
    textView.setText(packageList.get(position).getAppName()); 
    imageView.setImageDrawable(packageList.get(position).getIcon());
    return rowView;
    }
  }


