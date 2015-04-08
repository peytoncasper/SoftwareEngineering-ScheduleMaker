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
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
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

    public final String URL_REQUEST = "edu.uta.ucs.URL_REQUEST";
    public final String SPOOF_SERVER_RESPONSE = "edu.uta.ucs.SPOOF_SERVER_RESPONSE";

    private final String SPOOFED_RESPONSE ="" +
            "{\"Results\":[{\"CourseId\":\"CSE-3330\",\"CourseName\":\"CSE 3330 - DATABASE SYSTEMS AND FILE STRUCTURES\",\"CourseResults\":[{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"89473\",\"Section\":\"001\",\"CourseName\":null,\"Room\":\"TBA\",\"Instructor\":\"Medhat M Saleh\",\"MeetingTime\":\"2:00PM-4:50PM\",\"Status\":\"Open\"}]},{\"CourseId\":\"CSE-2320\",\"CourseName\":\"CSE 2320 - ALGORITHMS \\u0026amp; DATA STRUCTURES\",\"CourseResults\":[{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"87695\",\"Section\":\"001\",\"CourseName\":null,\"Room\":\"TBA\",\"Instructor\":\"Alexandra Stefan\",\"MeetingTime\":\"9:00AM-10:20AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"85768\",\"Section\":\"002\",\"CourseName\":null,\"Room\":\"TBA\",\"Instructor\":\"Bob P Weems\",\"MeetingTime\":\"11:00AM-12:20PM\",\"Status\":\"Open\"}]}],\"TimeTaken\":6.5217624}"
             + "";
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
        String url = intent.getStringExtra(URL_REQUEST);
        String response;
        boolean spoofServer = intent.getExtras().getBoolean(SPOOF_SERVER_RESPONSE);
        Log.d("New Request URL", url);
        Log.d("Spoof New Request: ", ((Boolean) spoofServer).toString());

        if (spoofServer)
            response = SPOOFED_RESPONSE;
        else
            response = fetchJSON(url);


        response = response.replaceAll("Open", "OPEN");
        response = response.replaceAll("Closed", "CLOSED");
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MainActivity.ResponseReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra("edu.uta.ucs.SERVER_RESPONSE", response);
        sendBroadcast(broadcastIntent);

        //Log.d("Broadcasting Response: ",response);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Service has been created", Toast.LENGTH_LONG).show();
        Log.d("Service Test", "Service has been created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service has been stopped", Toast.LENGTH_LONG).show();
        Log.d("Service Test", "Service has been stopped");
    }

    /**
     * Making service call
     * @url - url to make request
     * */
    public String fetchJSON(String url) {

        Log.d("Service URL:", url);
        String response = "";
        try {
            // http client
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
            HttpConnectionParams.setSoTimeout(httpParams, 45000);
            DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
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

        Log.d("Server reply:", response);
        return response;
    }

    public class LocalBinder extends Binder {

        public HTTPGetService getService() {
            return HTTPGetService.this;
        }
    }
}
