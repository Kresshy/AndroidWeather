package hu.sch.kresshy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class mainActivity extends Activity implements LocationListener {

	TextView city;
	TextView temp;
	TextView desc;
	TextView wind;
	TextView humi;
	TextView date;
	TextView day;
	TextView time;
	TextView day1name;
	TextView day2name;
	TextView day3name;
	TextView day4name;
	TextView day1high;
	TextView day2high;
	TextView day3high;
	TextView day4high;
	TextView day1low;
	TextView day2low;
	TextView day3low;
	TextView day4low;
	TextView day1cond;
	TextView day2cond;
	TextView day3cond;
	TextView day4cond;

	ImageView icontoday;
	ImageView day1;
	ImageView day2;
	ImageView day3;
	ImageView day4;

	LinearLayout mainlayout;

	String streetAddress;
	String cityS;

	SharedPreferences sp;

	private LocationManager locmanager;

	private final String NA = "N/A";
	private final int REQUEST_GETADDRESS = 1;
	private final String DELIMITER = "#";

	NodeList current_conditions;
	NodeList forecast_conditions;

	NodeList condition_day1;
	NodeList condition_day2;
	NodeList condition_day3;
	NodeList condition_day4;

	NodeList name_day1;
	NodeList name_day2;
	NodeList name_day3;
	NodeList name_day4;

	NodeList high_day1;
	NodeList high_day2;
	NodeList high_day3;
	NodeList high_day4;

	NodeList low_day1;
	NodeList low_day2;
	NodeList low_day3;
	NodeList low_day4;

	NodeList temperature;
	NodeList condition;
	NodeList wind_con;
	NodeList humidity;

	String condition_day1S;
	String condition_day2S;
	String condition_day3S;
	String condition_day4S;

	String name_day1N;
	String name_day2N;
	String name_day3N;
	String name_day4N;

	String high_day1S;
	String high_day2S;
	String high_day3S;
	String high_day4S;

	// int high_day1f;
	// int high_day1c;
	// int high_day2f;
	// int high_day2c;
	// int high_day3f;
	// int high_day3c;
	// int high_day4f;
	// int high_day4c;

	String low_day1S;
	String low_day2S;
	String low_day3S;
	String low_day4S;

	// int low_day1f;
	// int low_day1c;
	// int low_day2f;
	// int low_day2c;
	// int low_day3f;
	// int low_day3c;
	// int low_day4f;
	// int low_day4c;

	String tempS;
	String descS;
	String windS;
	String humiS;

	Thread weatherThread;
	Thread getLocByIPthread;
	ProgressDialog dialog, dialog2;

	private String DEFAULT_CITY = "";

	public static synchronized void logToLogCat(String TAG, String message) {
		Log.i(TAG, message);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		logToLogCat("mainActivity", "onCreate");

		// startService(new Intent(getApplicationContext(),
		// MyWidgetService.class));
		// startService(new Intent(getApplicationContext(),
		// EarthquakeService.class));

		mainlayout = (LinearLayout) findViewById(R.id.background);

		city = (TextView) findViewById(R.id.City);

		date = (TextView) findViewById(R.id.date);
		day = (TextView) findViewById(R.id.day);
		time = (TextView) findViewById(R.id.time);

		temp = (TextView) findViewById(R.id.degree);
		desc = (TextView) findViewById(R.id.description);
		wind = (TextView) findViewById(R.id.wind);
		humi = (TextView) findViewById(R.id.feellike);
		icontoday = (ImageView) findViewById(R.id.icontoday);

		day1 = (ImageView) findViewById(R.id.day1);
		day2 = (ImageView) findViewById(R.id.day2);
		day3 = (ImageView) findViewById(R.id.day3);
		day4 = (ImageView) findViewById(R.id.day4);

		day1name = (TextView) findViewById(R.id.day1name);
		day2name = (TextView) findViewById(R.id.day2name);
		day3name = (TextView) findViewById(R.id.day3name);
		day4name = (TextView) findViewById(R.id.day4name);

		day1high = (TextView) findViewById(R.id.day1hi);
		day2high = (TextView) findViewById(R.id.day2hi);
		day3high = (TextView) findViewById(R.id.day3hi);
		day4high = (TextView) findViewById(R.id.day4hi);

		day1low = (TextView) findViewById(R.id.day1lo);
		day2low = (TextView) findViewById(R.id.day2lo);
		day3low = (TextView) findViewById(R.id.day3lo);
		day4low = (TextView) findViewById(R.id.day4lo);

		day1cond = (TextView) findViewById(R.id.day1wind);
		day2cond = (TextView) findViewById(R.id.day2wind);
		day3cond = (TextView) findViewById(R.id.day3wind);
		day4cond = (TextView) findViewById(R.id.day4wind);

		if (chkStatusNoNotify()) {
			dialog2 = ProgressDialog.show(this, "",
					"Updating location info...", true, true);
			Runnable runnn = new IPLocUpdater();
			getLocByIPthread = new Thread(runnn);
			getLocByIPthread.start();

			try {
				getLocByIPthread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		sp = getSharedPreferences(getPackageName(), MODE_PRIVATE);
		String loadedcity = sp.getString("location", DEFAULT_CITY);
		streetAddress = loadedcity;

		if (chkStatusNoNotify()) {
			dialog = ProgressDialog.show(this, "", "Updating weather...", true,
					true);
			Runnable runn = new WeatherUpdater();
			weatherThread = new Thread(runn);
			weatherThread.start();
		}

		Thread myThread = null;
		Runnable runnable = new CountDownRunner();
		myThread = new Thread(runnable);
		myThread.start();

		Calendar calendar = Calendar.getInstance();
		int daynum = calendar.get(Calendar.DAY_OF_WEEK);

		switch (daynum) {
		case 1:
			day.setText("Sunday");
			break;
		case 2:
			day.setText("Monday");
			break;
		case 3:
			day.setText("Tuesday");
			break;
		case 4:
			day.setText("Wednesday");
			break;
		case 5:
			day.setText("Thursday");
			break;
		case 6:
			day.setText("Friday");
			break;
		case 7:
			day.setText("Saturday");
			break;
		default:
			day.setText("Dunno");
			break;
		}

		String currentDateTimeString = DateFormat.getDateInstance().format(
				new Date());
		date.setText(currentDateTimeString);

		logToLogCat("mainActivity", "onCreate AlarmManager EarthquakeService");
		// prepare Alarm Service to trigger Service
//		Intent intent = new Intent(getApplicationContext(),
//				EarthquakeService.class);
//		PendingIntent pendingIntent = PendingIntent.getService(
//				getApplicationContext(), 0, intent, 0);
//		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//
//		Calendar mycalendar = Calendar.getInstance();
//		mycalendar.setTimeInMillis(System.currentTimeMillis());
//		mycalendar.add(Calendar.SECOND, 10);
//		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
//				mycalendar.getTimeInMillis(), 20 * 60 * 1000, pendingIntent);

		// alarmManager.set(AlarmManager.RTC_WAKEUP,
		// System.currentTimeMillis() + 1000, pendingIntent);

//		EarthquakeService.SaveAlarmManager(alarmManager, pendingIntent);

	}

	public boolean chkStatus() {
		logToLogCat("mainActivity", "chkStatus");
		final ConnectivityManager connMgr = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		final android.net.NetworkInfo netWorkInfo = connMgr
				.getActiveNetworkInfo();

		// final android.net.NetworkInfo mobile = connMgr
		// .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (netWorkInfo.isConnected()) {
			Toast.makeText(this, "Connected via Wifi", Toast.LENGTH_LONG)
					.show();
			return true;
			// } else if (mobile.isConnected()) {
			// Toast.makeText(this, "Connected via Mobile 3G ",
			// Toast.LENGTH_LONG)
			// .show();
			// return true;
		} else {

			AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
			alt_bld.setMessage("Choose to switch on!")
					.setCancelable(true)
					.setPositiveButton("Enable Wifi",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									startActivityForResult(
											new Intent(
													android.provider.Settings.ACTION_WIFI_SETTINGS),
											0);
								}
							})
					.setNegativeButton("Enable 3G",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									Intent intent = new Intent(
											Settings.ACTION_DATA_ROAMING_SETTINGS);
									ComponentName cName = new ComponentName(
											"com.android.phone",
											"com.android.phone.Settings");
									intent.setComponent(cName);
									startActivityForResult(intent, 0);

								}
							});
			AlertDialog alert = alt_bld.create();
			// Title for AlertDialog
			alert.setTitle("No Network");
			// Icon for AlertDialog
			alert.setIcon(R.drawable.ic_launcher);
			alert.show();
			Toast.makeText(this, "No Network Access", Toast.LENGTH_LONG).show();

			// TO START OPTION IF NO PROVIDER.......
			//
			// startActivityForResult(new
			// Intent(android.provider.Settings.ACTION_SOUND_SETTINGS), 0);
			// Intent intent=new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
			// ComponentName cName = new
			// ComponentName("com.android.phone","com.android.phone.Settings");
			// intent.setComponent(cName);
			// startActivityForResult(intent, 0);

			return false;
		}
	}

	public boolean chkStatusNoNotify() {
		logToLogCat("mainActivity", "chkStatusNoNotify");
		final ConnectivityManager connMgr = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		final android.net.NetworkInfo netWorkInfo = connMgr
				.getActiveNetworkInfo();

		// final android.net.NetworkInfo mobile = connMgr
		// .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (netWorkInfo != null) {
			if (netWorkInfo.isConnected()) {
				return true;
			} else {
				AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
				alt_bld.setMessage("Choose to switch on!")
						.setCancelable(true)
						.setPositiveButton("Enable Wifi",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										startActivityForResult(
												new Intent(
														android.provider.Settings.ACTION_WIFI_SETTINGS),
												0);
									}
								})
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
//										Intent intent = new Intent(
//												Settings.ACTION_DATA_ROAMING_SETTINGS);
//										ComponentName cName = new ComponentName(
//												"com.android.phone",
//												"com.android.phone.Settings");
//										intent.setComponent(cName);
//										startActivityForResult(intent, 0);

									}
								});
				AlertDialog alert = alt_bld.create();
				// Title for AlertDialog
				alert.setTitle("No Network");
				// Icon for AlertDialog
				alert.setIcon(R.drawable.ic_launcher);
				alert.show();
				Toast.makeText(this, "No Network Access", Toast.LENGTH_LONG)
						.show();
				return false;
			}
		} else {
			AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
			alt_bld.setMessage("Choose to switch on!")
					.setCancelable(true)
					.setPositiveButton("Enable Wifi",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									startActivityForResult(
											new Intent(
													android.provider.Settings.ACTION_WIFI_SETTINGS),
											0);
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
//									Intent intent = new Intent(
//											Settings.ACTION_DATA_ROAMING_SETTINGS);
//									ComponentName cName = new ComponentName(
//											"com.android.phone",
//											"com.android.phone.Settings");
//									intent.setComponent(cName);
//									startActivityForResult(intent, 0);

								}
							});
			AlertDialog alert = alt_bld.create();
			// Title for AlertDialog
			alert.setTitle("No Network");
			// Icon for AlertDialog
			alert.setIcon(R.drawable.ic_launcher);
			alert.show();
			Toast.makeText(this, "No Network Access", Toast.LENGTH_LONG).show();

			// TO START OPTION IF NO PROVIDER.......
			//
			// startActivityForResult(new
			// Intent(android.provider.Settings.ACTION_SOUND_SETTINGS), 0);
			// Intent intent=new
			// Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
			// ComponentName cName = new
			// ComponentName("com.android.phone","com.android.phone.Settings");
			// intent.setComponent(cName);
			// startActivityForResult(intent, 0);

			return false;
		}

	}

	// A MENU LETREHOZASA
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.clear();
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	// ACTIVITY RESULTOK

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		logToLogCat("mainActivity", "onActivityResult");
		if (resultCode == RESULT_CANCELED)
			return;

		switch (requestCode) {
		case REQUEST_GETADDRESS:
			logToLogCat("mainActivity", "onActivityResult REQUEST_GETADDRESS");

			String toget = data.getStringExtra(getPackageName());
			streetAddress = toget;

			Editor e = sp.edit();

			if (streetAddress != null) {

				e.putString("location", streetAddress);
				e.commit();

				if (chkStatusNoNotify()) {
					dialog = ProgressDialog.show(this, "",
							"Updating weather...", true, true);
					Runnable runn = new WeatherUpdater();
					weatherThread = new Thread(runn);
					weatherThread.start();
				}

			} else {

				e.putString("location", DEFAULT_CITY);
				e.commit();
				if (chkStatusNoNotify()) {
					dialog = ProgressDialog.show(this, "",
							"Updating weather...", true, true);
					Runnable runn = new WeatherUpdater();
					weatherThread = new Thread(runn);
					weatherThread.start();
				}
			}

			break;

		default:

			break;
		}
	}

	// OPTIONS ITEM SELECT

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		logToLogCat("mainActivity", "onOptionsItemSelected");
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.refresh:
			logToLogCat("mainActivity", "onOptionsItemSelected refresh");
			if (chkStatusNoNotify()) {
				dialog = ProgressDialog.show(this, "", "Updating weather...",
						true, true);
				Runnable runn = new WeatherUpdater();
				weatherThread = new Thread(runn);
				weatherThread.start();
			}
			return true;

		case R.id.location:
			logToLogCat("mainActivity", "onOptionsItemSelected location");
			Intent myIntent = new Intent(getApplicationContext(),
					myLocation.class);
			startActivityForResult(myIntent, REQUEST_GETADDRESS);

			return true;

		case R.id.Networklocation:
			logToLogCat("mainActivity", "onOptionsItemSelected Networklocation");

			if (chkStatusNoNotify()) {
				dialog2 = ProgressDialog.show(this, "",
						"Updating location info...", true, true);
				Runnable runn = new LocationByNetUpdater();
				getLocByIPthread = new Thread(runn);
				getLocByIPthread.start();
			}

			try {
				getLocByIPthread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (chkStatusNoNotify()) {
				dialog = ProgressDialog.show(this, "", "Updating weather...",
						true, true);
				Runnable runnn = new WeatherUpdater();
				weatherThread = new Thread(runnn);
				weatherThread.start();
			}

			return true;

			// case R.id.GPSlocation:
			// logToLogCat("mainActivity", "onOptionsItemSelected GPSlocation");
			// locmanager = (LocationManager) this
			// .getSystemService(Context.LOCATION_SERVICE);
			// if (locmanager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			//
			// locmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
			// 0, 0, mainActivity.this);
			//
			// } else {
			// Toast.makeText(this, "Enable GPS!", Toast.LENGTH_LONG).show();
			// startActivityForResult(
			// new Intent(
			// android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),
			// 0);
			// }
			// return true;
			//
			//
			//
			// case R.id.help:
			//
			// Intent myIntent2 = new Intent(getApplicationContext(),
			// helpActivity.class);
			// startActivity(myIntent2);
			//
			// return true;

		default:

			return super.onOptionsItemSelected(item);
		}
	}

	// A CIM ALAPJAN A VAROS VISSZAFEJTESE, cim ---> geocoordinatak ---> reverse
	// geocode ---> Varos, Orszag

	public LocInfo getCity(String streetAddress) throws IOException {
		logToLogCat("mainActivity", "getCity");
		Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);

		List<Address> locations = null;

		locations = geocoder.getFromLocationName(streetAddress, 10);

		double lat = locations.get(0).getLatitude();
		double lng = locations.get(0).getLongitude();

		List<Address> myList;

		myList = geocoder.getFromLocation(lat, lng, 1);

		// A MEGFELELÕ VÁROS ÉS ORSZÁGKÓD MENTÉSE

		if (myList.get(0).getAdminArea() != null)
			cityS = myList.get(0).getAdminArea().toString();
		if (myList.get(0).getLocality() != null)
			cityS = myList.get(0).getLocality().toString();
		cityS = cityS + ", " + myList.get(0).getCountryCode().toString();

		// A VÁROSNÉVVEL VALÓ VISSZATÉRÉS AZ IDÕJÁRÁS ELKÉRÉSÉHEZ

		LocInfo loc = new LocInfo();

		if (myList.get(0).getLocality() != null) {
			logToLogCat("mainActivity", myList.get(0).getLocality());
			loc.city = myList.get(0).getLocality();
		}

		if (myList.get(0).getAdminArea() != null
				|| myList.get(0).getCountryName() != null) {
			logToLogCat("mainActivity", myList.get(0).getCountryName() + " "
					+ myList.get(0).getSubAdminArea() + " "
					+ myList.get(0).getAdminArea());
			// return myList.get(0).getAdminArea();
			if (myList.get(0).getCountryName().equals("United States")
					|| myList.get(0).getCountryName() == null)
				loc.AdminArea = myList.get(0).getAdminArea();
			else
				loc.AdminArea = myList.get(0).getCountryName();
		}

		// logToLogCat("mainActivity", myList.get(0).getThoroughfare());
		// loc.AdminArea = myList.get(0).getThoroughfare();
		return loc;
	}

	// public void getWeather(String City) throws ParserConfigurationException,
	// SAXException, IOException {
	//
	// logToLogCat("mainActivity", "getWeather");
	//
	// // AZ XML FÁJL LEKÉRÉSE GOOGLE WEATHER APIRÓL A MEGFELELÕ VÁROSSAL
	// // URLENCODEOLVA
	//
	// String URLencodedCity = URLEncoder.encode(City);
	//
	// URL url = new URL("http://www.google.com/ig/api?weather="
	// + URLencodedCity + "&hl=en");
	//
	// // AZ XML FÁJL FELDOLGOZÁSA
	//
	// DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	// DocumentBuilder db = dbf.newDocumentBuilder();
	// Document doc = db.parse(new InputSource(url.openStream()));
	// doc.getDocumentElement().normalize();
	//
	// // A MEGFELELÕ IDÕJÁRÁS ELÕREJELZÉS ELKÉRÉSE
	//
	// // JELENLEGI IDÕJÁRÁS
	//
	// if (doc.getElementsByTagName("current_conditions") != null)
	// current_conditions = doc.getElementsByTagName("current_conditions");
	// else
	// current_conditions = null;
	//
	// // ELÕREJELZÉS
	//
	// if (doc.getElementsByTagName("forecast_conditions") != null)
	// forecast_conditions = doc
	// .getElementsByTagName("forecast_conditions");
	// else
	// forecast_conditions = null;
	//
	// // AZ ELÕREJELZÉS LEKÉRÉSE
	//
	// if (forecast_conditions != null) {
	// condition_day1 = ((Element) forecast_conditions.item(0))
	// .getElementsByTagName("condition");
	// condition_day2 = ((Element) forecast_conditions.item(1))
	// .getElementsByTagName("condition");
	// condition_day3 = ((Element) forecast_conditions.item(2))
	// .getElementsByTagName("condition");
	// condition_day4 = ((Element) forecast_conditions.item(3))
	// .getElementsByTagName("condition");
	//
	// name_day1 = ((Element) forecast_conditions.item(0))
	// .getElementsByTagName("day_of_week");
	// name_day2 = ((Element) forecast_conditions.item(1))
	// .getElementsByTagName("day_of_week");
	// name_day3 = ((Element) forecast_conditions.item(2))
	// .getElementsByTagName("day_of_week");
	// name_day4 = ((Element) forecast_conditions.item(3))
	// .getElementsByTagName("day_of_week");
	//
	// high_day1 = ((Element) forecast_conditions.item(0))
	// .getElementsByTagName("high");
	// high_day2 = ((Element) forecast_conditions.item(1))
	// .getElementsByTagName("high");
	// high_day3 = ((Element) forecast_conditions.item(2))
	// .getElementsByTagName("high");
	// high_day4 = ((Element) forecast_conditions.item(3))
	// .getElementsByTagName("high");
	//
	// low_day1 = ((Element) forecast_conditions.item(0))
	// .getElementsByTagName("low");
	// low_day2 = ((Element) forecast_conditions.item(1))
	// .getElementsByTagName("low");
	// low_day3 = ((Element) forecast_conditions.item(2))
	// .getElementsByTagName("low");
	// low_day4 = ((Element) forecast_conditions.item(3))
	// .getElementsByTagName("low");
	// } else {
	// condition_day1 = null;
	// condition_day2 = null;
	// condition_day3 = null;
	// condition_day4 = null;
	//
	// name_day1 = null;
	// name_day2 = null;
	// name_day3 = null;
	// name_day4 = null;
	//
	// high_day1 = null;
	// high_day2 = null;
	// high_day3 = null;
	// high_day4 = null;
	//
	// low_day1 = null;
	// low_day2 = null;
	// low_day3 = null;
	// low_day4 = null;
	// }
	//
	// if (((Element) condition_day1.item(0)).getAttribute("data") != null)
	// condition_day1S = ((Element) condition_day1.item(0))
	// .getAttribute("data");
	// else
	// condition_day1S = NA;
	// if (((Element) condition_day2.item(0)).getAttribute("data") != null)
	// condition_day2S = ((Element) condition_day2.item(0))
	// .getAttribute("data");
	// else
	// condition_day2S = NA;
	// if (((Element) condition_day3.item(0)).getAttribute("data") != null)
	// condition_day3S = ((Element) condition_day3.item(0))
	// .getAttribute("data");
	// else
	// condition_day3S = NA;
	// if (((Element) condition_day4.item(0)).getAttribute("data") != null)
	// condition_day4S = ((Element) condition_day4.item(0))
	// .getAttribute("data");
	// else
	// condition_day4S = NA;
	//
	// // String condition_day1S = ((Element)
	// // condition_day1.item(0)).getAttribute("data");
	// // String condition_day2S = ((Element)
	// // condition_day2.item(0)).getAttribute("data");
	// // String condition_day3S = ((Element)
	// // condition_day3.item(0)).getAttribute("data");
	// // String condition_day4S = ((Element)
	// // condition_day4.item(0)).getAttribute("data");
	//
	// if (((Element) name_day1.item(0)).getAttribute("data") != null)
	// name_day1N = ((Element) name_day1.item(0)).getAttribute("data");
	// else
	// name_day1N = NA;
	// if (((Element) name_day2.item(0)).getAttribute("data") != null)
	// name_day2N = ((Element) name_day2.item(0)).getAttribute("data");
	// else
	// name_day2N = NA;
	// if (((Element) name_day3.item(0)).getAttribute("data") != null)
	// name_day3N = ((Element) name_day3.item(0)).getAttribute("data");
	// else
	// name_day3N = NA;
	// if (((Element) name_day4.item(0)).getAttribute("data") != null)
	// name_day4N = ((Element) name_day4.item(0)).getAttribute("data");
	// else
	// name_day4N = NA;
	//
	// // String name_day1N = ((Element)
	// // name_day1.item(0)).getAttribute("data");
	// // String name_day2N = ((Element)
	// // name_day2.item(0)).getAttribute("data");
	// // String name_day3N = ((Element)
	// // name_day3.item(0)).getAttribute("data");
	// // String name_day4N = ((Element)
	// // name_day4.item(0)).getAttribute("data");
	//
	// if (((Element) high_day1.item(0)).getAttribute("data") != null)
	// high_day1S = ((Element) high_day1.item(0)).getAttribute("data");
	// else
	// high_day1S = "0";
	// if (((Element) high_day2.item(0)).getAttribute("data") != null)
	// high_day2S = ((Element) high_day2.item(0)).getAttribute("data");
	// else
	// high_day2S = "0";
	// if (((Element) high_day3.item(0)).getAttribute("data") != null)
	// high_day3S = ((Element) high_day3.item(0)).getAttribute("data");
	// else
	// high_day3S = "0";
	// if (((Element) high_day4.item(0)).getAttribute("data") != null)
	// high_day4S = ((Element) high_day4.item(0)).getAttribute("data");
	// else
	// high_day4S = "0";
	//
	// // String high_day1S = ((Element)
	// // high_day1.item(0)).getAttribute("data");
	// // String high_day2S = ((Element)
	// // high_day2.item(0)).getAttribute("data");
	// // String high_day3S = ((Element)
	// // high_day3.item(0)).getAttribute("data");
	// // String high_day4S = ((Element)
	// // high_day4.item(0)).getAttribute("data");
	//
	// high_day1f = Integer.parseInt(high_day1S);
	// high_day1c = (high_day1f - 32) * 5 / 9;
	// high_day2f = Integer.parseInt(high_day2S);
	// high_day2c = (high_day2f - 32) * 5 / 9;
	// high_day3f = Integer.parseInt(high_day3S);
	// high_day3c = (high_day3f - 32) * 5 / 9;
	// high_day4f = Integer.parseInt(high_day4S);
	// high_day4c = (high_day4f - 32) * 5 / 9;
	//
	// if (((Element) low_day1.item(0)).getAttribute("data") != null)
	// low_day1S = ((Element) low_day1.item(0)).getAttribute("data");
	// else
	// low_day1S = "0";
	// if (((Element) low_day2.item(0)).getAttribute("data") != null)
	// low_day2S = ((Element) low_day2.item(0)).getAttribute("data");
	// else
	// low_day2S = "0";
	// if (((Element) low_day3.item(0)).getAttribute("data") != null)
	// low_day3S = ((Element) low_day3.item(0)).getAttribute("data");
	// else
	// low_day3S = "0";
	// if (((Element) low_day4.item(0)).getAttribute("data") != null)
	// low_day4S = ((Element) low_day4.item(0)).getAttribute("data");
	// else
	// low_day4S = "0";
	//
	// // String low_day1S = ((Element) low_day1.item(0)).getAttribute("data");
	// // String low_day2S = ((Element) low_day2.item(0)).getAttribute("data");
	// // String low_day3S = ((Element) low_day3.item(0)).getAttribute("data");
	// // String low_day4S = ((Element) low_day4.item(0)).getAttribute("data");
	//
	// low_day1f = Integer.parseInt(low_day1S);
	// low_day1c = (low_day1f - 32) * 5 / 9;
	// low_day2f = Integer.parseInt(low_day2S);
	// low_day2c = (low_day2f - 32) * 5 / 9;
	// low_day3f = Integer.parseInt(low_day3S);
	// low_day3c = (low_day3f - 32) * 5 / 9;
	// low_day4f = Integer.parseInt(low_day4S);
	// low_day4c = (low_day4f - 32) * 5 / 9;
	//
	// // A JELENLEGI IDÕJÁRÁS LEKÉRÉSE
	// // HA A JELENLEGI IDOJARAS NEM NULL
	//
	// if (current_conditions != null) {
	// temperature = ((Element) current_conditions.item(0))
	// .getElementsByTagName("temp_c");
	// condition = ((Element) current_conditions.item(0))
	// .getElementsByTagName("condition");
	// wind_con = ((Element) current_conditions.item(0))
	// .getElementsByTagName("wind_condition");
	// humidity = ((Element) current_conditions.item(0))
	// .getElementsByTagName("humidity");
	//
	// // A JELENLEGI IDÕJÁRÁS BEÁLLÍTÁSA
	//
	// tempS = ((Element) temperature.item(0)).getAttribute("data") + "°C";
	// descS = ((Element) condition.item(0)).getAttribute("data");
	// windS = ((Element) wind_con.item(0)).getAttribute("data");
	// humiS = ((Element) humidity.item(0)).getAttribute("data");
	//
	// } else {
	//
	// tempS = NA;
	// descS = NA;
	// windS = NA;
	// humiS = NA;
	// }
	//
	// }

	public void getWeather(String City, String Country)
			throws ParserConfigurationException, SAXException, IOException,
			URISyntaxException {

		streetAddress = City;

		City = City.replaceAll("á", "a");
		City = City.replaceAll("é", "e");
		City = City.replaceAll("ó", "o");
		City = City.replaceAll("õ", "o");
		City = City.replaceAll("ö", "o");
		City = City.replaceAll("ú", "U");
		City = City.replaceAll("û", "U");
		City = City.replaceAll("ü", "U");
		City = City.replaceAll("í", "i");
		City = City.replaceAll("Á", "A");
		City = City.replaceAll("É", "E");
		City = City.replaceAll("Ó", "O");
		City = City.replaceAll("Õ", "O");
		City = City.replaceAll("Ö", "O");
		City = City.replaceAll("Ú", "U");
		City = City.replaceAll("Û", "U");
		City = City.replaceAll("Ü", "U");
		City = City.replaceAll("Í", "I");

		URI uri = new URI("http",
				"//api.wunderground.com/api/1581002a1df007d6/conditions/forecast/astronomy/q/"
						+ Country + "/" + City + ".xml", null);

		String request = uri.toASCIIString();

		logToLogCat("mainActivity", "request: " + request);

		URL url = new URL(request);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new InputSource(url.openStream()));
		doc.getDocumentElement().normalize();

		NodeList displayLocation = doc.getElementsByTagName("display_location");
		Element displayLocationElement = (Element) displayLocation.item(0);
		NodeList cityLocation = displayLocationElement
				.getElementsByTagName("city");
		NodeList countryLocation = displayLocationElement
				.getElementsByTagName("country");

		streetAddress += ", "
				+ ((Element) countryLocation.item(0)).getFirstChild()
						.getNodeValue();

		NodeList currentObservation = doc
				.getElementsByTagName("current_observation");
		Element currentObservationElement = (Element) currentObservation
				.item(0);
		NodeList weather = currentObservationElement
				.getElementsByTagName("weather");
		NodeList tempC = currentObservationElement
				.getElementsByTagName("temp_c");
		NodeList relativeHumidity = currentObservationElement
				.getElementsByTagName("relative_humidity");
		NodeList windKph = currentObservationElement
				.getElementsByTagName("wind_kph");

		tempS = "Temp: "
				+ ((Element) tempC.item(0)).getFirstChild().getNodeValue()
				+ " °C";
		descS = ((Element) weather.item(0)).getFirstChild().getNodeValue();
		windS = "Wind: "
				+ ((Element) windKph.item(0)).getFirstChild().getNodeValue()
				+ " km/h";
		humiS = "Humidity: "
				+ ((Element) relativeHumidity.item(0)).getFirstChild()
						.getNodeValue();

		NodeList simpleForecast = doc.getElementsByTagName("simpleforecast");
		Element simpleForecastElement = (Element) simpleForecast.item(0);
		NodeList forecastDays = simpleForecastElement
				.getElementsByTagName("forecastdays");
		Element forecastDaysElement = (Element) forecastDays.item(0);
		NodeList forecastDay = forecastDaysElement
				.getElementsByTagName("forecastday");

		List<String> conditionsList = new ArrayList<String>();
		List<String> nameList = new ArrayList<String>();
		List<String> highList = new ArrayList<String>();
		List<String> lowList = new ArrayList<String>();

		for (int i = 0; i < forecastDay.getLength(); i++) {

			Element forecastDayElement = (Element) forecastDay.item(i);

			NodeList date = forecastDayElement.getElementsByTagName("date");
			NodeList high = forecastDayElement.getElementsByTagName("high");
			NodeList low = forecastDayElement.getElementsByTagName("low");
			NodeList conditions = forecastDayElement
					.getElementsByTagName("conditions");

			Element dateElement = (Element) date.item(0);
			Element highElement = (Element) high.item(0);
			Element lowElement = (Element) low.item(0);

			NodeList dateItem = dateElement
					.getElementsByTagName("weekday_short");
			NodeList highItem = highElement.getElementsByTagName("celsius");
			NodeList lowItem = lowElement.getElementsByTagName("celsius");

			conditionsList.add(((Element) conditions.item(0)).getFirstChild()
					.getNodeValue());
			nameList.add(((Element) dateItem.item(0)).getFirstChild()
					.getNodeValue());
			highList.add(((Element) highItem.item(0)).getFirstChild()
					.getNodeValue());
			lowList.add(((Element) lowItem.item(0)).getFirstChild()
					.getNodeValue());

		}

		condition_day1S = conditionsList.get(0);
		condition_day2S = conditionsList.get(1);
		condition_day3S = conditionsList.get(2);
		condition_day4S = conditionsList.get(3);

		name_day1N = nameList.get(0);
		name_day2N = nameList.get(1);
		name_day3N = nameList.get(2);
		name_day4N = nameList.get(3);

		high_day1S = "High: " + highList.get(0) + " °C";
		high_day2S = "High: " + highList.get(1) + " °C";
		high_day3S = "High: " + highList.get(2) + " °C";
		high_day4S = "High: " + highList.get(3) + " °C";

		low_day1S = "Low: " + lowList.get(0) + " °C";
		low_day2S = "Low: " + lowList.get(1) + " °C";
		low_day3S = "Low: " + lowList.get(2) + " °C";
		low_day4S = "Low: " + lowList.get(3) + " °C";

	}

	public JSONObject getJSONFromUrl(String url) {

		InputStream is = null;
		JSONObject jObj = null;
		String json = "";

		// Making HTTP request
		try {
			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// return JSON String
		return jObj;

	}

	public String getLocationFromIP() throws JSONException, URISyntaxException,
			ParserConfigurationException, SAXException, IOException {

		// getting JSON string from URL
		JSONObject json = getJSONFromUrl("http://api.hostip.info/get_json.php");
		String ipfromjson = json.getString("ip");

		// get location from ip
		JSONObject geojson = getJSONFromUrl("http://freegeoip.net/json/"
				+ ipfromjson);
		String geofromipjson = geojson.getString("city");

		logToLogCat("JSON location", geofromipjson);

		return geofromipjson;
	}

	// public void setData() {
	// logToLogCat("mainActivity", "setData");
	// runOnUiThread(new Runnable() {
	// public void run() {
	// Editor e = sp.edit();
	// e.putString("weather", descS + DELIMITER + tempS + DELIMITER
	// + high_day1c + DELIMITER + low_day1c);
	// e.putString("location", cityS);
	// e.commit();
	//
	// // prepare Alarm Service to trigger Widget
	// Intent intent = new Intent(MyAppWidgetProvider.MY_WIDGET_UPDATE);
	// PendingIntent pendingIntent = PendingIntent.getBroadcast(
	// mainActivity.this, 0, intent, 0);
	//
	// AlarmManager alarmManager = (AlarmManager)
	// getSystemService(ALARM_SERVICE);
	//
	// // Calendar mycalendar = Calendar.getInstance();
	// // mycalendar.setTimeInMillis(System.currentTimeMillis());
	// // mycalendar.add(Calendar.SECOND, 10);
	// // alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
	// // mycalendar.getTimeInMillis(), 20 * 1000, pendingIntent);
	//
	// alarmManager.set(AlarmManager.RTC_WAKEUP,
	// System.currentTimeMillis() + 1000, pendingIntent);
	//
	// MyAppWidgetProvider.SaveAlarmManager(alarmManager,
	// pendingIntent);
	//
	// city.setText(cityS);
	//
	// // AZ ELÕREJELZÉS BEÁLLÍTÁSA KEPEK ES ADATOK
	//
	// day1.setImageDrawable(setImage(condition_day1S));
	// day2.setImageDrawable(setImage(condition_day2S));
	// day3.setImageDrawable(setImage(condition_day3S));
	// day4.setImageDrawable(setImage(condition_day4S));
	//
	// day1name.setText(name_day1N);
	// day2name.setText(name_day2N);
	// day3name.setText(name_day3N);
	// day4name.setText(name_day4N);
	//
	// day1high.setText("High: " + high_day1c + "°C");
	// day2high.setText("High: " + high_day2c + "°C");
	// day3high.setText("High: " + high_day3c + "°C");
	// day4high.setText("High: " + high_day4c + "°C");
	//
	// day1low.setText("Low: " + low_day1c + "°C");
	// day2low.setText("Low: " + low_day2c + "°C");
	// day3low.setText("Low: " + low_day3c + "°C");
	// day4low.setText("Low: " + low_day4c + "°C");
	//
	// day1cond.setText(condition_day1S);
	// day2cond.setText(condition_day2S);
	// day3cond.setText(condition_day3S);
	// day4cond.setText(condition_day4S);
	//
	// // AZ IDOJARASI ADATOK BEALLITASA
	//
	// temp.setText(tempS);
	// humi.setText(humiS);
	// desc.setText(descS);
	// wind.setText(windS);
	//
	// // KEP ES HATTER BEALLITASA
	//
	// icontoday.setImageDrawable(setImage(descS));
	// mainlayout.setBackgroundDrawable(setBg(descS));
	// }
	// });
	// }

	public void setData() {
		runOnUiThread(new Runnable() {
			public void run() {
				logToLogCat("mainActivity", "setData");
				Editor e = sp.edit();
				e.putString("weather", descS + DELIMITER + tempS + DELIMITER
						+ high_day1S + DELIMITER + low_day1S);
				e.putString("location", cityS);
				e.commit();

				// prepare Alarm Service to trigger Widget
				Intent intent = new Intent(MyAppWidgetProvider.MY_WIDGET_UPDATE);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						mainActivity.this, 0, intent, 0);

				AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

				// Calendar mycalendar = Calendar.getInstance();
				// mycalendar.setTimeInMillis(System.currentTimeMillis());
				// mycalendar.add(Calendar.SECOND, 10);
				// alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				// mycalendar.getTimeInMillis(), 20 * 1000, pendingIntent);

				alarmManager.set(AlarmManager.RTC_WAKEUP,
						System.currentTimeMillis() + 1000, pendingIntent);

				MyAppWidgetProvider.SaveAlarmManager(alarmManager,
						pendingIntent);

				logToLogCat("mainActivity", "StreetAddress: " + streetAddress);
				city.setText(streetAddress);

				// AZ ELÕREJELZÉS BEÁLLÍTÁSA KEPEK ES ADATOK

				day1.setImageDrawable(setImage(condition_day1S));
				day2.setImageDrawable(setImage(condition_day2S));
				day3.setImageDrawable(setImage(condition_day3S));
				day4.setImageDrawable(setImage(condition_day4S));

				day1name.setText(name_day1N);
				day2name.setText(name_day2N);
				day3name.setText(name_day3N);
				day4name.setText(name_day4N);

				day1high.setText(high_day1S);
				day2high.setText(high_day2S);
				day3high.setText(high_day3S);
				day4high.setText(high_day4S);

				day1low.setText(low_day1S);
				day2low.setText(low_day2S);
				day3low.setText(low_day3S);
				day4low.setText(low_day4S);

				day1cond.setText(condition_day1S);
				day2cond.setText(condition_day2S);
				day3cond.setText(condition_day3S);
				day4cond.setText(condition_day4S);

				// AZ IDOJARASI ADATOK BEALLITASA

				temp.setText(tempS);
				humi.setText(humiS);
				desc.setText(descS);
				wind.setText(windS);

				// KEP ES HATTER BEALLITASA

				icontoday.setImageDrawable(setImage(descS));
				mainlayout.setBackgroundDrawable(setBg(descS));
			}
		});
	}

	// A MEGFELELÕ KÉPET BEÁLLÍTÓ FÜGGVÉNY A KÉPPEL TÉR VISSZA

	// public Drawable setImage(String condition) {
	//
	// logToLogCat("mainActivity", "setImage");
	// Resources res = getResources();
	// Drawable drawable;
	//
	// if (condition.equals("Fog")) {
	// drawable = res.getDrawable(R.drawable.fog);
	// return drawable;
	// }
	// if (condition.equals("Overcast")) {
	// drawable = res.getDrawable(R.drawable.overcast);
	// return drawable;
	// }
	// if (condition.equals("Partly Sunny")) {
	// drawable = res.getDrawable(R.drawable.partlysunny);
	// return drawable;
	// }
	// if (condition.equals("Freezing Drizzle")) {
	// drawable = res.getDrawable(R.drawable.freezingdrizzle);
	// return drawable;
	// }
	// if (condition.equals("Drizzle")) {
	// drawable = res.getDrawable(R.drawable.freezingdrizzle);
	// return drawable;
	// }
	// if (condition.equals("Clear")) {
	// drawable = res.getDrawable(R.drawable.clear);
	// return drawable;
	// }
	// if (condition.equals("Cloudy")) {
	// drawable = res.getDrawable(R.drawable.cloudy);
	// return drawable;
	// }
	// if (condition.equals("Haze")) {
	// drawable = res.getDrawable(R.drawable.haze);
	// return drawable;
	// }
	// if (condition.equals("Light rain")) {
	// drawable = res.getDrawable(R.drawable.lightrain);
	// return drawable;
	// }
	// if (condition.equals("Mostly Cloudy")) {
	// drawable = res.getDrawable(R.drawable.mostlycloudy);
	// return drawable;
	// }
	// if (condition.equals("Partly Cloudy")) {
	// drawable = res.getDrawable(R.drawable.partlycloudy);
	// return drawable;
	// }
	// if (condition.equals("Rain")) {
	// drawable = res.getDrawable(R.drawable.rain);
	// return drawable;
	// }
	// if (condition.equals("Rain Showers")) {
	// drawable = res.getDrawable(R.drawable.rainshowers);
	// return drawable;
	// }
	// if (condition.equals("Showers")) {
	// drawable = res.getDrawable(R.drawable.rainshowers);
	// return drawable;
	// }
	// if (condition.equals("Thunderstorm")) {
	// drawable = res.getDrawable(R.drawable.storm);
	// return drawable;
	// }
	// if (condition.equals("Chance of Rain")) {
	// drawable = res.getDrawable(R.drawable.chanceofrain);
	// return drawable;
	// }
	// if (condition.equals("Chance of Showers")) {
	// drawable = res.getDrawable(R.drawable.chanceofrain);
	// return drawable;
	// }
	// if (condition.equals("Chance of Snow")) {
	// drawable = res.getDrawable(R.drawable.chanceofsnow);
	// return drawable;
	// }
	// if (condition.equals("Chance of Storm")) {
	// drawable = res.getDrawable(R.drawable.chanceofstorm);
	// return drawable;
	// }
	// if (condition.equals("Mostly Sunny")) {
	// drawable = res.getDrawable(R.drawable.mostlysunny);
	// return drawable;
	// }
	// if (condition.equals("Scattered Showers")) {
	// drawable = res.getDrawable(R.drawable.scatteredshowers);
	// return drawable;
	// }
	// if (condition.equals("Sunny")) {
	// drawable = res.getDrawable(R.drawable.sunny);
	// return drawable;
	// }
	// if (condition.equals("Snow")) {
	// drawable = res.getDrawable(R.drawable.snow);
	// return drawable;
	// }
	// if (condition.equals("Light snow")) {
	// drawable = res.getDrawable(R.drawable.snow);
	// return drawable;
	// }
	// if (condition.equals("Snow showers")) {
	// drawable = res.getDrawable(R.drawable.snowshowers);
	// return drawable;
	// }
	// if (condition.equals("Smoke")) {
	// drawable = res.getDrawable(R.drawable.smoke);
	// return drawable;
	// }
	// if (condition.equals("Rain and Snow")) {
	// drawable = res.getDrawable(R.drawable.rainandsnow);
	// return drawable;
	// }
	//
	// return res.getDrawable(R.drawable.na);
	// }

	public Drawable setImage(String condition) {

		Resources res = getResources();
		Drawable drawable;

		if (condition.equals("Light Freezing Fog")
				|| condition.equals("Heavy Freezing Fog")
				|| condition.contains("Fog")) {
			drawable = res.getDrawable(R.drawable.fog);
			return drawable;
		}
		if (condition.equals("Overcast")) {
			drawable = res.getDrawable(R.drawable.overcast);
			return drawable;
		}
		if (condition.equals("Partly Sunny")) {
			drawable = res.getDrawable(R.drawable.partlysunny);
			return drawable;
		}
		if (condition.equals("Light Freezing Drizzle")
				|| condition.equals("Heavy Freezing Drizzle")
				|| condition.equals("Freezing Drizzle")) {
			drawable = res.getDrawable(R.drawable.freezingdrizzle);
			return drawable;
		}
		if (condition.equals("Light Drizzle")
				|| condition.equals("Heavy Drizzle")
				|| condition.equals("Drizzle")) {
			drawable = res.getDrawable(R.drawable.freezingdrizzle);
			return drawable;
		}
		if (condition.equals("Clear")) {
			drawable = res.getDrawable(R.drawable.clear);
			return drawable;
		}
		if (condition.equals("Cloudy") || condition.contains("Cloud")) {
			drawable = res.getDrawable(R.drawable.cloudy);
			return drawable;
		}
		if (condition.equals("Light Haze") || condition.equals("Heavy Haze")
				|| condition.equals("Haze")) {
			drawable = res.getDrawable(R.drawable.haze);
			return drawable;
		}
		if (condition.equals("Light Rain")) {
			drawable = res.getDrawable(R.drawable.lightrain); // EZT MÉG
																// ELDÖNTENI
			return drawable;
		}
		if (condition.equals("Mostly Cloudy")) {
			drawable = res.getDrawable(R.drawable.mostlycloudy);
			return drawable;
		}
		if (condition.equals("Partly Cloudy")
				|| condition.equals("Scattered Clouds")) {
			drawable = res.getDrawable(R.drawable.partlycloudy);
			return drawable;
		}
		if (condition.equals("Rain") || condition.equals("Light Freezing Rain")
				|| condition.equals("Heavy Freezing Rain")) {
			drawable = res.getDrawable(R.drawable.rain);
			return drawable;
		}
		if (condition.equals("Light Rain Showers")
				|| condition.equals("Heavy Rain Showers")
				|| condition.equals("Rain Showers")) {
			drawable = res.getDrawable(R.drawable.rainshowers);
			return drawable;
		}
		if (condition.equals("Showers")) {
			drawable = res.getDrawable(R.drawable.rainshowers);
			return drawable;
		}
		if (condition.equals("Light Thunderstorm")
				|| condition.equals("Heavy Thunderstorm")
				|| condition.equals("Thunderstorm")
				|| condition.contains("Thundrstorms")) {
			drawable = res.getDrawable(R.drawable.storm);
			return drawable;
		}
		if (condition.equals("Chance of Rain")
				|| condition.equals("Chance of Freezing Rain")) {
			drawable = res.getDrawable(R.drawable.chanceofrain);
			return drawable;
		}
		if (condition.equals("Chance of Showers")) {
			drawable = res.getDrawable(R.drawable.chanceofrain);
			return drawable;
		}
		if (condition.equals("Chance of Snow")) {
			drawable = res.getDrawable(R.drawable.chanceofsnow);
			return drawable;
		}
		if (condition.equals("Chance of Thunderstorms")
				|| condition.equals("Chance of a Thunderstorm")) {
			drawable = res.getDrawable(R.drawable.chanceofstorm);
			return drawable;
		}
		if (condition.equals("Mostly Sunny")) {
			drawable = res.getDrawable(R.drawable.mostlysunny);
			return drawable;
		}
		if (condition.equals("Scattered Showers")) {
			drawable = res.getDrawable(R.drawable.scatteredshowers);
			return drawable;
		}
		if (condition.equals("Sunny")) {
			drawable = res.getDrawable(R.drawable.sunny);
			return drawable;
		}
		if (condition.equals("Snow") || condition.contains("Snow")) {
			drawable = res.getDrawable(R.drawable.snow);
			return drawable;
		}
		if (condition.equals("Light snow") || condition.equals("Sleet")
				|| condition.equals("Chance of Freezing Rain")) {
			drawable = res.getDrawable(R.drawable.snow);
			return drawable;
		}
		if (condition.equals("Snow showers")
				|| condition.equals("Chance of Flurries")
				|| condition.equals("Flurries")) {
			drawable = res.getDrawable(R.drawable.snowshowers);
			return drawable;
		}
		if (condition.equals("Smoke")) {
			drawable = res.getDrawable(R.drawable.smoke);
			return drawable;
		}
		if (condition.equals("Rain and Snow")) {
			drawable = res.getDrawable(R.drawable.rainandsnow);
			return drawable;
		}
		if(condition.equals("Heavy Rain")) {
			drawable = res.getDrawable(R.drawable.rain);
			return drawable;
		}

		return res.getDrawable(R.drawable.na);
	}

	// A MEGFELELÕ HÁTTERET BEÁLLÍTÓ FÜGGVÉNY KÉPPEL TÉR VISSZA

	// public Drawable setBg(String condition) {
	//
	// logToLogCat("mainActivity", "setBg");
	// Resources res = getResources();
	// Drawable drawable;
	//
	// if (condition.equals("Fog")) {
	// drawable = res.getDrawable(R.drawable.bgsnow);
	// return drawable;
	// }
	// if (condition.equals("Overcast")) {
	// drawable = res.getDrawable(R.drawable.bgcloud);
	// return drawable;
	// }
	// if (condition.equals("Partly Sunny")) {
	// drawable = res.getDrawable(R.drawable.bgsun);
	// return drawable;
	// }
	// if (condition.equals("Freezing Drizzle")) {
	// drawable = res.getDrawable(R.drawable.bgrain);
	// return drawable;
	// }
	// if (condition.equals("Drizzle")) {
	// drawable = res.getDrawable(R.drawable.bgrain);
	// return drawable;
	// }
	// if (condition.equals("Clear")) {
	// drawable = res.getDrawable(R.drawable.bgsun);
	// return drawable;
	// }
	// if (condition.equals("Cloudy")) {
	// drawable = res.getDrawable(R.drawable.bgcloud);
	// return drawable;
	// }
	// if (condition.equals("Haze")) {
	// drawable = res.getDrawable(R.drawable.bgsnow);
	// return drawable;
	// }
	// if (condition.equals("Light rain")) {
	// drawable = res.getDrawable(R.drawable.bgrain);
	// return drawable;
	// }
	// if (condition.equals("Mostly Cloudy")) {
	// drawable = res.getDrawable(R.drawable.bgcloud);
	// return drawable;
	// }
	// if (condition.equals("Partly Cloudy")) {
	// drawable = res.getDrawable(R.drawable.bgcloud);
	// return drawable;
	// }
	// if (condition.equals("Rain")) {
	// drawable = res.getDrawable(R.drawable.bgrain);
	// return drawable;
	// }
	// if (condition.equals("Rain Showers")) {
	// drawable = res.getDrawable(R.drawable.bgrain);
	// return drawable;
	// }
	// if (condition.equals("Showers")) {
	// drawable = res.getDrawable(R.drawable.bgrain);
	// return drawable;
	// }
	// if (condition.equals("Thunderstorm")) {
	// drawable = res.getDrawable(R.drawable.bgstorm);
	// return drawable;
	// }
	// if (condition.equals("Chance of Rain")) {
	// drawable = res.getDrawable(R.drawable.bgrain);
	// return drawable;
	// }
	// if (condition.equals("Chance of Showers")) {
	// drawable = res.getDrawable(R.drawable.bgrain);
	// return drawable;
	// }
	// if (condition.equals("Chance of Snow")) {
	// drawable = res.getDrawable(R.drawable.bgsnow);
	// return drawable;
	// }
	// if (condition.equals("Chance of Storm")) {
	// drawable = res.getDrawable(R.drawable.bgstorm);
	// return drawable;
	// }
	// if (condition.equals("Mostly Sunny")) {
	// drawable = res.getDrawable(R.drawable.bgsun);
	// return drawable;
	// }
	// if (condition.equals("Scattered Showers")) {
	// drawable = res.getDrawable(R.drawable.bgrain);
	// return drawable;
	// }
	// if (condition.equals("Sunny")) {
	// drawable = res.getDrawable(R.drawable.bgsun);
	// return drawable;
	// }
	// if (condition.equals("Snow")) {
	// drawable = res.getDrawable(R.drawable.bgsnow);
	// return drawable;
	// }
	// if (condition.equals("Light snow")) {
	// drawable = res.getDrawable(R.drawable.bgsnow);
	// return drawable;
	// }
	// if (condition.equals("Snow showers")) {
	// drawable = res.getDrawable(R.drawable.bgsnow);
	// return drawable;
	// }
	// if (condition.equals("Smoke")) {
	// drawable = res.getDrawable(R.drawable.bgsnow);
	// return drawable;
	// }
	// if (condition.equals("Rain and Snow")) {
	// drawable = res.getDrawable(R.drawable.bgsnow);
	// return drawable;
	// }
	//
	// return res.getDrawable(R.drawable.bg);
	// }

	public Drawable setBg(String condition) {

		Resources res = getResources();
		Drawable drawable;

		if (condition.equals("Fog") || condition.contains("Fog")) {
			drawable = res.getDrawable(R.drawable.bgsnow);
			return drawable;
		}
		if (condition.equals("Overcast")) {
			drawable = res.getDrawable(R.drawable.bgcloud);
			return drawable;
		}
		if (condition.equals("Partly Sunny")) {
			drawable = res.getDrawable(R.drawable.bgsun);
			return drawable;
		}
		if (condition.equals("Freezing Drizzle")
				|| condition.contains("Drizzle")) {
			drawable = res.getDrawable(R.drawable.bgrain);
			return drawable;
		}
		if (condition.equals("Drizzle")) {
			drawable = res.getDrawable(R.drawable.bgrain);
			return drawable;
		}
		if (condition.equals("Clear")) {
			drawable = res.getDrawable(R.drawable.bgsun);
			return drawable;
		}
		if (condition.equals("Cloudy") || condition.contains("Cloud")) {
			drawable = res.getDrawable(R.drawable.bgcloud);
			return drawable;
		}
		if (condition.equals("Haze") || condition.contains("Haze")) {
			drawable = res.getDrawable(R.drawable.bgsnow);
			return drawable;
		}
		if (condition.equals("Light Rain") || condition.contains("Rain")) {
			drawable = res.getDrawable(R.drawable.bgrain);
			return drawable;
		}
		if (condition.equals("Mostly Cloudy")) {
			drawable = res.getDrawable(R.drawable.bgcloud);
			return drawable;
		}
		if (condition.equals("Partly Cloudy")) {
			drawable = res.getDrawable(R.drawable.bgcloud);
			return drawable;
		}
		if (condition.equals("Rain")) {
			drawable = res.getDrawable(R.drawable.bgrain);
			return drawable;
		}
		if (condition.equals("Rain Showers")) {
			drawable = res.getDrawable(R.drawable.bgrain);
			return drawable;
		}
		if (condition.equals("Showers")) {
			drawable = res.getDrawable(R.drawable.bgrain);
			return drawable;
		}
		if (condition.equals("Thunderstorm")
				|| condition.contains("Thunderstorm")) {
			drawable = res.getDrawable(R.drawable.bgstorm);
			return drawable;
		}
		if (condition.equals("Chance of Rain")) {
			drawable = res.getDrawable(R.drawable.bgrain);
			return drawable;
		}
		if (condition.equals("Chance of Showers")) {
			drawable = res.getDrawable(R.drawable.bgrain);
			return drawable;
		}
		if (condition.equals("Chance of Snow")) {
			drawable = res.getDrawable(R.drawable.bgsnow);
			return drawable;
		}
		if (condition.equals("Chance of Storm")) {
			drawable = res.getDrawable(R.drawable.bgstorm);
			return drawable;
		}
		if (condition.equals("Mostly Sunny")) {
			drawable = res.getDrawable(R.drawable.bgsun);
			return drawable;
		}
		if (condition.equals("Scattered Showers")) {
			drawable = res.getDrawable(R.drawable.bgrain);
			return drawable;
		}
		if (condition.equals("Sunny")) {
			drawable = res.getDrawable(R.drawable.bgsun);
			return drawable;
		}
		if (condition.equals("Snow")) {
			drawable = res.getDrawable(R.drawable.bgsnow);
			return drawable;
		}
		if (condition.equals("Light Snow")) {
			drawable = res.getDrawable(R.drawable.bgsnow);
			return drawable;
		}
		if (condition.equals("Snow Showers")) {
			drawable = res.getDrawable(R.drawable.bgsnow);
			return drawable;
		}
		if (condition.equals("Smoke")) {
			drawable = res.getDrawable(R.drawable.bgsnow);
			return drawable;
		}
		if (condition.equals("Rain and Snow")) {
			drawable = res.getDrawable(R.drawable.bgsnow);
			return drawable;
		}

		return res.getDrawable(R.drawable.bg);
	}

	// AZ ORAHOZ AZ UJ THREAD

	public void doWork() {
		runOnUiThread(new Runnable() {
			public void run() {
				try {

					TextView txtCurrentTime = (TextView) findViewById(R.id.time);
					Date dt = new Date();
					int hours = dt.getHours();
					int minutes = dt.getMinutes();
					int seconds = dt.getSeconds();

					String curTime = hours + ":" + minutes + ":" + seconds;

					if (seconds < 10)
						curTime = hours + ":" + minutes + ":" + "0" + seconds;
					if (minutes < 10)
						curTime = hours + ":" + "0" + minutes + ":" + seconds;
					if (hours < 10)
						curTime = "0" + hours + ":" + minutes + ":" + seconds;
					if (seconds < 10 && minutes < 10)
						curTime = hours + ":" + "0" + minutes + ":" + "0"
								+ seconds;
					if (seconds < 10 && hours < 10)
						curTime = "0" + hours + ":" + minutes + ":" + "0"
								+ seconds;
					if (minutes < 10 && hours < 10)
						curTime = "0" + hours + ":" + "0" + minutes + ":"
								+ seconds;
					if (seconds < 10 && minutes < 10 && hours < 10)
						curTime = "0" + hours + ":" + "0" + minutes + ":" + "0"
								+ seconds;

					txtCurrentTime.setText(curTime);
				} catch (Exception e) {
				}
			}
		});
	}

	// AZ ORA MEGVALOSITASA

	class CountDownRunner implements Runnable {
		// @Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					doWork();
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} catch (Exception e) {
				}
			}
		}

	}

	class LocInfo {

		String city;
		String AdminArea;

	}

	class WeatherUpdater implements Runnable {

		public void run() {
			logToLogCat("mainActivity", "WeatherUpdate");
			try {

				LocInfo locInfo = getCity(streetAddress);
				getWeather(locInfo.city, locInfo.AdminArea);
				setData();
				handler.sendEmptyMessage(0);
				Thread.currentThread().interrupt();

			} catch (ParserConfigurationException e) {

				Editor ed = sp.edit();
				ed.putString("location", DEFAULT_CITY);
				ed.commit();
				handler.sendEmptyMessage(0);
				toashandler.sendEmptyMessage(0);

				Thread.currentThread().interrupt();
			} catch (SAXException e) {

				Editor ed = sp.edit();
				ed.putString("location", DEFAULT_CITY);
				ed.commit();
				handler.sendEmptyMessage(0);
				toashandler.sendEmptyMessage(0);

				Thread.currentThread().interrupt();
			} catch (IOException e) {

				Editor ed = sp.edit();
				ed.putString("location", DEFAULT_CITY);
				ed.commit();
				handler.sendEmptyMessage(0);
				toashandler.sendEmptyMessage(0);

				Thread.currentThread().interrupt();
			} catch (Exception e) {

				Editor ed = sp.edit();
				ed.putString("location", DEFAULT_CITY);
				ed.commit();
				handler.sendEmptyMessage(0);
				toashandler.sendEmptyMessage(0);

				Thread.currentThread().interrupt();
			}

		}
	}

	class IPLocUpdater implements Runnable {

		public void run() {
			logToLogCat("mainActivity", "IPLocUpdater");

			try {
				DEFAULT_CITY = getLocationFromIP();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			handler2.sendEmptyMessage(0);
			Thread.currentThread().interrupt();
		}
	}

	class LocationByNetUpdater implements Runnable {

		public void run() {
			logToLogCat("mainActivity", "LocationByNetUpdater");

			try {
				streetAddress = getLocationFromIP();

			} catch (JSONException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			handler2.sendEmptyMessage(0);
			Thread.currentThread().interrupt();
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			dialog.dismiss();
		}

	};

	private Handler handler2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			dialog2.dismiss();
		}

	};

	private Handler toashandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			badData();
		};
	};

	public void badData() {
		Toast.makeText(
				this,
				"Can't get weather info, try again or set your location to nearest bigger town!",
				Toast.LENGTH_LONG).show();
	}

	// LOCATIONMANAGER HA MEGVALTOZIK A HELYZET CSAK EGYSZER KERJUK LE A
	// HELYZETET
	// UTANA LEIRATKOZUNK ROLA
	// UTANA PEDIG AZ UJ HELYZETUNK ALAPJAN LEKERDEZZUK AZ IDOJARAST
	// ##############################################################################################

	public void onLocationChanged(Location location) {
		logToLogCat("mainActivity", "onLocationChanged");
		try {
			locmanager.removeUpdates(mainActivity.this);

			double lat = location.getLatitude();
			double lng = location.getLongitude();

			List<Address> myList;

			Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
			myList = geocoder.getFromLocation(lat, lng, 1);

			locmanager.removeUpdates(mainActivity.this);

			String loc;

			if (myList.get(0).getLocality() != null) {
				loc = myList.get(0).getLocality();
			}

			else {
				loc = myList.get(0).getAdminArea();
			}

			streetAddress = loc;
			if (chkStatusNoNotify()) {
				dialog = ProgressDialog.show(this, "", "Updating weather...",
						true, true);
				Runnable runn = new WeatherUpdater();
				weatherThread = new Thread(runn);
				weatherThread.start();
			}
			Editor e = sp.edit();
			e.putString("location", loc);
			e.commit();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ##############################################################################################

	// EZEK INNEN NINCSENEK MEGVALOSITVA MERT MINEK
	// *********************************************

	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "GPS is disabled", Toast.LENGTH_SHORT).show();

	}

	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "GPS is enabled", Toast.LENGTH_SHORT).show();

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// city.append("New status: " + provider + " statusCode: "
		// + status);
	}
	// ******************************************************************************************

}
