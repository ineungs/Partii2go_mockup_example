package th.or.nectec.party.offline.android.Partii2Go;

/**
 * Created by suwan on 11/26/2016 AD.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import th.or.nectec.partii.embedded.android.EmbeddedUtils.Constant;

public class SettingPreferencesActivity extends PreferenceActivity {

    static Context context;
    MyPreferenceFragment mf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        mf = new MyPreferenceFragment();
        getFragmentManager().beginTransaction().replace(android.R.id.content, mf).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mf.setPreferenceProperty();
    }

    public static class MyPreferenceFragment extends PreferenceFragment implements  SharedPreferences.OnSharedPreferenceChangeListener
    {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            setPreferenceProperty();
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        public void setPreferenceProperty(){
            String langStr = PreferenceManager.getDefaultSharedPreferences(context).getString("language_preference", null);
            String apikeyStr = PreferenceManager.getDefaultSharedPreferences(context).getString("apikey_preference", null);

            if (langStr != null){
                ListPreference langPref = (ListPreference) findPreference("language_preference");
                EditTextPreference apiPref = (EditTextPreference) findPreference("apikey_preference");


                if(langStr.equals("TH")) {
                    Constant.setThai();
                    langPref.setSummary(Constant.DISPLAYED_LANG);

                    if(apikeyStr == null || apikeyStr.trim().length() == 0){
                        apiPref.setSummary(Constant.APIKEY_SUMMARY);
                    }
                    else{
                        apiPref.setSummary(apikeyStr);
                    }
                }
                else{
                    Constant.setEnglish();
                    langPref.setSummary(Constant.DISPLAYED_LANG);

                    if(apikeyStr == null || apikeyStr.trim().length() == 0){
                        apiPref.setSummary(Constant.APIKEY_SUMMARY);
                    }
                    else{
                        apiPref.setSummary(apikeyStr);
                    }
                }
            }
        }
    }
}
