package edu.uta.ucs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity {
    public static final String ACTION_RESP ="edu.uta.ucs.intent.action.MAIN_ACTIVITY";

    String[] desiredCourseList = {};//{"ENGL-1301","MATH-1426","PHYS-1443","CSE-1105"};
    String baseURL = "http://ucs-scheduler.cloudapp.net/UTA/ClassStatus?classes=";
    TextView responseDisplay;

    HTTPGetService httpGetService;
    boolean dbServiceStatus;

    private TextView mainText;
    private EditText courseInput;
    private Switch spoofServerSwitch;
    private Switch useDefaultCourseList;
    private ListView sectionListView;
    private ResponseReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_main);

        courseInput =((EditText) findViewById(R.id.editText));

        spoofServerSwitch = (Switch) findViewById(R.id.spoofServerSwitch);
        spoofServerSwitch.setChecked(true);

        useDefaultCourseList = (Switch) findViewById(R.id.useDefaultCourseListSwitch);
        useDefaultCourseList.setChecked(false);
        useDefaultCourseList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    courseInput.setText("ENGL-1301,MATH-1426,PHYS-1443,CSE-1105");
                }
                else courseInput.setText("");
            }
        });

        responseDisplay = (TextView)findViewById(R.id.textView);
        responseDisplay.setText("Press FETCH JSON to attempt a data fetch");

        sectionListView = (ListView) findViewById(R.id.listView);

    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter filter = new IntentFilter(MainActivity.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);

        receiver = new ResponseReceiver();

        registerReceiver(receiver, filter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void requestJSON(View view){

        responseDisplay.setText("Please wait, attempting to fetch data...");

        StringBuilder urlBuilder = new StringBuilder(baseURL);
        String classTextField = courseInput.getText().toString();
        urlBuilder.append( ( classTextField.length() > 0 ? classTextField:"") + "," );
        Log.d("URL BUILDING", urlBuilder.toString());
        String url = urlBuilder.length() > 0 ? urlBuilder.substring( 0, urlBuilder.length() - 1 ): "";

        boolean switchStatus = spoofServerSwitch.isChecked();

        Intent intent = new Intent(this, HTTPGetService.class);
        if(spoofServerSwitch.isChecked()) {
            intent.putExtra(HTTPGetService.URL_REQUEST, HTTPGetService.SPOOF_SERVER);
            intent.putExtra(HTTPGetService.SPOOFED_RESPONSE, HTTPGetService.SPOOFED_CLASSLIST_RESPONSE);
        }
        else
            intent.putExtra(HTTPGetService.URL_REQUEST, url);

        intent.putExtra(HTTPGetService.SOURCE_INTENT, this.ACTION_RESP);

        startService(intent);
}

    public void stopMethod(){
    }

    public class ResponseReceiver extends BroadcastReceiver{


        @Override
        public void onReceive(Context context, Intent intent) {
            String response = intent.getStringExtra(HTTPGetService.SERVER_RESPONSE);
            Log.d("Received: ",response);
            responseDisplay.setText("About to Show text!");
            responseDisplay.setText(response);
            ArrayList<Course> courseList = new ArrayList<Course>();
            int numberOfSectionsTotal = 0;

            try {
                JSONObject rawResult = new JSONObject(response);
                JSONArray jsonCourses = rawResult.getJSONArray("Results");
                SharedPreferences.Editor editor = getSharedPreferences("SharedPrefs", MODE_PRIVATE).edit();
                editor.putString("fetchedCourseListJSON",rawResult.getString("Results"));
                editor.apply();
                float timeTaken = Float.parseFloat(rawResult.getString("TimeTaken"));
                Log.d("New Request Time Taken:", Float.toString(timeTaken));
                courseList = Course.buildCourseList(jsonCourses);

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
}
