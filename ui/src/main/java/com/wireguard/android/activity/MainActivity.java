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
import android.widget.TextView;
import android.widget.Toast;
import com.wireguard.android.R;
import com.wireguard.android.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {
    private MainViewModel mainViewModel;
    private TextView bubbleStatus;
    private Button connectButton;
    private boolean connectionStateFlag;

    private static final int REQUEST_CODE_VPN_PERMISSION = 23491;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        if (mainViewModel.isUserLoggedIn(this)) {
            setContentView(R.layout.activity_main);
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        mainViewModel.buildRepositoryInstance(this, mainViewModel.getUserURL(this));
        initUI();
    }

    @Override protected void onResume() {
        super.onResume();
        if (mainViewModel.isVPNConnected(this, connectionStateFlag)) {
            connectionStateFlag = false;
            bubbleStatus.setText(getString(R.string.not_connected_bubble));
            connectButton.setText(getString(R.string.connect));
        } else {
            connectionStateFlag = true;
            bubbleStatus.setText(getString(R.string.connected_bubble));
            connectButton.setText(getString(R.string.disconnect));
        }
    }

    private void initUI() {
        initViews();
        initListeners();
    }

    private void initViews() {
        bubbleStatus = findViewById(R.id.bubbleStatus);
        connectButton = findViewById(R.id.connectButton);
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
                if (state) {
                    Toast.makeText(MainActivity.this, getString(R.string.connected_bubble), Toast.LENGTH_SHORT).show();
                    bubbleStatus.setText(getString(R.string.connected_bubble));
                    connectButton.setText(getString(R.string.disconnect));
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.not_connected_bubble), Toast.LENGTH_SHORT).show();
                    bubbleStatus.setText(getString(R.string.not_connected_bubble));
                    connectButton.setText(getString(R.string.connect));
                }
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
                                if (state) {
                                    Toast.makeText(MainActivity.this, getString(R.string.connected_bubble), Toast.LENGTH_SHORT).show();
                                    bubbleStatus.setText(getString(R.string.connected_bubble));
                                    connectButton.setText(getString(R.string.disconnect));
                                } else {
                                    Toast.makeText(MainActivity.this, getString(R.string.not_connected_bubble), Toast.LENGTH_SHORT).show();
                                    bubbleStatus.setText(getString(R.string.not_connected_bubble));
                                    connectButton.setText(getString(R.string.connect));
                                }
                            }
                        });
            } else {
                connectionStateFlag = false;
                Toast.makeText(this, getString(R.string.not_connected_bubble), Toast.LENGTH_SHORT).show();
                bubbleStatus.setText(getString(R.string.not_connected_bubble));
                connectButton.setText(getString(R.string.connect));
            }
        }
    }
}
