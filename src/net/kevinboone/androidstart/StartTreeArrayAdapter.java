/*
 * net.kevinboone.androidstart.StartTreeArrayAdapter
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

public class StartTreeArrayAdapter extends ArrayAdapter<StartTreeNode>
  {
  private Context context;
  private ArrayList<StartTreeNode> startTreeNodes; 
  int resId;
  private LayoutInflater inflater; 
  private View rowView = null;
  private static final double pointsToPixels = 
    MainActivity.getContext().getResources().getDisplayMetrics().density * 
     1.0/72.0 * 160.0;

  public StartTreeArrayAdapter 
      (Context context, int resId, ArrayList<StartTreeNode> startTreeNodes)
    {
    super (context, resId, startTreeNodes);
    this.resId = resId;
    this.context = context;
    this.startTreeNodes = startTreeNodes;
    inflater = (LayoutInflater) context
      .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

  @Override
  public View getView (int position, View convertView, ViewGroup parent)
    {
    StartTreeNode node = startTreeNodes.get (position);

    rowView = inflater.inflate(resId, parent, false);

    TextView textView = (TextView) rowView.findViewById(R.id.label);
    ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
    ImageView flagView = (ImageView) rowView.findViewById(R.id.flag);

    textView.setText(node.getTitle()); 
    imageView.setImageDrawable(node.getDrawable());
    if (node instanceof StartTree)
      {
      flagView.setImageDrawable(MainActivity.getContext()
        .getResources().getDrawable (R.drawable.ic_right_arrow));
      }

    textView.setTextSize (android.util.TypedValue.COMPLEX_UNIT_PT, 
      MainActivity.getListTextSize());

    imageView.getLayoutParams().height = (int) (MainActivity.getListTextSize() 
      * pointsToPixels);
    imageView.getLayoutParams().width = (int) (MainActivity.getListTextSize()
      * pointsToPixels);
    imageView.requestLayout();

    flagView.getLayoutParams().height = (int) (MainActivity.getListTextSize()
      * pointsToPixels);
    flagView.getLayoutParams().width = (int) (MainActivity.getListTextSize()
      * pointsToPixels);
    flagView.requestLayout();

    return rowView;
    }
  }



