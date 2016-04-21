package com.multipay.android.utils;

import java.util.ArrayList;
import java.util.List;

public class PaymentTypes {
	
	public class PaymentType {
		private String id;
		private String name;
	}

	private List<PaymentType> paymentTypes;
	
	private static PaymentTypes instance = null;

	protected PaymentTypes() {
		paymentTypes = new ArrayList<PaymentType>();
	}

	public static PaymentTypes getInstance() {
		if (instance == null) {
			instance = new PaymentTypes();
		}
		return instance;
	}
	
	public void fillTypes(List<PaymentType> paymentTypes) {
		this.paymentTypes = paymentTypes;
	}
}
