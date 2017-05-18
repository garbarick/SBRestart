package ru.net.serbis.restart;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;

/**
 * SEBY0408
 */
public class Application implements Comparable<Application>
{
    protected String name;
    protected String packageName;

    public Application(String name, String packageName)
    {
        this.name = name;
        this.packageName = packageName;
    }

    public int compareTo(Application application)
    {
        return name.compareToIgnoreCase(application.name);
    }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof Application)
        {
            Application application = (Application) object;
            return packageName.equals(application.packageName);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return packageName.hashCode();
    }

    public BitmapDrawable getIcon(Context context)
    {
        try
        {
            PackageManager pm = context.getPackageManager();
            return (BitmapDrawable) pm.getApplicationIcon(packageName);
        }
        catch (Throwable e)
        {
            return null;
        }
    }

    public Bitmap getBitmap(Context context)
    {
        BitmapDrawable icon = getIcon(context);
        if (icon != null)
        {
            Bitmap bitmap = icon.getBitmap();
            Bitmap result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            canvas.drawBitmap(bitmap, 0, 0, new Paint());
            return result;
        }
        return null;
    }
}
