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
import android.widget.Toast;
import com.wireguard.android.R;
import com.wireguard.android.model.User;
import com.wireguard.android.resource.StatusResource;
import com.wireguard.android.viewmodel.LoginViewModel;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private EditText bubbleName;
    private EditText userName;
    private EditText password;
    private Button sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUI();
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    @Override protected void onStart() {
        super.onStart();
    }

    @Override protected void onPause() {
        super.onPause();
    }

    private void initUI() {
        initViews();
        initListeners();
    }

    private void initListeners() {
        sign.setOnClickListener(new OnClickListener() {
            @Override public void onClick(final View v) {
                final String username = userName.getText().toString().trim();
                final String inputPassword = password.getText().toString().trim();
                login(username,inputPassword);
            }
        });
    }

    private void initViews() {
        bubbleName = findViewById(R.id.bubbleName);
        userName = findViewById(R.id.userName);
        password = findViewById(R.id.password);
        sign = findViewById(R.id.signButton);
    }

    private void login(String username, String password) {
        HashMap<String,String> data = new HashMap<>();
        data.put("username",username);
        data.put("password",password);
        loginViewModel.login(data,this).observe(this, new Observer<StatusResource<User>>() {
            @Override public void onChanged(final StatusResource<User> userStatusResource) {
                switch (userStatusResource.status){
                    case SUCCESS:
                        Toast.makeText(LoginActivity.this,"Success",Toast.LENGTH_SHORT).show();
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
