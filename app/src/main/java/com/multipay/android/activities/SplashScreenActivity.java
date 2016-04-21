package com.multipay.android.activities;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mercadopago.core.MercadoPago;
import com.multipay.android.multipay.R;
import com.multipay.android.helpers.SessionManager;

public class SplashScreenActivity extends Activity {
	private long ms = 0;
	private long splashTime = 2000;
	private boolean splashActive = true;
	private boolean paused = false;

	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	/**
	 * SENDER_ID Este es el numero de proyecto obtenido en la consola API de
	 * Google que es quien envia las notificaciones a los servidores GCM (Google
	 * Cloud Messaging.
	 */
	private String SENDER_ID = "437797444824";
	private static final String LOGCAT_TAG = "SplashScreenActivity";
	private GoogleCloudMessaging gcm;
	private String registrationId;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		context = getApplicationContext();

		// Obtengo atributos de preferencias de usuario de MercadoPago. 
		//MercadoPago.getInstance().getPreferenceAttributes();
		
		// Reviso si el dispositivo tiene Google Play Services APK. Si es asi prosigo con la registracion al GCM.
		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(this);
			registrationId = getRegistrationId(context);
			Log.i(LOGCAT_TAG, registrationId);
			// Si registrationId esta vacio, me registro en segundo plano.
			if (registrationId.isEmpty()) {
				registerInBackground();
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
		// Verifico al dispositivo por el APK de Google Play Services.
		checkPlayServices();
	}

	/**
	 * Verifico que el dispositivo tenga el APK de Google Play Services.
	 * Si no lo tiene, muestro un dialogo que permite a los usuarios descargarlo desde
	 * Google Play Store o habilitarlo desde los ajustes del dispositivo.
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.e(LOGCAT_TAG, "Error!. Este dispositivo no se encuentra soportado por Google Play Services.");
				finish();
			}
			return false;
		}
		return true;
	}

	/**
	 * Guarda el registration ID y el appVersion en Preferencias de Aplicacion
	 * {@code SharedPreferences}.
	 *
	 * @param context
	 *            application's context.
	 * @param registrationId
	 *            registration ID
	 */
	private void storeRegistrationId(Context context, String registrationId) {
		SessionManager.getInstance(context).storeRegistrationId(registrationId);
		SessionManager.getInstance(context).storeAppVersion(getAppVersion(context));
	}

	/**
	 * Obtiene el registration ID actual si es que hay uno.
	 * <p>
	 * Si el resultado es vacio, la aplicacion debe registrarse al GCM.
	 *
	 * @return registration ID, o string vacio si no existe un registration ID.
	 */
	private String getRegistrationId(Context context) {
		String registrationId = SessionManager.getInstance(context).retrieveRegistrationId();
		if (registrationId.isEmpty()) {
			Log.i(LOGCAT_TAG, "Registration not found.");
			return "";
		}
		// Verifico si la aplicacion fue actualizada.
		// Si es asi, debo limpiar y obtener un nuevo ID de Registracion.
		int registeredVersion = SessionManager.getInstance(context).retrieveAppVersion();
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(LOGCAT_TAG, "Version de MultiPay ha cambiado.");
			return "";
		}
		return registrationId;
	}

	/**
	 * Registra la aplicacion con el servidor GCM de forma asincronica.
	 * <p>
	 * Gaurda el registration ID y la version de la aplicacion en Preferencias de la Aplicacion.
	 */
	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					registrationId = gcm.register(SENDER_ID);
					msg = "Se ha registrado el dispositivo al GCM, ID de Registracion= " + registrationId;

					// Envio el registration ID al servidor nuestro para poder enviar notificaciones a nuestra aplicacion.
					sendRegistrationIdToBackend();

					// Gaurdo el registrationId en Preferencias de la Aplicacion.
					storeRegistrationId(context, registrationId);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					// If there is an error, don't just keep trying to register.
					// Require the user to click a button again, or perform
					// exponential back-off.
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
			}
		}.execute(null, null, null);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * @return Obtiene el Codigo de Version de la Aplicacion del {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// No deberia pasar nunca.
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * Envia el registration ID al servidor nuestro por HTTP, para que se pueda enviar notificaciones al dispositivo que se registro.
	 */
	private void sendRegistrationIdToBackend() {
		// TODO Your implementation here.
		Log.i(LOGCAT_TAG, "info al server" + registrationId);
	}
}