package com.multipay.android.activities;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;

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
import com.mercadopago.util.LayoutUtil;
import com.multipay.android.helpers.SessionManager;
import com.multipay.android.R;
import com.multipay.android.tasks.LoadImage;
import com.multipay.android.utils.Constant;
import com.multipay.android.utils.GooglePlusSignInUtils;
import com.multipay.android.utils.MultipayMenuItems;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BuyerMenuActivity extends AppCompatActivity {

	private static int CARD_REQUEST_CODE = 666;

	private ImageView socialLogo;
	private ProfilePictureView facebookProfilePicture;
	private ImageView gPlusProfilePicture;
	private TextView profileUsername;
	private String signInType;
	private String name;
	GooglePlusSignInUtils gpLogin;
	private SessionManager session;

    protected List<String> mSupportedPaymentTypes = new ArrayList<String>(){{
        add("credit_card");
		/*add("debit_card");
		add("prepaid_card");*/
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
				new LoadImage(gPlusProfilePicture).execute(GooglePlusSignInUtils.googlePlusProfilePhotoUrl);
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
    public void onBackPressed() {}

	private void makeMeRequest() {
		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
					@Override
					public void onCompleted(JSONObject user, GraphResponse response) {
						if (user != null) {
							// Set the id for the ProfilePictureView view that in turn displays the profile picture.
							facebookProfilePicture.setProfileId(user.optString("id"));
						}

						if (response.getError() != null) {
							// Handle errors, will do so later.
						}
					}
				}
		);
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
				addCardActivityIntent.putExtra("merchantPublicKey", Constant.MERCHANT_PUBLIC_KEY);
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
								.setPublicKey(Constant.MERCHANT_PUBLIC_KEY)
								.setSupportedPaymentTypes(mSupportedPaymentTypes)
								.startPaymentMethodsActivity();
					}
				}
			}
		} else if (requestCode == MercadoPago.CONGRATS_REQUEST_CODE) {

			LayoutUtil.showRegularLayout(this);
		}
	}

	public void paymentHistory(View view) {
		Intent paymentHistoryActivityIntent = new Intent(this, PaymentHistoryActivity.class);
    	startActivity(paymentHistoryActivityIntent);
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