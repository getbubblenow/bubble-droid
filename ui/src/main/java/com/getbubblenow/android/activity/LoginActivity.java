package com.getbubblenow.android.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.security.KeyChain;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.getbubblenow.android.R;
import com.getbubblenow.android.repository.DataRepository;
import com.getbubblenow.android.resource.StatusResource;
import com.getbubblenow.android.viewmodel.LoginViewModel;
import com.getbubblenow.android.model.User;
import com.getbubblenow.android.repository.DataRepository;
import com.getbubblenow.android.resource.StatusResource;
import com.getbubblenow.android.viewmodel.LoginViewModel;

public class LoginActivity extends BaseActivityBubble {

    private LoginViewModel loginViewModel;
    private EditText bubbleName;
    private EditText userName;
    private EditText password;
    private AppCompatButton sign;

    private static final String BASE_URL_PREFIX = "https://";
    private static final String BASE_URL_SUFFIX = ":1443/api/";
    private static final String SEPARATOR = "\\.";
    private static final int REQUEST_CODE = 1555;
    private static final String CERTIFICATE_NAME = "Bubble Certificate";
    private boolean bubbleNameStateFlag = false;
    private boolean userNameStateFlag = false;
    private boolean passwordStateFlag = false;
    private static final String BUBBLE_NAME_KEY = "bubbleName";
    private static final String USER_NAME_KEY = "userName";
    private static final String PASSWORD_KEY = "password";
    private static final String NO_INTERNET_CONNECTION = "no internet connection";
    private static final String LOGIN_FAILED = "Login Failed";


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
                if (url.split(SEPARATOR).length != 3) {
                    Toast.makeText(LoginActivity.this, getResources().getText(R.string.hostname_not_valid), Toast.LENGTH_LONG).show();
                    return;
                }
                if (DataRepository.getRepositoryInstance() == null) {
                    loginViewModel.buildRepositoryInstance(LoginActivity.this, url);
                } else {
                    loginViewModel.buildClientService(url);
                }
                loginViewModel.setUserURL(LoginActivity.this, url);
                final String usernameInput = userName.getText().toString().trim();
                final String passwordInput = password.getText().toString().trim();
                showLoadingDialog();
                login(usernameInput, passwordInput);
            }
        });
        bubbleNameStateListener();
        userNameStateListener();
        passwordStateListener();
    }


    private void initViews() {
        bubbleName = findViewById(R.id.bubbleName);
        userName = findViewById(R.id.userName);
        password = findViewById(R.id.password);
        sign = findViewById(R.id.signButton);
    }

    private void bubbleNameStateListener() {
        bubbleName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

            }

            @Override public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {

            }

            @Override public void afterTextChanged(final Editable s) {

                if (bubbleName.getText().toString().trim().isEmpty()) {
                    bubbleNameStateFlag = false;
                } else {
                    bubbleNameStateFlag = true;
                }
                setButtonState();
            }
        });
    }

    private void userNameStateListener() {
        userName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

            }

            @Override public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {

            }

            @Override public void afterTextChanged(final Editable s) {

                if (userName.getText().toString().trim().isEmpty()) {
                    userNameStateFlag = false;
                } else {
                    userNameStateFlag = true;
                }
                setButtonState();
            }
        });
    }

    private void passwordStateListener() {

        password.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

            }

            @Override public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {

            }

            @Override public void afterTextChanged(final Editable s) {

                if (password.getText().toString().trim().isEmpty()) {
                    passwordStateFlag = false;
                } else {
                    passwordStateFlag = true;
                }
                setButtonState();
            }
        });
    }

    private void setButtonState(){
        if(userNameStateFlag && bubbleNameStateFlag && passwordStateFlag){
            sign.setBackgroundDrawable(getDrawable(R.drawable.sign_in_enable));
            sign.setEnabled(true);
        }
        else {
            sign.setBackgroundDrawable(getDrawable(R.drawable.sign_in_disable));
            sign.setEnabled(false);
        }
    }

    private void login(String username, String password) {
        loginViewModel.login(username, password, this).observe(this, new Observer<StatusResource<byte[]>>() {
            @Override public void onChanged(final StatusResource<byte[]> userStatusResource) {
                switch (userStatusResource.status) {
                    case SUCCESS:
                        closeLoadingDialog();
                        final Intent intent = KeyChain.createInstallIntent();
                        intent.putExtra(KeyChain.EXTRA_CERTIFICATE, userStatusResource.data);
                        intent.putExtra(KeyChain.EXTRA_NAME, CERTIFICATE_NAME);
                        startActivityForResult(intent, REQUEST_CODE);
                        break;
                    case LOADING:
                        Log.d("TAG", "Loading");
                        break;
                    case ERROR:
                        closeLoadingDialog();
                        if(userStatusResource.message.equals(NO_INTERNET_CONNECTION)){
                            showNetworkNotAvailableMessage();
                        }
                        else if(userStatusResource.message.equals(LOGIN_FAILED)){
                            Toast.makeText(LoginActivity.this,LOGIN_FAILED,Toast.LENGTH_LONG).show();
                        }
                        else {
                            showErrorDialog(userStatusResource.message);
                        }
                        break;
                }
            }
        });
    }

    @Override protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
                showLoadingDialog();
              loginViewModel.getConfig(this).observe(this, new Observer<StatusResource<Object>>() {
                  @Override public void onChanged(final StatusResource<Object> objectStatusResource) {
                      closeLoadingDialog();
                      switch (objectStatusResource.status){
                          case SUCCESS:
                              Toast.makeText(LoginActivity.this, getString(R.string.success), Toast.LENGTH_SHORT).show();
                              loginViewModel.setHostName(LoginActivity.this,bubbleName.getText().toString().trim());
                              Log.d("TAG", "Success");
                              final Intent mainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
                              mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                              startActivity(mainActivityIntent);
                              break;
                          case LOADING:
                              break;
                          case ERROR:
                              if(objectStatusResource.message.equals(NO_INTERNET_CONNECTION)){
                                  showNetworkNotAvailableMessage();
                              }
                              else if(objectStatusResource.message.equals(LOGIN_FAILED)){
                                  Toast.makeText(LoginActivity.this,LOGIN_FAILED,Toast.LENGTH_LONG).show();
                              }
                              else {
                                  showErrorDialog(objectStatusResource.message);
                              }
                              break;
                      }
                  }
              });
        } else {
            Toast.makeText(this, getString(R.string.cerificate_install), Toast.LENGTH_LONG).show();
        }
    }

    @Override protected void onSaveInstanceState(@NonNull final Bundle outState) {
        outState.putBoolean(BUBBLE_NAME_KEY,bubbleNameStateFlag);
        outState.putBoolean(USER_NAME_KEY,userNameStateFlag);
        outState.putBoolean(PASSWORD_KEY,passwordStateFlag);
        super.onSaveInstanceState(outState);
    }

    @Override protected void onRestoreInstanceState(@NonNull final Bundle savedInstanceState) {
        bubbleNameStateFlag = savedInstanceState.getBoolean(BUBBLE_NAME_KEY);
        userNameStateFlag = savedInstanceState.getBoolean(USER_NAME_KEY);
        passwordStateFlag = savedInstanceState.getBoolean(PASSWORD_KEY);
        super.onRestoreInstanceState(savedInstanceState);
    }
}
