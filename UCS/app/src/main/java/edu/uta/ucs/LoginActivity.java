package edu.uta.ucs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {

    public static final String ACTION_LOGIN ="edu.uta.ucs.intent.action.ACTION_LOGIN";
    public static final String ACTION_RESET_PASSWORD ="edu.uta.ucs.intent.action.ACTION_RESET_PASSWORD";

    private static final String LOGIN_URL = "http://ucs-scheduler.cloudapp.net/UTA/ValidateLogin?";
    private static final String[] LOGIN_PARAMS ={"username=","&password="};
    private static final String EMAIL_EXISTS_URL = "http://ucs-scheduler.cloudapp.net/UTA/EmailExists?email=";

    private static final String SPOOFED_LOGIN = "{\"Success\":true,\"Email\":\"a@a.a\"}";
    private static final String SPOOFED_RESET_PASSWORD = "{\"Success\":true}";

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
    HTTPGetService HTTPGetService;

    String m_Text;
    private String rawServerResponse;

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

        HTTPGetService = new HTTPGetService();

        LocalBroadcastManager.getInstance(this).registerReceiver(new LoginReceiver(), new IntentFilter(ACTION_LOGIN));
        LocalBroadcastManager.getInstance(this).registerReceiver(new ResetPasswordReceiver(), new IntentFilter(ACTION_RESET_PASSWORD));
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    public void onResume(){
        super.onResume();
        TimeShort testTime = new TimeShort("5:25PM");
        Log.d("TimeTest24", testTime.toString24h());
        Log.d("TimeTest12", testTime.toString12h());

        Day day = Day.valueOf("M");
        Log.d("DayTest", day.toString());
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

        String url = LOGIN_URL + LOGIN_PARAMS[0] + email + LOGIN_PARAMS[1] + password;



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

            Intent intent = new Intent(this, HTTPGetService.class);

            intent.putExtra(HTTPGetService.URL_REQUEST, HTTPGetService.SPOOF_SERVER);
            intent.putExtra(HTTPGetService.SPOOFED_RESPONSE, SPOOFED_LOGIN);
            intent.putExtra(HTTPGetService.SOURCE_INTENT, ACTION_LOGIN);

            startService(intent);
        }
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
                    Intent intent = new Intent(getApplicationContext(), HTTPGetService.class);

                    intent.putExtra(HTTPGetService.URL_REQUEST, EMAIL_EXISTS_URL+email);
                    //intent.putExtra(HTTPGetService.SPOOFED_RESPONSE, SPOOFED_RESET_PASSWORD);
                    intent.putExtra(HTTPGetService.SOURCE_INTENT, ACTION_RESET_PASSWORD);

                    startService(intent);
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

    private boolean isPasswordValid(String password) {
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
        List<String> emails = new ArrayList<String>();
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
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    private class ResetPasswordReceiver extends BroadcastReceiver{


        @Override
        public void onReceive(Context context, Intent intent) {
            JSONObject response = null;
            boolean success = false;
            try {
                response = new JSONObject(intent.getStringExtra(HTTPGetService.SERVER_RESPONSE));
                success = response.getBoolean("Success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("Reset Password Receiver","Launched Receiver");

            if (success) {
                Toast.makeText(LoginActivity.this, "Successfully reset password",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(LoginActivity.this, "Failed to reset password",Toast.LENGTH_LONG).show();
            }

        }
    }

    private class LoginReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            showProgress(false);
            JSONObject response = null;
            boolean success = false;
            try {
                response = new JSONObject(intent.getStringExtra(HTTPGetService.SERVER_RESPONSE));
                success = response.getBoolean("Success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("Login Receiver","Launched Receiver");
            Log.d("Received: ",response.toString());

            if (success) {
                mEmailView.setText("");
                mPasswordView.setText("");
                Intent launchMainActivity = new Intent(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(launchMainActivity);
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }
    }
}



