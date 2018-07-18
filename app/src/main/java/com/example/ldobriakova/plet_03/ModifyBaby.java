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
import android.widget.RadioButton;
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
public class ModifyBaby extends Activity {

	// Progress Dialog Object
	ProgressDialog prgDialog;
	// Error Msg TextView Object
	TextView errorMsg;
	// Edit View Objects
	EditText userNameET, babyAliasET, genderET,birthdayET;
	String userName, babyAlias, oldbabyAlias, childGender, childBD, gender_adult;
	RadioButton gender_m, gender_f;
	//Spinner genderSpinner;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.baby_modify);
		// Find Error Msg Text View control by ID
		//errorMsg = (TextView)findViewById(R.id.register_error);
		// Find User Name Edit View control by ID
		userNameET = (EditText)findViewById(R.id.user_name);
		// Find Name Edit View control by ID
		babyAliasET = (EditText)findViewById(R.id.babyAlias);
		// Find Gender Edit View control by ID
		//genderET = (EditText)findViewById(R.id.baby_gender);
		// Find Age Edit View control by ID
		birthdayET = (EditText)findViewById(R.id.baby_birthday);

		// Instantiate Progress Dialog object
		prgDialog = new ProgressDialog(this);
		// Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);
		Bundle extras = getIntent().getExtras();
		userName = extras.getString("userName");
		oldbabyAlias = extras.getString("babyAlias");
		childGender = extras.getString("babyGender");
		childBD = extras.getString("birthDay");
		userNameET.setText(userName);
		babyAliasET.setText(oldbabyAlias);
		MaskedTextChangedListener listener = new MaskedTextChangedListener(
				"[00]-[00]-[0000]",	birthdayET,
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
		birthdayET.setText(childBD);
		gender_m = (RadioButton)findViewById(R.id.radio_gender_m);
		gender_f = (RadioButton)findViewById(R.id.radio_gender_f);
		if (childGender.contains("female")){
			gender_f.setChecked(true);
		gender_adult = "female";}
		else
		{
			gender_m.setChecked(true);
			gender_adult = "male";
		}
	}

	public void onRadioButtonClicked(View view) {
		// Is the button now checked?
		boolean checked = ((RadioButton) view).isChecked();

		// Check which radio button was clicked
		switch(view.getId()) {
			case R.id.radio_gender_f:
				if (checked)
					gender_adult = "female";
				break;
			case R.id.radio_gender_m:
				if (checked)
					gender_adult = "male";
				break;
		}
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


	/**
	 * Method gets triggered when Register button is clicked
	 * 
	 * @param view
	 */

	public void updateBaby(View view){
		enableProgressDialog(true);
		String babyAlias = babyAliasET.getText().toString();
		// Get age ET control value
		String babybirthDate = birthdayET.getText().toString();
		//Get gender
		//String gender = genderSpinner.getSelectedItem().toString();
		String gender = gender_adult;
Log.d("MILA ","the values of the fields babyAlias = "+babyAlias + " babybirthDate = " + babybirthDate + "userName =  " + userName + "gender = " + gender);
		if(!userName.isEmpty()&&!babyAlias.isEmpty()&&!babybirthDate.isEmpty()&&!gender.isEmpty()) {

			RegisterAPI.getInstance(this).updateBaby(userName, babyAlias, oldbabyAlias, gender, babybirthDate, new RegisterAPI.RegistrationCallback() {
				@Override
				public void onResponse(String str) {
					try {
						enableProgressDialog(false);
						JSONObject jsonResponse = new JSONObject(str);
						String result = jsonResponse.getString("result");
						if (result.equals("OK")) {
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

					Intent i = new Intent(ModifyBaby.this,RemoveModBaby.class);
					i.putExtra("userName",userName);
					startActivity(i);
				}
			});

		}

	@Override
	public void onBackPressed()
	{
		Intent i = new Intent(ModifyBaby.this,RemoveModBaby.class);
		i.putExtra("userName",userName);
		startActivity(i);

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


}
