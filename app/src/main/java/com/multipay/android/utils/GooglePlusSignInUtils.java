package com.multipay.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.multipay.android.activities.SignInActivity;
import com.multipay.android.multipay.R;

public class GooglePlusSignInUtils implements ConnectionCallbacks, OnConnectionFailedListener, OnClickListener {

	private static final int REQUEST_CODE_SIGN_IN = 0;
	private static final String LOGCAT_TAG = "GooglePlusSignInUtils";
	public static final String GOOGLEPLUS_NAME = "name";
	public static final String GOOGLEPLUS_EMAIL = "email";
	public static final String GOOGLEPLUS_FIRST_NAME = "first_name";
	public static final String GOOGLEPLUS_LAST_NAME = "last_name";
	public static final String GOOGLEPLUS_USERID = "userid";
	// Tamanio de la foto de perfil en pixeles.
	private static final int PROFILE_PIC_SIZE = 400;
	// Cliente para interactuar con la API de Google.
	private static GoogleApiClient mGoogleApiClient;
	private GoogleSignInAccount googleSignInAccount;
	private Context context;
	private Activity activity;
	private GooglePlusSignInStatus GooglePlusSignInStatus;
	private SignInButton GooglePlusSignInButton;
	public static String GooglePlusProfilePhotoUrl;
	private TextView userNameView;
	private ImageView imgProfilePic;

	/**
	 * A flag indicating that a PendingIntent is in progress and prevents us
	 * from starting further intents.
	 */
	private boolean mIntentInProgress;

	private boolean mSignInClicked;

	private ConnectionResult mConnectionResult;

	private String personName;

	public GooglePlusSignInUtils(Activity activity) {
		this.context = activity.getApplicationContext();
		this.activity = activity;
		mSignInClicked = false;
		userNameView = new TextView(context);
		imgProfilePic = new ImageView(context);
		GooglePlusSignInButton = (SignInButton) activity.findViewById(R.id.gplus_sign_in_button);
		GooglePlusSignInButton.setOnClickListener(this);

		// Configure sign-in to request the user's ID, email address, and basic profile. ID and basic profile are included in DEFAULT_SIGN_IN.
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestProfile()
				.requestEmail()
				.build();

		// Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
		mGoogleApiClient = new GoogleApiClient.Builder(activity)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(Auth.GOOGLE_SIGN_IN_API, gso)
				.addApi(Plus.API)
				.build();

		signInWithGplus();
	}

	public TextView getUserNameView() {
		return userNameView;
	}

	public void setUserNameView(TextView userNameView) {
		this.userNameView = userNameView;
	}

	public String getPersonName() {
		return personName;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
	}

	public void setGooglePlusSignInStatus(GooglePlusSignInStatus GooglePlusSignInStatus) {
		this.GooglePlusSignInStatus = GooglePlusSignInStatus;
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!result.hasResolution()) {
			//Activity activity = (Activity) context;
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), activity, 0).show();
			return;
		}
		if (!mIntentInProgress) {
			// Store the ConnectionResult for later usage
			mConnectionResult = result;
			// El usuario intento loguearse entonces se procura resolver todos los errores hasta el logueo, o hasta que se cancele.
			if (mSignInClicked) {
				resolveSignInError();
			}
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		SignInActivity.mConnectionProgressDialog.dismiss();
		mSignInClicked = false;
		//Toast.makeText(context, "conectado!", Toast.LENGTH_LONG).show();

		// Get user's information
		getProfileInformation();
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		mGoogleApiClient.connect(GoogleApiClient.SIGN_IN_MODE_OPTIONAL);
	}

	/**
	 * Method to resolve any signin errors
	 * */
	private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult(activity, REQUEST_CODE_SIGN_IN);
			} catch (SendIntentException e) {
				mIntentInProgress = false;
				mGoogleApiClient.connect(GoogleApiClient.SIGN_IN_MODE_OPTIONAL);
			}
		}
	}

	/**
	 * Sign-in into google
	 * */
	public void signInWithGplus() {
		if (!mGoogleApiClient.isConnected()) {
			// We should always have a connection result ready to resolve,
			// so we can start that process.
			if (mConnectionResult != null) {
				resolveSignInError();
			} else {
				// If we don't have one though, we can start connect in
				// order to retrieve one.
				mGoogleApiClient.connect(GoogleApiClient.SIGN_IN_MODE_OPTIONAL);
			}
		}
		if (!mGoogleApiClient.isConnecting()) {
			mSignInClicked = true;
			resolveSignInError();
		}
	}

	/**
	 * Sign-out from google
	 * */
	public static void signOutFromGplus() {
		Auth.GoogleSignInApi.signOut(mGoogleApiClient);
		if (mGoogleApiClient.isConnected()) {
			// Clear the default account in order to allow the user
			// to potentially choose a different account from the
			// account chooser.
			//Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);

			// Disconnect from Google Play Services, then reconnect in
			// order to restart the process from scratch.
			mGoogleApiClient.disconnect();
			Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient);
			mGoogleApiClient.connect(GoogleApiClient.SIGN_IN_MODE_OPTIONAL);
		}
	}

	/**
	 * Fetching user's information name, email, profile pic
	 * */
	private void getProfileInformation() {
		try {
			if (googleSignInAccount != null) {
				final String personName = googleSignInAccount.getDisplayName();
				GooglePlusProfilePhotoUrl = googleSignInAccount.getPhotoUrl().getPath();
				String personGooglePlusProfile = googleSignInAccount.getPhotoUrl().getPath();
				final String email = googleSignInAccount.getEmail();

				Plus.PeopleApi.load(mGoogleApiClient, "me").setResultCallback(new ResultCallback<People.LoadPeopleResult>() {
					@Override
					public void onResult(@NonNull People.LoadPeopleResult loadPeopleResult) {
						if (loadPeopleResult.getStatus().isSuccess()) {
							Person person = loadPeopleResult.getPersonBuffer().get(0);
							String firstName = person.getName().getGivenName();
							String lastName = person.getName().getFamilyName();
							Bundle profile = new Bundle();
							profile.putString(GOOGLEPLUS_NAME, personName);
							profile.putString(GOOGLEPLUS_EMAIL, email);
							profile.putString(GOOGLEPLUS_USERID, googleSignInAccount.getId());
							profile.putString(GOOGLEPLUS_FIRST_NAME, firstName);
							profile.putString(GOOGLEPLUS_LAST_NAME, lastName);
							// by default the profile url gives 50x50 px image only
							// we can replace the value with whatever dimension we want by
							// replacing sz=X
							GooglePlusProfilePhotoUrl = GooglePlusProfilePhotoUrl.substring(0, GooglePlusProfilePhotoUrl.length() - 2) + PROFILE_PIC_SIZE;

							//new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);
							userNameView.setText(personName);
							GooglePlusSignInStatus.onSuccessGooglePlusSignIn(profile);
						}
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onActivityResult(int requestCode, int responseCode, Intent intent) {
		// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
		if (requestCode == REQUEST_CODE_SIGN_IN) {
			if (responseCode == Activity.RESULT_OK) {
				SignInActivity.mConnectionProgressDialog.dismiss();
			}
			GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
			if (result.isSuccess()) {
				googleSignInAccount = result.getSignInAccount();
				getProfileInformation();
			}
		}

		if (requestCode == REQUEST_CODE_SIGN_IN) {
			if (responseCode != -1) {
				mSignInClicked = false;
			}

			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect(GoogleApiClient.SIGN_IN_MODE_OPTIONAL);
			}
		}
	}

	public void connect() {
		if (mSignInClicked) {
			mGoogleApiClient.connect(GoogleApiClient.SIGN_IN_MODE_OPTIONAL);
		}
	}

	public void disconnect() {
		mGoogleApiClient.disconnect();
	}

	@Override
	public void onClick(View v) {
		SignInActivity.mConnectionProgressDialog.show();
		mSignInClicked = true;
		Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
		activity.startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
	}

	public interface GooglePlusSignInStatus {
		void onSuccessGooglePlusSignIn(Bundle profile);
	}
}