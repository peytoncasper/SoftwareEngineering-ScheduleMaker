package edu.uta.ucs;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * This IntentService is designed to interact with a web page and either get or send information there
 * helper methods.
 */
public class HTTPService extends IntentService {

    public static final String REQUEST_TYPE= "edu.uta.ucs.REQUEST_TYPE";
    public static final String REQUEST_URL = "edu.uta.ucs.REQUEST_URL";
    public static final String REQUEST_JSON_POST = "edu.uta.ucs.REQUEST_JSON_POST";
    public static final String SPOOF_SERVER_RESPONSE = "edu.uta.ucs.SPOOF_SERVER_RESPONSE";
    public static final String SERVER_RESPONSE = "edu.uta.ucs.SERVER_RESPONSE";
    public static final String SPOOFED_RESPONSE = "edu.uta.ucs.SPOOFED_RESPONSE";
    public static final String SOURCE_INTENT = "SOURCE_INTENT";
    public static final String BAD_RESPONSE = "{\"Success\":false}";

    private static final int socketTimeoutMilliseconds = 45000; // 45 second timeout
    private static final int connectionTimeoutMilliseconds = 10000; // 10 second timeout
    // Unused public static final String GOOD_RESPONSE = "{\"Success\":true}";


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */

    public HTTPService() {
        super("HTTPGetService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String requestType = intent.getStringExtra(REQUEST_TYPE);

        switch (requestType){
            case REQUEST_URL:
                getURL(intent);
                break;
            case REQUEST_JSON_POST:
                postJSON(intent);
                break;

        }
    }

    private void postJSON(Intent intent){

        String response;


        String urlString = intent.getStringExtra(REQUEST_URL).replace(" ", "");
        String source = intent.getStringExtra(SOURCE_INTENT);
        String jsonString = intent.getStringExtra(REQUEST_JSON_POST);

        if (!urlString.equalsIgnoreCase(SPOOF_SERVER_RESPONSE)) {
            try {
                URL targetURL = new URL(urlString);
                response = postJSON(targetURL, jsonString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                response = BAD_RESPONSE.substring(0, BAD_RESPONSE.length()-1) + ",\"Message\":\"Malformed URL\"}";
            }
        }
        else{
            response = intent.getStringExtra(SPOOFED_RESPONSE);
            Log.i("HTTPGetService", "Spoofing response");
        }

        Log.i("HTTPService SOURCE", source);
        Log.i("HTTPService URL", urlString);

        Log.i("HTTPService Response", response);

        Intent broadcastIntent = new Intent(intent.getStringExtra(SOURCE_INTENT));
        broadcastIntent.putExtra(SERVER_RESPONSE, response);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
        sendBroadcast(broadcastIntent);
    }

    private void getURL(Intent intent){

        String response;

        String urlString = intent.getStringExtra(REQUEST_URL).replace(" ", "");                         // Remove any whitespace
        String source = intent.getStringExtra(SOURCE_INTENT);


        if (!urlString.equalsIgnoreCase(SPOOF_SERVER_RESPONSE)) {

            try {
                URL url = new URL(urlString);
                response = fetchJSON(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                response = BAD_RESPONSE.substring(0, BAD_RESPONSE.length()-1) + ",\"Message\":\"Malformed URL\"}";
            }
        }
        else{
            response = intent.getStringExtra(SPOOFED_RESPONSE);
            Log.i("HTTPGetService", "Spoofing response");
        }
        Log.i("HTTPService SOURCE", source);
        Log.i("HTTPService URL", urlString);

        Log.i("HTTPService Response", response);

        Intent broadcastIntent = new Intent(intent.getStringExtra(SOURCE_INTENT));
        broadcastIntent.putExtra(SERVER_RESPONSE, response);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
        sendBroadcast(broadcastIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Toast.makeText(getBaseContext(), "HTTPGetService has been created", Toast.LENGTH_LONG).show();
        Log.d("Service Test", "HTTPGetService has been created");
        //messenger = new Messenger(new MessageHandler());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Toast.makeText(getBaseContext(), "HTTPGetService has been stopped", Toast.LENGTH_LONG).show();
        Log.d("Service Test", "HTTPGetService has been stopped");
    }

    public String postJSON(URL targetURL, String jsonString){

        String response;


        try {

            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, connectionTimeoutMilliseconds);
            HttpConnectionParams.setSoTimeout(httpParams, socketTimeoutMilliseconds);
            HttpClient httpClient = new DefaultHttpClient(httpParams);
            HttpPost httpPost = new HttpPost(targetURL.toURI());

            // Prepare JSON to send by setting the entity
            httpPost.setEntity(new StringEntity(jsonString, "UTF-8"));

            // Set up the header types needed to properly transfer JSON
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept-Encoding", "application/json");
            httpPost.setHeader("Accept-Language", "en-US");

            // Execute POST
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            Log.d("HTTPService postJSON", "HTTP Request Failed");
            response = BAD_RESPONSE.substring(0, BAD_RESPONSE.length()-1) + ",\"Message\":\"bad connection\"}";
        }

        return response;
    }

    /**
     * Making service call
     * @param url - url to make request
     * */
    public String fetchJSON(URL url) {

        Log.d("HTTPGetService URL:", url.toString());
        String response;
        try {
            // http client
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, connectionTimeoutMilliseconds);
            HttpConnectionParams.setSoTimeout(httpParams, socketTimeoutMilliseconds);
            DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
            HttpEntity httpEntity;
            HttpResponse httpResponse;
            Log.d("test:", "fetchJSON HTTP parameters set");

            HttpGet httpGet = new HttpGet(url.toURI());
            Log.d("test:", "HTTPGet setup");
            httpResponse = httpClient.execute(httpGet);
            Log.d("test:", "HTTPGet executed - response received");

            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            Log.d("HTTPService fetchJSON", "HTTP Request Failed");
            response = BAD_RESPONSE.substring(0, BAD_RESPONSE.length()-1) + ",\"Message\":\"bad connection\"}";
        }


        Log.d("Server reply:", response);
        return response;
    }

    /**
     * Creates and starts a HTTPService for JSON post to a url
     *
     * @param targetURL String url where JSON will be posted
     * @param jsonToPost JSONObject to be posted
     * @param recieverTag String will be the intentFilter when response is posted
     * @param context context to create intent with. Usually will be the calling class followed by ".this"
     *                <br>EX: MainActivity.this
     */
    public static void PostJSON(String targetURL, JSONObject jsonToPost,String recieverTag, Context context){

        Intent intent = new Intent(context, HTTPService.class);
        intent.putExtra(HTTPService.REQUEST_TYPE, HTTPService.REQUEST_JSON_POST);
        intent.putExtra(HTTPService.REQUEST_JSON_POST, jsonToPost.toString());

        if(UserData.spoofServer()){ // Spoof server response

            Log.i("HTTPService PostJSON", "Spoofing JSON Post to URL: " + targetURL);

            intent.putExtra(HTTPService.REQUEST_URL, HTTPService.SPOOF_SERVER_RESPONSE);

            String spoofData = loadSpoofData(targetURL); // Attempt to load spoof data from file

            // Check to see if specific URL had data available
            // if not, strip parameters and get generic response
            if(spoofData == null) {
                String spoofURL = null;
                int baseURLEnd = targetURL.indexOf("?");
                if (baseURLEnd != -1)
                    spoofURL = targetURL.substring(0, baseURLEnd);

                if(spoofURL != null) // if there was a generic response, use it
                    spoofData = loadSpoofData(spoofURL);
                if(spoofData == null) // if there was not a generic response return a json with success false and a message
                    spoofData = BAD_RESPONSE.substring(0, BAD_RESPONSE.length()-1) + ",\"Message\":\"no spoof URL response stored\"}";
            }

            Log.i("HTTPService PostJSON", "Spoof response expected: " + spoofData);
            intent.putExtra(HTTPService.SPOOFED_RESPONSE, spoofData);
        }
        else{   // Attempt a real server response

            Log.i("HTTPService PostJSON", "Creating a url request for: " + targetURL);

            intent.putExtra(HTTPService.REQUEST_URL, targetURL);

        }

        intent.putExtra(HTTPService.SOURCE_INTENT, recieverTag);
        context.startService(intent);

    }

    /**
     * Creates and starts a HTTPService for URL response
     *
     * @param urlToFetch String url to fetch JSON from
     * @param recieverTag String will be the intentFilter when response is posted
     * @param context context to create intent with. Usually will be the calling class followed by ".this"
     *                <br>EX: MainActivity.this
     */
    public static void FetchURL(String urlToFetch, String recieverTag, Context context){

        Intent intent = new Intent(context, HTTPService.class);
        intent.putExtra(HTTPService.REQUEST_TYPE, HTTPService.REQUEST_URL);

        if(UserData.spoofServer()){ // Spoof server response

            Log.i("HTTPService", "Spoofing response for url request: " + urlToFetch);

            intent.putExtra(HTTPService.REQUEST_URL, HTTPService.SPOOF_SERVER_RESPONSE);

            String spoofData = loadSpoofData(urlToFetch);

            // Check to see if specific URL had data available
            // if not, strip parameters and get generic response
            if(spoofData == null) {
                String spoofURL = null;
                int baseURLEnd = urlToFetch.indexOf("?");
                if (baseURLEnd != -1)
                    spoofURL = urlToFetch.substring(0, baseURLEnd);

                if(spoofURL != null) // if there was a generic response, use it
                    spoofData = loadSpoofData(spoofURL);
                if(spoofData == null) // if there was not a generic response return a json with success false and a message
                    spoofData = BAD_RESPONSE.substring(0, BAD_RESPONSE.length()-1) + ",\"Message\":\"no spoof URL response stored\"}";
            }

            Log.i("HTTPService FetchURL", "Spoof Data:" + spoofData);
            intent.putExtra(HTTPService.SPOOFED_RESPONSE, spoofData);
        }
        else{   // Attempt a real server response

            Log.i("HTTPService", "Creating a url request for: " + urlToFetch);

            intent.putExtra(HTTPService.REQUEST_URL, urlToFetch);

        }

        intent.putExtra(HTTPService.SOURCE_INTENT, recieverTag);
        context.startService(intent);
    }

    private static String loadSpoofData(String url){
        String spoofData;

        spoofData = loadStringFromAsset("spoof_data.json");

        try {
            JSONObject spoofDataJSON = new JSONObject(spoofData);
            if (spoofDataJSON.has(url))
                spoofData = spoofDataJSON.getString(url);
            else
                spoofData = null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return spoofData;
    }


    private static String loadStringFromAsset(String fileName){
        String fileContent;

        try {

            InputStream is = UserData.getContext().getAssets().open(fileName);

            int size = is.available();

            byte[] buffer = new byte[size];

            //noinspection ResultOfMethodCallIgnored
            is.read(buffer);

            is.close();

            fileContent = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return fileContent;
    }


}
