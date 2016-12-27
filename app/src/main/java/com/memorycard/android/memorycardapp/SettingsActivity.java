package com.memorycard.android.memorycardapp;

import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingsActivity extends PreferenceActivity {

    private static SwitchPreference switchPreference;
    private static EditTextPreference editTextPreference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();

    }
    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
            initPreferences();
        }
        private void initPreferences() {
            switchPreference = (SwitchPreference)findPreference("switch_preference_isCount");
            editTextPreference = (EditTextPreference)findPreference("countdown_value");
            if(switchPreference.isChecked()){
                editTextPreference.setEnabled(true);
            }else
                editTextPreference.setEnabled(false);

            switchPreference.setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(Preference preference,
                                                          Object newValue) {
                            if((Boolean)newValue) {
                                editTextPreference.setEnabled(true);
                            }
                            else {
                                editTextPreference.setEnabled(false);
                            }
                            return true;
                        }
                    }
            );
        }

    }


}
