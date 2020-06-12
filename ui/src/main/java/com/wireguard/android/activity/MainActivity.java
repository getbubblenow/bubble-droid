package com.wireguard.android.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.wireguard.android.R;
import com.wireguard.android.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private MainViewModel mainViewModel;
    private TextView bubbleStatus;
    private TextView titleMyBubble;
    private Button connectButton;
    private ImageView imageMyBubble;
    private ImageView mark;
    private boolean connectionStateFlag;

    private static final int REQUEST_CODE_VPN_PERMISSION = 23491;
    public static final int LEFT = 16;
    public static final int RIGHT = 16;
    public static final int TOP = 150;
    public static final int BOTTOM_CONNECTED = 90;
    public static final int BOTTOM_DISCONNECTED = 100;

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
    }

    private void initListeners() {
        connectButton.setOnClickListener(new OnClickListener() {
            @Override public void onClick(final View v) {
                connect();
            }
        });
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
            bubbleStatus.setPadding(LEFT, TOP, RIGHT,BOTTOM_CONNECTED);
            connectButton.setText(getString(R.string.disconnect));
            imageMyBubble.setImageResource(R.drawable.bubble_connected);
            mark.setVisibility(View.VISIBLE);
            titleMyBubble.setVisibility(View.GONE);
        } else {
            bubbleStatus.setText(getString(R.string.not_connected_bubble));
            bubbleStatus.setTextColor(getResources().getColor(R.color.gray));
            bubbleStatus.setPadding(LEFT,TOP,RIGHT,BOTTOM_DISCONNECTED);
            connectButton.setText(getString(R.string.connect));
            imageMyBubble.setImageResource(R.drawable.bubble_disconnected);
            mark.setVisibility(View.GONE);
            titleMyBubble.setVisibility(View.VISIBLE);
        }
    }
}
