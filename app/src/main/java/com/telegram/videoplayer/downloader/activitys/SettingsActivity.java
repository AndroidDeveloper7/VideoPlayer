package com.telegram.videoplayer.downloader.activitys;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.telegram.videoplayer.downloader.Adshandler;
import com.telegram.videoplayer.downloader.R;
import com.telegram.videoplayer.downloader.utildata.PreferenceUtil;
import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.common.prefs.ATESwitchPreference;

import java.util.Objects;

public class SettingsActivity extends BaseActivity implements ColorChooserDialog.ColorCallback {
    Toolbar toolbar;

    @Override
    public void onColorChooserDismissed(@NonNull ColorChooserDialog colorChooserDialog) {
    }

    @Override

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.preference_activity_custom);
        getWindow().setFlags(1024, 1024);
        Toolbar toolbar2 = findViewById(R.id.toolbar);
        this.toolbar = toolbar2;
        toolbar2.setTitle("Setting");
        setSupportActionBar(this.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        FrameLayout nativeads = findViewById(R.id.nativeads);
        Adshandler.refreshAd(nativeads, this);
        if (bundle == null) {
            getFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
            return;
        }
        SettingsFragment settingsFragment = (SettingsFragment) getFragmentManager().findFragmentById(R.id.content_frame);
        if (settingsFragment != null) {
            settingsFragment.invalidateSettings();
        }
    }

    @Override
    public void onColorSelection(ColorChooserDialog colorChooserDialog, int i) {
        ThemeStore editTheme = ThemeStore.editTheme(this);
        int title = colorChooserDialog.getTitle();
        if (title == R.string.accent_color) {
            editTheme.accentColor(i);
        } else if (title == R.string.primary_text_color) {
            editTheme.textColorPrimary(i);
        } else if (title == R.string.secondary_text_color) {
            editTheme.textColorSecondary(i);
        }
        editTheme.commit();
        recreate();
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        finish();
        return true;
    }

    @SuppressWarnings({"deprecation", "SameReturnValue"})
    public static class SettingsFragment extends PreferenceFragment {
        @SuppressWarnings("deprecation")
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            addPreferencesFromResource(R.xml.preferences);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        }

        public void onViewCreated(View view, Bundle bundle) {
            super.onViewCreated(view, bundle);
            invalidateSettings();
        }

        @SuppressWarnings("deprecation")
        public void invalidateSettings() {


            ATESwitchPreference aTESwitchPreference = findPreference(PreferenceUtil.AUTOPLAYNEXT);
            Objects.requireNonNull(aTESwitchPreference).setChecked(PreferenceUtil.getInstance(getActivity()).getAutoplaynext());
            aTESwitchPreference.setOnPreferenceChangeListener((preference, obj) -> {
                PreferenceUtil.getInstance(SettingsFragment.this.getActivity()).setAutoplaynext((Boolean) obj);
                return true;
            });
            aTESwitchPreference.setOnPreferenceClickListener(preference -> true);
            ATESwitchPreference aTESwitchPreference2 = findPreference(PreferenceUtil.BATTERYLOCK);
            Objects.requireNonNull(aTESwitchPreference2).setChecked(PreferenceUtil.getInstance(getActivity()).getBatterylock());
            aTESwitchPreference2.setOnPreferenceChangeListener((preference, obj) -> {
                PreferenceUtil.getInstance(SettingsFragment.this.getActivity()).setBatterylock((Boolean) obj);
                return true;
            });
            aTESwitchPreference2.setOnPreferenceClickListener(preference -> true);
            final Preference findPreference = findPreference("Resume");
            int resumestatus = PreferenceUtil.getInstance(getActivity()).getResumestatus();
            if (resumestatus == 0) {
                Objects.requireNonNull(findPreference).setSummary("Yes");
            } else if (resumestatus == 1) {
                Objects.requireNonNull(findPreference).setSummary("No");
            } else {
                Objects.requireNonNull(findPreference).setSummary("Ask at Startup");
            }
            findPreference.setOnPreferenceClickListener(preference -> SettingsFragment.this.lambda$invalidateSettings$3$SettingsActivity$SettingsFragment(findPreference));
            final Preference findPreference2 = findPreference("Orientation");

            int orientation = PreferenceUtil.getInstance(getActivity()).getOrientation();
            if (orientation == 0) {
                Objects.requireNonNull(findPreference2).setSummary("Sensor");
            } else if (orientation == 1) {
                Objects.requireNonNull(findPreference2).setSummary("Landscape");
            } else {
                Objects.requireNonNull(findPreference2).setSummary("Portrait");
            }
            findPreference2.setOnPreferenceClickListener(preference -> SettingsFragment.this.lambda$invalidateSettings$4$SettingsActivity$SettingsFragment(findPreference2));
        }

        public boolean lambda$invalidateSettings$3$SettingsActivity$SettingsFragment(final Preference preference) {
            try {
                final MaterialDialog build = new MaterialDialog.Builder(getActivity()).customView(R.layout.dialog_orientation, false).build();
                RadioGroup radioGroup = Objects.requireNonNull(build.getCustomView()).findViewById(R.id.radioGroup);
                RadioButton radioButton = build.getCustomView().findViewById(R.id.radioButtonSensor);
                RadioButton radioButton2 = build.getCustomView().findViewById(R.id.radioButtonLandscape);
                RadioButton radioButton3 = build.getCustomView().findViewById(R.id.radioButtonPortrait);
                ((TextView) build.getCustomView().findViewById(R.id.txtTitle)).setText("Resume Video");
                radioButton.setText("Yes");
                radioButton2.setText("No");
                radioButton3.setText("Ask at Startup");
                int resumestatus = PreferenceUtil.getInstance(getActivity()).getResumestatus();
                if (resumestatus == 0) {
                    preference.setSummary("Yes");
                    radioButton.setChecked(true);
                    radioButton2.setChecked(false);
                    radioButton3.setChecked(false);
                } else if (resumestatus == 1) {
                    preference.setSummary("No");
                    radioButton.setChecked(false);
                    radioButton2.setChecked(true);
                    radioButton3.setChecked(false);
                } else {
                    preference.setSummary("Ask at Startup");
                    radioButton.setChecked(false);
                    radioButton2.setChecked(false);
                    radioButton3.setChecked(true);
                }
                radioGroup.setOnCheckedChangeListener((radioGroup1, i) -> {
                    RadioButton radioButton1 = radioGroup1.findViewById(i);
                    if (radioButton1 != null && i > -1) {
                        Log.e("rbsumdfe ", " : " + radioButton1.getText());
                        if (radioButton1.getText().toString().equalsIgnoreCase("Yes")) {
                            preference.setSummary("Yes");
                            PreferenceUtil.getInstance(SettingsFragment.this.getActivity()).saveResumestatus(0);
                        } else if (radioButton1.getText().toString().equalsIgnoreCase("No")) {
                            preference.setSummary("No");
                            PreferenceUtil.getInstance(SettingsFragment.this.getActivity()).saveResumestatus(1);
                        } else {
                            preference.setSummary("Ask at Startup");
                            PreferenceUtil.getInstance(SettingsFragment.this.getActivity()).saveResumestatus(2);
                        }
                    }
                    build.dismiss();
                });
                build.getCustomView().findViewById(R.id.txtCancel).setOnClickListener(view -> build.dismiss());
                build.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        public boolean lambda$invalidateSettings$4$SettingsActivity$SettingsFragment(final Preference preference) {
            try {
                final MaterialDialog build = new MaterialDialog.Builder(getActivity()).customView(R.layout.dialog_orientation, false).build();
                RadioGroup radioGroup = Objects.requireNonNull(build.getCustomView()).findViewById(R.id.radioGroup);
                RadioButton radioButton = build.getCustomView().findViewById(R.id.radioButtonSensor);
                RadioButton radioButton2 = build.getCustomView().findViewById(R.id.radioButtonLandscape);
                RadioButton radioButton3 = build.getCustomView().findViewById(R.id.radioButtonPortrait);
                int orientation = PreferenceUtil.getInstance(getActivity()).getOrientation();
                if (orientation == 0) {
                    preference.setSummary("Sensor");
                    radioButton.setChecked(true);
                    radioButton2.setChecked(false);
                    radioButton3.setChecked(false);
                } else if (orientation == 1) {
                    preference.setSummary("Landscape");
                    radioButton.setChecked(false);
                    radioButton2.setChecked(true);
                    radioButton3.setChecked(false);
                } else {
                    preference.setSummary("Portrait");
                    radioButton.setChecked(false);
                    radioButton2.setChecked(false);
                    radioButton3.setChecked(true);
                }
                radioGroup.setOnCheckedChangeListener((radioGroup1, i) -> {
                    RadioButton radioButton1 = radioGroup1.findViewById(i);
                    if (radioButton1 != null && i > -1) {
                        Log.e("rb.getText() ", " : " + radioButton1.getText());
                        if (radioButton1.getText().toString().equalsIgnoreCase("Sensor")) {
                            preference.setSummary("Sensor");
                            PreferenceUtil.getInstance(SettingsFragment.this.getActivity()).saveOrientation(0);
                        } else if (radioButton1.getText().toString().equalsIgnoreCase("Landscape")) {
                            preference.setSummary("Landscape");
                            PreferenceUtil.getInstance(SettingsFragment.this.getActivity()).saveOrientation(1);
                        } else {
                            preference.setSummary("Portrait");
                            PreferenceUtil.getInstance(SettingsFragment.this.getActivity()).saveOrientation(2);
                        }
                    }
                    build.dismiss();
                });
                build.getCustomView().findViewById(R.id.txtCancel).setOnClickListener(view -> build.dismiss());
                build.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
    }
}
