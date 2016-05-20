package com.multipay.android.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.multipay.android.dtos.LoginRequestDTO;
import com.multipay.android.dtos.LoginResponseDTO;
import com.multipay.android.helpers.SessionManager;
import com.multipay.android.R;
import com.multipay.android.services.LoginService;
import com.multipay.android.utils.Constant;
import com.multipay.android.utils.Device;
import com.multipay.android.utils.FacebookSignInUtils;
import com.multipay.android.utils.FacebookSignInUtils.FacebookSignInStatus;
import com.multipay.android.utils.GooglePlusSignInUtils;
import com.multipay.android.utils.GooglePlusSignInUtils.GooglePlusSignInStatus;
import com.multipay.android.utils.MultipayMenuItems;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignInActivity extends ActionBarActivity implements FacebookSignInStatus, GooglePlusSignInStatus {
 
    private static final String LOGCAT_TAG = "SignInActivity";
    
    private FacebookSignInUtils facebookSignInUtils;
    private GooglePlusSignInUtils googlePlusSignInUtils;
    
    private EditText email;
    private EditText password;
 
    private ProgressDialog mConnectionProgressDialog;
    
    private SessionManager session;
    private String mobileId;
    private LoginResponseDTO loginResponse;

    private LoginService loginService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        loginService = retrofit.create(LoginService.class);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_sign_in);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        
    	session = SessionManager.getInstance(this.getApplicationContext());
        mobileId = Device.getDevice(getApplicationContext()).getMACAddress();

        facebookSignInUtils = new FacebookSignInUtils(this);
        facebookSignInUtils.setFacebookSignInStatus(this);
        facebookSignInUtils.setEnable(true);
 
        googlePlusSignInUtils = new GooglePlusSignInUtils(this);
        googlePlusSignInUtils.setGooglePlusSignInStatus(this);
        
        email = (EditText) findViewById(R.id.email_input);
        password = (EditText) findViewById(R.id.password_input);
        
        mConnectionProgressDialog = new ProgressDialog(this);
        mConnectionProgressDialog.setMessage("Autenticandose...\nEspere un momento, por favor.");
    }
    
    // se llama despues de onPause()
    @Override
    protected void onResume() {
        super.onResume();
        facebookSignInUtils.onResume();
    }

    // se llama cuando justo antes de que A llame a B
    @Override
    protected void onPause() {
        super.onPause();
        facebookSignInUtils.onPause();
    }

    // se llama justo antes de que alguien haga finish() d esta actividad o el sistema la mate.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        facebookSignInUtils.onDestroy();
    }
 
    // se llama despues de onCreate() y antes de onResume()
    @Override
    protected void onStart() {
        super.onStart();
        googlePlusSignInUtils.connect();
    }
 
    // se llama antes de onDestroy()
    @Override
    protected void onStop() {
        super.onStop();
        googlePlusSignInUtils.disconnect();
        LoginManager.getInstance().logOut();
    }
 
    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
    	facebookSignInUtils.onActivityResult(requestCode, responseCode, intent);
    	googlePlusSignInUtils.onActivityResult(requestCode, responseCode, intent);
    }

    @Override
    public void onSuccessFacebookSignIn(Bundle profile) {
        facebookTokenInfo(profile.getString(FacebookSignInUtils.FACEBOOK_ACCESS_TOKEN));
    }

    @Override
    public void onSuccessGooglePlusSignIn(Bundle profile) {
        googleTokenInfo(profile.getString(GooglePlusSignInUtils.GOOGLEPLUS_TOKEN_ID));
    }
    
    public void signIn(View view) {
		Boolean cancel = false;
        String userEmail = this.email.getText().toString();
        String userPassword = this.password.getText().toString();
        View focusView = null;

        // Se validan los campos obligatorios
        if (TextUtils.isEmpty(userPassword)) {
                this.password.setError("El campo contrasenia es obligatorio.");
                focusView = this.password;
                cancel = true;
        }
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
        if (!cancel) {
            attemptNativeLogin(userEmail, userPassword);

        } else {
            // Se devuelve el foco al campo que no fue completado.
            focusView.requestFocus();
        }
    }
    
    private void setLoginResponse(LoginResponseDTO loginResponse) {
        this.loginResponse = loginResponse;
    }

    private void attemptNativeLogin(final String userEmail, final String userPassword) {
        LoginRequestDTO userLogin = new LoginRequestDTO();
        userLogin.setUserEmail(userEmail);
        userLogin.setUserPassword(userPassword);
        userLogin.setMobileId(this.mobileId);
        userLogin.setRegistrationId(session.retrieveRegistrationId());
        userLogin.setSeller(session.getMode().equals("SELLER"));
        mConnectionProgressDialog.show();
        Call<LoginResponseDTO> call = loginService.attemptNativeLogin(userLogin);
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
                    Toast.makeText(getApplicationContext(), "Multipay no ha podido autenticar. Intente nuevamente.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponseDTO> call, Throwable t) {
                mConnectionProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Multipay no ha podido autenticar. Intente nuevamente.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void googleTokenInfo(String tokenId) {
        LoginRequestDTO userLogin = new LoginRequestDTO();
        userLogin.setSocialToken(tokenId);
        userLogin.setMobileId(this.mobileId);
        userLogin.setRegistrationId(FirebaseInstanceId.getInstance().getToken());
        userLogin.setSeller(session.getMode().equals("SELLER"));
        mConnectionProgressDialog.show();
        Call<LoginResponseDTO> call = loginService.googleTokenInfo(userLogin);
        call.enqueue(new Callback<LoginResponseDTO>() {
            @Override
            public void onResponse(Call<LoginResponseDTO> call, Response<LoginResponseDTO> response) {
                mConnectionProgressDialog.dismiss();
                setLoginResponse(response.body());
                Boolean valid;
                if (loginResponse != null) {
                    valid = loginResponse.getValid();
                    if (valid) {
                        session.createSignInSession(loginResponse.getUserName(), loginResponse.getUserEmail(), "GOOGLE");
                        if (session.getMode().equals("SELLER")) {
                            Intent sellerMenuActivityIntent = new Intent(getApplicationContext(), SellerMenuActivity.class);
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
                    Toast.makeText(getApplicationContext(), "Multipay no ha podido autenticar. Intente nuevamente.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponseDTO> call, Throwable t) {
                mConnectionProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Multipay no ha podido autenticar. Intente nuevamente.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void facebookTokenInfo(String accessToken) {
        LoginRequestDTO userLogin = new LoginRequestDTO();
        userLogin.setSocialToken(accessToken);
        userLogin.setMobileId(this.mobileId);
        userLogin.setRegistrationId(session.retrieveRegistrationId());
        userLogin.setSeller(session.getMode().equals("SELLER"));
        mConnectionProgressDialog.show();
        Call<LoginResponseDTO> call = loginService.facebookTokenInfo(userLogin);
        call.enqueue(new Callback<LoginResponseDTO>() {
            @Override
            public void onResponse(Call<LoginResponseDTO> call, Response<LoginResponseDTO> response) {
                mConnectionProgressDialog.dismiss();
                setLoginResponse(response.body());
                Boolean valid;
                if (loginResponse != null) {
                    valid = loginResponse.getValid();
                    if (valid) {
                        session.createSignInSession(loginResponse.getUserName(), loginResponse.getUserEmail(), "FACEBOOK");
                        if (session.getMode().equals("SELLER")) {
                            Intent sellerMenuActivityIntent = new Intent(getApplicationContext(), SellerMenuActivity.class);
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
                    Toast.makeText(getApplicationContext(), "Multipay no ha podido autenticar. Intente nuevamente.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponseDTO> call, Throwable t) {
                mConnectionProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Multipay no ha podido autenticar. Intente nuevamente.", Toast.LENGTH_LONG).show();
            }
        });
    }
    
    public void launchSignUpActivity(View view) {
    	Intent signUpActivityIntent = new Intent(this, SignUpActivity.class);
		startActivity(signUpActivityIntent);
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.menu_activity_not_signed_in, menu);
    	    return super.onCreateOptionsMenu(menu);
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
            	MultipayMenuItems.openAbout(getApplicationContext());
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
        // TODO si no esta en proceso de logueo, dejar que vuelva a la pantalla de seleccion de modo (vendedor/comprador).
    	super.onBackPressed();
    }
}