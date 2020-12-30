package com.kabasonic.messenger;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.kabasonic.messenger.utils.Constant;
import com.kabasonic.messenger.utils.Methods;

public class SettingsFragment extends PreferenceFragmentCompat implements View.OnClickListener {

    public static final String TAG = "SettingsFragment";
    private AlertDialog alertDialog;
    private View viewDialogColors;
    Methods methods;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    int appTheme;
    int themeColor;
    int appColor;
    Constant constant;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // inflate the layout using the cloned inflater, not default inflater
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        setPreferencesFromResource(R.xml.root_preferences, rootKey);

    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = sharedPreferences.edit();

        Preference pref_nav_security = findPreference("pref_nav_security");
        pref_nav_security.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference _pref) {
                // Do what you want
                NavDirections action = SettingsFragmentDirections.actionSettingsFragmentToSettingsSecurityFragment();
                Navigation.findNavController(getView()).navigate(action);
                return true;
            }
        });

        Preference pref_colors = findPreference("pref_colors");
        pref_colors.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onPreferenceClick(Preference preference) {
                dialogColor();
                return true;
            }
        });
    }

    private void dialogColor() {
        viewLoadElements();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(viewDialogColors);
        builder.setTitle("Colors")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void viewLoadElements() {
        LayoutInflater inflater = getLayoutInflater();
        viewDialogColors = inflater.inflate(R.layout.dialog_choose_color, null);
        View btRed = viewDialogColors.findViewById(R.id.colorApp1);
        View btBlue = viewDialogColors.findViewById(R.id.colorApp2);
        View btGreen = viewDialogColors.findViewById(R.id.colorApp3);
        View btPink = viewDialogColors.findViewById(R.id.colorApp4);
        View btPurple = viewDialogColors.findViewById(R.id.colorApp5);
        View btOrange = viewDialogColors.findViewById(R.id.colorApp6);

        btRed.setOnClickListener(this);
        btBlue.setOnClickListener(this);
        btGreen.setOnClickListener(this);
        btPink.setOnClickListener(this);
        btPurple.setOnClickListener(this);
        btOrange.setOnClickListener(this);
    }

    private void saveValues(int color){
        Constant.color = color;
        methods = new Methods();
        methods.setColorTheme();
        editor.putInt("color", color);
        editor.putInt("theme",Constant.theme);
        editor.commit();
    }

    @Override
    public void onClick(View view) {
        int color = 0;
        switch (view.getId()) {
            case R.id.colorApp1:
                Log.d(TAG, "Color RED");

                 color = 0xffF44336;
                break;
            case R.id.colorApp2:
                Log.d(TAG, "Color BLUE");
                color = 0xff3F51B5;
                break;
            case R.id.colorApp3:
                Log.d(TAG, "Color GREEN");
                color = 0xff4CAF50;
                break;
            case R.id.colorApp4:
                Log.d(TAG, "Color PINK");
                color = 0xffE91E63;
                break;
            case R.id.colorApp5:
                Log.d(TAG, "Color PURPLE");
                color = 0xFF7B1FA2;
                break;
            case R.id.colorApp6:
                Log.d(TAG, "Color ORANGE");
                color = 0xffFF9800;
                break;
        }
        saveValues(color);
        alertDialog.dismiss();

    }
}