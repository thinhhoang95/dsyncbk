package hu.pe.thinhhoang.aaosync;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import java.lang.ref.PhantomReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import hu.pe.thinhhoang.aaosync.database.exams.ExamsHelper;
import hu.pe.thinhhoang.aaosync.database.grades.GradesHelper;
import hu.pe.thinhhoang.aaosync.database.timetable.SubjectHelper;
import hu.pe.thinhhoang.aaosync.service.SyncHelper;
import hu.pe.thinhhoang.aaosync.service.SyncOrchestrator;
import hu.pe.thinhhoang.aaosync.settings.Settings;
import hu.pe.thinhhoang.aaosync.utils.TimeTeller;
import hu.pe.thinhhoang.aaosync.utils.VoidInterface;

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
public class SettingsActivity extends AppCompatPreferenceActivity {
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
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
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

    private static void setSummaryValue(Preference preference)
    {
        String stringValue = PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), "");

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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || ResetFragment.class.getName().equals(fragmentName)
                || TimeTablePreferenceFragment.class.getName().equals(fragmentName);
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
            setHasOptionsMenu(true);

            /* Bind the summaries of EditText/List/Dialog/Ringtone preferences
             to their values. When their values change, their summaries are
             updated to reflect the new value, per the Android Design
             guidelines.
             bindPreferenceSummaryToValue(findPreference("example_text"));
             bindPreferenceSummaryToValue(findPreference("example_list")); */

            // Allow sync switch
            SettingsActivityServiceAssist sasa = new SettingsActivityServiceAssist();
            sasa.serviceOperationStateListener(findPreference("enable_service"), getActivity().getApplicationContext());

            // Last sync
            EditTextPreference edf = (EditTextPreference) findPreference("lastSync");
            Settings.setContext(getActivity().getApplicationContext());
            SimpleDateFormat sdf = new SimpleDateFormat(Settings.STANDARD_DATE_FORMAT);
            try {
                Date lsd = sdf.parse(Settings.getLastSync());
                Log.v("AAOSyncT","Last sync at "+lsd.getTime());
                edf.setSummary(TimeTeller.getTimeAgo(lsd.getTime()));
            } catch (Exception e)
            {
                edf.setSummary("Chưa đồng bộ lần nào");
            }

            // GradesSync frequency
            ListPreference syncDurationPref = (ListPreference) findPreference("sync_duration");
            int index = syncDurationPref.findIndexOfValue(Settings.getSyncDuration()+"");

            // Set the summary to reflect the new value.
            syncDurationPref.setSummary(
                    index >= 0
                            ? syncDurationPref.getEntries()[index]
                            : null);

            syncDurationPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    // Reset the GradesSync service
                    if(!Settings.getMSSV().equals("undef") && SyncHelper.isNetworkAvailable(getActivity().getApplicationContext()) && Settings.getAllowSync())
                    {
                        SyncHelper.cancelAlarm(getActivity().getApplicationContext());
                        getActivity().getApplicationContext().startService(new Intent(getActivity().getApplicationContext(), SyncOrchestrator.class));
                    }

                    // Bind new value

                    ListPreference listPreference = (ListPreference) preference;
                    int index = listPreference.findIndexOfValue(newValue.toString());

                    // Set the summary to reflect the new value.
                    preference.setSummary(
                            index >= 0
                                    ? listPreference.getEntries()[index]
                                    : null);

                    return true;
                }
            });

            // Edit MSSV
            EditTextPreference mssvPref = (EditTextPreference) findPreference("MSSV");
            mssvPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    GradesHelper gradeMachine = new GradesHelper(getActivity().getApplicationContext());
                    gradeMachine.emptyTable();
                    Settings.setGradeCount(0);
                    return true;
                }
            });

            setSummaryValue(findPreference("MSSV"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows TimeTable-related preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class TimeTablePreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_timetable);
            setHasOptionsMenu(true);

            // Event handlers
            bindPreferenceSummaryToValue(findPreference("facility"));
            setSummaryValue(findPreference("facility"));

            bindPreferenceSummaryToValue(findPreference("notify_me_before"));
            setSummaryValue(findPreference("notify_me_before"));

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }


    /**
     * This fragment shows Reset-related preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ResetFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_reset);
            setHasOptionsMenu(true);
            Settings.setContext(getActivity());

            // Reset grades configurations
            Preference gradesPreference = (Preference) findPreference("reset_grades");
            gradesPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    confirmAndExecute(new VoidInterface() {
                        @Override
                        public void callback(Object param) {
                            GradesHelper gradesHelper = new GradesHelper(getActivity());
                            gradesHelper.emptyTable();
                            Settings.setGradeCount(0);
                        }
                    });
                    return true;
                }
            });

            // Reset timetable configurations
            Preference timetablePreference = (Preference) findPreference("reset_timetable");
            timetablePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    confirmAndExecute(new VoidInterface() {
                        @Override
                        public void callback(Object param) {
                            SubjectHelper subjectHelper = new SubjectHelper(getActivity());
                            subjectHelper.emptyTable();
                            Settings.resetTimeTableSynced();
                        }
                    });
                    return true;
                }
            });

            // Reset exams configurations
            Preference examPreference = (Preference) findPreference("reset_exams");
            examPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    confirmAndExecute(new VoidInterface() {
                        @Override
                        public void callback(Object param) {
                            ExamsHelper examsHelper = new ExamsHelper(getActivity());
                            examsHelper.emptyTable();
                            Settings.setExamsSyncedInvalidate(); // To refresh data the next time ExamsFragment is launched!
                        }
                    });
                    ExamsHelper examsHelper = new ExamsHelper(getActivity());
                    examsHelper.emptyTable();
                    Settings.setExamsSyncedInvalidate(); // To refresh data the next time ExamsFragment is launched!
                    return true;
                }
            });

            // Reset notifications configurations
            Preference notifyPreference = (Preference) findPreference("reset_notify");
            notifyPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    confirmAndExecute(new VoidInterface() {
                        @Override
                        public void callback(Object param) {
                            ExamsHelper examsHelper = new ExamsHelper(getActivity());
                            examsHelper.eraseAllExamsNotifiers();
                        }
                    });
                    return true;
                }
            });

            // Reset everything
            Preference allPreference = findPreference("reset_all");
            allPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    confirmAndExecute(new VoidInterface() {
                        @Override
                        public void callback(Object param) {
                            GradesHelper gradesHelper = new GradesHelper(getActivity());
                            gradesHelper.emptyTable();
                            Settings.setGradeCount(0);
                            SubjectHelper subjectHelper = new SubjectHelper(getActivity());
                            subjectHelper.emptyTable();
                            Settings.resetTimeTableSynced();
                            ExamsHelper examsHelper = new ExamsHelper(getActivity());
                            examsHelper.emptyTable();
                            Settings.setExamsSyncedInvalidate(); // To refresh data the next time ExamsFragment is launched!
                            examsHelper.eraseAllExamsNotifiers();
                        }
                    });
                    return true;
                }
            });

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        private void confirmAndExecute(final VoidInterface voidInterface)
        {
            if (voidInterface!=null)
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                // set title
                alertDialogBuilder.setTitle("Xác nhận");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Bạn có chắc chắn muốn xóa dữ liệu không? Sau khi xóa, chương trình sẽ tải dữ liệu mới.")
                        .setCancelable(true)
                        .setPositiveButton("CHẮC CHẮN",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                voidInterface.callback(null);
                                new AlertDialog.Builder(getActivity()).setTitle("Hoàn tất").setMessage("Hoạt động mà bạn yêu cầu đã hoàn thành.").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }).show();
                            }
                        })
                        .setNegativeButton("THÔI",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        }
    }


    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
