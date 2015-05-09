package edu.uta.ucs;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupSimplePreferencesScreen();
    }

    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    @SuppressWarnings("deprecation")
    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

        // In the simplified UI, fragments are not used at all and we instead
        // use the older PreferenceActivity APIs.

        // Add 'general' preferences.
        addPreferencesFromResource(R.xml.pref_general);

        if(UserData.getEmail() != null) {

            // Add 'notifications' preferences, and a corresponding header.
            PreferenceCategory fakeHeader = new PreferenceCategory(this);
            fakeHeader.setTitle(R.string.pref_header_account_settings);
            getPreferenceScreen().addPreference(fakeHeader);
            addPreferencesFromResource(R.xml.pref_account);

            Preference updatePasswordButton = findPreference(getString(R.string.pref_key_update_password));
            updatePasswordButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    updateElementDialog("Password", SettingsActivity.this);
                    return false;
                }
            });

            Preference updateEmailButton = findPreference(getString(R.string.pref_key_update_email));
            updateEmailButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    updateElementDialog("Email", SettingsActivity.this);
                    return false;
                }
            });

            Preference deleteAccountButton = findPreference(getString(R.string.pref_key_delete_account));
            deleteAccountButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    deleteAccountDialog(SettingsActivity.this);
                    return false;
                }
            });
        }

        /*
        // Add 'notifications' preferences, and a corresponding header.
        PreferenceCategory fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_notifications);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_notification);

        // Add 'data and sync' preferences, and a corresponding header.
        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_data_sync);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_account);

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences to
        // their values. When their values change, their summaries are updated
        // to reflect the new value, per the Android Design guidelines.
        bindPreferenceSummaryToValue(findPreference("example_text"));
        bindPreferenceSummaryToValue(findPreference("example_list"));
        bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        */
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Determines whether the simplified settings UI should be shown. This is
     * true if this is forced via ALWAYS_SIMPLE_PREFS (Currently removed), or the device
     * doesn't have newer APIs like {@link PreferenceFragment}, or the device
     * doesn't have an extra-large screen. In these cases, a single-pane
     * "simplified" settings UI should be shown.
     */
    private static boolean isSimplePreferences(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        if (!isSimplePreferences(this)) {
            loadHeadersFromResource(R.xml.pref_headers, target);
        }
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    @SuppressWarnings("unused")
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
        }
    }

    /**
     * This fragment shows user account preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AccountPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


            if(UserData.getEmail() != null) {
                addPreferencesFromResource(R.xml.pref_account);

                // Bind the summaries of EditText/List/Dialog/Ringtone preferences
                // to their values. When their values change, their summaries are
                // updated to reflect the new value, per the Android Design
                // guidelines.
                //bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
                Preference updatePasswordButton = findPreference(getString(R.string.pref_key_update_password));
                updatePasswordButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        updateElementDialog("Password", getActivity());
                        return false;
                    }
                });


                Preference updateEmailButton = findPreference(getString(R.string.pref_key_update_email));
                updateEmailButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        updateElementDialog("Email", getActivity());
                        return false;
                    }
                });

                Preference deleteAccountButton = findPreference(getString(R.string.pref_key_delete_account));
                deleteAccountButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        deleteAccountDialog(getActivity());
                        return false;
                    }
                });
            }
        }
    }

    private static void deleteAccountDialog(final Context context){

        AlertDialog.Builder confirmDelete = new AlertDialog.Builder(context);
        confirmDelete.setTitle("Are you sure you want to delete your account?");
        confirmDelete.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String deleteAccountURL = UserData.getContext().getString(R.string.delete_account) + UserData.getEmail();
                HTTPService.FetchURL(deleteAccountURL, "null", context);
            }
        });
        confirmDelete.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        confirmDelete.create().show();
    }


    /**
     * Creates a dialog which prompts user for password and element to change
     */
    private static void updateElementDialog(String updateElement, final Context context){

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View changeElement = inflater.inflate(R.layout.dialog_change_element, null);

        final String baseURL = UserData.getContext().getString(R.string.change_settings_base) + updateElement;

        TextView newElement = (TextView) changeElement.findViewById(R.id.textview_new_type);
        TextView confirmElement = (TextView) changeElement.findViewById(R.id.textview_confirm_type);

        newElement.setText(newElement.getText() + updateElement + ":");
        confirmElement.setText(confirmElement.getText() + updateElement + ":");

        final EditText oldEditText = (EditText) changeElement.findViewById(R.id.edittext_current_type);
        final EditText newEditText = (EditText) changeElement.findViewById(R.id.edittext_new_type);
        final EditText confirmEditText = (EditText) changeElement.findViewById(R.id.edittext_confirm_type);


        final AlertDialog.Builder changeElementBuilder = new AlertDialog.Builder(context);
        changeElementBuilder.setTitle("Change " + updateElement);
        changeElementBuilder.setView(changeElement);
        changeElementBuilder.setPositiveButton("UPDATE " + updateElement.toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        changeElementBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        final AlertDialog changeElementDialog = changeElementBuilder.create();
        changeElementDialog.show();

        changeElementDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldElement = oldEditText.getText().toString();
                String newElement = newEditText.getText().toString();
                String confirmElement = confirmEditText.getText().toString();
                boolean dismiss = true;

                if (oldElement.equals("")) {
                    oldEditText.setError("Cannot be blank");
                    dismiss = false;
                } else oldEditText.setError(null);

                if (newElement.equals("")) {
                    newEditText.setError("Cannot be blank");
                    dismiss = false;
                } else if(dismiss) newEditText.setError(null);

                if (!newElement.equals(confirmElement)) {
                    newEditText.setError("Must Match");
                    confirmEditText.setError("Must Match");
                    dismiss = false;
                } else if(dismiss) {
                    newEditText.setError(null);
                    confirmEditText.setError(null);

                }

                if (dismiss) {
                    String changeElementURL = baseURL + "?email=" + UserData.getEmail() + "?password=" + oldElement + "?confirm=" + newElement;
                    HTTPService.FetchURL(changeElementURL, "null", context);
                    changeElementDialog.dismiss();
                }
            }
        });

        changeElementDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeElementDialog.dismiss();
            }
        });

        if(updateElement.toUpperCase().contains("PASSWORD")){
            newEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            confirmEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

            newEditText.setTransformationMethod(new PasswordTransformationMethod());
            confirmEditText.setTransformationMethod(new PasswordTransformationMethod());
        }
        else {
            newEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            confirmEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

            newEditText.setTransformationMethod(null);
            confirmEditText.setTransformationMethod(null);

        }

    }

    static void startActivity(Context context){
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }
}
