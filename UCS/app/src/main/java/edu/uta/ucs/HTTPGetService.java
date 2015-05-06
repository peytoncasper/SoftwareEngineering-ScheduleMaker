package edu.uta.ucs;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class HTTPGetService extends IntentService {

    public static final String URL_REQUEST = "edu.uta.ucs.URL_REQUEST";
    public static final String SPOOF_SERVER_RESPONSE = "edu.uta.ucs.SPOOF_SERVER_RESPONSE";
    public static final String SERVER_RESPONSE = "edu.uta.ucs.SERVER_RESPONSE";
    public static final String SPOOFED_RESPONSE = "edu.uta.ucs.SPOOFED_RESPONSE";
    public static final String SPOOF_SERVER = "SPOOF";
    public static final String SOURCE_INTENT = "SOURCE_INTENT";
    public static final String SOURCE_OPCODE = "SOURCE_OPCODE";
    public static final String SPOOFED_LOGIN_RESPONSE = "{\"Success\":true,\"Email\":\"a@a.a\"}";

    Messenger messenger;
    String url;


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */

    public HTTPGetService() {
        super("HTTPGetService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String response;

        String url = intent.getStringExtra(URL_REQUEST);    // Get url
        url = url.replace(" ", "");                         // Remove any whitespace
        String source = intent.getStringExtra(SOURCE_INTENT);

        Log.d("HTTPGetService SOURCE", source);
        Log.d("HTTPGetService URL", url);

        if (url.equalsIgnoreCase(SPOOF_SERVER)) {
            response = intent.getStringExtra(SPOOFED_RESPONSE);
            Log.d("HTTPGetService", "Spoofing response");
        }
        else
            response = fetchJSON(url);

        SharedPreferences.Editor editor = getSharedPreferences("SharedPrefs", MODE_PRIVATE).edit();
        editor.putString("Server Message",response);
        editor.apply();

        Intent broadcastIntent = new Intent(intent.getStringExtra(SOURCE_INTENT));
        broadcastIntent.putExtra(SERVER_RESPONSE, response);
        broadcastIntent.putExtra(SOURCE_OPCODE, intent.getIntExtra(SOURCE_OPCODE, 0));
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
        sendBroadcast(broadcastIntent);

        this.stopSelf();
        //Log.d("Broadcasting Response: ",response);
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

    /**
     * Making service call
     * @url - url to make request
     * */
    public String fetchJSON(String url) {

        Log.d("HTTPGetService URL:", url);
        String response = "";
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

    /**
     * Starts a service for URL
     *
     * @param urlToFetch
     * @param reciever
     * @param context
     * @throws MalformedURLException
     */
    public static void FetchURL(String urlToFetch, String reciever, Context context){
        URL url = null;
        try {
            url = new URL(urlToFetch);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }
        Intent intent = new Intent(context, HTTPGetService.class);
        intent.putExtra(HTTPGetService.URL_REQUEST, url.toString());

        intent.putExtra(HTTPGetService.SOURCE_INTENT, reciever);
        context.startService(intent);
    }


}
