package edu.uta.ucs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Messenger;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

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
    //private ResponseReceiver receiver;
    ArrayList<Course> courseList;
    int numberOfSectionsTotal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_main);

        spoofServerSwitch = (Switch) findViewById(R.id.spoofServer);
        spoofServerSwitch.setChecked(true);

        responseDisplay = (TextView)findViewById(R.id.textView);
        responseDisplay.setText("Press FETCH JSON to attempt a data fetch");

        sectionListView = (ListView) findViewById(R.id.listView);

        httpGetServiceBound = true;
        courseList = new ArrayList<>();
    }

    public void requestJSON(View view){

        responseDisplay.setText("Please wait, attempting to fetch data...");
        Log.d("MainActivity", "Initiating JSON Request");


        StringBuilder urlBuilder = new StringBuilder(baseURL);
        String classTextField = ((TextView) findViewById(R.id.classesTextField)).getText().toString();

        Log.d("classTextField",classTextField);
        if (classTextField.length() > 0 ){
            urlBuilder.append(classTextField + ",");
        }
        String url = urlBuilder.length() > 0 ? urlBuilder.substring( 0, urlBuilder.length() - 1 ): "";

        if(spoofServerSwitch.isChecked())
            url = HTTPGetService.SPOOF_SERVER_RESPONSE;


        new HTTPGetService(new HTTPGetCallback() {
            @Override
            public void onResult(JSONObject result) {

                try {
                    courseList = getCourses(result.getJSONArray("Results"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ArrayList<Section> sectionArrayList = new ArrayList<Section>(numberOfSectionsTotal);

                for (Course course : courseList)
                    sectionArrayList.addAll(course.getSectionList());

                updateUpdateListView(sectionArrayList);
            }
        }, url).execute();
    }

    private ArrayList<Course> getCourses(JSONArray jsonCourses) throws JSONException {
        numberOfSectionsTotal = 0;
        ArrayList<Course> courseArrayList = new ArrayList<>(jsonCourses.length());

        for(int index = jsonCourses.length(); index != 0;index--){
            Log.d("New Course: ", jsonCourses.getJSONObject(index - 1).toString());
            courseArrayList.add( new Course(jsonCourses.getJSONObject(index - 1)));
            numberOfSectionsTotal++;
        }
        Collections.reverse(courseArrayList);

        return courseArrayList;
    }

    private void updateUpdateListView(ArrayList sectionArrayList){
        ListAdapter adapter = new MySectionArrayAdapter(MainActivity.this, R.layout.list_item, sectionArrayList);
        sectionListView.setAdapter(adapter);
    }

}
