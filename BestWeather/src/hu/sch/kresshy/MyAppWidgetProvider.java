package hu.sch.kresshy;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

public class MyAppWidgetProvider extends AppWidgetProvider {

	private final String DELIMITER = "#";
	String curTime;

	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		// Timer timer = new Timer();
		// timer.scheduleAtFixedRate(new MyTime(context, appWidgetManager), 1,
		// 1000);

		final int N = appWidgetIds.length;

		SharedPreferences sp = context
				.getSharedPreferences("hu.sch.kresshy", 0);
		String City = sp.getString("location", "Budapest");

		String weather = sp.getString("weather", "na#na#na#na");
		String[] splitWeather = weather.split(DELIMITER);

		// ComponentName thisWidget = new ComponentName(context,
		// MyAppWidgetProvider.class);

		// int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

		// Build the intent to call the service
		// Intent intent = new Intent(context.getApplicationContext(),
		// MyWidgetService.class);
		// intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
		//
		// // Update the widgets via the service
		// context.startService(intent);

		// Perform this loop procedure for each App Widget that belongs to this
		// provider
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];

			// Create an Intent to launch ExampleActivity
			Intent myintent = new Intent(context, mainActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
					myintent, 0);

			// Get the layout for the App Widget
			RemoteViews views = new RemoteViews(context.getPackageName(),
					R.layout.widget_layout);

			if (splitWeather[0].equals("Fog")) {
				views.setImageViewResource(R.id.wicon, R.drawable.fog);
			}
			if (splitWeather[0].equals("Overcast")) {
				views.setImageViewResource(R.id.wicon, R.drawable.overcast);
			}
			if (splitWeather[0].equals("Partly Sunny")) {
				views.setImageViewResource(R.id.wicon, R.drawable.partlysunny);
			}
			if (splitWeather[0].equals("Freezing Drizzle")) {
				views.setImageViewResource(R.id.wicon,
						R.drawable.freezingdrizzle);
			}
			if (splitWeather[0].equals("Drizzle")) {
				views.setImageViewResource(R.id.wicon,
						R.drawable.freezingdrizzle);
			}
			if (splitWeather[0].equals("Clear")) {
				views.setImageViewResource(R.id.wicon, R.drawable.clear);
			}
			if (splitWeather[0].equals("Cloudy")) {
				views.setImageViewResource(R.id.wicon, R.drawable.cloudy);
			}
			if (splitWeather[0].equals("Haze")) {
				views.setImageViewResource(R.id.wicon, R.drawable.haze);
			}
			if (splitWeather[0].equals("Light rain")) {
				views.setImageViewResource(R.id.wicon, R.drawable.lightrain);
			}
			if (splitWeather[0].equals("Mostly Cloudy")) {
				views.setImageViewResource(R.id.wicon, R.drawable.mostlycloudy);
			}
			if (splitWeather[0].equals("Partly Cloudy")) {
				views.setImageViewResource(R.id.wicon, R.drawable.partlycloudy);
			}
			if (splitWeather[0].equals("Rain")) {
				views.setImageViewResource(R.id.wicon, R.drawable.rain);
			}
			if (splitWeather[0].equals("Rain Showers")) {
				views.setImageViewResource(R.id.wicon, R.drawable.rainshowers);
			}
			if (splitWeather[0].equals("Showers")) {
				views.setImageViewResource(R.id.wicon, R.drawable.rainshowers);
			}
			if (splitWeather[0].equals("Thunderstorm")) {
				views.setImageViewResource(R.id.wicon, R.drawable.storm);
			}
			if (splitWeather[0].equals("Chance of Rain")) {
				views.setImageViewResource(R.id.wicon, R.drawable.chanceofrain);
			}
			if (splitWeather[0].equals("Chance of Showers")) {
				views.setImageViewResource(R.id.wicon, R.drawable.chanceofrain);
			}
			if (splitWeather[0].equals("Chance of Snow")) {
				views.setImageViewResource(R.id.wicon, R.drawable.chanceofsnow);
			}
			if (splitWeather[0].equals("Chance of Storm")) {
				views.setImageViewResource(R.id.wicon, R.drawable.chanceofstorm);
			}
			if (splitWeather[0].equals("Mostly Sunny")) {
				views.setImageViewResource(R.id.wicon, R.drawable.mostlysunny);
			}
			if (splitWeather[0].equals("Scattered Showers")) {
				views.setImageViewResource(R.id.wicon,
						R.drawable.scatteredshowers);
			}
			if (splitWeather[0].equals("Sunny")) {
				views.setImageViewResource(R.id.wicon, R.drawable.sunny);
			}
			if (splitWeather[0].equals("Snow")) {
				views.setImageViewResource(R.id.wicon, R.drawable.snow);
			}
			if (splitWeather[0].equals("Light snow")) {
				views.setImageViewResource(R.id.wicon, R.drawable.snow);
			}
			if (splitWeather[0].equals("Snow showers")) {
				views.setImageViewResource(R.id.wicon, R.drawable.snowshowers);
			}
			if (splitWeather[0].equals("Smoke")) {
				views.setImageViewResource(R.id.wicon, R.drawable.smoke);
			}
			if (splitWeather[0].equals("Rain and Snow")) {
				views.setImageViewResource(R.id.wicon, R.drawable.rainandsnow);
			}
			if (splitWeather[0].equals("Scattered Clouds")) {
				views.setImageViewResource(R.id.wicon, R.drawable.cloudy);
			}

			// views.setImageViewResource(R.id.wicon, R.drawable.na);

			views.setTextViewText(R.id.wtemp, splitWeather[1]);
			views.setTextViewText(R.id.wdesc, splitWeather[0]);

			// views.setTextViewText(R.id.wtime, curTime);

			Calendar calendar = Calendar.getInstance();
			int daynum = calendar.get(Calendar.DAY_OF_WEEK);

			switch (daynum) {
			case 1:
				views.setTextViewText(R.id.wday, "Sun");
				break;
			case 2:
				views.setTextViewText(R.id.wday, "Mon");
				break;
			case 3:
				views.setTextViewText(R.id.wday, "Tue");
				break;
			case 4:
				views.setTextViewText(R.id.wday, "Wed");
				break;
			case 5:
				views.setTextViewText(R.id.wday, "Thu");
				break;
			case 6:
				views.setTextViewText(R.id.wday, "Fri");
				break;
			case 7:
				views.setTextViewText(R.id.wday, "Sat");
				break;
			default:
				views.setTextViewText(R.id.wday, "Dunno");
				break;
			}

			String currentDateTimeString = DateFormat.getDateInstance().format(
					new Date());
			views.setTextViewText(R.id.wdate, currentDateTimeString);

			views.setOnClickPendingIntent(R.id.pos, pendingIntent);
			views.setTextViewText(R.id.pos, City);
			views.setTextViewText(R.id.whi, splitWeather[2]);
			views.setTextViewText(R.id.wlo, splitWeather[3]);
			views.setOnClickPendingIntent(R.id.wicon, pendingIntent);

			Log.i("BestWeather", "Widget Update");
			// Tell the AppWidgetManager to perform an update on the current app
			// widget
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}

	public static String MY_WIDGET_UPDATE = "MY_OWN_WIDGET_UPDATE";

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);

		if (MY_WIDGET_UPDATE.equals(intent.getAction())) {

			Bundle extras = intent.getExtras();
			if (extras != null) {
				AppWidgetManager appWidgetManager = AppWidgetManager
						.getInstance(context);
				ComponentName thisAppWidget = new ComponentName(
						context.getPackageName(),
						MyAppWidgetProvider.class.getName());
				int[] appWidgetIds = appWidgetManager
						.getAppWidgetIds(thisAppWidget);

				onUpdate(context, appWidgetManager, appWidgetIds);

			}
		}
	}

	static AlarmManager myAlarmManager;
	static PendingIntent myPendingIntent;

	public static void SaveAlarmManager(AlarmManager alarmManager,
			PendingIntent pendingIntent) {

		myAlarmManager = alarmManager;
		myPendingIntent = pendingIntent;

	}

	@Override
	public void onDisabled(Context context) {
		myAlarmManager.cancel(myPendingIntent);

		super.onDisabled(context);
	}

	// private class MyTime extends TimerTask {
	//
	// RemoteViews remoteViews;
	// AppWidgetManager appWidgetManager;
	// ComponentName thisWidget;
	//
	// public MyTime(Context context, AppWidgetManager appWidgetManager) {
	//
	// this.appWidgetManager = appWidgetManager;
	// remoteViews = new RemoteViews(context.getPackageName(),
	// R.layout.widget_layout);
	// thisWidget = new ComponentName(context, MyAppWidgetProvider.class);
	//
	// }
	//
	// @Override
	// public void run() {
	//
	// Date dt = new Date();
	// int hours = dt.getHours();
	// int minutes = dt.getMinutes();
	//
	// String curTime = hours + "     " + minutes;
	//
	// if (minutes < 10) curTime = hours + "     0" + minutes;
	// if (hours < 10) curTime = "0" + hours + "     " + minutes;
	// if (hours < 10 && minutes < 10) curTime = "0" + hours + "     0" +
	// minutes;
	//
	// remoteViews.setTextViewText(R.id.wtime, curTime);
	// appWidgetManager.updateAppWidget(thisWidget, remoteViews);
	// }
	// }
}
