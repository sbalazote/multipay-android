package com.multipay.android.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.multipay.android.dtos.LoginResponseDTO;
import com.multipay.android.dtos.RegistrationRequestDTO;
import com.multipay.android.helpers.SessionManager;
import com.multipay.android.R;
import com.multipay.android.services.RegistrationService;
import com.multipay.android.utils.Constant;
import com.multipay.android.utils.Device;
import com.multipay.android.utils.MultipayMenuItems;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUpActivity extends AppCompatActivity implements OnItemSelectedListener {

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
	/*private LinearLayout phoneLayout;
	private EditText areaCode;
	private EditText phoneNumber;*/
	private boolean isSeller;
	private RegistrationService registrationService;

	private ProgressDialog mConnectionProgressDialog;

	private SessionManager session;
	private String mobileId;

	private LoginResponseDTO loginResponse;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

		session = SessionManager.getInstance(this.getApplicationContext());
		mobileId = Device.getDevice(getApplicationContext()).getMACAddress();

		HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
		logging.setLevel(HttpLoggingInterceptor.Level.BODY);
		OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
		httpClient.addInterceptor(logging);

		Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).serializeNulls().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(Constant.MERCHANT_BASE_URL)
				.addConverterFactory(GsonConverterFactory.create(gson))
				.client(httpClient.build())
				.build();
		registrationService = retrofit.create(RegistrationService.class);
		
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
        
        /*phoneLayout = (LinearLayout) findViewById(R.id.phone_layout);
        areaCode = (EditText) findViewById(R.id.area_code);
        phoneNumber = (EditText) findViewById(R.id.phone_number);*/
        
        isSeller = SessionManager.getInstance(getApplicationContext()).getMode().equals("SELLER");
        
        if (isSeller) {
        	surname.setVisibility(View.GONE);
        	identificationLayout.setVisibility(View.GONE);
        	addressLayout.setVisibility(View.GONE);
        	//phoneLayout.setVisibility(View.GONE);
        }

		mConnectionProgressDialog = new ProgressDialog(this);
		mConnectionProgressDialog.setMessage("Registrandose...\nEspere un momento, por favor.");
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

	private void setLoginResponse(LoginResponseDTO loginResponse) {
		this.loginResponse = loginResponse;
	}

	public void signUp(View view) {
		Boolean cancel = false;
        String userEmail = this.email.getText().toString();
        String userName = this.name.getText().toString();
        String userPassword = this.password.getText().toString();
        View focusView = null;

        // Se validan los campos.
        if (TextUtils.isEmpty(userEmail)) {
			this.email.setError("El campo email es obligatorio.");
			focusView = this.email;
			cancel = true;
        }
		if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
			this.email.setError("El campo email no es valido.");
			focusView = this.email;
			cancel = true;
		}
        if (TextUtils.isEmpty(userName)) {
            this.name.setError("El campo nombre es obligatorio.");
            focusView = this.name;
            cancel = true;
        }
        if (TextUtils.isEmpty(userPassword)) {
            this.password.setError("El campo contraseniaa es obligatorio.");
            focusView = this.password;
            cancel = true;
        }

		if (!cancel) {
			RegistrationRequestDTO registrationRequestDTO = new RegistrationRequestDTO();
			registrationRequestDTO.setMobileId(this.mobileId);
			registrationRequestDTO.setRegistrationId(session.retrieveRegistrationId());
			registrationRequestDTO.setEmail(userEmail);
			registrationRequestDTO.setName(userName);
			registrationRequestDTO.setLastName(surname.getText().toString());
			registrationRequestDTO.setPassword(userPassword);
			registrationRequestDTO.setIdentificationType(documentType.getSelectedItem().toString());
			registrationRequestDTO.setIdentificationNumber(documentNumber.getText().toString());
			registrationRequestDTO.setAddressName(streetName.getText().toString());
			if (!streetNumber.getText().toString().isEmpty()) {
				registrationRequestDTO.setAddressNumber(Integer.parseInt(streetNumber.getText().toString()));
			}
			if (!zipCode.getText().toString().isEmpty()) {
				registrationRequestDTO.setAddressZipCode(zipCode.getText().toString());
			}
			/*if (!areaCode.getText().toString().isEmpty()) {
				registrationRequestDTO.setPhoneAreaCode(Integer.parseInt(areaCode.getText().toString()));
			}
			if (!phoneNumber.getText().toString().isEmpty()) {
				registrationRequestDTO.setPhoneNumber(phoneNumber.getText().toString());
			}*/
			registrationRequestDTO.setSeller(isSeller);
			mConnectionProgressDialog.show();
			Call<LoginResponseDTO> call = registrationService.attemptNativeRegistration(registrationRequestDTO);
			call.enqueue(new Callback<LoginResponseDTO>() {
				@Override
				public void onResponse(Call<LoginResponseDTO> call, Response<LoginResponseDTO> response) {
					mConnectionProgressDialog.dismiss();
					setLoginResponse(response.body());
					Boolean valid;
					if (loginResponse != null) {
						valid = loginResponse.getValid();
						if (valid) {
							session.createSignInSession(loginResponse.getUserName(), loginResponse.getUserEmail(), "NATIVE");
							if (session.getMode().equals("SELLER")) {
								Intent sellerMenuActivityIntent = new Intent(getApplicationContext(), SellerMenuActivity.class);
								sellerMenuActivityIntent.putExtra("com.multipay.android.FirstUse", true);
								sellerMenuActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(sellerMenuActivityIntent);
							} else {
								Intent buyerMenuActivityIntent = new Intent(getApplicationContext(), BuyerMenuActivity.class);
								buyerMenuActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(buyerMenuActivityIntent);
							}
							finish();
						} else {
							Toast.makeText(getApplicationContext(), loginResponse.getMessage(), Toast.LENGTH_LONG).show();
							email.setText("");
							password.setText("");
							email.requestFocus();
						}
					} else {
						Toast.makeText(getApplicationContext(), "Multipay no ha podido registrar. Intente nuevamente.", Toast.LENGTH_LONG).show();
					}
				}

				@Override
				public void onFailure(Call<LoginResponseDTO> call, Throwable t) {
					mConnectionProgressDialog.dismiss();
					Toast.makeText(getApplicationContext(), "Multipay no ha podido registrar. Intente nuevamente.", Toast.LENGTH_LONG).show();
				}
			});
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