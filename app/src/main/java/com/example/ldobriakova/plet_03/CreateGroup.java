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
public class CreateGroup extends Activity {

	// Progress Dialog Object
	ProgressDialog prgDialog;
	// Error Msg TextView Object
	TextView errorMsg;
	// Group Name Edit View Object
	EditText groupNameET, userNameET;
	String userName, groupName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_group);
		userNameET = (EditText)findViewById(R.id.user_name);
		Bundle extras = getIntent().getExtras();
		String userNameText =  extras.getString("userName");
		Log.d("What is returned on activity Login view ......",userNameText);
		userNameET.setText(userNameText);
		//userNameET.setText(extras.getString("userName"));
		userName = userNameET.getText().toString();
		groupNameET = (EditText)findViewById(R.id.groupName);
		// Find Name Edit View control by ID

		// Instantiate Progress Dialog object
		prgDialog = new ProgressDialog(this);
		// Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);

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


	@Override
	public void onBackPressed()
	{
		Intent i = new Intent(CreateGroup.this,SelectChild.class);
		i.putExtra("userName",userName);
		startActivity(i);

	}

	/**
	 * Method gets triggered when Register button is clicked
	 * 
	 * @param view
	 */
	public void createGroup(View view){
        enableProgressDialog(true);
	    // Get NAme ET control value

		String groupName = groupNameET.getText().toString();


		if(!userName.isEmpty()&&!groupName.isEmpty()) {

			RegisterAPI.getInstance(this).createGroup(userName, groupName, new RegisterAPI.RegistrationCallback() {
				@Override
				public void onResponse(String str) {
					try {
                        enableProgressDialog(false);
						JSONObject jsonResponse = new JSONObject(str);
						String result = jsonResponse.getString("result");
						Log.d("What is returned on group registration ......", result);
						//Log.d("What is returned on activity Login view ......", jsonResponse.toString());
						if (result.equals("OK")) {
							Log.d("attemptToregister group", "SUCCESSSSSSSS!!!!!..");
							//navigatetoSelectStudents();
							navigateToListGroups();
							//TODO Add popup informing if creation was successful!
							//Toast.makeText(getApplicationContext(), "Group have been created successfully!", Toast.LENGTH_LONG).show();

						} else {
							onFailRegistration(jsonResponse.getString("message"));
							//Toast.makeText(getApplicationContext(), "Group have NOT been created! PLEASE TRY AGAINE", Toast.LENGTH_LONG).show();
						}
					} catch (JSONException e) {
						//Toast.makeText(getApplicationContext(), "Group have NOT been created! PLEASE TRY AGAINE", Toast.LENGTH_LONG).show();
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




	private void onFailRegistration(final String message)
	{
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

			}
		});
	}

	public void navigateToListGroups() {
		runOnUiThread(new Runnable(){
			@Override
			public void run() {

				Intent i = new Intent(CreateGroup.this,ListGroups.class);
				i.putExtra("userName",userName);
				groupName = groupNameET.getText().toString();
				//i.putExtra("groupName",groupName);
				startActivity(i);
			}
		});
	}



    /**
	 * Set degault values for Edit View controls
	 */



}
