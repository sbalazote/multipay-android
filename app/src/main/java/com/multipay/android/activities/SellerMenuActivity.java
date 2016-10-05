package com.multipay.android.activities;

import java.io.InputStream;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.ProfilePictureView;
import com.multipay.android.R;
import com.multipay.android.helpers.SessionManager;
import com.multipay.android.utils.Constant;
import com.multipay.android.utils.GooglePlusSignInUtils;
import com.multipay.android.utils.MultipayMenuItems;

import org.json.JSONObject;

public class SellerMenuActivity extends AppCompatActivity {

	private ImageView socialLogo;
	private ProfilePictureView facebookProfilePicture;
	private ImageView gPlusProfilePicture;
	private TextView profileUsername;
	private String signInType;
	private String name;
	private String email;
	GooglePlusSignInUtils gpLogin;
	private WebView OAuthMPWebView;
	private SessionManager session;
	private LinearLayout sellerMenuOptions;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_seller_menu);
		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);
		
    	session = SessionManager.getInstance(this.getApplicationContext());
    	signInType = session.getUserSignInType();
    	name = session.getUsername();
		email = session.getUsernameEMail();
		
		socialLogo = (ImageView) findViewById(R.id.social_logo_imageview);
		
		// Find the user's profile picture FB custom view
		facebookProfilePicture = (ProfilePictureView) findViewById(R.id.facebook_profile_pictureview);
		facebookProfilePicture.setCropped(true);
		
		gPlusProfilePicture = (ImageView) findViewById(R.id.gplus_profile_pictureview);

		// Find the user's name view
		profileUsername = (TextView) findViewById(R.id.profile_username);

		sellerMenuOptions = (LinearLayout) findViewById(R.id.seller_menu_options);

		boolean firstUse = getIntent().getBooleanExtra("com.multipay.android.FirstUse", false);

		if (firstUse) {
			sellerMenuOptions.setVisibility(View.GONE);

			OAuthMPWebView = (WebView) findViewById(R.id.OAuthMP_webView);
			OAuthMPWebView.setWebViewClient(new MyWebViewClient());
			OAuthMPWebView.setVisibility(View.VISIBLE);
			OAuthMPRequest();
		}

		// Get the user's data.
		switch (signInType) {
			case "FACEBOOK":
				socialLogo.setImageResource(R.drawable.facebook_logo__blue);
				facebookProfilePicture.setVisibility(View.VISIBLE);
				makeMeRequest();
				gPlusProfilePicture.setVisibility(View.GONE);
				profileUsername.setText(name);
				break;
			case "GOOGLE":
				socialLogo.setImageResource(R.drawable.gplus_logo);
				facebookProfilePicture.setVisibility(View.GONE);
				gPlusProfilePicture.setVisibility(View.VISIBLE);
				new LoadProfileImage(gPlusProfilePicture).execute(GooglePlusSignInUtils.googlePlusProfilePhotoUrl);
				profileUsername.setText(name);
				break;
			default:
				socialLogo.setImageResource(R.drawable.ic_logo_multipay);
				facebookProfilePicture.setVisibility(View.GONE);
				gPlusProfilePicture.setVisibility(View.VISIBLE);
				gPlusProfilePicture.setImageResource(R.drawable.generic_user);
				profileUsername.setText(name);
				break;
		}
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.menu_activity_seller_signed_in, menu);
    	    return super.onCreateOptionsMenu(menu);
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_about:
            	MultipayMenuItems.openAbout(getApplicationContext());
            	return true;
            case R.id.action_make_payment:
                makePayment(item.getActionView());
                return true;
            case R.id.action_logout:
            	session.logoutUser();
            	finish();
                return true;
            case R.id.action_help:
            	MultipayMenuItems.openHelp(getApplicationContext());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
		if (OAuthMPWebView.isShown()) {
			OAuthMPWebView.setVisibility(View.GONE);
		}
    }
    
    /**
     * Background Async task to load user profile picture from url
     * */
    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
 
        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }
 
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }
 
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
    
    private void makeMeRequest() {
		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
			@Override
			public void onCompleted(JSONObject user, GraphResponse response) {
				// Si la respuesta fue exitosa.
				if (user != null) {
					// Seteo el id para la foto de perfil.
					facebookProfilePicture.setProfileId(user.optString("id"));
					// Seteo el TextView para que muestre el nombre de usuario Facebook.
					profileUsername.setText(user.optString("name"));
				}
				if (response.getError() != null) {
					// TODO Manejo de errores.
				}
			}
		});
		request.executeAsync();
	}

	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			//You can add some custom functionality here
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			//You can add some custom functionality here
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
									String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			//You can add some custom functionality here
		}
	}

	private void OAuthMPRequest() {
		WebSettings webSettings = OAuthMPWebView.getSettings();
		webSettings.setBuiltInZoomControls(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setUseWideViewPort(true);
		OAuthMPWebView.loadUrl(Constant.OAUTH_URL + "?client_id=" + Constant.CLIENT_ID + "&response_type=code&platform_id=mp&redirect_uri=" + Constant.MERCHANT_BASE_URL + Constant.MERCHANT_REDIRECT_URI + "?email=" + email);
	}

	public void paymentHistory(View view) {
		Intent paymentHistoryIntent = new Intent(this, PaymentHistoryActivity.class);
    	startActivity(paymentHistoryIntent);
	}

	public void makePayment(View view) {
		Intent makePaymentIntent = new Intent(this, MakePaymentActivity.class);
    	startActivity(makePaymentIntent);
	}
}