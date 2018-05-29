package com.example.ldobriakova.plet_03;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


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
	String userName, babyAlias, oldbabyAlias;


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
		genderET = (EditText)findViewById(R.id.baby_gender);
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
		userNameET.setText(userName);
		babyAliasET.setText(oldbabyAlias);

	}

public void getBabyData (String userName, String oldbabyAlias)
{

}
	/**
	 * Method gets triggered when Register button is clicked
	 * 
	 * @param view
	 */

	public void updateBaby(View view){
		String babyAlias = babyAliasET.getText().toString();
		// Get age ET control value
		String babybirthDate = birthdayET.getText().toString();
		//Get gender
		String babyGender = genderET.getText().toString();

		if(!userName.isEmpty()&&!babyAlias.isEmpty()&&!babybirthDate.isEmpty()&&!babyGender.isEmpty()) {

			RegisterAPI.getInstance(this).updateBaby(userName, babyAlias, oldbabyAlias, babyGender, babybirthDate, new RegisterAPI.RegistrationCallback() {
				@Override
				public void onResponse(String str) {
					try {

						JSONObject jsonResponse = new JSONObject(str);
						String result = jsonResponse.getString("result");
						Log.d("What is returned on update baby view ......", result);
						//Log.d("What is returned on activity Login view ......", jsonResponse.toString());
						if (result.equals("OK")) {
							Log.d("attemptToUpdate baby", "SUCCESSSSSSSS!!!!!..");
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

				}

				@Override
				public void onNetworkError() {

				}
			});
		}
		}


			private void navigatetoSelectBaby(){
			runOnUiThread(new Runnable(){
				@Override
				public void run() {

					Intent i = new Intent(ModifyBaby.this,SelectBaby.class);
					i.putExtra("userName",userName);
					startActivity(i);
				}
			});

		}

	@Override
	public void onBackPressed()
	{
		Intent i = new Intent(ModifyBaby.this,SelectBaby.class);
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

	public void backToSelectBaby(View view) {
			Intent i = new Intent(ModifyBaby.this,SelectBaby.class);
			i.putExtra("userName",userName);
			startActivity(i);


	}
}
