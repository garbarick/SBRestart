package ru.net.serbis.restart;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * SEBY0408
 */
public class ApplicationList extends ArrayAdapter<Application>
{
    private Activity context;

    public ApplicationList(Activity context, int textViewResourceId, List<Application> items)
    {
        super(context, textViewResourceId, items);
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        if (view == null)
        {
            view = context.getLayoutInflater().inflate(R.layout.application, null, true);
            view.findViewById(R.id.name_and_package).setMinimumWidth(parent.getWidth());
        }

        Application application = getItem(position);

        ((TextView) view.findViewById(R.id.name)).setText(application.name);
        ((TextView) view.findViewById(R.id.package_name)).setText(application.packageName);

        BitmapDrawable icon = application.getIcon(context);
        if (icon != null)
        {
            ((ImageView) view.findViewById(R.id.img)).setImageDrawable(icon);
        }

        return view;
    }
}
