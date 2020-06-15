package com.wireguard.android.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.wireguard.android.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;

public class ErrorDialogFragment extends DialogFragment {

    private AppCompatButton contactSupportButton;

    public static ErrorDialogFragment newInstance() {
        ErrorDialogFragment fragment = new ErrorDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.error_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        String message = "";
        if(bundle!=null){
            message = bundle.getString("message");
        }
        initUI(view,message);
    }

    private void initUI(View view, String message) {
        initViews(view);
        initListeners(message);
    }

    private void initViews(View view) {
        contactSupportButton = view.findViewById(R.id.contactSupportButton);
    }

    private void initListeners(String message) {
        contactSupportButton.setOnClickListener(new OnClickListener() {
            @Override public void onClick(final View v) {
                sendEmail(message);
            }
        });
    }

    private void sendEmail(String message){
        final Intent send = new Intent(Intent.ACTION_SENDTO);
        final String uriText = "mailto:" + Uri.encode("support@getbubblenow.com") +
                "?subject=" + Uri.encode("Error Message") +
                "&body=" + Uri.encode(message);
        final Uri uri = Uri.parse(uriText);
        send.setData(uri);
        startActivity(Intent.createChooser(send, "Send Error"));
    }
}
