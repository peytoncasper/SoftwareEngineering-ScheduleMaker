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
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class CreateAccount extends ActionBarActivity {
    public static final String ACTION_CREATE_ACCOUNT ="edu.uta.ucs.intent.action.CREATE_ACCOUNT";
    private static final String SPOOF_ACCOUNT_CREATION = "{\"Success\":true,\"Email\":\"b@b.b\",\"Username\":\"b\",\"Message\":\"Account Added.\"}";

    private static final String CREATE_ACCOUNT_URL = "http://ucs-scheduler.cloudapp.net/UTA/CreateAccount?";
    private static final String[] CREATE_ACCOUNT_PARAMS = {"username=","&password=","&email="};

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

    public void cancelAccountCreation(View view){
        ((EditText) findViewById(R.id.create_account_email)).setText("");
        ((EditText) findViewById(R.id.create_account_username)).setText("");
        ((EditText) findViewById(R.id.create_account_password)).setText("");
        ((EditText) findViewById(R.id.create_account_confirm_password)).setText("");
        finish();
    }

    public void attemptAccountCreation(View view){

        View focusView = null;
        String url=null;
        boolean cancel = false;

        String email = ((EditText) findViewById(R.id.create_account_email)).getText().toString();
        String username = ((EditText) findViewById(R.id.create_account_username)).getText().toString();
        String password = ((EditText) findViewById(R.id.create_account_password)).getText().toString();
        String passwordConfirmation = ((EditText) findViewById(R.id.create_account_confirm_password)).getText().toString();

        if(!emailIsValid(email)){
            //Break and inform user
            focusView = ((EditText) findViewById(R.id.create_account_email));
            ((EditText) findViewById(R.id.create_account_email)).setError(getString(R.string.error_invalid_email));
            cancel = true;

        }

        if(!passwordsValid(password, passwordConfirmation)){
            //break and
            ((EditText) findViewById(R.id.create_account_password)).setText("");
            ((EditText) findViewById(R.id.create_account_confirm_password)).setText("");
            focusView = ((EditText) findViewById(R.id.create_account_password));
            cancel = true;
        }

        url = CREATE_ACCOUNT_URL+CREATE_ACCOUNT_PARAMS[0]+username+CREATE_ACCOUNT_PARAMS[1]+password+CREATE_ACCOUNT_PARAMS[2]+email;

        Intent intent = new Intent(this, HTTPGetService.class);
        intent.putExtra(HTTPGetService.SOURCE_INTENT, ACTION_CREATE_ACCOUNT);
        if (cancel){

            focusView.requestFocus();
        }
        else {
            if (true) {
                intent.putExtra(HTTPGetService.URL_REQUEST, HTTPGetService.SPOOF_SERVER);
                intent.putExtra(HTTPGetService.SPOOFED_RESPONSE, SPOOF_ACCOUNT_CREATION);
            } else
                intent.putExtra(HTTPGetService.URL_REQUEST, url);

            startService(intent);
        }

    }

    private boolean emailIsValid(String email){
        return email.contains("@");
    }

    private boolean passwordsValid(String pass, String confirmPass){
        boolean match = pass.equals(confirmPass);
        if (!match){
            ((EditText) findViewById(R.id.create_account_password)).setError(getString(R.string.error_password_mismatch));
        }
        boolean length = pass.length()>0;
        if (!length){
            ((EditText) findViewById(R.id.create_account_password)).setError(getString(R.string.error_invalid_password));
        }
        return match && length;
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
                    startActivity(launchMainActivity);
                    finish();
                }
                else {
                    ((EditText) findViewById(R.id.create_account_password)).setText("");
                    ((EditText) findViewById(R.id.create_account_confirm_password)).setText("");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
