/*
 * net.kevinboone.androidutils.MyPackageManager
 * (c)2014 Kevin Boone
 */
package net.kevinboone.androidutils;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Hashtable;
import android.content.pm.PackageInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.ActivityNotFoundException;
import android.widget.Toast;

/** Simplifying wrapper around the Android platform package manager classes. */
public class MyPackageManager
  {
  static protected Hashtable iconCache = new Hashtable<String, Drawable>();
  static protected Hashtable titleCache = new Hashtable<String, String>();

  /** Get a list of installed applications in the form of an array
      of MyPackageInfo structures. */
  static public ArrayList<MyPackageInfo> getInstalledApps 
      (Context context, boolean getSysPackages)
    {
    ArrayList<MyPackageInfo> res = new ArrayList<MyPackageInfo>();        

    PackageManager packageManager = context.getPackageManager();

    Intent intent = new Intent(Intent.ACTION_MAIN, null);
    intent.addCategory(Intent.CATEGORY_LAUNCHER);
    List<ResolveInfo> resInfos = 
      packageManager.queryIntentActivities(intent, 0);

    // Remove duplicates
    TreeSet<String> packageNames = new TreeSet<String>();
    List<ApplicationInfo> appInfos = new ArrayList<ApplicationInfo>(0);
    for(ResolveInfo resolveInfo : resInfos) 
      {
      packageNames.add(resolveInfo.activityInfo.packageName);
      }

    Intent intent2 = new Intent(Intent.ACTION_MAIN, null);
    intent2.addCategory(Intent.CATEGORY_HOME);
    List<ResolveInfo> resInfos2 = 
      packageManager.queryIntentActivities(intent2, 0);

    // Remove duplicates
    for(ResolveInfo resolveInfo : resInfos2) 
      {
      packageNames.add(resolveInfo.activityInfo.packageName);
      }

    for (String packageName : packageNames) 
      {
      try 
        {
        ApplicationInfo appInfo = 
          packageManager.getApplicationInfo 
           (packageName, PackageManager.GET_META_DATA);
        
        MyPackageInfo newInfo = new MyPackageInfo();
        newInfo.appName = 
          appInfo.loadLabel (packageManager).toString();
        newInfo.pname = packageName;
        newInfo.icon = appInfo.loadIcon (packageManager);
        res.add(newInfo);
        } 
      catch (Exception e) 
        {
        }
      }

    return res; 
    }

  /** Use the package manager to get the icon for the specified 
      package, if possible. */
  public static synchronized Drawable getDrawableForPackage (Context context, 
      String packageName)
      throws NameNotFoundException
    {
    Drawable cachedDrawable = (Drawable) iconCache.get (packageName);
    if (cachedDrawable != null) return cachedDrawable;

    PackageManager packageManager = context.getPackageManager();

    Drawable drawable;
    ApplicationInfo appInfo = 
        packageManager.getApplicationInfo 
         (packageName, PackageManager.GET_META_DATA);
        
    drawable = appInfo.loadIcon (packageManager);
    iconCache.put (packageName, drawable);
    return drawable;
    }

  /** Use the package manager to get the title for the specified 
      package, if possible. */
  public synchronized static String getTitleForPackage (Context context, 
      String packageName)
      throws NameNotFoundException
    {
    String cachedTitle = (String) titleCache.get (packageName);
    if (cachedTitle != null) return cachedTitle;

    PackageManager packageManager = context.getPackageManager();

    Drawable drawable;
    ApplicationInfo appInfo = 
        packageManager.getApplicationInfo 
         (packageName, PackageManager.GET_META_DATA);

    String title = appInfo.loadLabel (packageManager).toString();
    titleCache.put (packageName, title);
    return title;
    }

  /** Launch an app by its package name. For reasons that are not really
      clear, this won't always work, and we need to try various methods.
      This method throws no exceptions, but returns true to indicate
      that something appeared to get launched */
  public static boolean launchPackage (Context context, String packageName)
    {
    boolean launched = false;
    Intent launchApp = context.getPackageManager().getLaunchIntentForPackage 
      (packageName);
    if (launchApp != null) try
      {
      // In principle, this should always work, as the Intent has come
      //  from the package manager. But does it? Does it heck. So we
      //  need to catch exceptions and try other methods if it fails
      context.startActivity (launchApp);
      launched = true;
      }
    catch (ActivityNotFoundException e)
      {
      }

    if (!launched)
      {
      // Try to launch the package as if it were a launcher. We need
      //  this because the thing we're launching might actually be a 
      //  launcher and, if so, getLaunchIntentForPackage won't actually
      //  find it (sigh :/)
      Intent intentToResolve = new Intent(Intent.ACTION_MAIN);
      intentToResolve.addCategory(Intent.CATEGORY_HOME);
      intentToResolve.setPackage(packageName);
      ResolveInfo ri = 
        context.getPackageManager().resolveActivity(intentToResolve, 0);
      if (ri != null) 
        {
        Intent intent = new Intent(intentToResolve);
        intent.setClassName(ri.activityInfo.applicationInfo.packageName, 
          ri.activityInfo.name);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        context.startActivity(intent);
        launched = true;
        }
      // Should we catch exceptions?
      }

    if (!launched)
      {
      try
        {
        Intent ii = new Intent();
        //ii.addCategory(Intent.CATEGORY_LAUNCHER); 
        //ii.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK 
        //   | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED); 
        ii.setPackage (packageName);
        context.startActivity(ii);
        launched = true;
        }
      catch (Throwable e)
        {
        }
      }
    return launched;
    }
  }


