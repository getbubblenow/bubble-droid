package com.wireguard.android.activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wireguard.android.R;
import com.wireguard.android.model.User;
import com.wireguard.android.repository.DataRepository;
import com.wireguard.android.resource.StatusResource;
import com.wireguard.android.viewmodel.LoginViewModel;

public class LoginActivity extends BaseActivityBubble {

    private LoginViewModel loginViewModel;
    private EditText bubbleName;
    private EditText userName;
    private EditText password;
    private Button sign;

    private static final String BASE_URL_PREFIX = "https://";
    private static final String BASE_URL_SUFFIX = ":1443/api/";
    private static final String SEPARATOR = "\\.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUI();
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    private void initUI() {
        initViews();
        initListeners();
    }

    private void initListeners() {
        sign.setOnClickListener(new OnClickListener() {
            @Override public void onClick(final View v) {
                final String url = BASE_URL_PREFIX + bubbleName.getText().toString() + BASE_URL_SUFFIX;
                if(url.split(SEPARATOR).length!=3){
                    Toast.makeText(LoginActivity.this,"Bubble name is not valid",Toast.LENGTH_LONG).show();
                    return;
                }
                if(DataRepository.getRepositoryInstance()==null) {
                    loginViewModel.buildRepositoryInstance(LoginActivity.this, url);
                }
                else {
                    loginViewModel.buildClientService(url);
                }
                loginViewModel.setUserURL(LoginActivity.this,url);
                final String username = userName.getText().toString().trim();
                final String password = LoginActivity.this.password.getText().toString().trim();
                showLoadingDialog();
                login(username, password);
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
        loginViewModel.login(username, password, this).observe(this, new Observer<StatusResource<User>>() {
            @Override public void onChanged(final StatusResource<User> userStatusResource) {
                switch (userStatusResource.status) {
                    case SUCCESS:
                        Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "Success");
                        closeLoadingDialog();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        break;
                    case LOADING:
                        Log.d("TAG", "Loading");
                        break;
                    case ERROR:
                        closeLoadingDialog();
                        Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "Error");
                        break;
                }
            }
        });
    }
}
