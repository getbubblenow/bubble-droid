package com.getbubblenow.android.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.getbubblenow.android.viewmodel.MainViewModel;
import com.getbubblenow.android.R;
import com.getbubblenow.android.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private MainViewModel mainViewModel;
    private TextView bubbleStatus;
    private TextView titleMyBubble;
    private Button connectButton;
    private ImageView imageMyBubble;
    private ImageView mark;
    private ImageButton myBubbleButton;
    private ImageButton accountButton;
    private TextView logout;
    private boolean connectionStateFlag;

    private static final int REQUEST_CODE_VPN_PERMISSION = 23491;
    public static final int CONNECTED_TEXT_VIEW_LEFT_MARGIN = 16;
    public static final int CONNECTED_TEXT_VIEW_RIGHT_MARGIN = 16;
    public static final int CONNECTED_TEXT_VIEW_TOP_MARGIN = 150;
    public static final int DISCONNECTED_TEXT_VIEW_BOTTOM_MARGIN = 90;
    public static final int CONNECTED_TEXT_VIEW_BOTTOM_MARGIN = 100;
    private static final String BASE_URL_PREFIX = "https://";
    private static final String BASE_URL_SUFFIX_MY_BUBBLE = ":1443/";
    private static final String BASE_URL_SUFFIX_ACCOUNT = ":1443/me";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        if (mainViewModel.isUserLoggedIn(this)) {
            setContentView(R.layout.activity_main);
            mainViewModel.buildRepositoryInstance(this, mainViewModel.getUserURL(this));
            initUI();
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override protected void onResume() {
        super.onResume();
        if (mainViewModel.isVPNConnected(this, connectionStateFlag)) {
            connectionStateFlag = false;
            setConnectionStateUI(false);
        } else {
            connectionStateFlag = true;
            setConnectionStateUI(true);
        }
    }

    private void initUI() {
        initViews();
        initListeners();
    }

    private void initViews() {
        bubbleStatus = findViewById(R.id.bubbleStatus);
        connectButton = findViewById(R.id.connectButton);
        imageMyBubble = findViewById(R.id.imageMyBubble);
        mark = findViewById(R.id.mark);
        titleMyBubble = findViewById(R.id.titleMyBubble);
        myBubbleButton = findViewById(R.id.myBubbleButton);
        accountButton = findViewById(R.id.accountButton);
        logout = findViewById(R.id.logout);
    }

    private void initListeners() {
        connectButton.setOnClickListener(new OnClickListener() {
            @Override public void onClick(final View v) {
                connect();
            }
        });
        myBubbleButton.setOnClickListener(new OnClickListener() {
            @Override public void onClick(final View v) {
                showMyBubble();
            }
        });
        accountButton.setOnClickListener(new OnClickListener() {
            @Override public void onClick(final View v) {
                showAccount();
            }
        });
        logout.setOnClickListener(new OnClickListener() {
            @Override public void onClick(final View v) {
                logout();
            }
        });
    }

    private void showAccount() {
        final String hostname = mainViewModel.getHostname(this);
        final String url = BASE_URL_PREFIX + hostname + BASE_URL_SUFFIX_ACCOUNT;
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void showMyBubble() {
        final String hostname = mainViewModel.getHostname(this);
        final String url = BASE_URL_PREFIX + hostname + BASE_URL_SUFFIX_MY_BUBBLE;
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void connect() {
        final boolean state = mainViewModel.isVPNConnected(this, connectionStateFlag);
        connectionStateFlag = state;
        mainViewModel.connect(state, MainActivity.this).observe(MainActivity.this, new Observer<Boolean>() {
            @Override public void onChanged(final Boolean state) {
                setConnectionStateUI(state);
            }
        });
    }

    @Override protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_VPN_PERMISSION) {
            if (resultCode == RESULT_OK) {
                mainViewModel.connectWithPermission(connectionStateFlag, this)
                        .observe(this, new Observer<Boolean>() {
                            @Override public void onChanged(final Boolean state) {
                                setConnectionStateUI(state);
                            }
                        });
            } else {
                connectionStateFlag = false;
                setConnectionStateUI(false);
            }
        }
    }

    private void setConnectionStateUI(boolean state) {
        if (state) {
            bubbleStatus.setText(getString(R.string.connected_bubble));
            bubbleStatus.setTextColor(getResources().getColor(R.color.connectedColor));
            bubbleStatus.setPadding(CONNECTED_TEXT_VIEW_LEFT_MARGIN, CONNECTED_TEXT_VIEW_TOP_MARGIN, CONNECTED_TEXT_VIEW_RIGHT_MARGIN, DISCONNECTED_TEXT_VIEW_BOTTOM_MARGIN);
            connectButton.setText(getString(R.string.disconnect));
            imageMyBubble.setImageResource(R.drawable.bubble_connected);
            mark.setVisibility(View.VISIBLE);
            titleMyBubble.setVisibility(View.GONE);
        } else {
            bubbleStatus.setText(getString(R.string.not_connected_bubble));
            bubbleStatus.setTextColor(getResources().getColor(R.color.gray));
            bubbleStatus.setPadding(CONNECTED_TEXT_VIEW_LEFT_MARGIN, CONNECTED_TEXT_VIEW_TOP_MARGIN, CONNECTED_TEXT_VIEW_RIGHT_MARGIN, CONNECTED_TEXT_VIEW_BOTTOM_MARGIN);
            connectButton.setText(getString(R.string.connect));
            imageMyBubble.setImageResource(R.drawable.bubble_disconnected);
            mark.setVisibility(View.GONE);
            titleMyBubble.setVisibility(View.VISIBLE);
        }
    }

    private void logout(){
        final Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mainViewModel.removeSharedPreferences(MainActivity.this);
        startActivity(intent);
    }
}
