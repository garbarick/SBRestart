package ru.net.serbis.restart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.*;

/**
 * SEBY0408
 */
public class ApplicationUtils
{
    private final String TAG = getClass().getName();

    public List<Application> getProcList(Context context)
    {
        List<Application> result = new ArrayList<Application>();
        PackageManager pm = context.getPackageManager();
        for (PackageInfo packageInfo : pm.getInstalledPackages(0))
        {
            Intent intent = pm.getLaunchIntentForPackage(packageInfo.packageName);
            if (intent != null)
            {
                result.add(getApplication(pm, packageInfo));
            }
        }
        Collections.sort(result);
        return result;
    }

    public Application getApplication(Context context, String packageName)
    {
        try
        {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA);
            return getApplication(pm, packageInfo);
        }
        catch (Throwable ignored)
        {
            return null;
        }
    }

    private Application getApplication(PackageManager pm, PackageInfo packageInfo)
    {
        String appName = packageInfo.applicationInfo.loadLabel(pm).toString();
        String appVersion = packageInfo.versionName;
        int appVerCode = packageInfo.versionCode;

        return new Application(appName + " " + appVersion + "." + appVerCode, packageInfo.packageName);
    }

    public Application getApplication(Context context, int widgetId)
    {
        SharedPreferences preferences = context.getSharedPreferences(Widget.WIDGET_PREFERENCE, Context.MODE_WORLD_READABLE);
        String widgetKey = String.valueOf(widgetId);
        if (preferences.contains(widgetKey))
        {
            String packageName = preferences.getString(widgetKey, null);
            if (packageName != null)
            {
                return new ApplicationUtils().getApplication(context, packageName);
            }
        }
        return null;
    }
}
