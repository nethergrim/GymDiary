package com.nethergrim.combogymdiary;

import com.nethergrim.combogymdiary.activities.StatisticsActivity;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class WidgetStatistics extends AppWidgetProvider {

	private Statistics statistics;

	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		statistics = new Statistics(context);
		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			Intent intent = new Intent(context, StatisticsActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
					intent, 0);
			RemoteViews views = new RemoteViews(context.getPackageName(),
					R.layout.widget_stats);
			views.setOnClickPendingIntent(R.id.tvWidget1, pendingIntent);
			views.setTextViewText(R.id.tvWidget1, statistics.getMainExercise());
			

			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}

}
