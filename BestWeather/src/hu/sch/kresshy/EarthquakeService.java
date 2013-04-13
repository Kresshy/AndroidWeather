package hu.sch.kresshy;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;

public class EarthquakeService extends Service {

	static AlarmManager alarmManager;
	static PendingIntent alarmIntent;
	private String cityS;
	private final String DEFAULT_CITY = "Budapest";
	private NodeList current_conditions;
	private NodeList forecast_conditions;
	private NodeList high_day1;
	private NodeList low_day1;
	private String high_day1S;
	private int high_day1f;
	private int high_day1c;
	private String low_day1S;
	private int low_day1f;
	private int low_day1c;
	private NodeList temperature;
	private NodeList condition;
	private String tempS;
	private String descS;
	private final String NA = "N/A";
	private final String DELIMITER = "#";

	SharedPreferences sp;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.i("BestWeather" , "Service Create");
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Log.i("BestWeather" , "Service onStart");

		// GET SHARED PREFERENCES OF THE APPLICATION
		sp = getSharedPreferences(getPackageName(), MODE_PRIVATE);
		cityS = sp.getString("location", DEFAULT_CITY);

		if (chkConnection()) {
			Thread weatherThread = null;
			Runnable runn = new WeatherUpdate();
			weatherThread = new Thread(runn);
			weatherThread.start();
		}

		return Service.START_NOT_STICKY;
	};
	
	public String getCity(String streetAddress) throws IOException {
		Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);

		List<Address> locations = null;

		locations = geocoder.getFromLocationName(streetAddress, 10);

		double lat = locations.get(0).getLatitude();
		double lng = locations.get(0).getLongitude();

		List<Address> myList;

		myList = geocoder.getFromLocation(lat, lng, 1);

		// A VÁROSNÉVVEL VALÓ VISSZATÉRÉS AZ IDÕJÁRÁS ELKÉRÉSÉHEZ

		if (myList.get(0).getLocality() != null)
			return myList.get(0).getLocality();
		if (myList.get(0).getAdminArea() != null)
			return myList.get(0).getAdminArea();
		return myList.get(0).getThoroughfare();
	}

	public void getWeather(String City) throws ParserConfigurationException,
			SAXException, IOException {

		// AZ XML FÁJL LEKÉRÉSE GOOGLE WEATHER APIRÓL A MEGFELELÕ VÁROSSAL
		// URLENCODEOLVA

		String URLencodedCity = URLEncoder.encode(City);

		URL url = new URL("http://www.google.com/ig/api?weather="
				+ URLencodedCity + "&hl=en");

		// AZ XML FÁJL FELDOLGOZÁSA

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new InputSource(url.openStream()));
		doc.getDocumentElement().normalize();

		// A MEGFELELÕ IDÕJÁRÁS ELÕREJELZÉS ELKÉRÉSE

		// JELENLEGI IDÕJÁRÁS

		if (doc.getElementsByTagName("current_conditions") != null)
			current_conditions = doc.getElementsByTagName("current_conditions");
		else
			current_conditions = null;

		// ELÕREJELZÉS

		if (doc.getElementsByTagName("forecast_conditions") != null)
			forecast_conditions = doc
					.getElementsByTagName("forecast_conditions");
		else
			forecast_conditions = null;

		// AZ ELÕREJELZÉS LEKÉRÉSE

		if (forecast_conditions != null) {

			high_day1 = ((Element) forecast_conditions.item(0))
					.getElementsByTagName("high");
			low_day1 = ((Element) forecast_conditions.item(0))
					.getElementsByTagName("low");
		} else {

			high_day1 = null;
			low_day1 = null;
		}

		if (((Element) high_day1.item(0)).getAttribute("data") != null)
			high_day1S = ((Element) high_day1.item(0)).getAttribute("data");
		else
			high_day1S = "0";

		high_day1f = Integer.parseInt(high_day1S);
		high_day1c = (high_day1f - 32) * 5 / 9;

		if (((Element) low_day1.item(0)).getAttribute("data") != null)
			low_day1S = ((Element) low_day1.item(0)).getAttribute("data");
		else
			low_day1S = "0";

		low_day1f = Integer.parseInt(low_day1S);
		low_day1c = (low_day1f - 32) * 5 / 9;

		// A JELENLEGI IDÕJÁRÁS LEKÉRÉSE
		// HA A JELENLEGI IDOJARAS NEM NULL

		if (current_conditions != null) {
			temperature = ((Element) current_conditions.item(0))
					.getElementsByTagName("temp_c");
			condition = ((Element) current_conditions.item(0))
					.getElementsByTagName("condition");

			// A JELENLEGI IDÕJÁRÁS BEÁLLÍTÁSA

			tempS = ((Element) temperature.item(0)).getAttribute("data") + "°C";
			descS = ((Element) condition.item(0)).getAttribute("data");

		} else {

			tempS = NA;
			descS = NA;
		}

	}

	public void setData() {
		
		Editor e = sp.edit();
		e.putString("weather", descS + DELIMITER + tempS + DELIMITER
				+ high_day1c + DELIMITER + low_day1c);
		e.commit();
		
		Log.i("BestWeather", "Committing weather");
		
		// prepare Alarm Service to trigger Widget
		Intent intent = new Intent(MyAppWidgetProvider.MY_WIDGET_UPDATE);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				EarthquakeService.this, 0, intent, 0);
		
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		
		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+1000, pendingIntent);
		
		MyAppWidgetProvider.SaveAlarmManager(alarmManager, pendingIntent);
		
		Log.i("BestWeather", "Alarm Widget to update");

	}
	
	class WeatherUpdate implements Runnable {

		public void run() {
			try {
				getWeather(getCity(cityS));
				setData();
				Thread.currentThread().interrupt();
			} catch (ParserConfigurationException e) {
				Editor ed = sp.edit();
				ed.putString("location", DEFAULT_CITY);
				ed.commit();
				Thread.currentThread().interrupt();
			} catch (SAXException e) {
				Editor ed = sp.edit();
				ed.putString("location", DEFAULT_CITY );
				ed.commit();
				Thread.currentThread().interrupt();
			} catch (IOException e) {
				Editor ed = sp.edit();
				ed.putString("location", DEFAULT_CITY);
				ed.commit();
				Thread.currentThread().interrupt();
			}
		}
	}

	public boolean chkConnection() {

		final ConnectivityManager connMgr = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		final android.net.NetworkInfo wifi = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		final android.net.NetworkInfo mobile = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (wifi.isConnected()) {
			return true;
		} else if (mobile.isConnected()) {
			return true;
		} else
			return false;

	}

	public static void SaveAlarmManager(AlarmManager aManager,
			PendingIntent pintent) {

		alarmManager = aManager;
		alarmIntent = pintent;
	}

}