package com.example.ldobriakova.plet_03;

/**
 * Created by l.dobriakova on 09/04/2018.
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.Object;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by f.gueli on 14/07/2017.
 */

public class RegisterAPI {

    private static final int RESPONSE_UNAUTHORIZED_401 = 401;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    //https://plet.replycloud.prv/pletapis/pletapis.svc/createCustomer?username=new00&surname=Berizzi&firstname=Federico&password=AB1234567890AB123456790AB123456&gender=male&birthDate=30/08/1977

    //private final static String BASE_URL = "https://plet.replycloud.prv/pletapis.svc/"; //INSERISCI L'URL DEL TUO SERVER
    private final static String BASE_URL = "https://plet.cloud.reply.eu/pletapis.svc/";

    private final static String AUTH_EMAIL_ENDPOINT = "api/auth/authenticate"; //INSERISCI IL TUO ENDPOINT PER IL LOGIN

    private final static String REGISTRATION_EMAIL_ENDPOINT = "api/v2/auth/register"; //INSERISCI IL TUO ENDPOINT PER LA REGISTRAZIONE


    private static RegisterAPI registerAPI;
    private OkHttpClient httpClient;
    private Gson gson;
      String responsetoSend;


    private RegisterAPI(final Context context) {


        gson = new GsonBuilder().create();
        /*httpClient = new OkHttpClient.Builder()
                .build();*/
        httpClient = getUnsafeOkHttpClient();

    }

    public static RegisterAPI getInstance(Context context) {
        if (registerAPI == null)
            registerAPI = new RegisterAPI(context.getApplicationContext());

        return registerAPI;
    }

    private OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //create a new group account for user in DB

    public void registerTeacher(String userName,String surName,String name,String email,String password,String phone,String city,String country_short,String birthDate,String gender, final RegistrationCallback callback) {
        JSONObject json = new JSONObject();
        String requestBody = "createTeacher?username="+userName+"&surname="+surName+"&firstname="+name+"&email=" + email + "&password=" + password+"&cellular=" +phone+"&city="+city+"&country="+country_short+"&birthDate="+birthDate+ "&gender="+gender;
        Request request = new Request.Builder()
                .url(BASE_URL + requestBody)
                .get()
                .build();
        Log.d("RESPONSE = ", request.toString());

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("RESPONSE on fail of network = ", e.toString());
                if (callback != null)
                    callback.onNetworkError();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    String alternate = response.body().string();
                    callback.onResponse(alternate);

                }
                else {
                    if (callback != null)
                        callback.onError(RegistrationResponse.RegistrationError.UNDEFINED_ERROR);
                }

            } });
    }

    //create a new personal account for user in DB

    public void registerEmail(String userName,String surName,String name,String email,String password,String phone,String city,String country_short,String birthDate,String gender, final RegistrationCallback callback) {
        JSONObject json = new JSONObject();
            String requestBody = "createCustomer?username="+userName+"&surname="+surName+"&firstname="+name+"&email=" + email + "&password=" + password+"&cellular=" +phone+"&city="+city+"&country="+country_short+"&birthDate="+birthDate+ "&gender="+gender;
            Request request = new Request.Builder()
                    .url(BASE_URL + requestBody)
                    .get()
                    .build();
            Log.d("RESPONSE = ", request.toString());

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("RESPONSE on fail of network = ", e.toString());
                if (callback != null)
                    callback.onNetworkError();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    String alternate = response.body().string();
                    callback.onResponse(alternate);

                }
                else {
                    if (callback != null)
                        callback.onError(RegistrationResponse.RegistrationError.UNDEFINED_ERROR);
                }

            } });
    }
//Unregister product instance from the user account
public void unregUser(String userName, String productID, String serialNumb,final RegistrationCallback callback) {
    JSONObject json = new JSONObject();
    String requestBody = "unregisterProdInstance?username="+userName+"&productid="+productID+"&serialnumber="+serialNumb;
    Request request = new Request.Builder()
            .url(BASE_URL + requestBody)
            .get()
            .build();
    Log.d("RESPONSE = ", request.toString());

    httpClient.newCall(request).enqueue(new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.d("RESPONSE on fail of network = ", e.toString());
            if (callback != null)
                callback.onNetworkError();
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {

            if (response.isSuccessful()) {
                String alternate = response.body().string();
                callback.onResponse(alternate);
            }
            else {
                if (callback != null)
                    callback.onError(RegistrationResponse.RegistrationError.UNDEFINED_ERROR);
            }
        } });
}

    //Adding baby to DB
    public void createGroup(String username, String groupname, final RegistrationCallback callback) {
        JSONObject json = new JSONObject();
        String requestBody = "createGroup?teacherUsername="+username+"&groupName="+groupname;
        Request request = new Request.Builder()
                .url(BASE_URL + requestBody)
                .get()
                .build();
        Log.d("RESPONSE = ", request.toString());

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("RESPONSE on fail of network = ", e.toString());
                if (callback != null)
                    callback.onNetworkError();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    String alternate = response.body().string();
                    callback.onResponse(alternate);
                }
                else {
                    if (callback != null)
                        callback.onError(RegistrationResponse.RegistrationError.UNDEFINED_ERROR);
                }
            } });
    }
 //   API for HW creation
 public void createRecognHW(String wrstName, String wrstMac, final RegistrationCallback callback) {
    // JSONObject json = new JSONObject();
     String requestBody = "createRecogHW?hwCodename="+wrstMac+"&hwCommonName="+wrstName;
     Request request = new Request.Builder()
             .url(BASE_URL + requestBody)
             .get()
             .build();
     Log.d("MILA create new wristband = ", request.toString());

     httpClient.newCall(request).enqueue(new Callback() {
         @Override
         public void onFailure(Call call, IOException e) {
             Log.d("RESPONSE on fail of network = ", e.toString());
             if (callback != null)
                 callback.onNetworkError();
         }
         @Override
         public void onResponse(Call call, Response response) throws IOException {

             if (response.isSuccessful()) {
                 String alternate = response.body().string();
                 callback.onResponse(alternate);
             }
             else {
                 if (callback != null)
                     callback.onError(RegistrationResponse.RegistrationError.UNDEFINED_ERROR);
             }
         } });
 }

 //delete student from group
    //deleteStudentFromGroup?studentID={STUDENTID}&groupID={GROUPID}
 public void deleteStudFromGroup(String studentID,String groupID, final RegistrationCallback callback){
     String requestBody = "deleteStudentFromGroup?studentID=" + studentID+"&groupID="+groupID;
     Request request = new Request.Builder()
             .url(BASE_URL + requestBody)
             .get()
             .build();
     Log.d("MILA delete student from group = ", request.toString());

     httpClient.newCall(request).enqueue(new Callback() {
         @Override
         public void onFailure(Call call, IOException e) {
             Log.d("RESPONSE on fail of network = ", e.toString());
             if (callback != null)
                 callback.onNetworkError();
         }
         @Override
         public void onResponse(Call call, Response response) throws IOException {

             if (response.isSuccessful()) {
                 String alternate = response.body().string();
                 Log.d("student deleted",alternate);
                 callback.onResponse(alternate);
             }
             else {
                 if (callback != null)
                     callback.onError(RegistrationResponse.RegistrationError.UNDEFINED_ERROR);
             }
         } });
 }
//deleteGroup
public void deleteGroup(String teacherUsername,String groupID, final RegistrationCallback callback){
    String requestBody = "deleteGroup?teacherUsername=" + teacherUsername+"&groupID="+groupID;
    Request request = new Request.Builder()
            .url(BASE_URL + requestBody)
            .get()
            .build();
    Log.d("MILA delete group = ", request.toString());

    httpClient.newCall(request).enqueue(new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.d("RESPONSE on fail of network = ", e.toString());
            if (callback != null)
                callback.onNetworkError();
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {

            if (response.isSuccessful()) {
                String alternate = response.body().string();
                Log.d("MILA delete group",alternate);
                callback.onResponse(alternate);
            }
            else {
                if (callback != null)
                    callback.onError(RegistrationResponse.RegistrationError.UNDEFINED_ERROR);
            }
        } });
}

 //assign wristband to student
 public void assignWrst(String wristID, String studentID, final RegistrationCallback callback){
     String requestBody = "associateRecogHwToStudent?studentID=" + studentID +"&recogHwID=" + wristID;
     Request request = new Request.Builder()
             .url(BASE_URL + requestBody)
             .get()
             .build();
     Log.d("MILA assign wristband = ", request.toString());

     httpClient.newCall(request).enqueue(new Callback() {
         @Override
         public void onFailure(Call call, IOException e) {
             Log.d("RESPONSE on fail of network = ", e.toString());
             if (callback != null)
                 callback.onNetworkError();
         }
         @Override
         public void onResponse(Call call, Response response) throws IOException {

             if (response.isSuccessful()) {
                 String alternate = response.body().string();
                 Log.d("MILA assign wristband",alternate);
                 callback.onResponse(alternate);
             }
             else {
                 if (callback != null)
                     callback.onError(RegistrationResponse.RegistrationError.UNDEFINED_ERROR);
             }
         } });
 }
 //deleteWrst

    public void deleteWrst(String wristID, final RegistrationCallback callback){
        String requestBody = "deleteRecogHW?recogHwID=" + wristID;
        Request request = new Request.Builder()
                .url(BASE_URL + requestBody)
                .get()
                .build();
        Log.d("MILA delete wristband = ", request.toString());

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("RESPONSE on fail of network = ", e.toString());
                if (callback != null)
                    callback.onNetworkError();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    String alternate = response.body().string();
                    Log.d("MILA delete wristband",alternate);
                    callback.onResponse(alternate);
                }
                else {
                    if (callback != null)
                        callback.onError(RegistrationResponse.RegistrationError.UNDEFINED_ERROR);
                }
            } });
    }


//get the list of wristband
    public void getWristList(final RegistrationCallback callback){
        String requestBody = "getRecogHWList";
        Request request = new Request.Builder()
                .url(BASE_URL + requestBody)
                .get()
                .build();
        Log.d("MILA get Wristband list = ", request.toString());

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("MILA on fail of network = ", e.toString());
                if (callback != null)
                    callback.onNetworkError();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    String alternate = response.body().string();
                    Log.d("List of products",alternate);
                    callback.onResponse(alternate);
                }
                else {
                    if (callback != null)
                        callback.onError(RegistrationResponse.RegistrationError.UNDEFINED_ERROR);
                }
            } });
    }

    //Adding baby to DB
    public void registerBaby(String username, String firstname, String gender, String birthDate,String token, final RegistrationCallback callback) {
        JSONObject json = new JSONObject();
        String requestBody = "createbaby?username="+username+"&babyAlias="+firstname+"&birthDate="+birthDate+"&babyGender="+gender+"&Token="+token;
        Request request = new Request.Builder()
                .url(BASE_URL + requestBody)
                .get()
                .build();
        Log.d("MILA = ", request.toString());

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("RESPONSE on fail of network = ", e.toString());
                if (callback != null)
                    callback.onNetworkError();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    String alternate = response.body().string();
                    callback.onResponse(alternate);
                }
                else {
                    if (callback != null)
                        callback.onError(RegistrationResponse.RegistrationError.UNDEFINED_ERROR);
                }
            } });
    }

    //add student to group, the student first have to be associated to the wristband
    public void addStudentToGroup(String studentID, String groidID, final RegistrationCallback callback) {

            String requestBody = "addStudentToGroup?studentID=" + studentID + "&groupID=" + groidID;
            Request request = new Request.Builder()
                    .url(BASE_URL + requestBody)
                    .get()
                    .build();
            Log.d("RESPONSE = ", request.toString());

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("RESPONSE on fail of network = ", e.toString());
                    if (callback != null)
                        callback.onNetworkError();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (response.isSuccessful()) {
                        String alternate = response.body().string();
                        callback.onResponse(alternate);
                    } else {
                        if (callback != null)
                            callback.onError(RegistrationResponse.RegistrationError.UNDEFINED_ERROR);
                    }
                }
            });


    }

    public void queryStudentsByGroup(String username, String groidID, final RegistrationCallback callback) {
        JSONObject json = new JSONObject();
       if(groidID != null) {
            String requestBody = "getStudentsList?teacherUsername=" + username + "&groupID=" + groidID;
            Request request = new Request.Builder()
                    .url(BASE_URL + requestBody)
                    .get()
                    .build();
            Log.d("MILA = ", request.toString());

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("RESPONSE on fail of network = ", e.toString());
                    if (callback != null)
                        callback.onNetworkError();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (response.isSuccessful()) {
                        String alternate = response.body().string();
                        callback.onResponse(alternate);
                        Log.d("MILA = ", alternate);
                    } else {
                        if (callback != null)
                            callback.onError(RegistrationResponse.RegistrationError.UNDEFINED_ERROR);
                    }
                }
            });
        }
        else
        {
            String requestBody = "getStudentsList?teacherUsername=" + username;
            Request request = new Request.Builder()
                    .url(BASE_URL + requestBody)
                    .get()
                    .build();
            Log.d("MILA  = ","if the groupId= null" + request.toString());

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("RESPONSE on fail of network = ", e.toString());
                    if (callback != null)
                        callback.onNetworkError();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (response.isSuccessful()) {
                        String alternate = response.body().string();
                        callback.onResponse(alternate);
                    } else {
                        if (callback != null)
                            callback.onError(RegistrationResponse.RegistrationError.UNDEFINED_ERROR);
                    }
                }
            });
        }

    }

    public void registerStudent(String username, String firstname, String surname, String gender, String birthDate, final RegistrationCallback callback) {
        JSONObject json = new JSONObject();
        String requestBody = "createStudent?teacherusername="+username+"&surname="+surname+"&name="+firstname+"&birthDate="+birthDate+"&gender="+gender;
        Request request = new Request.Builder()
                .url(BASE_URL + requestBody)
                .get()
                .build();
        Log.d("RESPONSE = ", request.toString());

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("RESPONSE on fail of network = ", e.toString());
                if (callback != null)
                    callback.onNetworkError();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    String alternate = response.body().string();
                    callback.onResponse(alternate);
                }
                else {
                    if (callback != null)
                        callback.onError(RegistrationResponse.RegistrationError.UNDEFINED_ERROR);
                }
            } });
    }

    //Update baby

    public void updateBaby(String username, String alias, String oldAlias, String gender, String birthDate, final RegistrationCallback callback) {
        JSONObject json = new JSONObject();
        String requestBody = "updatebaby?username="+username+"&newbabyAlias="+alias+"&oldBabyAlias=" + oldAlias + "&birthDate="+birthDate+"&babyGender="+gender;
        Request request = new Request.Builder()
                .url(BASE_URL + requestBody)
                .get()
                .build();
        Log.d("RESPONSE = ", request.toString());

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("RESPONSE on fail of network = ", e.toString());
                if (callback != null)
                    callback.onNetworkError();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    String alternate = response.body().string();
                    callback.onResponse(alternate);
                }
                else {
                    if (callback != null)
                        callback.onError(RegistrationResponse.RegistrationError.UNDEFINED_ERROR);
                }
            } });
    }

    //update user
    public void updateUser(String userName,String surName,String name,String email,String password,String phone,String city,String country_short,String birthDate,String gender, final RegistrationCallback callback) {
        JSONObject json = new JSONObject();
        String requestBody = "updatecustomer?username="+userName+"&surname="+surName+"&firstname="+name+"&email=" + email + "&password=" + password+"&cellular=" +phone+"&city="+city+"&country="+country_short+"&birthDate="+birthDate+ "&gender="+gender;
        Request request = new Request.Builder()
                .url(BASE_URL + requestBody)
                .get()
                .build();
        Log.d("RESPONSE = ", request.toString());

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("RESPONSE on fail of network = ", e.toString());
                if (callback != null)
                    callback.onNetworkError();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    String alternate = response.body().string();
                    callback.onResponse(alternate);

                }
                else {
                    if (callback != null)
                        callback.onError(RegistrationResponse.RegistrationError.UNDEFINED_ERROR);
                }

            } });
    }

   //reset password

    public void resetPwd(String username, final RegistrationCallback callback)
    {
        //deletebaby?username={USERNAME}&babyAlias={BABYALIAS}
      /*  String requestBody = "deletecustomer?username=" + username;
        Request request = new Request.Builder()
                .url(BASE_URL + requestBody)
                .get()
                .build();
        Log.d("RESPONSE = ", request.toString());
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("RESPONSE on fail of network = ", e.toString());
                if (callback != null)
                    callback.onNetworkError();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    String alternate = response.body().string();

                    callback.onResponse(alternate);
                }
                else {
                    if (callback != null)
                        callback.onError(RegistrationResponse.RegistrationError.UNDEFINED_ERROR);
                }
            } });*/

      callback.onResponse("OK");
    }


    //Delete user from DB

      public void userDelete(String username, final RegistrationCallback callback)
    {
        //deletebaby?username={USERNAME}&babyAlias={BABYALIAS}
        String requestBody = "deletecustomer?username=" + username;
        Request request = new Request.Builder()
                .url(BASE_URL + requestBody)
                .get()
                .build();
        Log.d("RESPONSE = ", request.toString());
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("RESPONSE on fail of network = ", e.toString());
                if (callback != null)
                    callback.onNetworkError();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    String alternate = response.body().string();
                    Log.d("remove baby",alternate);
                    callback.onResponse(alternate);
                }
                else {
                    if (callback != null)
                        callback.onError(RegistrationResponse.RegistrationError.UNDEFINED_ERROR);
                }
            } });

    }

public void getAverageTime(String productID, String serialNumber,String childID,String formattedYesterday, String formattedDate, final RegistrationCallback callback)
{
    String requestBody = "getActivitySessionsList?babyID=" + childID + "&startTime="+formattedYesterday+"&stopTime="+formattedDate;
    Request request = new Request.Builder()
            .url(BASE_URL + requestBody)
            .get()
            .build();
    Log.d("RESPONSE = ", request.toString());
    httpClient.newCall(request).enqueue(new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.d("RESPONSE on fail of network = ", e.toString());
            if (callback != null)
                callback.onNetworkError();
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {

            if (response.isSuccessful()) {
                String alternate = response.body().string();
                Log.d("average time",alternate);
                callback.onResponse(alternate);
            }
            else {
                if (callback != null)
                    callback.onError(RegistrationResponse.RegistrationError.UNDEFINED_ERROR);
            }
        } });
}
    public void registerProdInstance(String username, String productID, String serial_Number, String toyAlias, final RegistrationCallback callback)
    {
        //deletebaby?username={USERNAME}&babyAlias={BABYALIAS}
        String requestBody = "registerProdInstance?username=" + username + "&productid="+productID+"&serialnumber="+serial_Number + "&toyalias=" + toyAlias;
        Request request = new Request.Builder()
                .url(BASE_URL + requestBody)
                .get()
                .build();
        Log.d("RESPONSE = ", request.toString());
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("RESPONSE on fail of network = ", e.toString());
                if (callback != null)
                    callback.onNetworkError();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    String alternate = response.body().string();
                    Log.d("remove baby",alternate);
                    callback.onResponse(alternate);
                }
                else {
                    if (callback != null)
                        callback.onError(RegistrationResponse.RegistrationError.UNDEFINED_ERROR);
                }
            } });

    }

    //Get product list introduced by manufacturer
    public void getCountryList(final RegistrationCallback callback){
        String requestBody = "getCountries";
        Request request = new Request.Builder()
                .url(BASE_URL + requestBody)
                .get()
                .build();
        Log.d("RESPONSE getCountry = ", request.toString());

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("RESPONSE on fail of network = ", e.toString());
                if (callback != null)
                    callback.onNetworkError();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    String alternate = response.body().string();
                    Log.d("List of countries",alternate);
                    callback.onResponse(alternate);
                }
                else {
                    if (callback != null)
                        callback.onError(RegistrationResponse.RegistrationError.UNDEFINED_ERROR);
                }
            } });
    }

    //Get product list introduced by manufacturer
    public void getProductList(final RegistrationCallback callback){
        String requestBody = "getProductList";
        Request request = new Request.Builder()
                .url(BASE_URL + requestBody)
                .get()
                .build();
        Log.d("RESPONSE getProductlist = ", request.toString());

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("RESPONSE on fail of network = ", e.toString());
                if (callback != null)
                    callback.onNetworkError();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    String alternate = response.body().string();
                    Log.d("List of products",alternate);
                    callback.onResponse(alternate);
                }
                else {
                    if (callback != null)
                        callback.onError(RegistrationResponse.RegistrationError.UNDEFINED_ERROR);
                }
            } });
    }

    //Select Products filtered by userID
    //getUserInstances?username={USERNAME}

    //start activity session from the toy
    public void start_stop_ActivitySession(String param, String babyID, String productID, String serialNumber, String activityID, final RegistrationCallback callback) {
        //  startActivitySession?productID={PRODUCTID}&serialnumber={SERIALNUMBER}&babyid={BABYID}&activityid={ACTIVITYID}
        String requestBody = param + "ActivitySession?productID="+productID+"&serialnumber=" + serialNumber + "&babyid=" + babyID + "&activityid=" + activityID;
        Request request = new Request.Builder()
                .url(BASE_URL + requestBody)
                .get()
                .build();
        Log.d("RESPONSE startActivitySession = ", request.toString());

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("RESPONSE on fail of network = ", e.toString());
                if (callback != null)
                    callback.onNetworkError();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    String alternate = response.body().string();
                    Log.d("List of products",alternate);
                    callback.onResponse(alternate);
                }
                else {
                    if (callback != null)
                        callback.onError(RegistrationResponse.RegistrationError.UNDEFINED_ERROR);
                }
            } });

    }

    public void queryProductList(String username, final RegistrationCallback callback) {

        String requestBody = "getUserInstances?username="+username;
        Request request = new Request.Builder()
                .url(BASE_URL + requestBody)
                .get()
                .build();
        Log.d("RESPONSE getUserInstances = ", request.toString());

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("RESPONSE on fail of network = ", e.toString());
                if (callback != null)
                    callback.onNetworkError();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    String alternate = response.body().string();
                    Log.d("List of products",alternate);
                    callback.onResponse(alternate);
                }
                else {
                    if (callback != null)
                        callback.onError(RegistrationResponse.RegistrationError.UNDEFINED_ERROR);
                }
            } });

    }

    public void queryGroups(String username, final RegistrationCallback callback) {

        String requestBody = "getGroupsList?teacherUsername="+username;
        Request request = new Request.Builder()
                .url(BASE_URL + requestBody)
                .get()
                .build();
        Log.d("RESPONSE getUserInstances = ", request.toString());

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("RESPONSE on fail of network = ", e.toString());
                if (callback != null)
                    callback.onNetworkError();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    String alternate = response.body().string();
                    Log.d("List of products",alternate);
                    callback.onResponse(alternate);
                }
                else {
                    if (callback != null)
                        callback.onError(RegistrationResponse.RegistrationError.UNDEFINED_ERROR);
                }
            } });

    }


    //Delete baby from DB
    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json");
    public void deleteBaby(String username, String babyAlias, final RegistrationCallback callback)
    {
        //deletebaby?username={USERNAME}&babyAlias={BABYALIAS}
        String requestBody = "deletebaby?username=" + username + "&babyAlias=" + babyAlias;
        Request request = new Request.Builder()
                .url(BASE_URL + requestBody)
                .get()
                .build();
        Log.d("RESPONSE = ", request.toString());
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("RESPONSE on fail of network = ", e.toString());
                if (callback != null)
                    callback.onNetworkError();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    String alternate = response.body().string();
                    Log.d("remove baby",alternate);
                    callback.onResponse(alternate);
                }
                else {
                    if (callback != null)
                        callback.onError(RegistrationResponse.RegistrationError.UNDEFINED_ERROR);
                }
            } });

    }

    public void controllerDataCollection(String enable, String ipAddr, final RegistrationCallback callback)
    {
        String enableValue = enable;
        JSONObject mainObj = new JSONObject();
        JSONObject logObj = new JSONObject();
        JSONObject urlData = new JSONObject();

        try{
            //postData.put("requestType", "logger");
            urlData.put("address","plet.cloud.reply.eu");
            urlData.put("port","443");
            urlData.put("path","/pletapis.svc/saveComponentEvents");
            urlData.put("protocol","https");
            urlData.put("cert","-----BEGIN CERTIFICATE-----\\nMIIE0DCCA7igAwIBAgIBBzANBgkqhkiG9w0BAQsFADCBgzELMAkGA1UEBhMCVVMx\\nEDAOBgNVBAgTB0FyaXpvbmExEzARBgNVBAcTClNjb3R0c2RhbGUxGjAYBgNVBAoT\\nEUdvRGFkZHkuY29tLCBJbmMuMTEwLwYDVQQDEyhHbyBEYWRkeSBSb290IENlcnRp\\nZmljYXRlIEF1dGhvcml0eSAtIEcyMB4XDTExMDUwMzA3MDAwMFoXDTMxMDUwMzA3\\nMDAwMFowgbQxCzAJBgNVBAYTAlVTMRAwDgYDVQQIEwdBcml6b25hMRMwEQYDVQQH\\nEwpTY290dHNkYWxlMRowGAYDVQQKExFHb0RhZGR5LmNvbSwgSW5jLjEtMCsGA1UE\\nCxMkaHR0cDovL2NlcnRzLmdvZGFkZHkuY29tL3JlcG9zaXRvcnkvMTMwMQYDVQQD\\nEypHbyBEYWRkeSBTZWN1cmUgQ2VydGlmaWNhdGUgQXV0aG9yaXR5IC0gRzIwggEi\\nMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC54MsQ1K92vdSTYuswZLiBCGzD\\nBNliF44v/z5lz4/OYuY8UhzaFkVLVat4a2ODYpDOD2lsmcgaFItMzEUz6ojcnqOv\\nK/6AYZ15V8TPLvQ/MDxdR/yaFrzDN5ZBUY4RS1T4KL7QjL7wMDge87Am+GZHY23e\\ncSZHjzhHU9FGHbTj3ADqRay9vHHZqm8A29vNMDp5T19MR/gd71vCxJ1gO7GyQ5HY\\npDNO6rPWJ0+tJYqlxvTV0KaudAVkV4i1RFXULSo6Pvi4vekyCgKUZMQWOlDxSq7n\\neTOvDCAHf+jfBDnCaQJsY1L6d8EbyHSHyLmTGFBUNUtpTrw700kuH9zB0lL7AgMB\\nAAGjggEaMIIBFjAPBgNVHRMBAf8EBTADAQH/MA4GA1UdDwEB/wQEAwIBBjAdBgNV\\nHQ4EFgQUQMK9J47MNIMwojPX+2yz8LQsgM4wHwYDVR0jBBgwFoAUOpqFBxBnKLbv\\" +
                    "n9r0FQW4gwZTaD94wNAYIKwYBBQUHAQEEKDAmMCQGCCsGAQUFBzABhhhodHRwOi8v\\nb2NzcC5nb2RhZGR5LmNvbS8wNQYDVR0fBC4wLDAqoCigJoYkaHR0cDovL2NybC5n\\nb2RhZGR5LmNvbS9nZHJvb3QtZzIuY3JsMEYGA1UdIAQ/MD0wOwYEVR0gADAzMDEG\\nCCsGAQUFBwIBFiVodHRwczovL2NlcnRzLmdvZGFkZHkuY29tL3JlcG9zaXRvcnkv\\nMA0GCSqGSIb3DQEBCwUAA4IBAQAIfmyTEMg4uJapkEv/oV9PBO9sPpyIBslQj6Zz\\n91cxG7685C/b+LrTW+C05+Z5Yg4MotdqY3MxtfWoSKQ7CC2iXZDXtHwlTxFWMMS2\\nRJ17LJ3lXubvDGGqv+QqG+6EnriDfcFDzkSnE3ANkR/0yBOtg2DZ2HKocyQetawi\\nDsoXiWJYRBuriSUBAA/NxBti21G00w9RKpv0vHP8ds42pM3Z2Czqrpv1KrKQ0U11\\nGIo/ikGQI31bS/6kA1ibRrLDYGCD+H1QQc7CoZDDu+8CL9IVVO5EFdkKrqeKM+2x\\nLXY2JtwE65/3YR8V3Idv7kaWKK2hJn0KCacuBKONvPi8BDAB\\n-----END CERTIFICATE-----\\n");
            logObj.put("enable",enableValue);
        logObj.put("url",urlData);
        mainObj.put("requestType","logger");
        mainObj.put("log",logObj);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MEDIA_TYPE,mainObj.toString());//RequestBody.create("application/json", postData.toString());
        Request request = new Request.Builder().url("http:/" + ipAddr)
                .post(body)
                .build();
        Log.d("RESPONSE = ", request.toString());

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("RESPONSE on fail of network = ", e.toString());
                if (callback != null)
                    callback.onNetworkError();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    String alternate = response.body().string();
                    Log.d("User data ",alternate);
                    callback.onResponse(alternate);
                }
                else {
                    if (callback != null)
                        callback.onError(RegistrationResponse.RegistrationError.UNDEFINED_ERROR);
                }
            } });

    }
    public void setAdress(String ipAddr, final RegistrationCallback callback){

        JSONObject postData = new JSONObject();
        try{
            postData.put("requestType", "changeHttp");
            postData.put("ipTarget","91.128.225.130");
            postData.put("portTarget","443");
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        //RequestBody postBody = new FormBody.Builder().add("message","{\"requestType\" : \"changeHttp\",\"ipTarget\" : \"91.218.225.130\",\"portTarget\" : 443}").build();
        RequestBody body = RequestBody.create(MEDIA_TYPE,postData.toString());//RequestBody.create("application/json", postData.toString());
        Request request = new Request.Builder().url("http:/" + ipAddr)
                .post(body)
                .build();
        Log.d("RESPONSE = ", request.toString());

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("RESPONSE on fail of network = ", e.toString());
                if (callback != null)
                    callback.onNetworkError();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    String alternate = response.body().string();
                    Log.d("User data ",alternate);
                    callback.onResponse(alternate);
                }
                else {
                    if (callback != null)
                        callback.onError(RegistrationResponse.RegistrationError.UNDEFINED_ERROR);
                }
            } });


    }

    //get user data from DB
    //

    public void getUserInfo(String username, final RegistrationCallback callback) {

        String requestBody = "getUserData?username="+username;
        Request request = new Request.Builder()
                .url(BASE_URL + requestBody)
                .get()
                .build();
        Log.d("RESPONSE = ", request.toString());

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("RESPONSE on fail of network = ", e.toString());
                if (callback != null)
                    callback.onNetworkError();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    String alternate = response.body().string();
                    Log.d("User data ",alternate);
                    callback.onResponse(alternate);
                }
                else {
                    if (callback != null)
                        callback.onError(RegistrationResponse.RegistrationError.UNDEFINED_ERROR);
                }
            } });

    }

    //Retrieve list of baby assossiated with the user
       public void queryBaby(String username, final RegistrationCallback callback) {

       String requestBody = "getBabyList?username="+username;
        Request request = new Request.Builder()
                .url(BASE_URL + requestBody)
                .get()
                .build();
        Log.d("RESPONSE = ", request.toString());

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("RESPONSE on fail of network = ", e.toString());
                if (callback != null)
                    callback.onNetworkError();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    String alternate = response.body().string();
                    Log.d("List of babies",alternate);
                    callback.onResponse(alternate);
                }
                else {
                    if (callback != null)
                        callback.onError(RegistrationResponse.RegistrationError.UNDEFINED_ERROR);
                }
            } });

    }

    public void loginAPI(String username, String passhash, Integer isTeacher, final RegistrationCallback callback){

        JSONObject postData = new JSONObject();
        try{
            postData.put("username", username);
            postData.put("passhash",passhash);
            postData.put("isTeacher",isTeacher);
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        //RequestBody postBody = new FormBody.Builder().add("message","{\"requestType\" : \"changeHttp\",\"ipTarget\" : \"91.218.225.130\",\"portTarget\" : 443}").build();
        RequestBody body = RequestBody.create(MEDIA_TYPE,postData.toString());//RequestBody.create("application/json", postData.toString());
        Request request = new Request.Builder().url("https://plet.cloud.reply.eu/pletapis.svc/login")
                .post(body)
                .build();
        Log.d("RESPONSE = ", request.toString());

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("RESPONSE on fail of network = ", e.toString());
                if (callback != null)
                    callback.onNetworkError();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    String alternate = response.body().string();
                    Log.d("User data ",alternate);
                    callback.onResponse(alternate);
                }
                else {
                    if (callback != null)
                        callback.onError(RegistrationResponse.RegistrationError.UNDEFINED_ERROR);
                }
            } });


    }



    public void userLogin(String userName, String passWord, final RegistrationCallback callback) {

            Log.d("RESPONSE user login = ", userName);
            String requestBody = "login?username="+userName + "&passhash=" + passWord;

            Request request = new Request.Builder()
                    .url(BASE_URL + requestBody)
                    .get()
                    // .tag(REGISTRATION_EMAIL_ENDPOINT)
                    .build();
            Log.d("Request for user login = ", request.toString());
            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("RESPONSE on fail of network = ", e.toString());
                   if (callback != null)
                        callback.onNetworkError();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (response.isSuccessful()) {
                        String alternate = response.body().string();
                        callback.onResponse(alternate);

                    }
                    else {
                        if (callback != null)
                            callback.onError(RegistrationResponse.RegistrationError.UNDEFINED_ERROR);
                    }

                } });


}


public interface RegistrationCallback {
        void onResponse(String str);

        void onError(RegistrationResponse.RegistrationError error);

        void onNetworkError();
    }
    /*public interface AuthenticationCallback {
        void onResponse(AuthResponse response);

        void onError(AuthResponse.AuthenticationError error);

        void onNetworkError();
    }*/
    public interface ResponseCallback<T> {
        void onResponse(Call call, T t);

        void onAuthError(Call call);

        void onError(Call call);

        void onNetworkError(Call call);

    }

}