package com.example.ldobriakova.plet_03;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.redmadrobot.inputmask.MaskedTextChangedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * Register Activity Class
 */
public class RegisterBaby extends Activity {

	// Progress Dialog Object
	ProgressDialog prgDialog;
	// Error Msg TextView Object
	TextView errorMsg;
	// User Name Edit View Object
	EditText userNameET, babyAliasET, genderET, birthdayET;
	String userName;
	Spinner genderSpinner;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.baby_register);

		userNameET = (EditText)findViewById(R.id.user_name);
		// Find Name Edit View control by ID
		babyAliasET = (EditText)findViewById(R.id.babyAlias);
		// Find Gender Edit View control by ID

		// Find Age Edit View control by ID
		//birthdayET = (EditText)findViewById(R.id.baby_birthday);

		// Instantiate Progress Dialog object
		prgDialog = new ProgressDialog(this);
		// Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);
		Bundle extras = getIntent().getExtras();
		userNameET.setText(extras.getString("userName"));
		userName = userNameET.getText().toString();
		//userNameET.setText();
		birthdayET  = (EditText)findViewById(R.id.baby_birthday);

		MaskedTextChangedListener listener = new MaskedTextChangedListener(
				"[00]/[00]/[0000]",	birthdayET,
				new MaskedTextChangedListener.ValueListener() {
					@Override
					public void onTextChanged(boolean maskFilled, @NonNull final String extractedValue) {
						Log.d(RegisterActivity.class.getSimpleName(),extractedValue);
						Log.d(RegisterActivity.class.getSimpleName(), String.valueOf(maskFilled));
					}
				}
		);
		birthdayET.addTextChangedListener(listener);
		birthdayET.setOnFocusChangeListener(listener);
		birthdayET.setHint(listener.placeholder());
		addItemOnGender();
	}
    private void enableProgressDialog(final boolean enable)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(enable)
                    prgDialog.show();
                else
                    prgDialog.hide();
            }
        });
    }

	public void addItemOnGender(){
		genderSpinner =  (Spinner)findViewById(R.id.spinnerGender);
		List<String> list = new ArrayList<String>();
		list.add("male");
		list.add("female");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		genderSpinner.setAdapter(dataAdapter);
	}
	@Override
	public void onBackPressed()
	{
		Intent i = new Intent(RegisterBaby.this,SelectBaby.class);
		i.putExtra("userName",userName);
		startActivity(i);

	}

	/**
	 * Method gets triggered when Register button is clicked
	 * 
	 * @param view
	 */
	public void registerBaby(View view){
        enableProgressDialog(true);
	    // Get NAme ET control value
		//userName = userNameET.getText().toString();
		// Get NAme ET control value
		String babyAlias = babyAliasET.getText().toString();
		// Get age ET control value
		String babybirthDate = birthdayET.getText().toString();
		//Get gender
		// genderET.getText().toString();
		String babyGender = genderSpinner.getSelectedItem().toString();

		if(!userName.isEmpty()&&!babyAlias.isEmpty()&&!babybirthDate.isEmpty()&&!babyGender.isEmpty()) {

			RegisterAPI.getInstance(this).registerBaby(userName, babyAlias, babyGender, babybirthDate, new RegisterAPI.RegistrationCallback() {
				@Override
				public void onResponse(String str) {
					try {
                        enableProgressDialog(false);
						JSONObject jsonResponse = new JSONObject(str);
						String result = jsonResponse.getString("result");
						Log.d("What is returned on activity Login view ......", result);
						//Log.d("What is returned on activity Login view ......", jsonResponse.toString());
						if (result.equals("OK")) {
							Log.d("attemptToLogin", "SUCCESSSSSSSS!!!!!..");
							navigatetoSelectBaby();

						} else {
							onFailRegistration(jsonResponse.getString("message"));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onError(RegistrationResponse.RegistrationError error) {
                    enableProgressDialog(false);
				}

				@Override
				public void onNetworkError() {
                    enableProgressDialog(false);
				}
			});
		}
		}


			private void navigatetoSelectBaby(){
			runOnUiThread(new Runnable(){
				@Override
				public void run() {

					Intent i = new Intent(RegisterBaby.this,SelectBaby.class);
					//String userName = userNameET.getText().toString();
					i.putExtra("userName",userName);
					startActivity(i);
								}
			});

		}


	private void onFailRegistration(final String message)
	{
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

			}
		});
	}
	/**
	 * Set degault values for Edit View controls
	 */
	public void setDefaultValues(){
		babyAliasET.setText("");

	}

	public void backToSelectBaby(View view) {
		Intent i = new Intent(RegisterBaby.this,SelectBaby.class);
		i.putExtra("userName",userName);
		startActivity(i);
	}
}
