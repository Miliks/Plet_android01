package com.example.ldobriakova.plet_03;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.redmadrobot.inputmask.MaskedTextChangedListener;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 
 * Register Activity Class
 */
public class RegisterStudent extends Activity {

	// Progress Dialog Object
	ProgressDialog prgDialog;
	// Error Msg TextView Object
	TextView errorMsg;
	// User Name Edit View Object
	EditText userNameET, babyNameET, babySurnameET, genderET, birthdayET;
	String userName, gender_adult,groupId;
	//Spinner genderSpinner;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.student_register);
		userNameET = (EditText)findViewById(R.id.user_name);
		// Find Name Edit View control by ID
		babyNameET = (EditText)findViewById(R.id.babyName);
		babySurnameET = (EditText)findViewById(R.id.babySurname);
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
		groupId = extras.getString("groupID");
		userName = userNameET.getText().toString();
		//userNameET.setText();
		birthdayET  = (EditText)findViewById(R.id.baby_birthday);

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
		//addItemOnGender();
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

	@Override
	public void onBackPressed()
	{
		Intent i = new Intent(RegisterStudent.this,ListGroups.class);
		i.putExtra("userName",userName);
		startActivity(i);

	}

	/**
	 * Method gets triggered when Register button is clicked
	 * 
	 * @param view
	 */
	public void registerStudent(View view){
        enableProgressDialog(true);
	    // Get NAme ET control value
		//userName = userNameET.getText().toString();
		// Get NAme ET control value
		String babyName = babyNameET.getText().toString();
		String babySurname = babySurnameET.getText().toString();
		// Get age ET control value
		String babybirthDate = birthdayET.getText().toString();
		//Get gender
		// genderET.getText().toString();
		String babyGender = gender_adult;

				//genderSpinner.getSelectedItem().toString();

		if(!userName.isEmpty()&&!babyName.isEmpty()&&!babySurname.isEmpty()&&!babybirthDate.isEmpty()&&!babyGender.isEmpty()) {

			RegisterAPI.getInstance(this).registerStudent(userName, babyName, babySurname, babyGender, babybirthDate, new RegisterAPI.RegistrationCallback() {
				@Override
				public void onResponse(String str) {
					try {
                        enableProgressDialog(false);
						JSONObject jsonResponse = new JSONObject(str);
						String result = jsonResponse.getString("result");
						Log.d("What is returned on activity Register student view ......", result);
						//Log.d("What is returned on activity Login view ......", jsonResponse.toString());
						if (result.equals("OK")) {
							Log.d("attemptToLogin", "SUCCESSSSSSSS!!!!!..");
							navigatetoListStudent();

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


			private void navigatetoListStudent(){
			runOnUiThread(new Runnable(){
				@Override
				public void run() {
					enableProgressDialog(true);

					Intent i = new Intent(RegisterStudent.this,ListAllStudents.class);
					//String userName = userNameET.getText().toString();
					i.putExtra("userName",userName);
					i.putExtra("groupID",groupId);
					i.putExtra("isTeacher",true);
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




}
