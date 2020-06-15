package com.wireguard.android.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.wireguard.android.R;
import com.wireguard.android.fragment.LoadingDialogFragment;

import java.net.ConnectException;
import java.net.UnknownHostException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;

public class BaseActivityBubble extends AppCompatActivity {

    public static final String LOADING_TAG = "loading_tag";
    public static final String NO_CONNECTION_TAG = "no_connection_tag";
    public static final String RATE_TAG = "rate tag";
    private final long LOADER_DELAY = 1000;

    private LoadingDialogFragment loadingDialog;
    private boolean showDialog = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void showLoadingDialog() {
        showDialog = true;

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (showDialog) {
                    if (loadingDialog == null
                            || loadingDialog.getDialog() == null
                            || !loadingDialog.getDialog().isShowing()
                            || loadingDialog.isRemoving()) {
                        if (loadingDialog != null) {
                            loadingDialog.dismissAllowingStateLoss();
                        }
                        if (getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                            loadingDialog = LoadingDialogFragment.newInstance();
                            getSupportFragmentManager().beginTransaction().
                                    add(loadingDialog, LOADING_TAG).commitAllowingStateLoss();
                        }
                    }
                }
            }

        }, LOADER_DELAY);
    }

    public void closeLoadingDialog() {
        showDialog = false;
        if (loadingDialog != null && loadingDialog.getDialog() != null
                && loadingDialog.getDialog().isShowing()
                && !loadingDialog.isRemoving()) {
            loadingDialog.dismissAllowingStateLoss();
        }
    }

    protected void showNetworkNotAvailableMessage(Context context) {
        if (context instanceof LoginActivity) {
            ((LoginActivity) context).runOnUiThread(new Runnable() {
                @Override public void run() {
                    final LayoutInflater inflater = ((LoginActivity) context).getLayoutInflater();
                    final View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) ((LoginActivity) context).findViewById(R.id.custom_toast_container));
                    final Toast toast = new Toast(context);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 60);
                    toast.setView(layout);
                    toast.show();
                }
            });
        }
    }
}
