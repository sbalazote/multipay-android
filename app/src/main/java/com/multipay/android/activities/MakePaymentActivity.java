package com.multipay.android.activities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.multipay.android.multipay.R;
import com.multipay.android.helpers.SessionManager;
import com.multipay.android.tasks.LoadImages;
import com.multipay.android.utils.ItemCategories;
import com.multipay.android.utils.ItemCategories.ItemCategory;
import com.multipay.android.utils.MultipayMenuItems;
import com.multipay.android.utils.PaymentMethods;

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
		return true;
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
		Intent paymentLinkBeamActivityIntent = new Intent(this, PaymentLinkBeamActivity.class);
		startActivity(paymentLinkBeamActivityIntent);
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
	
	public void onItemSelected(AdapterView<?> parent, View view, 
            int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
