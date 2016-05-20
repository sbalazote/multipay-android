package com.multipay.android.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.multipay.android.helpers.SessionManager;
import com.multipay.android.R;

public class SplashScreenActivity extends AppCompatActivity {
	private long ms = 0;
	private long splashTime = 2000;
	private boolean splashActive = true;
	private boolean paused = false;

	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	private static final String LOGCAT_TAG = "SplashScreenActivity";
	private String registrationId;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);
		context = getApplicationContext();

		// Reviso si el dispositivo tiene Google Play Services APK. Si es asi prosigo con la registracion al FCM.
		if (checkPlayServices()) {
			registrationId = getRegistrationIdFromSession(context);
			Log.i(LOGCAT_TAG, registrationId);
			// Si registrationId obtenido de la sesion esta vacio, pido el token a FCM.
			if (registrationId.isEmpty()) {
				getRegistrationIdFromFCM();
			}
		} else {
			Log.e(LOGCAT_TAG, "Error!. No se ha encontrado el APK de Google Play Services.");
			finish();
			return;
		}

		// TODO Hacer que el tiempo de espera sea el real hasta poder obtener el registrationId.
		Thread mythread = new Thread() {
			public void run() {
				try {
					while (splashActive && ms < splashTime) {
						if (!paused)
							ms = ms + 100;
						sleep(100);
					}
				} catch (Exception e) {
				} finally {
					Intent selectModeActivityIntent = new Intent(SplashScreenActivity.this, SelectModeActivity.class);
					startActivity(selectModeActivityIntent);
				}
			}
		};
		mythread.start();
	}

	@Override
	protected void onResume() {
		super.onResume();
		checkPlayServices();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * Verifico que el dispositivo tenga el APK de Google Play Services.
	 * Si no lo tiene, muestro un dialogo que permite a los usuarios descargarlo desde
	 * Google Play Store o habilitarlo desde los ajustes del dispositivo.
	 */
	/*private boolean checkPlayServices() {
		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (apiAvailability.isUserResolvableError(resultCode)) {
				Dialog dialog = apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
				dialog.create();
				dialog.show();
			} else {
				Log.i(LOGCAT_TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}*/
	private boolean checkPlayServices() {
		return true;
	}

	private void storeRegistrationId() {
		SessionManager.getInstance(context).storeRegistrationId(registrationId);
		SessionManager.getInstance(context).storeAppVersion(getAppVersion(context));
	}

	private String getRegistrationIdFromSession(Context context) {
		String registrationId = SessionManager.getInstance(context).retrieveRegistrationId();
		if (registrationId.isEmpty()) {
			Log.w(LOGCAT_TAG, "Registracion al FCM no encontrada.");
			return "";
		}
		int registeredVersion = SessionManager.getInstance(context).retrieveAppVersion();
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.w(LOGCAT_TAG, "Version de MultiPay ha cambiado.");
			return "";
		}
		return registrationId;
	}

	private void getRegistrationIdFromFCM() {
		registrationId = FirebaseInstanceId.getInstance().getToken();
		Log.d(LOGCAT_TAG, "InstanceID token: " + registrationId);
		String msg = "Se ha registrado el dispositivo a FCM, con ID: " + registrationId;

		// Guardo el registrationId en Preferencias de la Aplicacion.
		storeRegistrationId();
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	}

	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// No deberia pasar nunca.
			throw new RuntimeException("Could not get package name: " + e);
		}
	}
}