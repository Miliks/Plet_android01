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
public class CreateWrist extends Activity {

	// Progress Dialog Object
	ProgressDialog prgDialog;
	// Error Msg TextView Object
	TextView errorMsg;
	// User Name Edit View Object
	EditText wrstAlias, wrstMac;
	String wrstId, groupId, myEtText,studentID;
	//Spinner genderSpinner;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_wrist);
		wrstAlias = (EditText)findViewById(R.id.wrstAlias);
		// Find Name Edit View control by ID
		wrstMac = (EditText)findViewById(R.id.wrstMac);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			groupId = extras.getString("groupID");
			myEtText = extras.getString("userName");
			studentID = extras.getString("studentId");
		}
		// Instantiate Progress Dialog object
		prgDialog = new ProgressDialog(this);
		// Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);
        Log.d("MILA","On create new wristband view with groupID =" + groupId + "studentID = " + studentID);

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
		Intent i = new Intent(CreateWrist.this,ListWristbnd.class);
		i.putExtra("userName",myEtText);
		i.putExtra("groupID",groupId);
		i.putExtra("studentId",studentID);
		startActivity(i);

	}

	/**
	 * Method gets triggered when Register button is clicked
	 * 
	 * @param view
	 */
	public void createWrst(View view){
        enableProgressDialog(true);

		String wrstName = wrstAlias.getText().toString();
		String wrstMacAddr = wrstMac.getText().toString();

		if(!wrstName.isEmpty()&&!wrstMacAddr.isEmpty()) {

			RegisterAPI.getInstance(this).createRecognHW(wrstName, wrstMacAddr, new RegisterAPI.RegistrationCallback() {
				@Override
				public void onResponse(String str) {
					try {
                        enableProgressDialog(false);
						JSONObject jsonResponse = new JSONObject(str);
						String result = jsonResponse.getString("result");
						Log.d("MILA What is returned on wrist view ......", jsonResponse.toString());
						if (result.equals("OK")) {
							Log.d("register HW", "SUCCESSSSSSSS!!!!!..");
							navigatetoListWrist();

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


			private void navigatetoListWrist(){
			runOnUiThread(new Runnable(){
				@Override
				public void run() {
					enableProgressDialog(true);

					Intent i = new Intent(CreateWrist.this,ListWristbnd.class);
					i.putExtra("studentID",studentID);
					i.putExtra("groupID",groupId);
					i.putExtra("userName",myEtText);
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
