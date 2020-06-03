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
import com.wireguard.android.model.ObservableTunnel;
import com.wireguard.android.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {
    private MainViewModel mainViewModel;
    private TextView bubbleStatus;
    private TextView deviceStatus;
    private Button connectButton;
    public ObservableTunnel pendingTunnel;
    private Boolean pendingTunnelUp;

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
        pendingTunnel = mainViewModel.getTunnel(this);
    }

    private void initUI() {
        initViews();
        initListeners();
    }

    private void initViews() {
        bubbleStatus = findViewById(R.id.bubbleStatus);
        deviceStatus = findViewById(R.id.deviceStatus);
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
            setTunnelState(true);
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
               Toast.makeText(this,"Connected",Toast.LENGTH_SHORT).show();
           }
           else {
               Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show();
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
}
