package edu.uta.ucs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * This Activity is the input form for a user to create a new account. It should only be launched from the login screen.
 */
public class CreateAccount extends Activity {
    public static final String ACTION_CREATE_ACCOUNT ="edu.uta.ucs.intent.action.CREATE_ACCOUNT";

    // URL Extensions which make the create account URL
    public static final String CREATE_ACCOUNT_URL[] = {
            UserData.getContext().getString(R.string.create_account_base),
            UserData.getContext().getString(R.string.create_account_param_username),
            UserData.getContext().getString(R.string.create_account_param_password),
            UserData.getContext().getString(R.string.create_account_param_email)};

    // ProgressDialog to show after url request is sent to server. The reciever will dismiss it, so it must be referable outside of the method which will create and start it.
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load layout
        setContentView(R.layout.activity_create_account);

        // Register receivers with LocalBroadcastManager
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
        String url;
        boolean cancel = false;

        // Get data from layout fields
        String email = ((EditText) findViewById(R.id.create_account_email)).getText().toString();
        String username = ((EditText) findViewById(R.id.create_account_username)).getText().toString();
        String password = ((EditText) findViewById(R.id.create_account_password)).getText().toString();
        String passwordConfirmation = ((EditText) findViewById(R.id.create_account_confirm_password)).getText().toString();

        // Check valid
        if(!emailIsValid(email)){
            //Break and inform user
            focusView = findViewById(R.id.create_account_email);
            ((EditText) findViewById(R.id.create_account_email)).setError(getString(R.string.error_invalid_email));
            cancel = true;

        }

        // Check valid
        if(!passwordsValid(password, passwordConfirmation)){
            //break and
            ((EditText) findViewById(R.id.create_account_password)).setText("");
            ((EditText) findViewById(R.id.create_account_confirm_password)).setText("");
            focusView = findViewById(R.id.create_account_password);
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

            /* Removed after implementation of HTTPService.FetchURL()
            // Spoof data switch
            if (spoofData) {
                // Put spoof request instead of url in intent extras
                intent.putExtra(HTTPService.REQUEST_URL, HTTPService.SPOOF_SERVER);
                intent.putExtra(HTTPService.SPOOFED_RESPONSE, SPOOF_ACCOUNT_CREATION);
            } else
                // Put creation request in intent extras
                intent.putExtra(HTTPService.REQUEST_URL, url);

            // Launch service
            startService(intent);
            */

            // Create progress dialog to inform app is waiting for server response.
            progressDialog = new ProgressDialog(CreateAccount.this);
            progressDialog.setTitle("Attempting to create account");
            progressDialog.setMessage("please wait for server response...");
        }

    }

    /**
     * Checks to make sure email is valid
     *
     * @param email email to be checked for validity
     * @return boolean
     */
    private boolean emailIsValid(String email){
        return email.contains("@") && email.contains(".") && email.endsWith("");
    }

    /**
     * Checks to make sure given passwords are valid
     *
     * @param pass password field - primary
     * @param confirmPass password field - secondary
     * @return boolean
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


    /**
     * Receiver class for BroadcastManager.
     */
    private class CreateAccountReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            JSONObject response;
            boolean success;
            String message;

            try {

                // create JSONObject from server response. Should always work.
                response = new JSONObject(intent.getStringExtra(HTTPService.SERVER_RESPONSE));
                // Server generated. Represents a valid server request.
                success = response.getBoolean("Success");

                // If server sent a message with the response, show it.
                if(response.has("Message")) {
                    if(success)
                        message = response.getString("Message");
                    else message = "Error: " + response.getString("Message");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }

                // If server sent a time taken tag, display it.
                if(response.has("TimeTaken")){
                    float timeTaken = Float.parseFloat(response.getString("TimeTaken"));
                    Log.d("New Request Time Taken:", Float.toString(timeTaken));
                }

                if(success){
                    // User account details from server response. Should only have Email at this point.
                    UserData.setUserData(response);

                    // Launch Main Activity
                    Intent launchMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(launchMainActivity);

                    // Destroy this activity.
                    finish();
                }
                else {
                    // Clears passwords fields. Just in case...
                    ((EditText) findViewById(R.id.create_account_password)).setText("");
                    ((EditText) findViewById(R.id.create_account_confirm_password)).setText("");

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
