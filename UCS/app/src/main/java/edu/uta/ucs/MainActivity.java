package edu.uta.ucs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    TextView mainText;
    String[] desiredCourseList = {"3330", "2320"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mainText = (TextView) findViewById(R.id.mainText);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startMethod();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startMethod(){
        Intent i = new Intent(this, DatabaseService.class);
        i.putExtra("url", "http://softengbackend.cloudapp.net/UTA/ClassStatus?classNumbers=");
        startService(i);
    }
    public void stopMethod(){
        Intent i = new Intent(this, DatabaseService.class);
        stopService(i);
    }
}
