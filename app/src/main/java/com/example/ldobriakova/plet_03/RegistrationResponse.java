package com.example.ldobriakova.plet_03;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by f.gueli on 05/09/2017.
 */

public class RegistrationResponse {

    private Boolean success;
    private String error;
    private String responseFromServer;

    public enum RegistrationError {
        UNDEFINED_ERROR("undefined_error"),
        WRONG_USER("You have specified a wrong username or password. Check the inserted data and retry."),
        ERROR_REGISTERING_USER("error_registering_user"),
        VALIDATION_ERROR("validation_error"),
        USER_FOUND_WITH_THIS_PHONE_NUMBER("user_found_with_this_mobile_phone_number"),
        USER_FOUND_WITH_THIS_EMAIL("user_found_with_this_email");

        String message;

        RegistrationError(String message) {
            this.message = message;
        }

        public static RegistrationError fromString(String text) {
            for (RegistrationError b : RegistrationError.values()) {
                if (b.message.equalsIgnoreCase(text)) {
                    Log.d("Registration Response == ", b.getMessage());
                    return b;

                }
            }
            return UNDEFINED_ERROR;
        }


        public String getMessage()
        {
            return this.message;
        }
    }


    public String isSuccessful(String message) {

        Log.d("Registration Response from server = ", message);

        JSONObject jsonResponse = null;
        try {
            jsonResponse = new JSONObject(message);

        JSONObject result = jsonResponse.getJSONObject("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "OK";
    }

    public String getError() {
        return error;
    }
}
