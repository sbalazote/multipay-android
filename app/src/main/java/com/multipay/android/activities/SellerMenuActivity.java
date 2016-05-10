package com.multipay.android.activities;

import java.io.InputStream;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.ProfilePictureView;
import com.multipay.android.multipay.R;
import com.multipay.android.helpers.SessionManager;
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
	GooglePlusSignInUtils gpLogin;
	private WebView current_promos;
	private SessionManager session;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_seller_menu);
		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);
		
    	session = SessionManager.getInstance(this.getApplicationContext());
    	signInType = session.getUserSignInType();
    	name = session.getUsername();
		
		socialLogo = (ImageView) findViewById(R.id.social_logo_imageview);
		
		// Find the user's profile picture FB custom view
		facebookProfilePicture = (ProfilePictureView) findViewById(R.id.facebook_profile_pictureview);
		facebookProfilePicture.setCropped(true);
		
		gPlusProfilePicture = (ImageView) findViewById(R.id.gplus_profile_pictureview);

		// Find the user's name view
		profileUsername = (TextView) findViewById(R.id.profile_username);
		
		current_promos = (WebView) findViewById(R.id.current_promos_webView);
		WebSettings webSettings = current_promos.getSettings();
		webSettings.setBuiltInZoomControls(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setUseWideViewPort(true);
		current_promos.loadUrl("https://www.mercadopago.com/mla/credit_card_promos.htm");
		current_promos.setVisibility(View.GONE);

		// Get the user's data.
		if (signInType.equals("FACEBOOK")) {
			socialLogo.setImageResource(R.drawable.facebook_logo__blue);
			facebookProfilePicture.setVisibility(View.VISIBLE);
			makeMeRequest();
			gPlusProfilePicture.setVisibility(View.GONE);
			profileUsername.setText(name);
		} else if (signInType.equals("GOOGLE")) {
			socialLogo.setImageResource(R.drawable.gplus_logo);
			facebookProfilePicture.setVisibility(View.GONE);
			gPlusProfilePicture.setVisibility(View.VISIBLE);
			new LoadProfileImage(gPlusProfilePicture).execute(GooglePlusSignInUtils.googlePlusProfilePhotoUrl);
			profileUsername.setText(name);
		} else {
			socialLogo.setImageResource(R.drawable.ic_logo_multipay);
			facebookProfilePicture.setVisibility(View.GONE);
			gPlusProfilePicture.setVisibility(View.VISIBLE);
			gPlusProfilePicture.setImageResource(R.drawable.generic_user);
			profileUsername.setText(name);
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
		if (current_promos.isShown()) {
			current_promos.setVisibility(View.GONE);
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
            //bitmap = result;
        }
    }
    
    private void makeMeRequest() {
		/*Session session = Session.getActiveSession();
		// Make an API call to get user data and define a 
		// new callback to handle the response.
		Request request = Request.newMeRequest(session, 
				new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser user, Response response) {
				// If the response is successful
				if (user != null) {
					// Set the id for the ProfilePictureView
					// view that in turn displays the profile picture.
					facebookProfilePicture.setProfileId(user.getId());
					// Set the Textview's text to the user's name.
					//userNameView.setText(user.getName());
				}

				if (response.getError() != null) {
					// Handle errors, will do so later.
				}
			}
		});*/
		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		// LLamada a la API para obtener informacion del usuario y defino un callback para manejar la respuesta.
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

	public void paymentHistory(View view) {
		Intent paymentHistoryIntent = new Intent(this, PaymentHistoryActivity.class);
    	startActivity(paymentHistoryIntent);
	}
	
	public void viewPromos(View view) {
		current_promos.setVisibility(View.VISIBLE);
	}
	
	public void makePayment(View view) {
		Intent makePaymentIntent = new Intent(this, MakePaymentActivity.class);
    	startActivity(makePaymentIntent);
	}
}
