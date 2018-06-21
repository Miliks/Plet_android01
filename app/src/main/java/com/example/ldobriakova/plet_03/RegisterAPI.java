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

    //create a new user in DB

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

    //Adding baby to DB
    public void registerBaby(String username, String firstname, String gender, String birthDate, final RegistrationCallback callback) {
        JSONObject json = new JSONObject();
        String requestBody = "createbaby?username="+username+"&babyAlias="+firstname+"&birthDate="+birthDate+"&babyGender="+gender;
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
        String requestBody = "updatebaby?username="+username+"&babyAlias="+alias+"&oldBabyAlias=" + oldAlias + "&birthDate="+birthDate+"&babyGender="+gender;
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


    //Delete baby from DB

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