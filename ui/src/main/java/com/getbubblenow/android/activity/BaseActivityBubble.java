package com.getbubblenow.android.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.getbubblenow.android.R;
import com.getbubblenow.android.fragment.ErrorDialogFragment;
import com.getbubblenow.android.fragment.LoadingDialogFragment;
import com.getbubblenow.android.fragment.ErrorDialogFragment;
import com.getbubblenow.android.fragment.LoadingDialogFragment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;

public class BaseActivityBubble extends AppCompatActivity {

    public static final String LOADING_TAG = "loading_tag";
    public static final String NO_CONNECTION_TAG = "no_connection_tag";
    public static final String ERROR_TAG = "error_tag";
    public static final String RATE_TAG = "rate tag";
    private final long LOADER_DELAY = 1000;

    private LoadingDialogFragment loadingDialog;
    private ErrorDialogFragment errorDialog;
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

    protected void showNetworkNotAvailableMessage() {
        final LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.toast_layout, findViewById(R.id.custom_toast_container));
        final Toast toast = new Toast(this);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 60);
        toast.setView(layout);
        toast.show();
    }

    public void showErrorDialog(String message){
        errorDialog = ErrorDialogFragment.newInstance();
        final Bundle bundle = new Bundle();
        bundle.putString("message",message);
        errorDialog.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(errorDialog,ERROR_TAG).commitAllowingStateLoss();
    }

}
