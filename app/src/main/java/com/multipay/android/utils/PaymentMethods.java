package com.multipay.android.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PaymentMethods {
	
	public class PaymentMethod {
		private String id;
		private String name;
	    private String payment_type_id;
	    private String thumbnail;
	    private String secure_thumbnail;
		
	    public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getPayment_type_id() {
			return payment_type_id;
		}
		public void setPayment_type_id(String payment_type_id) {
			this.payment_type_id = payment_type_id;
		}
		public String getThumbnail() {
			return thumbnail;
		}
		public void setThumbnail(String thumbnail) {
			this.thumbnail = thumbnail;
		}
		public String getSecure_thumbnail() {
			return secure_thumbnail;
		}
		public void setSecure_thumbnail(String secure_thumbnail) {
			this.secure_thumbnail = secure_thumbnail;
		}
	}
	
	private List<PaymentMethod> paymentMethods;
	
	private static PaymentMethods instance = null;

	protected PaymentMethods() {
		paymentMethods = new ArrayList<PaymentMethod>();
	}

	public static PaymentMethods getInstance() {
		if (instance == null) {
			instance = new PaymentMethods();
		}
		return instance;
	}
	
	public void fillMethods(List<PaymentMethod> paymentMethods) {
		this.paymentMethods = paymentMethods;
	}
	
	public List<PaymentMethod> getMethods() {
		return this.paymentMethods;
	}
	
	public List<String> getPaymentMethodNames() {
		List<String> names = new ArrayList<String>();
		
		Iterator<PaymentMethod> it = this.paymentMethods.iterator();
	
		while (it.hasNext()) {
			PaymentMethod pm = it.next();
			names.add(pm.getName());
		}
		
		return names;
	}
	
	public List<String> getSecureThumbnails() {
		List<String> thumbnails = new ArrayList<String>();
		
		Iterator<PaymentMethod> it = this.paymentMethods.iterator();
	
		while (it.hasNext()) {
			PaymentMethod pm = it.next();
			thumbnails.add(pm.getSecure_thumbnail());
		}
		
		return thumbnails;
	}
}
