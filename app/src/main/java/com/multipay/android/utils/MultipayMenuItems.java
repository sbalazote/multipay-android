package com.multipay.android.utils;

import android.content.Context;
import android.widget.Toast;

public class MultipayMenuItems {

	public static void openHelp(Context context) {
    	CharSequence text = "2015 - Multipay Co.\nDirijase a http://multipay.ddns.net:8080 para mas informacion";;
    	int duration = Toast.LENGTH_LONG;
    	Toast toast = Toast.makeText(context, text, duration);
    	toast.show();
	}
	
	public static void openAbout(Context context) {
    	CharSequence text;
    	int duration = Toast.LENGTH_LONG;
    	
    	if (!Device.getDevice(context).isNFCPresent()) {
    		text = "NFC no disponible!";
    	} else {
    		if (!Device.getDevice(context).isNFCEnabled()) {
    			text = "Debe activar NFC en ajustes";
    		} else {
    			text = "El equipo funciona correctamente";
    		}
    	}

    	Toast toast = Toast.makeText(context, text, duration);
    	toast.show();
	}
}
