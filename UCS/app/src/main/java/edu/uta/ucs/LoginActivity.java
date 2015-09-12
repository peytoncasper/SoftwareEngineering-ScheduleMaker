package edu.uta.ucs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;



/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {

    public static final String ACTION_LOGIN ="edu.uta.ucs.intent.action.ACTION_LOGIN";
    public static final String ACTION_LOGOUT ="edu.uta.ucs.intent.action.ACTION_LOGOUT";
    public static final String ACTION_RESET_PASSWORD ="edu.uta.ucs.intent.action.ACTION_RESET_PASSWORD";

    private static final String LOGIN_URL = UserData.getContext().getString(R.string.login_base);
    private static final String[] LOGIN_PARAMS ={
            UserData.getContext().getString(R.string.login_param_username),
            UserData.getContext().getString(R.string.login_param_hashedpwd)};
    private static final String EMAIL_EXISTS_URL = UserData.getContext().getString(R.string.email_exists_base);

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    //private UserLoginTask mAuthTask = null;
    //private EmailExistsTask mEmailTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == getResources().getInteger(R.integer.defaultImeActionID) || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        LocalBroadcastManager.getInstance(this).registerReceiver(new LoginReceiver(), new IntentFilter(ACTION_LOGIN));
        LocalBroadcastManager.getInstance(this).registerReceiver(new LogoutReceiver(), new IntentFilter(ACTION_LOGOUT));
        LocalBroadcastManager.getInstance(this).registerReceiver(new ResetPasswordReceiver(), new IntentFilter(ACTION_RESET_PASSWORD));

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
                SettingsActivity.startActivity(LoginActivity.this);
                break;
            case R.id.action_logout:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String hashedpwd = "";

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(password.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

            //convert the byte to hex format method 2
            StringBuffer hexString = new StringBuffer();
            for (int i=0;i<byteData.length;i++) {
                String hex=Integer.toHexString(0xff & byteData[i]);
                if(hex.length()==1) hexString.append('0');
                hexString.append(hex);
            }
            hashedpwd = hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        JSONObject loginData = new JSONObject();

        try {
            loginData.put("username",email);
            loginData.put("hashedpwd",hashedpwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = LOGIN_URL + LOGIN_PARAMS[0] + email + LOGIN_PARAMS[1] + hashedpwd;

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        Log.d("LoginActivity", "PasswordCheck");
        if ( !(password.length()>0) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            Log.d("LoginActivity", "Password fails");
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }/* else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }*/

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            //HTTPService.FetchURL(url, ACTION_LOGIN, this);
            HTTPService.PostJSON(LOGIN_URL, loginData,ACTION_LOGIN,this);

            /*
            Intent intent = new Intent(this, HTTPService.class);

            intent.putExtra(HTTPService.REQUEST_URL, url);
            //intent.putExtra(HTTPGetService.SPOOFED_RESPONSE, SPOOFED_LOGIN);
            intent.putExtra(HTTPService.SOURCE_INTENT, ACTION_LOGIN);

            startService(intent);
            */
        }
    }

    /**
     * Open createAccount Activity
     * @param view View this function is called from
     */
    public void createAccount(View view){
        mEmailView.setText("");
        mPasswordView.setText("");
        Intent launchCreateAccountActivity = new Intent(LoginActivity.this, CreateAccount.class);
        LoginActivity.this.startActivity(launchCreateAccountActivity);
    }

    public void resetPasswordDialog(View view){
        Log.d("Reset Dialog", "Attempting to show dialogue");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter account email address");

        // Set up the input
        final EditText input = new EditText(this);
        input.setText(mEmailView.getText().toString());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = input.getText().toString();

                if (isEmailValid(email)){

                    HTTPService.FetchURL(EMAIL_EXISTS_URL + email, ACTION_RESET_PASSWORD, LoginActivity.this);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }


    @SuppressWarnings("unused")
    boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return true;//password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    @SuppressWarnings("unused")
    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    /**
     * Receiver class for BroadcastManager.
     */
    private class ResetPasswordReceiver extends BroadcastReceiver{


        @Override
        public void onReceive(Context context, Intent intent) {
            JSONObject response;
            boolean success;
            String message;

            try {

                response = new JSONObject(intent.getStringExtra(HTTPService.SERVER_RESPONSE));
                success = response.getBoolean("Success");
                if(response.has("Message")) {
                    if(success)
                        message = response.getString("Message");
                    else message = "Error: " + response.getString("Message");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
                if(response.has("TimeTaken")){
                    float timeTaken = Float.parseFloat(response.getString("TimeTaken"));
                    Log.d("New Request Time Taken:", Float.toString(timeTaken));
                }

                if (success) {
                    Toast.makeText(LoginActivity.this, "Successfully reset password",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Failed to reset password",Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    /**
     * Receiver class for BroadcastManager.
     */
    private class LoginReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            JSONObject response;
            boolean success;
            String message;
            try {
                response = new JSONObject(intent.getStringExtra(HTTPService.SERVER_RESPONSE));
                success = response.getBoolean("SUCCESS");
                if(response.has("Message")) {
                    if(success)
                        message = response.getString("Message");
                    else message = "Error: " + response.getString("Message");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
                if(response.has("TimeTaken")){
                    float timeTaken = Float.parseFloat(response.getString("TimeTaken"));
                    Log.d("New Request Time Taken:", Float.toString(timeTaken));
                }
                if (success) {
                    mEmailView.setText("");
                    mPasswordView.setText("");
                    
                    // Set UserData to be called from various places in the app
                    UserData.setUserData(response);
                    Intent launchMainActivity = new Intent(LoginActivity.this, MainActivity.class);
                    LoginActivity.this.startActivity(launchMainActivity);
                } else {
                    showProgress(false);
                    mPasswordView.setText("");
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Receiver class for BroadcastManager.
     */
    private class LogoutReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            JSONObject response;
            boolean success;
            String message;

            try {
                response = new JSONObject(intent.getStringExtra(HTTPService.SERVER_RESPONSE));
                success = response.getBoolean("Success");
                if(response.has("Message")) {
                    if(success)
                        message = response.getString("Message");
                    else message = "Error: " + response.getString("Message") + " Upload failed. Saving userdata locally";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
                if(response.has("TimeTaken")){
                    float timeTaken = Float.parseFloat(response.getString("TimeTaken"));
                    Log.d("New Request Time Taken:", Float.toString(timeTaken));
                }
                if(success){

                    SharedPreferences.Editor scheduleEditor;
                    scheduleEditor = context.getSharedPreferences(Schedule.SCHEDULE_SAVEFILE, Context.MODE_PRIVATE).edit();
                    scheduleEditor.clear();
                    scheduleEditor.apply();

                    SharedPreferences.Editor blockoutTimesEditor;
                    blockoutTimesEditor = context.getSharedPreferences(SelectBlockoutTimes.BLOCKOUT_TIMES, MODE_PRIVATE).edit();
                    blockoutTimesEditor.clear();
                    blockoutTimesEditor.apply();

                    //UserData.setEmail(null);
                    //UserData.setMilitaryTime(false);


                    Log.i("LoginActivity", "Successful logout");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Intent logoutIntent = new Intent(context, LoginActivity.class);
            logoutIntent.putExtra("finish", true); // if you are checking for this in your other Activities
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(logoutIntent);

        }
    }
}



