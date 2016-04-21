package com.multipay.android.helpers;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.multipay.android.activities.SelectModeActivity;
import com.multipay.android.utils.GooglePlusSignInUtils;

public class SessionManager {

	// Preferencias MultiPay
	private static SharedPreferences sharedPreferences;

	// Editor para las Preferencias MultiPay.
	private static Editor editor;

	// Contexto Android para el cual se invoca la Sesion.
	private static Context context;

	// Nombre del archivo de Preferencias MultiPay.
	private static final String SHARED_PREFERENCES_FILENAME = "multipay_pf_file";

	/**
	 * Claves para las preferencias.
	 */

	// Estado de Logueo 
	private static final String IS_SIGNED_IN = "IsSignedIn";
	// Nombre de Usuario.
	private static final String KEY_USERNAME = "name";
	// Email de Usuario.
	private static final String KEY_USERNAME_EMAIL = "email";
	// Tipo de Logueo . (FACEBOOK / GOOGLE / NATIVE)
	private static final String KEY_SIGNIN_TYPE = "signInType";
	// Modo de Logueo. (SELLER / BUYER)
	private static final String KEY_SIGNIN_MODE = "signInMode";
	// ID de registracion a los servidores de Google Cloud Messaging. (GCM)
	private static final String KEY_REGISTRATION_ID = "registrationId";
	// Version actual de la aplicacion MultiPay.
	private static final String KEY_APP_VERSION = "appVersion";
	// Instancia estatica para MultiPay.
	private static SessionManager instance;

	public static SessionManager getInstance(Context instanceContext) {
		context = instanceContext;
		if (instance == null) {
			instance = new SessionManager(context);
		}
		return instance;
	}

	private SessionManager(Context context) {
		sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILENAME, Context.MODE_PRIVATE);
	}

	/**
	 * Creo sesion de Logueo.
	 * */
	public void createSignInSession(String name, String email, String signInType) {
		editor = sharedPreferences.edit();
		editor.putBoolean(IS_SIGNED_IN, true);
		editor.putString(KEY_USERNAME, name);
		editor.putString(KEY_USERNAME_EMAIL, email);
		editor.putString(KEY_SIGNIN_TYPE, signInType);
		editor.commit();
	}

	public String getMode() {
		return sharedPreferences.getString(KEY_SIGNIN_MODE, null);
	}
	
	public void setMode(String mode) {
		editor = sharedPreferences.edit();
		editor.putString(KEY_SIGNIN_MODE, mode);
		editor.commit();
	}
	
	public String getUserSignInType() {
		return sharedPreferences.getString(KEY_SIGNIN_TYPE, null);
	}

	public String getUsername() {
		return sharedPreferences.getString(KEY_USERNAME, null);
	}

	/**
	 * Verifico el estado de Logueo. Si no lo esta, redirijo a la pagina de
	 * seleccion de modo. (SELLER/BUYER)
	 * */
	public void checkLogin(Context context) {
		if (!this.isSignedIn()) {
			// Usuario no esta logueado. Lo redirijo a la pantalla de seleccion de modo.
			Intent selectModeActivityIntent = new Intent(context, SelectModeActivity.class);
			// Cierro todas las actividades en la pila de actividades.
			selectModeActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// Agrego flago indicando que comienza una nueva actividad.
			selectModeActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(selectModeActivityIntent);
		}
	}

	/**
	 * Limpio todos los valores almacenados en las preferencias cuando deslogueo
	 * a un Usuario.
	 * */
	public void logoutUser() {
		String signInType = sharedPreferences.getString(KEY_SIGNIN_TYPE, null);
		if (signInType.compareTo("FACEBOOK") == 0) {
			if (AccessToken.getCurrentAccessToken() != null) {
				//Session.getActiveSession().closeAndClearTokenInformation();
				LoginManager.getInstance().logOut();
			}
			//Session.setActiveSession(null);
		} else {
			GooglePlusSignInUtils.signOutFromGplus();
		}
		editor.clear();
		editor.commit();
		// Lo redirijo a la pantalla de seleccion de modo.
		Intent selectModeActivityIntent = new Intent(context, SelectModeActivity.class);
		// Cierro todas las actividades en la pila de actividades.
		selectModeActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// Agrego flago indicando que comienza una nueva actividad.
		selectModeActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(selectModeActivityIntent);
	}

	/**
	 * Verifico el estado de Logueo.
	 * */
	private boolean isSignedIn() {
		return sharedPreferences.getBoolean(IS_SIGNED_IN, false);
	}

	/**
	 * Obtengo el ID de Registracion a los servidores de GCM.
	 * @return
	 */
	public String retrieveRegistrationId() {
		return sharedPreferences.getString(KEY_REGISTRATION_ID, "");
	}
	
	/**
	 * Guardo el ID de Registracion a los servidores de GCM.
	 * @param registrationId
	 */
	public void storeRegistrationId(String registrationId) {
		editor = sharedPreferences.edit();
		editor.putString(KEY_REGISTRATION_ID, registrationId);
		editor.commit();
	}
	
	/**
	 * Obtengo la version de aplicacion MultiPay actual.
	 * @return
	 */
	public Integer retrieveAppVersion() {
		return sharedPreferences.getInt(KEY_APP_VERSION, Integer.MIN_VALUE);
	}

	/**
	 * Guardo la version de aplicacion MultiPay actual.
	 * @param appVersion
	 */
	public void storeAppVersion(int appVersion) {
		editor = sharedPreferences.edit();
		editor.putInt(KEY_APP_VERSION, appVersion);
		editor.commit();
	}
}