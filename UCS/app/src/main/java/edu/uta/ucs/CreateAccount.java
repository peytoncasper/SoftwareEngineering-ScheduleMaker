package edu.uta.ucs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class CreateAccount extends ActionBarActivity {
    public static final String ACTION_CREATE_ACCOUNT ="edu.uta.ucs.intent.action.CREATE_ACCOUNT";
    private static final String SPOOF_ACCOUNT_CREATION = "{\"Success\":true,\"Email\":\"b@b.b\",\"Username\":\"b\",\"Message\":\"Account Added.\"}";
    public static final String baseURL[] = {"http://ucs.azurewebsites.net/UTA/CreateAccount?","username=","&password=","&email="};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        LocalBroadcastManager.getInstance(this).registerReceiver(new CreateAccountReceiver(), new IntentFilter(ACTION_CREATE_ACCOUNT));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_account, menu);
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

    public void attemptAccountCreation(){
        String username=null;
        String password=null;
        String passwordConfirmation=null;
        String email=null;
        String url=null;

        if(!emailIsValid(email)){
            //Break and inform user
        }

        if(!password.equals(passwordConfirmation)){
            //break and
        }

        url = baseURL[0]+baseURL[1]+username+baseURL[2]+password+baseURL[3]+email;

        Intent intent = new Intent(this, HTTPGetService.class);
        if(true) {
            intent.putExtra(HTTPGetService.URL_REQUEST, HTTPGetService.SPOOF_SERVER);
            intent.putExtra(HTTPGetService.SPOOFED_RESPONSE, SPOOF_ACCOUNT_CREATION);
        }
        else
            intent.putExtra(HTTPGetService.URL_REQUEST, url);
    }

    private boolean emailIsValid(String email){
        return email.contains("@");
    }


    private class CreateAccountReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            JSONObject response = null;
            boolean success = false;
            try {
                response = new JSONObject(intent.getStringExtra(HTTPGetService.SERVER_RESPONSE));
                success = response.getBoolean("Success");
                if(success){
                    Toast.makeText(getApplicationContext(), "Account Created", Toast.LENGTH_LONG).show();
                    Intent launchMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                    getApplicationContext().startActivity(launchMainActivity);
                }
                else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
