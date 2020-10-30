package com.kabasonic.messenger.ui.onboarding.pages;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.kabasonic.messenger.R;

public class ScreenSlidePageFragmentThree extends Fragment {

    private OnBoarding onBoardingListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page_three, container, false);
        Button buttonGetStarted = (Button) view.findViewById(R.id.get_started_buton);
        buttonGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            onBoardingListener.checkButton(true);
            }
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof OnBoarding){
            onBoardingListener = (OnBoarding) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnBoarding interface");
        }
    }
}
