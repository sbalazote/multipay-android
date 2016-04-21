package com.multipay.android.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mercadopago.model.Customer;
import com.multipay.android.multipay.R;
import com.multipay.android.helpers.SessionManager;
import com.multipay.android.services.UsersService;
import com.multipay.android.utils.MultipayMenuItems;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUpActivity extends AppCompatActivity implements OnItemSelectedListener {
	private static String CLIENT_ID = "3108634673635661";
    private static String REDIRECT_URI = "http://multipay.ddns.net:8080/api/authorization";
    private static String OAUTH_URL ="https://auth.mercadolibre.com.ar/authorization";

	/*
	 * LOCALHOST_EMULATOR_BASE_URL
	 * 
	 * La URL 10.0.2.2 es la direccion que utiliza el emulador para referirse al
	 * 'localhost' de la maquina que hace de host. El puerto 5050 lo usa la
	 * aplicacion SharpProxy para poder acceder a la instancia local del IIS
	 * dentro del Visual Studio.
	 */ 
	private static final String LOCALHOST_EMULATOR_BASE_URL = "http://10.0.2.2:5000";
	/*
	 * LOCALHOST_DEVICE_BASE_URL
	 * 
	 * La URL es la direccion privada 'localhost' de la maquina que hace de
	 * host. El puerto 5050 lo usa la aplicacion SharpProxy para poder acceder a
	 * la instancia local del IIS dentro del Visual Studio.
	 */
	private static final String LOCALHOST_DEVICE_BASE_URL = "http://192.168.1.127:5000";
	private static final String DEBUG_BASE_URL = "http://multipay.ddns.net:8080";
	//private static final String RELEASE_BASE_URL = "";
	private final Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).serializeNulls().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();

	private EditText email;
	private EditText password;
	private EditText name;
	private EditText surname;
	private LinearLayout identificationLayout;
	private Spinner documentType;
	private EditText documentNumber;
	private LinearLayout addressLayout;
	private EditText streetName;
	private EditText streetNumber;
	private EditText zipCode;
	private LinearLayout phoneLayout;
	private EditText areaCode;
	private EditText phoneNumber;
	private boolean isSeller;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);
		
		email = (EditText) findViewById(R.id.email_input);
        password = (EditText) findViewById(R.id.password_input);
        name = (EditText) findViewById(R.id.name_input);
        surname = (EditText) findViewById(R.id.surname_input);
        
        identificationLayout = (LinearLayout) findViewById(R.id.identification_layout);
        documentType = (Spinner) findViewById(R.id.document_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.document_types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        documentType.setAdapter(adapter);
        documentType.setOnItemSelectedListener(this);
        documentNumber = (EditText) findViewById(R.id.document_number);
        
        addressLayout = (LinearLayout) findViewById(R.id.address_layout);
        streetName = (EditText) findViewById(R.id.street_name);
        streetNumber = (EditText) findViewById(R.id.street_number);
        zipCode = (EditText) findViewById(R.id.zip_code);
        
        phoneLayout = (LinearLayout) findViewById(R.id.phone_layout);
        areaCode = (EditText) findViewById(R.id.area_code);
        phoneNumber = (EditText) findViewById(R.id.phone_number);
        
        isSeller = SessionManager.getInstance(getApplicationContext()).getMode().equals("SELLER") ? true : false;
        
        if (isSeller) {
        	surname.setVisibility(View.GONE);
        	identificationLayout.setVisibility(View.GONE);
        	addressLayout.setVisibility(View.GONE);
        	phoneLayout.setVisibility(View.GONE);
        }
        
		
		//MercadoPago m = new MercadoPago("", SignUpActivity.this);
		//m.getPreferenceAttributes();
		//m.createSellerToken();
		//m.createToken();
		//m.authorize();
		
		//FragmentTransaction ft =  getFragmentManager().beginTransaction();
	    //ft.addToBackStack(null);
	 
	    // Create and show the dialog.
		//showDialog();
	    //AuthorizationFragment newFragment = new AuthorizationFragment();
	    //ft.add(R.id.checkout, newFragment);
	    //ft.commit();
	    //ft.show(newFragment);
	    //newFragment.getView(ft, "dialog");
		
		/*checkout = (WebView) findViewById(R.id.checkout);
		WebSettings webSettings = checkout.getSettings();
		webSettings.setBuiltInZoomControls(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setUseWideViewPort(true);
		checkout.loadUrl("https://auth.mercadolibre.com.ar/authorization?client_id=3108634673635661&response_type=code&platform_id=mp&redirect_uri=http://multipay.ddns.net:8080/api/authorization?email=pepe@gmail.com");
		
		Callback<PreferenceResponse> callback = new Callback<PreferenceResponse>() {
			@Override
			public void success(PreferenceResponse o, Response response) {
				checkout = (WebView) findViewById(R.id.checkout);
				WebSettings webSettings = checkout.getSettings();
				webSettings.setBuiltInZoomControls(true);
				webSettings.setLoadWithOverviewMode(true);
				webSettings.setUseWideViewPort(true);
				checkout.loadUrl(o.getSandbox_init_point());
			}

			@Override
			public void failure(RetrofitError arg0) {
				// TODO Auto-generated method stub
				
			}
	    };
	    String preferenceData = "{"
	    		+ "'items':"+
				"[{"+
					"'id': 'C�digo',"+
					"'title':'Multicolor kite',"+
					"'quantity':1,"+
					"'currency_id':'ARS',"+
					"'unit_price':10.0,"+
					"'category_id':'home',"+
					"'picture_url': 'http://i.imgur.com/4kBGegf.png'"+
				"}],"+
				"'marketplace_fee': 2.29"+
			"}";
	    JSONObject preferenceJSON = null;
	    TypedString in = new TypedString(preferenceData);*/
	   
	    //m.createPreference("APP_USR-8989156561599790-033007-5cf76210a8b1827641cdcab1e823765d__B_E__-73449193", in, callback);
	    //m.createPreference("APP_USR-3108634673635661-041402-4aab007397a20bdc7ee33815eecbf4fd__G_M__-73449193", in, callback);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.menu_activity_not_signed_in, menu);
    	    return super.onCreateOptionsMenu(menu);
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_about:
            	MultipayMenuItems.openAbout(getApplicationContext());
            	return true;
            case R.id.action_logout:
            	finish();
                return true;
            case R.id.action_help:
            	MultipayMenuItems.openHelp(getApplicationContext());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    // TODO consultar la db para alta nuevo usuario.
	public void signUp(View view) {
		Boolean cancel = false;
        String userEmail = this.email.getText().toString();
        String userName = this.name.getText().toString();
        String userPassword = this.password.getText().toString();
        View focusView = null;

        // Se validan los campos obligatorios
        if (TextUtils.isEmpty(userEmail)) {
                this.email.setError("El campo email es obligatorio.");
                focusView = this.email;
                cancel = true;
        }
        if (TextUtils.isEmpty(userName)) {
            this.name.setError("El campo nombre es obligatorio.");
            focusView = this.name;
            cancel = true;
        }
        if (TextUtils.isEmpty(userPassword)) {
            this.password.setError("El campo contrase�a es obligatorio.");
            focusView = this.password;
            cancel = true;
        }

		if (!cancel) {
			// TODO autenticarse en MP
			final Dialog auth_dialog = new Dialog(SignUpActivity.this);
			auth_dialog.setContentView(R.layout.auth_screen);

			WebView web = (WebView) auth_dialog.findViewById(R.id.authWebView);
			web.getSettings().setJavaScriptEnabled(true);
			web.loadUrl(OAUTH_URL + "?client_id=" + CLIENT_ID + "&response_type=code&platform_id=mp&redirect_uri="
					+ REDIRECT_URI);
			web.setWebViewClient(new WebViewClient() {

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

					if (url.contains("?code=") && authComplete != true) {
						Uri uri = Uri.parse(url);
						authCode = uri.getQueryParameter("code");
						Log.i("", "CODE : " + authCode);
						authComplete = true;
						resultIntent.putExtra("code", authCode);
						SignUpActivity.this.setResult(Activity.RESULT_OK, resultIntent);
						setResult(Activity.RESULT_CANCELED, resultIntent);

						//SharedPreferences.Editor edit = pref.edit();
						//edit.putString("Code", authCode);
						//edit.commit();
						auth_dialog.dismiss();
						//new TokenGet().execute();
						Toast.makeText(getApplicationContext(), "El AuthCode MP es: " + authCode, Toast.LENGTH_SHORT)
								.show();
						
						// TODO registrarse en la db
						Retrofit retrofit = new Retrofit.Builder()
								.baseUrl("https://api.mercadopago.com")
								.addConverterFactory(GsonConverterFactory.create())
								.build();

						UsersService service = retrofit.create(UsersService.class);

						Call<String> call = service.addSeller("dad@dad.com", "pepe", "2015-05-05", "sarasa", true, "ADADQEFCBR23423", null);
						call.enqueue(new retrofit2.Callback<String>() {
							@Override
							public void onResponse(Call<String> call, retrofit2.Response<String> response) {

							}

							@Override
							public void onFailure(Call<String> call, Throwable t) {

							}
						});
						//usersService.getUsers(callback);
					} else if (url.contains("error=access_denied")) {
						Log.i("", "ACCESS_DENIED_HERE");
						resultIntent.putExtra("code", authCode);
						authComplete = true;
						setResult(Activity.RESULT_CANCELED, resultIntent);
						Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_SHORT).show();

						auth_dialog.dismiss();
					}
				}
			});
			auth_dialog.show();
			auth_dialog.setTitle("Autorizar MultiPay");
			auth_dialog.setCancelable(true);

		} else {
			// Se devuelve el foco al campo que no fue completado.
			focusView.requestFocus();
		}
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
