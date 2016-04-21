package com.multipay.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.multipay.android.multipay.R;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class FacebookSignInUtils implements ProfilePictureView.OnErrorListener {

	private String LOGCAT_TAG = "FacebookSignInUtils";
    public static final String FACEBOOK_NAME = "name";
    public static final String FACEBOOK_EMAIL = "email";
    public static final String FACEBOOK_FIRST_NAME = "first_name";
    public static final String FACEBOOK_LAST_NAME = "last_name";
    public static final String FACEBOOK_USERID = "userid";
	private Context context;
	private LoginButton facebookSignInButton;
	private CallbackManager callbackManager;
	private AccessTokenTracker accessTokenTracker;
	private AccessToken accessToken;
	private ProfileTracker profileTracker;
	private static final List<String> PERMISSIONS = Arrays.asList("public_profile", "email");
	private FacebookSignInStatus facebookSignInStatus;
	private ProfilePictureView facebookProfilePictureView;
	private TextView userNameView;
	
	public FacebookSignInUtils(Activity activity) {
		this.context = activity.getApplicationContext();
		facebookSignInButton = (LoginButton) activity.findViewById(R.id.facebook_sign_in_button);
		facebookSignInButton.setReadPermissions(PERMISSIONS);

		FacebookSdk.sdkInitialize(this.context);
		callbackManager = CallbackManager.Factory.create();

		facebookSignInButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess(LoginResult loginResult) {
				Log.d(LOGCAT_TAG, "Sesion de Facebook abierta.");
				Log.d(LOGCAT_TAG, "Access Token Facebook: " + loginResult.getAccessToken().getToken());
				requestUserInfo();
			}

			@Override
			public void onCancel() {
				Log.d(LOGCAT_TAG, "Sesion de Facebook cerrada.");
			}

			@Override
			public void onError(FacebookException error) {
				Log.e(LOGCAT_TAG, "Error de Facebook." + error);
			}
		});

		accessTokenTracker = new AccessTokenTracker() {
			@Override
			protected void onCurrentAccessTokenChanged(
					AccessToken oldAccessToken,
					AccessToken currentAccessToken) {
				// Set the access token using
				// currentAccessToken when it's loaded or set.
			}
		};
		// If the access token is available already assign it.
		accessToken = AccessToken.getCurrentAccessToken();

		profileTracker = new ProfileTracker() {
			@Override
			protected void onCurrentProfileChanged(
					Profile oldProfile,
					Profile currentProfile) {
				// App code
			}
		};

		facebookProfilePictureView = new ProfilePictureView(context);
		userNameView = new TextView(context);
	}
	
	public void setEnable(boolean enabled) {
		facebookSignInButton.setEnabled(enabled);
	}
	
	public void setFacebookSignInStatus(FacebookSignInStatus facebookSignInStatus) {
		this.facebookSignInStatus = facebookSignInStatus;
	}
	
	public void onResume(){
	}
	
	public void onPause(){
	}
	
	public void onDestroy(){
		accessTokenTracker.stopTracking();
		profileTracker.stopTracking();
	}
	
	public void onActivityResult(int requestCode,int responseCode,Intent intent){
		callbackManager.onActivityResult(requestCode, responseCode, intent);
	}

	@Override
	public void onError(FacebookException error) {
		Log.e(LOGCAT_TAG, error.getMessage(), error);
	}
	
	private void requestUserInfo() {
		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		// LLamada a la API para obtener informacion del usuario y defino un callback para manejar la respuesta.
		GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
			@Override
			public void onCompleted(JSONObject user, GraphResponse response) {
				// Si la respuesta fue exitosa.
				if (user != null) {
					// Seteo el id para la foto de perfil.
					facebookProfilePictureView.setProfileId(user.optString("id"));
					// Seteo el TextView para que muestre el nombre de usuario Facebook.
					userNameView.setText(user.optString("name"));

					Bundle profile = new Bundle();
					profile.putString(FACEBOOK_NAME, user.optString("name"));
					profile.putString(FACEBOOK_EMAIL, user.optString("email"));
					profile.putString(FACEBOOK_USERID, user.optString("id"));
					profile.putString(FACEBOOK_FIRST_NAME,user.optString("first_name"));
					profile.putString(FACEBOOK_LAST_NAME, user.optString("last_name"));
					facebookSignInStatus.onSuccessFacebookSignIn(profile);
				}
				if (response.getError() != null) {
					// TODO Manejo de errores.
				}
			}
		});
		Bundle parameters = new Bundle();
		parameters.putString("fields", "id,name,first_name,last_name,email");
		request.setParameters(parameters);
		request.executeAsync();
	}
	
	public interface FacebookSignInStatus {
		void onSuccessFacebookSignIn(Bundle profile);
	}
}