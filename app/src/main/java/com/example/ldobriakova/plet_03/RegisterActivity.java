package com.example.ldobriakova.plet_03;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 
 * Register Activity Class
 */
public class RegisterActivity extends Activity {

	// Progress Dialog Object
	ProgressDialog prgDialog;
	// Error Msg TextView Object
	TextView errorMsg;
	// Create Edit View Objects for all fields to fill for registration
	EditText userNameET, nameET, surnameET, emailET, pwdET, genderET, birthdayET, phoneET,cityET;
	// Checkbox for privicy complience
	CheckBox checkBox;
	private Pattern pattern;
	private Matcher matcher;

	private static final String DATE_PATTERN =
			"(0?[1-9]|1[012]) [/.-] (0?[1-9]|[12][0-9]|3[01]) [/.-] ((19|20)\\d\\d)";



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		checkBox = (CheckBox) findViewById(R.id.checkbox);
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);

		Log.d("What Year we are ......", String.valueOf(year));
		/*if (checkBox.isChecked()) {
			checkBox.setChecked(false);
		}*/


		// Find User Name Edit View control by ID
		userNameET = (EditText)findViewById(R.id.user_name);
		// Find Name Edit View control by ID
		nameET = (EditText)findViewById(R.id.owner_name);
		// Find SurName Edit View control by ID
		surnameET = (EditText)findViewById(R.id.owner_surname);
		// Find Gender Edit View control by ID
		genderET = (EditText)findViewById(R.id.owner_gender);
		// Find Age Edit View control by ID
		birthdayET = (EditText)findViewById(R.id.owner_birthday);
		birthdayET.setText("DD/MM/YYYY");
		// Find City Edit View control by ID
		cityET = (EditText)findViewById(R.id.owner_city);
		// Find Email Edit View control by ID
		emailET = (EditText)findViewById(R.id.email);
		// Find Email Edit View control by ID
		phoneET = (EditText)findViewById(R.id.owner_phone);
		// Find Password Edit View control by ID
		pwdET = (EditText)findViewById(R.id.password);
		// Instantiate Progress Dialog object
		prgDialog = new ProgressDialog(this);
		// Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);


	}


	/**
	 * Method gets triggered when Register button is clicked
	 * 
	 * @param view
	 *
	 */


	public void registerUser(View view){

		// Get NAme ET control value
		String userName = userNameET.getText().toString();
		// Get NAme ET control value
		String name = nameET.getText().toString();
		// Get SurName ET control value
		String surName = surnameET.getText().toString();
		// Get age ET control value
		String birthDate = birthdayET.getText().toString();
		// Get phone number ET control value
		String phone = phoneET.getText().toString();
		// Get gender ET control value
		String gender = genderET.getText().toString();
		// Get city ET control value
		String city = cityET.getText().toString();
		// Get Email ET control value
		String email = emailET.getText().toString();
		// Get Password ET control value
		String password = pwdET.getText().toString();
		if(!userName.isEmpty()&&!password.isEmpty()&&!name.isEmpty()&&!surName.isEmpty()&&!birthDate.isEmpty()&&!gender.isEmpty()&&!city.isEmpty()) {
			matcher = Pattern.compile(DATE_PATTERN).matcher(birthDate);

//Birthday validator
     if (!matcher.matches()) {
				Toast.makeText(getApplicationContext(), "Invalid Birthday!", Toast.LENGTH_SHORT).show();
			}

			if(validateAdult(birthDate)) {


			RegisterAPI.getInstance(this).registerEmail(userName, email, surName, name, gender, birthDate, password, new RegisterAPI.RegistrationCallback() {
				@Override
				public void onResponse(String str) {
					try {
						JSONObject jsonResponse = new JSONObject(str);
						String result = jsonResponse.getString("result");
						Log.d("What is returned on activity Login view ......", result);
						//Log.d("What is returned on activity Login view ......", jsonResponse.toString());
						if (result.equals("OK")) {
							Log.d("attemptToLogin", "SUCCESSSSSSSS!!!!!..");
							navigatetoLoginActivity();
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
		else
			Toast.makeText(getApplicationContext(), "Please fill all information to proceed", Toast.LENGTH_LONG).show();
		}
}
			private void navigatetoLoginActivity(){
			runOnUiThread(new Runnable(){
				@Override
				public void run() {
					startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
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
		nameET.setText("");
		emailET.setText("");
		pwdET.setText("");
	}

	public void goToLogin(View view) {
		Intent navLogin = new Intent(getApplicationContext(),LoginActivity.class);
		navLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(navLogin);
	}

	public boolean validate(final String date){
		Log.d("attemptToLogin", "WE ARE in matcher....!!!!!..");
		matcher = pattern.matcher(date);

		if(matcher.matches()){
			matcher.reset();

			if(matcher.find()){
				String day = matcher.group(1);
				String month = matcher.group(2);
				int year = Integer.parseInt(matcher.group(3));

				if (day.equals("31") &&
						(month.equals("4") || month .equals("6") || month.equals("9") ||
								month.equals("11") || month.equals("04") || month .equals("06") ||
								month.equals("09"))) {
					return false; // only 1,3,5,7,8,10,12 has 31 days
				}

				else if (month.equals("2") || month.equals("02")) {
					//leap year
					if(year % 4==0){
						if(day.equals("30") || day.equals("31")){
							return false;
						}
						else{
							return true;
						}
					}
					else{
						if(day.equals("29")||day.equals("30")||day.equals("31")){
							return false;
						}
						else{
							return true;
						}
					}
				}

				else{
					return true;
				}
			}

			else{
				return false;
			}
		}
		else{
			return false;
		}
	}
	public boolean validateAdult(String birthDate)
	{
		String birthYear = birthDate.substring(6,10);
		//Log.d("birthYear = ", birthYear);
		int number = Integer.parseInt(birthYear) + 18;
		String added_date = birthDate.substring(0,6);
		//Log.d("first 6 symbols = ", added_date);
		added_date = added_date.concat(String.valueOf(number));
		Log.d("+18 years = ", added_date);
		Date date_test = null;
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		try {
			date_test = formatter.parse(added_date);
			Log.d("birthYear trasformed inside try = ", date_test.toString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Log.d("birthYear trasformed outside try = ", date_test.toString());
				Date nowDate = new Date(System.currentTimeMillis());


		//String s = new SimpleDateFormat("MM/dd/yyyy").format(nowDate);
		Log.d("Now...  = ", nowDate.toString());
		if(nowDate.after(date_test)){
			Log.d("Adult...  = ", "Congrats you r adult");
return true;
		}
		else return false;

	}

}
