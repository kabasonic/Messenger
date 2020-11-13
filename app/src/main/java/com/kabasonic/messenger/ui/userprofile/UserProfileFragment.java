package com.kabasonic.messenger.ui.userprofile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.ui.adapters.AdapterProfileDoubleItem;
import com.kabasonic.messenger.ui.adapters.items.RowItem;
import com.kabasonic.messenger.ui.authorization.otpcode.OTPCodeFragmentArgs;

import java.util.ArrayList;

public class UserProfileFragment extends Fragment {
    private ArrayList<RowItem> mArraList;
    private ListView mLvUser;
    private TextView mUserName, mUserStatus;
    private FloatingActionButton mSendMessage;
    private ImageView mUserImage;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_user,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getArgumentsFragment();
        initView(view);
        createLists();
        AdapterProfileDoubleItem adapterThreeRow = new AdapterProfileDoubleItem(getContext(), R.layout.double_row_profile, mArraList);
        mLvUser.setAdapter(adapterThreeRow);
    }

    private void getArgumentsFragment() {
        String uid;
        if (getArguments() != null) {

            UserProfileFragmentArgs args = UserProfileFragmentArgs.fromBundle(getArguments());
            uid = getArguments().getString("uid");
            Toast.makeText(getActivity(), uid, Toast.LENGTH_SHORT).show();
        }
    }

    private void initView(View view) {
        mLvUser = (ListView) view.findViewById(R.id.lvUserProfile);
        mUserName = (TextView) view.findViewById(R.id.userNameAccount);
        mUserStatus = (TextView) view.findViewById(R.id.userStatusAccount);
        //mUserImage = (ImageView) view.findViewById(R.id.app_bar_user_profile);
        mSendMessage = (FloatingActionButton) view.findViewById(R.id.send_message_profile);
    }

    private void createLists() {
        String[] titleLV = getResources().getStringArray(R.array.listOneTitleProfile);
        String[] subtitleLV = getResources().getStringArray(R.array.listOneSubtitleProfile);

        Integer[] imagesOne = {R.drawable.ic_round_alternate_email_24,
                R.drawable.ic_round_call_24,
                R.drawable.ic_round_info_24};

        Integer[] imagesTwo = {R.drawable.ic_round_notifications_24,
                R.drawable.ic_round_lock_24,
                R.drawable.ic_round_color_lens_24,
                R.drawable.ic_round_language_24,
                R.drawable.ic_round_live_help_24,
                R.drawable.ic_round_help_24};

        mArraList= new ArrayList<>();

        for (int i = 0; i < imagesOne.length; i++) {
            mArraList.add(new RowItem(imagesOne[i], titleLV[i], subtitleLV[i]));
        }

    }


}
