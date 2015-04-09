package edu.uta.ucs;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import edu.uta.ucs.HTTPGetService.HTTPGetServiceBinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends ActionBarActivity {

    String[] desiredCourseList = {};//{"ENGL-1301","MATH-1426","PHYS-1443","CSE-1105"};
    String baseURL = "http://ucs-scheduler.cloudapp.net/UTA/ClassStatus?classes=";
    TextView responseDisplay;

    HTTPGetService httpGetService;
    boolean httpGetServiceBound = false;
    Messenger httpGetServiceMessenger = null;

    private TextView mainText;
    private Switch spoofServerSwitch;
    private ListView sectionListView;
    private ResponseReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, filter);

        spoofServerSwitch = (Switch) findViewById(R.id.spoofServer);
        spoofServerSwitch.setChecked(true);

        responseDisplay = (TextView)findViewById(R.id.textView);
        responseDisplay.setText("Press FETCH JSON to attempt a data fetch");

        sectionListView = (ListView) findViewById(R.id.listView);

        Intent connectToHTTPGetService = new Intent(this, HTTPGetService.class);
        bindService(connectToHTTPGetService, httpGetServiceConnection, Context.BIND_AUTO_CREATE);
        httpGetServiceBound = true;



    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(httpGetServiceBound){
            httpGetServiceConnection = null;
            httpGetServiceBound = false;
        }
    }

    public void requestJSON(View view){

        responseDisplay.setText("Please wait, attempting to fetch data...");


        StringBuilder urlBuilder = new StringBuilder(baseURL);
        String classTextField = ((TextView) findViewById(R.id.editText)).getText().toString();

        Log.d("classTextField",classTextField);
        if (classTextField.length() > 0 ){
            urlBuilder.append(classTextField + ",");
        }
        String url = urlBuilder.length() > 0 ? urlBuilder.substring( 0, urlBuilder.length() - 1 ): "";

        Message message = Message.obtain(null,0,0,0,0);
        Bundle bundle = new Bundle();
        bundle.putString(HTTPGetService.URL_REQUEST, url);
        boolean switchStatus = spoofServerSwitch.isChecked();
        Log.i("MainActivity url",url);
        httpGetService.fetchJSON(url);

        /*
        intent.putExtra("edu.uta.ucs.URL_REQUEST", url);
        intent.putExtra("edu.uta.ucs.SPOOF_SERVER_RESPONSE", switchStatus);
        */
    }


    public class ResponseReceiver extends BroadcastReceiver{
        public static final String ACTION_RESP =
                "edu.uta.ucs.intent.action.MESSAGE_PROCESSED";

        @Override
        public void onReceive(Context context, Intent intent) {
            String response = intent.getStringExtra("edu.uta.ucs.SERVER_RESPONSE");
            Log.d("Received: ",response);
            responseDisplay.setText("About to Show text!");
            responseDisplay.setText(response);
            ArrayList<Course> courseList = new ArrayList<Course>();
            int numberOfSectionsTotal = 0;

            try {
                JSONObject rawResult = new JSONObject(response);
                JSONArray jsonCourses = rawResult.getJSONArray("Results");
                float timeTaken = Float.parseFloat(rawResult.getString("TimeTaken"));
                Log.d("New Request Time Taken:", Float.toString(timeTaken));
                courseList.ensureCapacity(jsonCourses.length());

                for(int index = jsonCourses.length(); index != 0;index--){
                    Log.d("New Course: ", jsonCourses.getJSONObject(index - 1).toString());
                    courseList.add( new Course(jsonCourses.getJSONObject(index - 1)));
                    numberOfSectionsTotal++;
                }
                Collections.reverse(courseList);

                responseDisplay.setText(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ArrayList<Section> sectionArrayList = new ArrayList<Section>(numberOfSectionsTotal);
            for (Course course : courseList){
                sectionArrayList.addAll(course.getSectionList());
            }

            Log.d("New Section", "ArrayList Built");
            ListAdapter adapter = new MySectionArrayAdapter(MainActivity.this, R.layout.list_item, sectionArrayList);
            Log.d("New Section", "ListView Built");
            sectionListView.setAdapter(adapter);

        }
    }

    ServiceConnection httpGetServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            HTTPGetServiceBinder binder = (HTTPGetServiceBinder) service;
            httpGetService = binder.getService();
            httpGetServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            httpGetServiceMessenger = null;
            httpGetServiceBound = false;
        }
    };
}
