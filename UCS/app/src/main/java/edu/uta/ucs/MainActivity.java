package edu.uta.ucs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import java.util.HashMap;


public class MainActivity extends ActionBarActivity {

    // Variables from JSON Parsing Example at http://www.androidhive.info/2012/01/android-json-parsing-tutorial/
    // JSON Node names
    private static final String TAG_CONTACTS = "contacts";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_GENDER = "gender";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_PHONE_MOBILE = "mobile";
    private static final String TAG_PHONE_HOME = "home";
    private static final String TAG_PHONE_OFFICE = "office";
    String[] desiredCourseList = {"CSE-3330", "CSE-2320"};
    String baseURL = "http://ucs-scheduler.cloudapp.net/UTA/ClassStatus?classes=";
    TextView responseDisplay;
    HTTPGetService HTTPGetService;
    boolean dbServiceStatus;
    // contacts JSONArray
    JSONArray contacts = null;
    // Hashmap for ListView
    ArrayList<HashMap<String, String>> contactList;
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

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void requestJSON(View view){
        responseDisplay.setText("Please wait, attempting to fetch data...");

        StringBuilder urlBuilder = new StringBuilder(baseURL);
        String classTextField = ((TextView) findViewById(R.id.editText)).getText().toString();
        for(String string: desiredCourseList){
            urlBuilder.append(string);
            urlBuilder.append(",");
        }

        Log.d("classTextField",classTextField);
        if (classTextField.length() > 0 ) urlBuilder.append(classTextField + ",");
        String url = urlBuilder.length() > 0 ? urlBuilder.substring( 0, urlBuilder.length() - 1 ): "";

        boolean switchStatus = spoofServerSwitch.isChecked();
        Intent intent = new Intent(this, HTTPGetService.class);
        intent.putExtra("edu.uta.ucs.URL_REQUEST", url);
        intent.putExtra("edu.uta.ucs.SPOOF_SERVER_RESPONSE", switchStatus);
        startService(intent);
}

    public void stopMethod(){
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

            /*  JSON Parsing Example from http://www.androidhive.info/2012/01/android-json-parsing-tutorial/
            if (response != null) {
                try {
                    JSONObject jsonObj = new JSONObject(response);

                    // Getting JSON Array node
                    contacts = jsonObj.getJSONArray(TAG_CONTACTS);

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        String id = c.getString(TAG_ID);
                        String name = c.getString(TAG_NAME);
                        String email = c.getString(TAG_EMAIL);
                        String address = c.getString(TAG_ADDRESS);
                        String gender = c.getString(TAG_GENDER);
                        Log.d("JSON Parsed Contact", id+name);

                        // Phone node is JSON Object
                        JSONObject phone = c.getJSONObject(TAG_PHONE);
                        String mobile = phone.getString(TAG_PHONE_MOBILE);
                        String home = phone.getString(TAG_PHONE_HOME);
                        String office = phone.getString(TAG_PHONE_OFFICE);

                        // tmp hashmap for single contact
                        HashMap<String, String> contact = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        contact.put(TAG_ID, id);
                        contact.put(TAG_NAME, name);
                        contact.put(TAG_EMAIL, email);
                        contact.put(TAG_PHONE_MOBILE, mobile);

                        // adding contact to contact list
                        contactList.add(contact);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException e){
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            */
        }
    }
}
