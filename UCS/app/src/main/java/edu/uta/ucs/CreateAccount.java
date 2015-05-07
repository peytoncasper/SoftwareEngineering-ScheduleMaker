package edu.uta.ucs;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * This Activity is the input form for a user to create a new account
 */
public class CreateAccount extends ActionBarActivity {
    public static final String ACTION_CREATE_ACCOUNT ="edu.uta.ucs.intent.action.CREATE_ACCOUNT";
    private static final String SPOOF_ACCOUNT_CREATION = "{\"Success\":true,\"Email\":\"b@b.b\",\"Username\":\"b\",\"Message\":\"Account Added.\"}";

    public static final String CREATE_ACCOUNT_URL[] = {"http://ucs.azurewebsites.net/UTA/CreateAccount?","username=","&password=","&email="};

    boolean spoofData = false;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Register this receiver with LocalBroadcastManager
        LocalBroadcastManager.getInstance(this).registerReceiver(new CreateAccountReceiver(), new IntentFilter(ACTION_CREATE_ACCOUNT));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
                SettingsActivity.startActivity(CreateAccount.this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * User requests account creation canceled
     * fields are reset and activity closed
     *
     * @param view view from which activity is called
     */
    public void cancelAccountCreation(View view){
        // Reset all of the input fields
        // *** MAY NOT BE NEEDED ***
        ((EditText) findViewById(R.id.create_account_email)).setText("");
        ((EditText) findViewById(R.id.create_account_username)).setText("");
        ((EditText) findViewById(R.id.create_account_password)).setText("");
        ((EditText) findViewById(R.id.create_account_confirm_password)).setText("");

        // Close Activity
        finish();
    }

    /**
     * User requests account be created with given fields
     *
     * @param view view from which activity is called
     */
    public void attemptAccountCreation(View view){
        // Initiate some values
        View focusView = null;
        String url=null;
        boolean cancel = false;

        // Get data from layout fields
        String email = ((EditText) findViewById(R.id.create_account_email)).getText().toString();
        String username = ((EditText) findViewById(R.id.create_account_username)).getText().toString();
        String password = ((EditText) findViewById(R.id.create_account_password)).getText().toString();
        String passwordConfirmation = ((EditText) findViewById(R.id.create_account_confirm_password)).getText().toString();

        // Check valid
        if(!emailIsValid(email)){
            //Break and inform user
            focusView = ((EditText) findViewById(R.id.create_account_email));
            ((EditText) findViewById(R.id.create_account_email)).setError(getString(R.string.error_invalid_email));
            cancel = true;

        }

        // Check valid
        if(!passwordsValid(password, passwordConfirmation)){
            //break and
            ((EditText) findViewById(R.id.create_account_password)).setText("");
            ((EditText) findViewById(R.id.create_account_confirm_password)).setText("");
            focusView = ((EditText) findViewById(R.id.create_account_password));
            cancel = true;
        }

        // Build url from user given fields
        url = CREATE_ACCOUNT_URL[0]+CREATE_ACCOUNT_URL[1]+username+CREATE_ACCOUNT_URL[2]+password+CREATE_ACCOUNT_URL[3]+email;

        /* Depreciated with implementation of HTTPService.FetchURL()
        // Create activity intent
        Intent intent = new Intent(this, HTTPService.class);
        // Listener filter for result broadcast
        intent.putExtra(HTTPService.SOURCE_INTENT, ACTION_CREATE_ACCOUNT);

        // Last chance to interrupt before account creation is attempted
        */
        if (cancel){

            focusView.requestFocus();
        }
        else {
            HTTPService.FetchURL(url, ACTION_CREATE_ACCOUNT, this);
            /* Depreciated with implementation of HTTPService.FetchURL()
            // Spoof data switch
            if (spoofData) {
                // Put spoof request instead of url in intent extras
                intent.putExtra(HTTPService.REQUEST_GET_URL, HTTPService.SPOOF_SERVER);
                intent.putExtra(HTTPService.SPOOFED_RESPONSE, SPOOF_ACCOUNT_CREATION);
            } else
                // Put creation request in intent extras
                intent.putExtra(HTTPService.REQUEST_GET_URL, url);

            // Launch service
            startService(intent);
            */


            progressDialog = new ProgressDialog(CreateAccount.this);
            progressDialog.setTitle("Attempting to create account");
            progressDialog.setMessage("please wait for server response...");
        }

    }

    /**
     * Checks to make sure email is valid
     *
     * @param email email to be checked for validity
     * @return
     */
    private boolean emailIsValid(String email){
        return email.contains("@");
    }

    /**
     * Checks to make sure given passwords are valid
     *
     * @param pass password field - primary
     * @param confirmPass password field - secondary
     * @return
     */
    private boolean passwordsValid(String pass, String confirmPass){
        // Check to make sure conformation password matches password
        boolean match = pass.equals(confirmPass);
        if (!match){
            ((EditText) findViewById(R.id.create_account_password)).setError(getString(R.string.error_password_mismatch));
        }
        // Ensure password meets length requirement
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
                response = new JSONObject(intent.getStringExtra(HTTPService.SERVER_RESPONSE));
                success = response.getBoolean("Success");
                String message = response.getString("Message");
                if(success){
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    Intent launchMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(launchMainActivity);
                    finish();
                }
                else {
                    Toast.makeText(CreateAccount.this, "Error: " + message, Toast.LENGTH_LONG).show();
                    ((EditText) findViewById(R.id.create_account_password)).setText("");
                    ((EditText) findViewById(R.id.create_account_confirm_password)).setText("");

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
