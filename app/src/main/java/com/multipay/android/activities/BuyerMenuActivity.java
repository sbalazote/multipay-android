package com.multipay.android.activities;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.ProfilePictureView;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.JsonUtil;
import com.multipay.android.helpers.SessionManager;
import com.multipay.android.multipay.R;
import com.multipay.android.tasks.LoadImage;
import com.multipay.android.utils.GooglePlusSignInUtils;
import com.multipay.android.utils.MultipayMenuItems;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BuyerMenuActivity extends AppCompatActivity {

	private static String MERCHANT_PUBLIC_KEY = "TEST-76fd8ba9-ddda-499f-8def-bd390d2d06b1";
	private static int CARD_REQUEST_CODE = 666;

	private ImageView socialLogo;
	private ProfilePictureView facebookProfilePicture;
	private ImageView gPlusProfilePicture;
	private TextView profileUsername;
	private String signInType;
	private String name;
	GooglePlusSignInUtils gpLogin;
	private WebView current_promos;
	private SessionManager session;

    protected List<String> mSupportedPaymentTypes = new ArrayList<String>(){{
        add("credit_card");
		add("debit_card");
		add("prepaid_card");
    }};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FacebookSdk.sdkInitialize(getApplicationContext());
		setContentView(R.layout.activity_buyer_menu);
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
		/*WebSettings webSettings = current_promos.getSettings();
		webSettings.setBuiltInZoomControls(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setUseWideViewPort(true);
		current_promos.loadUrl("https://www.mercadopago.com/mla/credit_card_promos.htm");*/
		
		// Get the user's data.
		if (signInType.compareTo("FACEBOOK") == 0) {
			socialLogo.setImageResource(R.drawable.facebook_logo__blue);
			facebookProfilePicture.setVisibility(View.VISIBLE);
			makeMeRequest();
			gPlusProfilePicture.setVisibility(View.GONE);
			profileUsername.setText(name);
		} else if (signInType.compareTo("GOOGLE") == 0) {
			socialLogo.setImageResource(R.drawable.gplus_logo);
			facebookProfilePicture.setVisibility(View.GONE);
			gPlusProfilePicture.setVisibility(View.VISIBLE);
			new LoadImage(gPlusProfilePicture).execute(GooglePlusSignInUtils.GooglePlusProfilePhotoUrl);
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
    	getMenuInflater().inflate(R.menu.menu_activity_buyer_signed_in, menu);
    	    return super.onCreateOptionsMenu(menu);
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
            	MultipayMenuItems.openAbout(getApplicationContext());
            	return true;
            case R.id.action_make_payment:
                makePayment(item.getActionView());
                return true;
            case R.id.action_logout:
            	finish();
            	session.logoutUser();
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
		if (current_promos.isShown()) {
			current_promos.setVisibility(View.GONE);
		}
    }
    
    private void makeMeRequest() {
		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		// LLamada a la API para obtener informacion del usuario y defino un callback para manejar la respuesta.
		GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {

					@Override
					public void onCompleted(JSONObject user, GraphResponse response) {
						// If the response is successful
						if (user != null) {
							// Set the id for the ProfilePictureView
							// view that in turn displays the profile picture.
							facebookProfilePicture.setProfileId(user.optString("id"));
							// Set the Textview's text to the user's name.
							//userNameView.setText(user.getName());
						}

						if (response.getError() != null) {
							// Handle errors, will do so later.
						}
					}
				}
		);
		/*Session session = Session.getActiveSession();
		// Make an API call to get user data and define a 
		// new callback to handle the response.
		Request request = Request.newMeRequest(session,
				new LoginClient.Request.GraphUserCallback() {
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
		request.executeAsync();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == MercadoPago.PAYMENT_METHODS_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {

				// Set payment method
				PaymentMethod paymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);

				// TODO: here call card activity
				Intent addCardActivityIntent = new Intent(this, AddCardActivity.class);
				addCardActivityIntent.putExtra("merchantPublicKey", MERCHANT_PUBLIC_KEY);
				addCardActivityIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
				startActivityForResult(addCardActivityIntent, CARD_REQUEST_CODE);
			} else {

				if ((data != null) && (data.getStringExtra("apiException") != null)) {
					Toast.makeText(getApplicationContext(), data.getStringExtra("apiException"), Toast.LENGTH_LONG).show();
				}
			}
		} else if (requestCode == CARD_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {

				// Create payment
				/*ExamplesUtils.createPayment(this, data.getStringExtra("token"),
						1, null, JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class), null);*/

			} else {

				if (data != null) {
					if (data.getStringExtra("apiException") != null) {

						Toast.makeText(getApplicationContext(), data.getStringExtra("apiException"), Toast.LENGTH_LONG).show();

					} else if (data.getBooleanExtra("backButtonPressed", false)) {

						new MercadoPago.StartActivityBuilder()
								.setActivity(this)
								.setPublicKey(MERCHANT_PUBLIC_KEY)
								.setSupportedPaymentTypes(mSupportedPaymentTypes)
								.startPaymentMethodsActivity();
					}
				}
			}
		} else if (requestCode == MercadoPago.CONGRATS_REQUEST_CODE) {

			//LayoutUtil.showRegularLayout(this);
		}
	}

	public void paymentHistory(View view) {
		Intent paymentHistoryActivityIntent = new Intent(this, PaymentHistoryActivity.class);
    	startActivity(paymentHistoryActivityIntent);
	}
	
	public void viewPromos(View view) {
		WebSettings webSettings = current_promos.getSettings();
		webSettings.setBuiltInZoomControls(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setUseWideViewPort(true);
		current_promos.loadUrl("https://www.mercadopago.com/mla/credit_card_promos.htm");
		current_promos.setVisibility(View.VISIBLE);
	}
	
	public void makePayment(View view) {
		Intent makePaymentActivityIntent = new Intent(this, MakePaymentActivity.class);
    	startActivity(makePaymentActivityIntent);
	}

	public void addCard(View view) {
		new MercadoPago.StartActivityBuilder()
				.setActivity(this)
				.setPublicKey("TEST-76fd8ba9-ddda-499f-8def-bd390d2d06b1")
				.setSupportedPaymentTypes(mSupportedPaymentTypes)
				.startPaymentMethodsActivity();
	}
}
