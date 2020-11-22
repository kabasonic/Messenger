package com.kabasonic.messenger.ui.bottomnavigation;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.kabasonic.messenger.R;

public class LoadingDialog {
    Activity activity;
    AlertDialog dialog;

    public LoadingDialog(Activity activity){
        this.activity = activity;
    }
    public void startLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading,null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    public void dismisDialog(){
        dialog.dismiss();
    }
}
