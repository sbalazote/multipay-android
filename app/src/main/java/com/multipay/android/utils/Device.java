package com.multipay.android.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;

public class Device {

	private static Device instance = new Device();
	private static Context context;

	private Device() {
	}

	public static Device getDevice(Context ctx) {
		context = ctx;
		return instance;
	}

	public boolean isNFCPresent() {
		NfcManager manager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
		NfcAdapter adapter = manager.getDefaultAdapter();
		return (adapter != null);
	}

	public boolean isNFCEnabled() {
		NfcManager manager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
		NfcAdapter adapter = manager.getDefaultAdapter();
		return adapter.isEnabled();
	}

	public final String getMACAddress() {
		String macAddress = "00B0D086BBF7";

		try {
			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

			if (wifiManager != null) {
				if (wifiManager.isWifiEnabled()) {
					// WIFI ALREADY ENABLED. GRAB THE MAC ADDRESS HERE
					WifiInfo info = wifiManager.getConnectionInfo();
					macAddress = info.getMacAddress();

				} else {
					// ENABLE THE WIFI FIRST
					wifiManager.setWifiEnabled(true);

					// WIFI IS NOW ENABLED. GRAB THE MAC ADDRESS HERE
					WifiInfo info = wifiManager.getConnectionInfo();
					macAddress = info.getMacAddress();

					// NOW DISABLE IT AGAIN
					wifiManager.setWifiEnabled(false);
				}

				// formatea el valor de la MAC
				if (macAddress != null) {
					macAddress = macAddress.replace(":", "");
				} else {
					macAddress = "00B0D086BBF7";
				}
			} else {
				// msg = "WiFi no encontrada: No se puede obtener la MacAddress";
			}
		} catch (Exception e) {
			// msg = "Error al obtener la MacAddress: " + e.getMessage();
		}

		return macAddress;
	}
}
