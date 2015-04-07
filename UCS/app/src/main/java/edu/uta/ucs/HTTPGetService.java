package edu.uta.ucs;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class HTTPGetService extends IntentService {

    private final IBinder mbinder = new LocalBinder();
    String url;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public HTTPGetService() {
        super("HTTPGetService");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mbinder;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String url = intent.getStringExtra("url");
        String response = fetchJSON(url);

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MainActivity.ResponseReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra("edu.uta.ucs.SERVER_RESPONSE", response);
        sendBroadcast(broadcastIntent);

        //Log.d("Broadcasting Response: ",response);
    }

    public class LocalBinder extends Binder{

        public HTTPGetService getService(){
            return HTTPGetService.this;
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this,"Service has been created", Toast.LENGTH_LONG).show();
        Log.d("Service Test", "Service has been created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this,"Service has been stopped", Toast.LENGTH_LONG).show();
        Log.d("Service Test", "Service has been stopped");

    }

    public String checkURL(){
        String result = null;

        return result;
    }

    /**
     * Making service call
     * @url - url to make request
     * */
    public String fetchJSON(String url) {

        Log.d("Service URL:",url);
        String response = "";

        try {
            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;
            Log.d("test:", "test1");

            HttpGet httpGet = new HttpGet(url);
            httpResponse = httpClient.execute(httpGet);
            Log.d("test:", "test2");

            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);
            Log.d("test:", "test3");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Log.d("JSON reply:",response);

        return response;
    }


}
