package com.wireguard.android.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        sign.setOnClickListener(new OnClickListener() {
            @Override public void onClick(final View v) {
                login();
            }
        });
    }

    private void login() {
        HashMap<String,String> data = new HashMap<>();
        final String username = userName.getText().toString();
        final String inputPassword = password.getText().toString();
        data.put("username",username);
        data.put("password",inputPassword);
        loginViewModel.login(data,this).observe(this, new Observer<StatusResource<User>>() {
            @Override public void onChanged(final StatusResource<User> userStatusResource) {
                switch (userStatusResource.status){
                    case SUCCESS:
                        Log.d("TAG","Success");
                        break;
                    case LOADING:
                        Log.d("TAG","Loading");
                        break;
                    case ERROR:
                        Log.d("TAG","Error");
                }
            }
        });
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
}
