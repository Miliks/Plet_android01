package com.example.ldobriakova.plet_03;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.redmadrobot.inputmask.MaskedTextChangedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.fabric.sdk.android.Fabric;

/**
 * 
 * Register Activity Class
 */
public class UserProfile extends Activity {

	// Progress Dialog Object
	ProgressDialog prgDialog;
	// Error Msg TextView Object
	TextView errorMsg;
	// Create Edit View Objects for all fields to fill for registration
	EditText userNameET, nameET, surnameET, emailET, pwdET,repwdET, birthdayET, phoneET,cityET;
	//EditText genderET;
	// Checkbox for privicy complience
	CheckBox checkBox;
	Spinner countrySpinner, genderSpinner;
	JSONArray countryList = new JSONArray();
	String combined, gender_adult, userBD, cel, city, country, email, gend, name, username, surname, userData, userBD_external;
	List<String> listName = new ArrayList<String>();
	JSONArray userDatalist = new JSONArray();
	RadioButton gender_m, gender_f;
	Boolean isTeacher;
	Button btn1, btn2, btn3;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
        Fabric.with(this, new Crashlytics());
		Bundle extras = getIntent().getExtras();
		isTeacher = extras.getBoolean("isTeacher");
		checkBox = (CheckBox) findViewById(R.id.checkbox);
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		countrySpinner = (Spinner)findViewById(R.id.spinnerCountry) ;
		prgDialog = new ProgressDialog(this);
		// Set Progress Dialog Text
		prgDialog.setMessage("Please wait...");
		// Set Cancelable as False
		prgDialog.setCancelable(false);
		prgDialog.setIndeterminate(true);
		checkBox = (CheckBox)findViewById(R.id.checkBox);
		checkBox.setChecked(false);
		TextView textView = (TextView)findViewById(R.id.textView2);
		btn1 = (Button)findViewById(R.id.register_button);
		btn2 = (Button)findViewById(R.id.child_data);
		btn3 = (Button)findViewById(R.id.modify_group);
		if(isTeacher) {
			LinearLayout.LayoutParams lp_l = new LinearLayout.LayoutParams(
					(ViewGroup.LayoutParams.WRAP_CONTENT), (ViewGroup.LayoutParams.WRAP_CONTENT));
			btn1.setLayoutParams(lp_l);
			btn2.setLayoutParams(lp_l);
			btn1.requestLayout();
			btn1.setEnabled(true);
			btn2.setEnabled(false);
			btn3.setEnabled(true);
			btn3.setVisibility(View.INVISIBLE);
		}
		else {
			LinearLayout.LayoutParams lp_l = new LinearLayout.LayoutParams(
					(ViewGroup.LayoutParams.MATCH_PARENT), (ViewGroup.LayoutParams.MATCH_PARENT));
			btn3.setLayoutParams(lp_l);
			btn1.setEnabled(false);
			btn1.setVisibility(View.INVISIBLE);
			btn2.setEnabled(true);
			btn2.setVisibility(View.INVISIBLE);
			btn3.setEnabled(false);
		}
		checkBox.setText("");
		textView.setText(Html.fromHtml("I have read & agree to the " +
				"<a href='https://plet.cloud.reply.eu/termsconditions.html'>TERMS AND CONDITIONS</a>"));
		textView.setClickable(true);
		textView.setMovementMethod(LinkMovementMethod.getInstance());

		// Find User Name Edit View control by ID
		userNameET = (EditText)findViewById(R.id.user_name);
		// Find Name Edit View control by ID
		nameET = (EditText)findViewById(R.id.owner_name);
		// Find SurName Edit View control by ID
		surnameET = (EditText)findViewById(R.id.owner_surname);
		// Find Gender Edit View control by ID
		//genderET = (EditText)findViewById(R.id.owner_gender);

		// Find Age Edit View control by ID

		birthdayET  = (EditText)findViewById(R.id.owner_birthday);

		MaskedTextChangedListener listener = new MaskedTextChangedListener(
		"[00]-[00]-[0000]",	birthdayET,
				new MaskedTextChangedListener.ValueListener() {
					@Override
					public void onTextChanged(boolean maskFilled, @NonNull final String extractedValue) {
						Log.d(UserProfile.class.getSimpleName(),extractedValue);
						Log.d(UserProfile.class.getSimpleName(), String.valueOf(maskFilled));
					}
				}
		);
		birthdayET.addTextChangedListener(listener);
		birthdayET.setOnFocusChangeListener(listener);
		birthdayET.setHint(listener.placeholder());

		// Find City Edit View control by ID
		cityET = (EditText)findViewById(R.id.owner_city);
		// Find Email Edit View control by ID
		//emailET = (EditText)findViewById(R.id.email);
		emailET = (EditText)findViewById(R.id.user_name);
		// Find Email Edit View control by ID
		phoneET = (EditText)findViewById(R.id.owner_phone);
		// Find Password Edit View control by ID
		pwdET = (EditText)findViewById(R.id.password);
		repwdET = (EditText)findViewById(R.id.repeat_password);
		// Instantiate Progress Dialog object
		prgDialog = new ProgressDialog(this);
		// Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);

		if (extras != null) {
			username = extras.getString("userName");
		}
		getUserInfo(username);

		Log.d("User to query in onCreate in RemoveModify Baby.....=",username);
		userNameET.setText(username);
		gender_m = (RadioButton)findViewById(R.id.radio_gender_m);
		gender_f = (RadioButton)findViewById(R.id.radio_gender_f);
		//getCountryList();

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
	private void createProfile(final JSONArray userData){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject usr = userData.getJSONObject(0);
					Log.d("User to query in onCreate in RemoveModify Baby.....=",usr.toString());
					userBD = usr.getString("Birthdate");
					birthdayET.setText(userBD);
					cel = usr.getString("CellularNumber");
					//phoneET.setText(cel);
					city = usr.getString("City");
					cityET.setText(city);
					name = usr.getString("Name");
					nameET.setText(name);
					surname = usr.getString("Surname");
					surnameET.setText(surname);
					country = usr.getString("Country");
					getCountryList();
					gender_adult = usr.getString("Gender");
					if (gender_adult.contains("female"))
						gender_f.setChecked(true);
					else
						gender_m.setChecked(true);

				}
				catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
	private void updateUISpinner( final List<String> list){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				addListenerOnSpinnerCountrySelection();
				addItemOnSpinnerCountryList(list);

			}
		});
	}
	private void addItemOnSpinnerCountryList(List<String> selection) {
		countrySpinner = (Spinner) findViewById(R.id.spinnerCountry);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,selection);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		countrySpinner.setAdapter(dataAdapter);

	}
	private void addListenerOnSpinnerCountrySelection() {
		Log.d("We are in listener on spinner","....");
		countrySpinner = (Spinner)findViewById(R.id.spinnerCountry);
		countrySpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
	}
	//Get country list from ARAS
	public void getCountryList()
	{
		enableProgressDialog(true);

		RegisterAPI.getInstance(this).getCountryList(new RegisterAPI.RegistrationCallback() {
			@Override
			public void onResponse(String str) {
				try {
					enableProgressDialog(false);
					JSONObject jsonResponse = new JSONObject(str);
					String result = jsonResponse.getString("result");
					JSONObject result_obj = jsonResponse.getJSONObject("content");
                    //listName.add("COUNTRY");
					listName.add(country);
					if (result.equals("OK")) {
						countryList = result_obj.getJSONArray("CountryList");
						if(countryList.length()>0) {
							for (int i = 0; i < countryList.length(); i++) {
								JSONObject b = countryList.getJSONObject(i);
								combined = b.getString("Abbreviation") + "_" + b.getString("CountryName");
								listName.add(combined);
							}
							updateUISpinner(listName);
						}
						else
						{
							updateUISpinner(listName);
						}


						prgDialog.dismiss();

					} else {

						cleanText(jsonResponse.getString("message"));

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


	//get info about user from ARAS to update it
	public void getUserInfo(String userName)
	{
		enableProgressDialog(true);

		RegisterAPI.getInstance(this).getUserInfo(userName, new RegisterAPI.RegistrationCallback() {
			@Override
			public void onResponse(String str) {
				try {
					enableProgressDialog(false);
					JSONObject jsonResponse = new JSONObject(str);
					JSONObject result_obj = jsonResponse.getJSONObject("content");
					userData = jsonResponse.getString("result");

					if(userData.equals("OK")) {
						if (!userData.isEmpty()) {
							try {
								userDatalist = result_obj.getJSONArray("UserData");

							} catch (JSONException e) {
								e.printStackTrace();
							}
							createProfile(userDatalist);
						}
					}
					else{
						Log.d("Inside cycle for ......", "No data associated with this user");
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




	private void cleanText(final String message)
	{
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

			}
		});
	}

	/**
	 * Method gets triggered when Register button is clicked
	 * 
	 * @param view
	 *
	 */

	public void updateUser(View view){

		// Get NAme ET control value
		final String userName = userNameET.getText().toString();
		// Get NAme ET control value
		String name = nameET.getText().toString();
		// Get SurName ET control value
		String surName = surnameET.getText().toString();
		// Get age ET control value
		String birthDate = birthdayET.getText().toString();
		// Get phone number ET control value
		String phone = phoneET.getText().toString();
		// Get gender ET control value
		//String gender = genderET.getText().toString();
		//String gender = genderSpinner.getSelectedItem().toString();
				// Get city ET control value
		String city = cityET.getText().toString();
		// Get Email ET control value
		String email = emailET.getText().toString();
		// Get Password ET control value
		String password = pwdET.getText().toString();
		String re_password = repwdET.getText().toString();
		String country = countrySpinner.getSelectedItem().toString();
		//Log.d("country = ", country);
		String  country_short;
		int i = country.indexOf("_");
		if (i== -1)
			country_short=country;
		else
		//Log.d("country index  _  = ", String.valueOf(i));

		country_short = country.substring(0,country.indexOf("_"));
		//Log.d("BD = ", birthDate);

//Verify is all mandatory fields are filled
		if(!userName.isEmpty()&&!password.isEmpty()&&password.equals(re_password)&&!name.isEmpty()&&!surName.isEmpty()&&!birthDate.isEmpty()&&!gender_adult.isEmpty()&&!city.isEmpty()&&!country.isEmpty()) {

			if (checkBox.isChecked()) {
				//Birthday formal validator
				if (isValidDate(birthDate)) {

					if (validateAdult(birthDate)) {
//TODO
						//substitute with the new API to update user data
						RegisterAPI.getInstance(this).updateUser(userName, surName, name,email, password,phone, city,country_short,birthDate,gender_adult, new RegisterAPI.RegistrationCallback() {
							@Override
							public void onResponse(String str) {
								try {
									JSONObject jsonResponse = new JSONObject(str);
									String result = jsonResponse.getString("result");
									if (result.equals("OK")) {
										String user = userName;
										//Log.d("attemptToLogin", "SUCCESSSSSSSS!!!!!..");
										runOnUiThread(new Runnable() {
											@Override
										public void run() {
											Toast.makeText(getApplicationContext(), "Data have been updated successfully!", Toast.LENGTH_LONG).show();}

									});


										//navigatetoLoginActivity();
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
					} else
						Toast.makeText(getApplicationContext(), "Sorry, you are not adult!", Toast.LENGTH_LONG).show();
				} else
					Toast.makeText(getApplicationContext(), "Invalid Date format!", Toast.LENGTH_SHORT).show();

			} else
				Toast.makeText(getApplicationContext(), "Please accept the privacy terms before proceeding!", Toast.LENGTH_SHORT).show();
		}
		else
		Toast.makeText(getApplicationContext(), "Please fill all mandatory information and verify it to proceed", Toast.LENGTH_LONG).show();
}
			private void navigatetoLoginActivity(){
			runOnUiThread(new Runnable(){
				@Override
				public void run() {
					startActivity(new Intent(UserProfile.this,LoginActivity.class));
				}
			});}



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
	public static boolean isValidDate(String inDate) {
		//Log.d("What is returned on activity Login view ......", inDate);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		dateFormat.setLenient(false);
		try {
			dateFormat.parse(inDate.trim());
		} catch (ParseException pe) {
			return false;
		}
		return true;
	}

	public void goToLogin(View view) {
		Intent navLogin = new Intent(getApplicationContext(),LoginActivity.class);
		navLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(navLogin);
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
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
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


	public void navigatetoAddChild(View view) {
		runOnUiThread(new Runnable(){
			@Override
			public void run() {

				Intent i = new Intent(UserProfile.this, RemoveModBaby.class);
				String userNameInternal = userNameET.getText().toString();
				//usernameET.getText().toString();
				i.putExtra("userName", userNameInternal);
				prgDialog.dismiss();
				startActivity(i);

			}
		});
	}

	public void modify_group(View view) {
		runOnUiThread(new Runnable(){
			@Override
			public void run() {

				Intent i = new Intent(UserProfile.this, CreateGroup.class);
				String userNameInternal = userNameET.getText().toString();
				//usernameET.getText().toString();
				i.putExtra("userName", userNameInternal);
				prgDialog.dismiss();
				startActivity(i);

			}
		});

	}
}
