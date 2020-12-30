package com.kabasonic.messenger;

import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.storage.internal.Sleeper;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class LockScreenFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "LockScreenFragment";
    private View view1, view2, view3, view4;
    private ArrayList<Integer> arrayPin = new ArrayList<>();
    private ArrayList<Integer> arraySubmitPin = new ArrayList<>();

    private Integer pin = 0;
    private Integer pinSubmit = 0;
    private TextView textView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lock_screen, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textView = (TextView) view.findViewById(R.id.textPin);

        ExtendedFloatingActionButton fab0 = (ExtendedFloatingActionButton) view.findViewById(R.id.fab0);
        ExtendedFloatingActionButton fab1 = (ExtendedFloatingActionButton) view.findViewById(R.id.fab1);
        ExtendedFloatingActionButton fab2 = (ExtendedFloatingActionButton) view.findViewById(R.id.fab2);
        ExtendedFloatingActionButton fab3 = (ExtendedFloatingActionButton) view.findViewById(R.id.fab3);
        ExtendedFloatingActionButton fab4 = (ExtendedFloatingActionButton) view.findViewById(R.id.fab4);
        ExtendedFloatingActionButton fab5 = (ExtendedFloatingActionButton) view.findViewById(R.id.fab5);
        ExtendedFloatingActionButton fab6 = (ExtendedFloatingActionButton) view.findViewById(R.id.fab6);
        ExtendedFloatingActionButton fab7 = (ExtendedFloatingActionButton) view.findViewById(R.id.fab7);
        ExtendedFloatingActionButton fab8 = (ExtendedFloatingActionButton) view.findViewById(R.id.fab8);
        ExtendedFloatingActionButton fab9 = (ExtendedFloatingActionButton) view.findViewById(R.id.fab9);
        ExtendedFloatingActionButton fabDelete = (ExtendedFloatingActionButton) view.findViewById(R.id.fabDelete);

        view1 = (View) view.findViewById(R.id.view1);
        view2 = (View) view.findViewById(R.id.view2);
        view3 = (View) view.findViewById(R.id.view3);
        view4 = (View) view.findViewById(R.id.view4);

        fab0.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);
        fab4.setOnClickListener(this);
        fab5.setOnClickListener(this);
        fab6.setOnClickListener(this);
        fab7.setOnClickListener(this);
        fab8.setOnClickListener(this);
        fab9.setOnClickListener(this);
        fabDelete.setOnClickListener(this);


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setPin() {
        if (arrayPin.size() == 4 && pin == 0) {
            for (Integer item : arrayPin) {
                pin += item.intValue();
            }
            textView.setText("Confirmed your PIN");
            arrayPin.clear();
            arraySubmitPin.clear();
            changeIndicatorColor();
        }
        if (arraySubmitPin.size() == 4 && pin != 0) {
            for (Integer item : arraySubmitPin) {
                pinSubmit += item.intValue();
            }
        }
        if (pin != 0 && pinSubmit != 0) {
            equalsPin();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void equalsPin() {
        if (pin == pinSubmit) {
            Log.d(TAG, "PINS OKAY SET LOCK SCREEN");
            this.pin = 0;
            this.pinSubmit = 0;
            arrayPin.clear();
            arraySubmitPin.clear();
            NavDirections action = LockScreenFragmentDirections.actionLockScreenFragmentToSettingsFragment();
            Navigation.findNavController(getView()).navigate(action);

        } else {
            textView.setText("Set your PIN");
            Toast.makeText(getContext(),"Your PIN incorrect. Please try again.",Toast.LENGTH_SHORT).show();
            Log.d(TAG, "PINS BAD TRY AGAIN");
            this.pin = 0;
            this.pinSubmit = 0;
            arrayPin.clear();
            arraySubmitPin.clear();
            changeIndicatorColor();
        }
    }

    private void backspacePin() {
        if (arrayPin.size() != 0) {
            arrayPin.remove(arrayPin.size() - 1);
        }
        if (arraySubmitPin.size() != 0) {
            arraySubmitPin.remove(arraySubmitPin.size() - 1);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void changeIndicatorColor() {
        if(arrayPin.size() == 0){
            view1.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorLight)));
            view2.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorLight)));
            view3.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorLight)));
            view4.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorLight)));
        }else if(arrayPin.size() == 1){
            view1.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
            view2.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorLight)));
            view3.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorLight)));
            view4.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorLight)));
        }else if(arrayPin.size() == 2){
            view1.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
            view2.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
            view3.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorLight)));
            view4.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorLight)));
        }else if(arrayPin.size() == 3){
            view1.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
            view2.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
            view3.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
            view4.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorLight)));
        }else if(arrayPin.size() == 4){
            view1.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
            view2.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
            view3.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
            view4.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab0:
                Log.d(TAG, "fab0");
                arrayPin.add(0);
                arraySubmitPin.add(0);
                break;
            case R.id.fab1:
                Log.d(TAG, "fab1");
                arrayPin.add(1);
                arraySubmitPin.add(1);
                break;
            case R.id.fab2:
                Log.d(TAG, "fab2");
                arrayPin.add(2);
                arraySubmitPin.add(2);
                break;
            case R.id.fab3:
                Log.d(TAG, "fab3");
                arrayPin.add(3);
                arraySubmitPin.add(3);
                break;
            case R.id.fab4:
                Log.d(TAG, "fab4");
                arrayPin.add(4);
                arraySubmitPin.add(4);
                break;
            case R.id.fab5:
                Log.d(TAG, "fab5");
                arrayPin.add(5);
                arraySubmitPin.add(5);
                break;
            case R.id.fab6:
                Log.d(TAG, "fab6");
                arrayPin.add(6);
                arraySubmitPin.add(6);
                break;
            case R.id.fab7:
                Log.d(TAG, "fab7");
                arrayPin.add(7);
                arraySubmitPin.add(7);
                break;
            case R.id.fab8:
                Log.d(TAG, "fab8");
                arrayPin.add(8);
                arraySubmitPin.add(8);
                break;
            case R.id.fab9:
                Log.d(TAG, "fab9");
                arrayPin.add(9);
                arraySubmitPin.add(9);
                break;
            case R.id.fabDelete:
                backspacePin();
                break;
        }
        changeIndicatorColor();
        setPin();
    }
}
