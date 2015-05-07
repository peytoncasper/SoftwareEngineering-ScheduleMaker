package edu.uta.ucs;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Messenger;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
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
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class HTTPService extends IntentService {

    public static final String REQUEST_TYPE= "edu.uta.ucs.REQUEST_TYPE";
    public static final String REQUEST_GET_URL = "edu.uta.ucs.REQUEST_GET_URL";
    public static final String REQUEST_JSON_POST = "edu.uta.ucs.REQUEST_JSON_POST";
    public static final String SPOOF_SERVER_RESPONSE = "edu.uta.ucs.SPOOF_SERVER_RESPONSE";
    public static final String SERVER_RESPONSE = "edu.uta.ucs.SERVER_RESPONSE";
    public static final String SPOOFED_RESPONSE = "edu.uta.ucs.SPOOFED_RESPONSE";
    public static final String SOURCE_INTENT = "SOURCE_INTENT";
    public static final String SOURCE_OPCODE = "SOURCE_OPCODE";
    public static final String BAD_RESPONSE = "{\"Success\":false}";
    public static final String GOOD_RESPONSE = "{\"Success\":true}";

    Messenger messenger;
    String url;


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
            case REQUEST_GET_URL:
                getURL(intent);
                return;
            case REQUEST_JSON_POST:
                postJSON(intent);
                return;

        }
    }

    private void postJSON(Intent intent){

        String response;
        URL targetURL = null;

        String urlString = intent.getStringExtra(REQUEST_GET_URL).replace(" ", "");
        String source = intent.getStringExtra(SOURCE_INTENT);
        String jsonString = intent.getStringExtra(REQUEST_JSON_POST);

        try {
            targetURL= new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.d("HTTPService SOURCE", source);
        Log.d("HTTPService URL", url);

        if (url.equalsIgnoreCase(SPOOF_SERVER_RESPONSE)) {
            response = intent.getStringExtra(SPOOFED_RESPONSE);
            Log.d("HTTPGetService", "Spoofing response");
        }
        else
            response = postJSON(targetURL, jsonString);

        Intent broadcastIntent = new Intent(intent.getStringExtra(SOURCE_INTENT));
        broadcastIntent.putExtra(SERVER_RESPONSE, response);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
        sendBroadcast(broadcastIntent);
    }

    private void getURL(Intent intent){

        String response;

        String url = intent.getStringExtra(REQUEST_GET_URL);    // Get url
        url = url.replace(" ", "");                         // Remove any whitespace
        String source = intent.getStringExtra(SOURCE_INTENT);

        Log.d("HTTPService SOURCE", source);
        Log.d("HTTPService URL", url);

        if (url.equalsIgnoreCase(SPOOF_SERVER_RESPONSE)) {
            response = intent.getStringExtra(SPOOFED_RESPONSE);
            if(response == null)
                response = BAD_RESPONSE;
            Log.i("HTTPService SPOOF", "Spoofing response");
            Log.i("HTTPService SPOOF", response);
        }
        else
            response = fetchJSON(url);

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

        String response = null;
        URL url = null;

        try {
            url = targetURL;

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url.toURI());

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

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return response;
    }

    /**
     * Making service call
     * @url - url to make request
     * */
    public String fetchJSON(String url) {

        Log.d("HTTPGetService URL:", url);
        String response = null;
        try {
            // http client
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
            HttpConnectionParams.setSoTimeout(httpParams, 45000);
            DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;
            Log.d("test:", "fetchJSON HTTP parameters set");

            HttpGet httpGet = new HttpGet(url);
            Log.d("test:", "HTTPGet setup");
            httpResponse = httpClient.execute(httpGet);
            Log.d("test:", "HTTPGet executed - response received");

            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.d("Service Test", "HTTP Request Failed - UnsupportedEncodingException");
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            Log.d("Service Test", "HTTP Request Failed - ClientProtocolException");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Service Test", "HTTP Request Failed - IOException");
            response = "{\"Success\":false}";
            Toast.makeText(getBaseContext(), response, Toast.LENGTH_LONG).show();

        }

        Log.d("Server reply:", response);
        return response;
    }

    public static void PostJSON(String targetURL, JSONObject jsonToPost,String recieverTag, Context context){

        Intent intent = new Intent(context, HTTPService.class);
        intent.putExtra(HTTPService.REQUEST_TYPE, HTTPService.REQUEST_JSON_POST);
        intent.putExtra(HTTPService.REQUEST_JSON_POST, jsonToPost.toString());

        if(UserData.spoofServer()){ // Spoof server response

            Log.i("HTTPService", "Spoofing JSON Post to URL: " + targetURL);

            intent.putExtra(HTTPService.REQUEST_JSON_POST, HTTPService.SPOOF_SERVER_RESPONSE);

            String spoofData = loadSpoofData(targetURL); // Attempt to load spoof data from file

            // Check to see if specific URL had data available
            // if not, strip parameters and get generic response
            if(spoofData == null) {
                String spoofURL = null;
                int baseURLEnd = targetURL.indexOf("?") - 1;
                if (baseURLEnd != -1)
                    spoofURL = targetURL.substring(0, baseURLEnd);

                if(spoofURL != null) // if there was a generic response, use it
                    spoofData = loadSpoofData(spoofURL);
                else // if there was not a generic response return a json with success false
                    spoofData = BAD_RESPONSE;
            }

            intent.putExtra(HTTPService.SPOOFED_RESPONSE, spoofData);
        }
        else{   // Attempt a real server response

            Log.i("HTTPService", "Creating a url request for: " + targetURL);

            intent.putExtra(HTTPService.REQUEST_JSON_POST, targetURL);

        }

        intent.putExtra(HTTPService.SOURCE_INTENT, recieverTag);
        context.startService(intent);

    }

    /**
     * Creates and starts a HTTPService for URL response
     *
     * @param urlToFetch
     * @param recieverTag
     * @param context
     * @throws MalformedURLException
     */
    public static void FetchURL(String urlToFetch, String recieverTag, Context context){

        Intent intent = new Intent(context, HTTPService.class);
        intent.putExtra(HTTPService.REQUEST_TYPE, HTTPService.REQUEST_GET_URL);

        if(UserData.spoofServer()){ // Spoof server response

            Log.i("HTTPService", "Spoofing response for url request: " + urlToFetch);

            intent.putExtra(HTTPService.REQUEST_GET_URL, HTTPService.SPOOF_SERVER_RESPONSE);

            String spoofData = loadSpoofData(urlToFetch);

            // Check to see if specific URL had data available
            // if not, strip parameters and get generic response
            if(spoofData == null) {
                String spoofURL = null;
                int baseURLEnd = urlToFetch.indexOf("?");
                if (baseURLEnd != -1)
                    spoofURL = urlToFetch.substring(0, baseURLEnd);

                if(spoofURL != null)
                    spoofData = loadSpoofData(spoofURL);
                else
                    spoofData = BAD_RESPONSE;
                Log.i("HTTPService FetchURL", "Spoof URL:" + spoofURL);
            }


            Log.i("HTTPService FetchURL", "Spoof Data:" + spoofData);
            intent.putExtra(HTTPService.SPOOFED_RESPONSE, spoofData);
        }
        else{   // Attempt a real server response

            Log.i("HTTPService", "Creating a url request for: " + urlToFetch);

            intent.putExtra(HTTPService.REQUEST_GET_URL, urlToFetch);

        }

        intent.putExtra(HTTPService.SOURCE_INTENT, recieverTag);
        context.startService(intent);
    }

    private static String loadSpoofData(String url){
        String spoofData = null;

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
        String fileContent = null;

        try {

            InputStream is = UserData.getContext().getAssets().open(fileName);

            int size = is.available();

            byte[] buffer = new byte[size];

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
