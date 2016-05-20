package com.multipay.android.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.multipay.android.helpers.SessionManager;
import com.multipay.android.R;
import com.multipay.android.tasks.LoadImages;
import com.multipay.android.utils.Constant;
import com.multipay.android.utils.ItemCategories;
import com.multipay.android.utils.ItemCategories.ItemCategory;
import com.multipay.android.utils.MultipayMenuItems;
import com.multipay.android.utils.PaymentMethods;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MakePaymentActivity extends AppCompatActivity implements OnItemSelectedListener {
	private Spinner paymentMethodsSpinner;
	private Spinner itemCategoriesSpinner;
	private Spinner maxInstallmentsSpinner;
	private String[] methodsArray;
	private String[] thumbnailsArray;
	private Bitmap[] thumbnails;
	private SessionManager session;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_make_payment);
		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);
		// Get a support ActionBar corresponding to this toolbar
		ActionBar ab = getSupportActionBar();
		// Enable the Up button
		ab.setDisplayHomeAsUpEnabled(true);
		session = SessionManager.getInstance(this.getApplicationContext());
		maxInstallmentsSpinner = (Spinner)findViewById(R.id.max_installments_spinner);
		paymentMethodsSpinner = (Spinner)findViewById(R.id.payment_methods_spinner);
		itemCategoriesSpinner = (Spinner)findViewById(R.id.item_category_spinner);
		Iterator<ItemCategory> it = ItemCategories.getInstance().getItems().iterator();
		List<String> items = new ArrayList<String>();
		while (it.hasNext()) {
			ItemCategory item = it.next();
			items.add(item.getDescription());
		}
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		itemCategoriesSpinner.setAdapter(adapter1);
		itemCategoriesSpinner.setOnItemSelectedListener(this);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.installments_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        maxInstallmentsSpinner.setAdapter(adapter);
        maxInstallmentsSpinner.setOnItemSelectedListener(this);
		
		int methodsArraySize = PaymentMethods.getInstance().getPaymentMethodNames().size();
		methodsArray = new String[methodsArraySize];
		methodsArray = PaymentMethods.getInstance().getPaymentMethodNames().toArray(methodsArray);
		int thumbnailsArraySize = PaymentMethods.getInstance().getSecureThumbnails().size();
		thumbnailsArray = new String[thumbnailsArraySize];
		thumbnailsArray = PaymentMethods.getInstance().getSecureThumbnails().toArray(thumbnailsArray);
		try {
			thumbnails = new LoadImages().execute(thumbnailsArray).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		paymentMethodsSpinner.setAdapter(new MyAdapter(MakePaymentActivity.this, R.layout.spinner_row, methodsArray));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_activity_seller_signed_in, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_about:
				MultipayMenuItems.openAbout(getApplicationContext());
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

	private void authorizeMultipay() {
		final Dialog auth_dialog = new Dialog(MakePaymentActivity.this);
		auth_dialog.setContentView(R.layout.auth_screen);

		SessionManager sessionManager = SessionManager.getInstance(this.getApplicationContext());

		WebView web = (WebView) auth_dialog.findViewById(R.id.authWebView);
		web.getSettings().setJavaScriptEnabled(true);
		web.loadUrl(Constant.OAUTH_URL + "?client_id=" + Constant.CLIENT_ID + "&response_type=code&platform_id=mp&redirect_uri=" + Constant.MERCHANT_BASE_URL + Constant.MERCHANT_REDIRECT_URI + "?email=" + sessionManager.getUsernameEMail());
		/*web.setWebViewClient(new WebViewClient() {

			boolean authComplete = false;
			Intent resultIntent = new Intent();

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}

			String authCode;

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);

				if (url.contains("?code=") && !authComplete) {
					Uri uri = Uri.parse(url);
					authCode = uri.getQueryParameter("code");
					Log.i("", "CODE : " + authCode);
					authComplete = true;
					resultIntent.putExtra("code", authCode);
					MakePaymentActivity.this.setResult(Activity.RESULT_OK, resultIntent);
					setResult(Activity.RESULT_CANCELED, resultIntent);

					auth_dialog.dismiss();
					Toast.makeText(getApplicationContext(), "El AuthCode MP es: " + authCode, Toast.LENGTH_SHORT)
							.show();

				} else if (url.contains("error=access_denied")) {
					Log.i("", "ACCESS_DENIED_HERE");
					resultIntent.putExtra("code", authCode);
					authComplete = true;
					setResult(Activity.RESULT_CANCELED, resultIntent);
					Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_SHORT).show();

					auth_dialog.dismiss();
				}
			}
		});*/
		auth_dialog.show();
		auth_dialog.setTitle("Autorizar MultiPay");
		auth_dialog.setCancelable(true);
	}
	
	public class MyAdapter extends ArrayAdapter<String>{
		 
        public MyAdapter(Context context, int textViewResourceId,   String[] objects) {
            super(context, textViewResourceId, objects);
        }
 
        @Override
        public View getDropDownView(int position, View convertView,ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }
 
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }
 
        public View getCustomView(int position, View convertView, ViewGroup parent) {
 
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_row, parent, false);
            TextView label = (TextView)row.findViewById(R.id.name);
            label.setText(methodsArray[position]);
 
            ImageView icon=(ImageView)row.findViewById(R.id.image);
            icon.setImageBitmap(thumbnails[position]);
 
            return row;
            }
        }
	
	public void sendPaymentLink(View view) {
		authorizeMultipay();
		Intent paymentLinkBeamActivityIntent = new Intent(this, PaymentLinkBeamActivity.class);
		startActivity(paymentLinkBeamActivityIntent);
	}

	public void onItemSelected(AdapterView<?> parent, View view,
            int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}