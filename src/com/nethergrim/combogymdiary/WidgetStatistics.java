package com.nethergrim.combogymdiary;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.nethergrim.combogymdiary.activities.StatisticsActivity;

public class WidgetStatistics extends AppWidgetProvider {

    public static void updateWidget(Context context,
                                    AppWidgetManager appWidgetManager,
                                    int widgetID) {
        Log.d("myLogs", "updateWidget id == " + widgetID);
        if (widgetID == -1) {
            return;
        }

        Intent intent = new Intent(context, StatisticsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, 0);
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.widget_stats);
        views.setOnClickPendingIntent(R.id.widget, pendingIntent);
        views.setTextViewText(R.id.tvWidget1, Statistics.getMainExercise(context));
        views.setTextViewText(R.id.tvWidget2, Statistics.getWeightDelta(context));

        appWidgetManager.updateAppWidget(widgetID, views);
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int appWidgetId : appWidgetIds) {
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putInt("widget_id", appWidgetId).apply();
            updateWidget(context, appWidgetManager,
                    appWidgetId);
        }
    }
}
