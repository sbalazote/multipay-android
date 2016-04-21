package com.multipay.android.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.multipay.android.multipay.R;
import com.multipay.android.helpers.SessionManager;
import com.multipay.android.utils.MultipayMenuItems;

public class PINChangeActivity extends AppCompatActivity {

    private EditText oldPIN;
    private EditText newPIN;
    private Button PINChange;
	private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_change);
        session = SessionManager.getInstance(this.getApplicationContext());
        setInputs();
    }
    
    /** Called when the user touches the button */
    public void PINChange(View view) {
    	oldPIN.getText();
    }
    
    //	Handle inputs
    private void setInputs(){
    	oldPIN = (EditText) findViewById(R.id.old_PIN);
    	newPIN = (EditText) findViewById(R.id.new_PIN);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.menu_activity_buyer_signed_in, menu);
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
            	session.logoutUser();
                return true;
            case R.id.action_help:
            	MultipayMenuItems.openHelp(getApplicationContext());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}