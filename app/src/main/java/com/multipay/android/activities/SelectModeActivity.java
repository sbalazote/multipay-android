package com.multipay.android.activities;

import java.io.IOException;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.mercadopago.core.MercadoPago;
import com.mercadopago.util.LayoutUtil;
import com.multipay.android.R;
import com.multipay.android.helpers.SessionManager;

public class SelectModeActivity extends AppCompatActivity implements SurfaceHolder.Callback {

	private MediaPlayer mediaPlayer;
	private SurfaceView surfaceView;
	private SessionManager session;
	private static final String LOGCAT_TAG = "SelectModeActivity";

	public static final int SELECT_MODE_REQUEST_CODE = 8;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_mode);
		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);
		session = SessionManager.getInstance(this.getApplicationContext());
		surfaceView = (SurfaceView) findViewById(R.id.surface);
		SurfaceHolder holder = surfaceView.getHolder();
		holder.addCallback(this);
		// Creo objeto donde se va a reproducir el video.
		mediaPlayer = new MediaPlayer();
		// Ajusto sonido a cero en ambos canales.
		mediaPlayer.setVolume(0.0f, 0.0f);
		// Ajusto video para que se reproduzca en modo bucle. (una vez que finaliza, vuelve a comenzar)
		mediaPlayer.setLooping(true);
	}

	@Override
	public void onBackPressed() {
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mediaPlayer.setDisplay(holder);
		Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.select_mode_video);
		try {
			mediaPlayer.setDataSource(SelectModeActivity.this, video);
			mediaPlayer.prepare();
		} catch (IllegalArgumentException | SecurityException | IOException | IllegalStateException e) {
			Log.e(LOGCAT_TAG, e.getMessage(), e);
		}
		// Obtengo las dimensiones del video.
		int videoWidth = mediaPlayer.getVideoHeight();
		int videoHeight = mediaPlayer.getVideoWidth();
		// Obtengo el ancho de la pantalla del dispositivo.
		int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		// Obtengo parametros del layout del SurfaceView.
		android.view.ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();
		// Ajusto el ancho de la SurfaceView al ancho de la pantalla.
		lp.width = screenWidth;
		// Ajusto la altura de la SurfaceView para que coincida con la rel. de aspecto del video.
		lp.height = (int) (((float) videoHeight / (float) videoWidth) * (float) screenWidth);
		// Confirmo los ajustes.
		surfaceView.setLayoutParams(lp);
		// Comienza la reproduccion del video.
		mediaPlayer.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == SELECT_MODE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {

			Intent signInActivityIntent = new Intent(this, SignInActivity.class);
			startActivity(signInActivityIntent);
			} else {

				if ((data != null) && (data.getStringExtra("apiException") != null)) {
					Toast.makeText(getApplicationContext(), data.getStringExtra("apiException"), Toast.LENGTH_LONG).show();
				}
			}
		} else if (requestCode == MercadoPago.CONGRATS_REQUEST_CODE) {

			LayoutUtil.showRegularLayout(this);
		}
	}

	/**
	 * Me dirijo hacia la pantalla de Logueo.
	 * @param view
	 */
	public void sellerLogin(View view) {
		// Ajusto en preferencias de MultiPay el modo Vendedor.
		session.setMode("SELLER");
		Intent signInActivityIntent = new Intent(this, SignInActivity.class);
		startActivity(signInActivityIntent);
	}

	/**
	 * Me dirijo hacia la pantalla de Logueo.
	 * @param view
	 */
	public void buyerLogin(View view) {
		// Ajusto en preferencias de MultiPay el modo Comprador.
		session.setMode("BUYER");
		Intent enterPhoneNumberIntent = new Intent(this, EnterPhoneNumberActivity.class);
		startActivityForResult(enterPhoneNumberIntent, SELECT_MODE_REQUEST_CODE);
	}
}