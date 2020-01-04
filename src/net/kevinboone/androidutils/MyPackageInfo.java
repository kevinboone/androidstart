/*
 * net.kevinboone.androidutils.MyPackageInfo
 * (c)2014 Kevin Boone
 */
package net.kevinboone.androidutils;
import android.graphics.drawable.Drawable;

/** Simple holder class for various package-related pieces of 
    information about an app */
public class MyPackageInfo
  {
  String appName = "";
  protected String pname = "";
  protected String versionName = "";
  protected int versionCode = 0;
  protected Drawable icon;

  public String toString() 
    {
    return appName + "\t" + pname + "\t" + versionName + "\t" + versionCode;
    }

  public String getAppName ()
    {
    return appName;
    }

  public Drawable getIcon ()
    {
    return icon;
    }

  public String getPackageName ()
    {
    return pname;
    }
  }


