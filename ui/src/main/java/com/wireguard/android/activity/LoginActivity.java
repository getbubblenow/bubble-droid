package com.wireguard.android.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wireguard.android.R;
import com.wireguard.android.model.User;
import com.wireguard.android.resource.StatusResource;
import com.wireguard.android.viewmodel.LoginViewModel;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private TextView bubbleNameTitle;
    private TextView userNameTitle;
    private TextView passwordTitle;
    private EditText bubbleName;
    private EditText userName;
    private EditText password;
    private Button sign;
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback mCallback;
    private boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUI();
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        mCallback = new ConnectivityManager.NetworkCallback(){
            @Override public void onAvailable(@NonNull final Network network) {
                isConnected = true;
            }

            @Override public void onLost(@NonNull final Network network) {
                isConnected = false;
            }
        };
        sign.setOnClickListener(new OnClickListener() {
            @Override public void onClick(final View v) {
                if (isConnected) {
                    login();
                } else {
                    Toast.makeText(LoginActivity.this, getString(R.string.turnInternet), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override protected void onStart() {
        super.onStart();
        final NetworkRequest request = new NetworkRequest.Builder().build();
        connectivityManager.registerNetworkCallback(request,mCallback);
    }

    @Override protected void onPause() {
        super.onPause();
        connectivityManager.unregisterNetworkCallback(mCallback);
    }

    private void initUI() {
        initViews();
    }

    private void initViews() {
        bubbleNameTitle = findViewById(R.id.bubbleNameTitle);
        userNameTitle = findViewById(R.id.userNameTitle);
        passwordTitle = findViewById(R.id.passwordTitle);
        bubbleName = findViewById(R.id.bubbleName);
        userName = findViewById(R.id.userName);
        password = findViewById(R.id.password);
        sign = findViewById(R.id.signButton);
    }

    private void login() {
        HashMap<String,String> data = new HashMap<>();
        final String username = userName.getText().toString().trim();
        final String inputPassword = password.getText().toString().trim();
        data.put("username",username);
        data.put("password",inputPassword);
        loginViewModel.login(data,this).observe(this, new Observer<StatusResource<User>>() {
            @Override public void onChanged(final StatusResource<User> userStatusResource) {
                switch (userStatusResource.status){
                    case SUCCESS:
                        Toast.makeText(LoginActivity.this,"Success",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                        Log.d("TAG","Success");
                        break;
                    case LOADING:
                        Log.d("TAG","Loading");
                        break;
                    case ERROR:
                        Toast.makeText(LoginActivity.this,"Login Failed",Toast.LENGTH_SHORT).show();
                        Log.d("TAG","Error");
                        break;
                }
            }
        });
    }
}
