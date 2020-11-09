package com.kabasonic.messenger.ui.bottomnavigation.contacts.tabs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kabasonic.messenger.MainActivity;
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.ui.adapters.AdapterSingleItem;
import com.kabasonic.messenger.ui.adapters.items.RowItem;
import com.kabasonic.messenger.ui.bottomnavigation.contacts.ContactsFragment;

import java.util.ArrayList;

public class AllContactsFragment extends Fragment {

    public static final String TAG = "AllContactsFragment";

    public ArrayList<RowItem> mRowItems;
    private RecyclerView mRecyclerView;
    private AdapterSingleItem mAdapterSingleItem;
    private RecyclerView.LayoutManager mLayoutManager;
    MainActivity mActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mActivity = (MainActivity) context;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_all_contacts, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createExampleList();
        buildRecyclerView();
    }


    public void createExampleList() {
        mRowItems = new ArrayList<>();

        mRowItems.add(new RowItem(R.drawable.image_profile_test, true, "Jacek Bura"));
        mRowItems.add(new RowItem(R.drawable.image_profile_test, false, "Filip Duda"));
        mRowItems.add(new RowItem(R.drawable.image_profile_test, true, "Yura Shnyt"));

    }

    public void buildRecyclerView() {
        mRecyclerView = getView().findViewById(R.id.rvAllContacts);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mActivity);
        mAdapterSingleItem = new AdapterSingleItem(mRowItems);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapterSingleItem);
        mAdapterSingleItem.setOnItemClickListener(new AdapterSingleItem.SingleItemRow() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(getContext(), "Clicked row", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMoreButtonClick(int position) {
                Toast.makeText(getContext(), "Clicked button", Toast.LENGTH_SHORT).show();
                alertWindow();
            }
        });
    }

    private void alertWindow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(R.array.dialog_contact, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "Opened dialog window ");
                switch (which) {
                    case 0:
                        Log.i(TAG, "Selected item " + which);
                        break;
                    case 1:
                        Log.i(TAG, "Selected item " + which);
                        break;
                    case 2:
                        Log.i(TAG, "Selected item " + which);
                        break;
                    case 3:
                        Log.i(TAG, "Selected item " + which);
                        break;
                    default:
                        Log.i(TAG, "Not selected item ");
                        break;
                }
            }
        });
        builder.show();
    }

}
