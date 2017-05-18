package ru.net.serbis.restart;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.*;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Arrays;
import java.util.List;

/**
 * SEBY0408
 */
public class Widget extends AppWidgetProvider
{
    private final String TAG = getClass().getName();

    public static final String WIDGET_PREFERENCE = "widget_preference";
    public static final String WIDGET_CLICK = "widget_click";
    public static final String WIDGET_EDIT = "widget_edit";
    private static final List<String> ACTIONS = Arrays.asList(WIDGET_CLICK, WIDGET_EDIT);

    @Override
    public void onUpdate(Context context, AppWidgetManager widgetManager, int[] widgetIds)
    {
        super.onUpdate(context, widgetManager, widgetIds);

        for (int widgetId : widgetIds)
        {
            Application application = new ApplicationUtils().getApplication(context, widgetId);
            if (application != null)
            {
                initWidget(context, application, widgetId);
            }
        }
    }

    public void initWidget(Context context, Application application, int widgetId)
    {
        RemoteViews views = getViews(context);

        Bitmap bitmap = application.getBitmap(context);
        if (bitmap != null)
        {
            views.setImageViewBitmap(R.id.img, bitmap);
        }

        views.setOnClickPendingIntent(R.id.img, getPendingSelfIntent(context, WIDGET_CLICK, widgetId));
        views.setOnClickPendingIntent(R.id.app, getPendingSelfIntent(context, WIDGET_EDIT, widgetId));

        AppWidgetManager.getInstance(context).updateAppWidget(widgetId, views);
    }

    private RemoteViews getViews(Context context)
    {
        return new RemoteViews(context.getPackageName(), R.layout.widget);
    }

    @Override
    public void onDeleted(Context context, int[] widgetIds)
    {
        super.onDeleted(context, widgetIds);

        SharedPreferences.Editor preferences = context.getSharedPreferences(WIDGET_PREFERENCE, Context.MODE_WORLD_WRITEABLE).edit();
        for (int widgetId : widgetIds)
        {
            String widgetKey = String.valueOf(widgetId);
            preferences.remove(widgetKey);
        }
        preferences.commit();
    }

    private PendingIntent getPendingSelfIntent(Context context, String action, int widgetId)
    {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        intent.setData(Uri.fromParts(action, getClass().getName(), String.valueOf(widgetId)));
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);

        String action = intent.getAction();

        if (ACTIONS.contains(action))
        {
            int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            Application application = new ApplicationUtils().getApplication(context, widgetId);
            if (application != null)
            {
                if (WIDGET_CLICK.equals(action))
                {
                    restartApplication(context, application);
                }
                else if (WIDGET_EDIT.equals(action))
                {
                    editWidget(context, widgetId);
                }
            }
        }
    }

    private void restartApplication(Context context, Application application)
    {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        manager.killBackgroundProcesses(application.packageName);
        manager.restartPackage(application.packageName);
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(application.packageName);
        if (launchIntent != null)
        {
            try
            {
                context.startActivity(launchIntent);
            }
            catch (Throwable e)
            {
                Log.e(TAG, "Error on launch " + application.name, e);
            }
        }
    }

    private void editWidget(Context context, int widgetId)
    {
        Intent editIntent = new Intent(context, Activity.class);
        editIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
        editIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        editIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        editIntent.putExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, WIDGET_EDIT);
        context.startActivity(editIntent);
    }
}
