package com.wireguard.android.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wireguard.android.Application;
import com.wireguard.android.R;
import com.wireguard.android.backend.GoBackend;
import com.wireguard.android.backend.Tunnel;
import com.wireguard.android.backend.Tunnel.State;
import com.wireguard.android.model.ObservableTunnel;
import com.wireguard.android.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {
    private MainViewModel mainViewModel;
    private TextView bubbleStatus;
    private Button connectButton;
    public ObservableTunnel pendingTunnel;
    private Boolean pendingTunnelUp;
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
        initUI();
    }

    private void initUI() {
        initViews();
        initListeners();
        connectionStateFlag = mainViewModel.getConnectionState(this);
        bubbleStatus.setText(mainViewModel.isBubbleConnected(this));
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
        ObservableTunnel tunnel = mainViewModel.getTunnel(this, connectionStateFlag);
        pendingTunnel = tunnel;
        mainViewModel.getTunnelManager().getTunnelState(pendingTunnel).whenComplete((state, throwable) -> {
            if (state == State.DOWN) {
                connectionStateFlag = true;
                setTunnelState(true);
            } else if (state == State.UP) {
                setTunnelState(false);
                connectionStateFlag = false;
            }
        });
    }

    private void setTunnelState(final Boolean checked) {
        if(pendingTunnel!=null) {
            final ObservableTunnel tunnel = pendingTunnel;
            Application.getBackendAsync().thenAccept(backend -> {
                if (backend instanceof GoBackend) {
                    final Intent intent = GoBackend.VpnService.prepare(this);
                    if (intent != null) {
                        pendingTunnelUp = checked;
                        startActivityForResult(intent, REQUEST_CODE_VPN_PERMISSION);
                        return;
                    }
                }
                setTunnelStateWithPermissionsResult(tunnel, checked);
            });
        }

}

    private void setTunnelStateWithPermissionsResult(final ObservableTunnel tunnel, final boolean checked) {
        tunnel.setStateAsync(Tunnel.State.of(checked)).whenComplete((observableTunnel, throwable) ->{
            if(throwable==null){
                if(observableTunnel.equals(State.DOWN)) {
                    Toast.makeText(this, getString(R.string.not_connected_bubble), Toast.LENGTH_SHORT).show();
                    bubbleStatus.setText(getString(R.string.not_connected_bubble));
                }
                else  {
                    Toast.makeText(this, getString(R.string.connected_bubble), Toast.LENGTH_SHORT).show();
                    bubbleStatus.setText(getString(R.string.connected_bubble));
                }
            }
            else {
                Toast.makeText(this,getString(R.string.failed_bubble),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_VPN_PERMISSION) {
            if (pendingTunnel != null && pendingTunnelUp != null) setTunnelStateWithPermissionsResult(pendingTunnel, pendingTunnelUp);
            pendingTunnel = null;
            pendingTunnelUp = null;
        }
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        mainViewModel.setConnectionState(this,connectionStateFlag,bubbleStatus.getText().toString());
    }
}
