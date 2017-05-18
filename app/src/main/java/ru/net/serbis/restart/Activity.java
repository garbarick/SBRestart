package ru.net.serbis.restart;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class Activity extends android.app.Activity
{
    private final String TAG = this.getClass().getName();

    private int widgetId;
    private ListView procList;
    private Button createWidget;
    private Application selected;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setResult(RESULT_CANCELED);

        procList = (ListView) findViewById(R.id.procList);
        createWidget = (Button) findViewById(R.id.createUpdateWidget);
        widgetId = getIntent().getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        initProcList();
        initCreateUpdateWidget();
        initCurrentSate();
    }

    public void initProcList()
    {
        ListAdapter adapter = new ApplicationList(this, android.R.layout.simple_list_item_1, new ApplicationUtils().getProcList(this));
        procList.setAdapter(adapter);
        procList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView parent, View itemClicked, int position, long id)
            {
                itemClicked.setSelected(true);
                selected = (Application) parent.getItemAtPosition(position);
                createWidget.setEnabled(true);
            }
        });
    }

    private void initCreateUpdateWidget()
    {
        createWidget.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                SharedPreferences.Editor preferences = getSharedPreferences(Widget.WIDGET_PREFERENCE, MODE_WORLD_WRITEABLE).edit();
                preferences.putString(String.valueOf(widgetId), selected.packageName);
                preferences.commit();

                new Widget().initWidget(Activity.this, selected, widgetId);

                Intent intent = new Intent();
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
                setResult(RESULT_OK, intent);

                finish();
            }
        });
    }

    private void initCurrentSate()
    {
        if (Widget.WIDGET_EDIT.equals(getIntent().getExtras().get(AppWidgetManager.EXTRA_CUSTOM_INFO)))
        {
            createWidget.setText(R.string.edit_widget);
        }
    }
}
