package com.wireguard.android.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.wireguard.android.R;
import com.wireguard.android.util.UserStore;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(UserStore.USER_TOKEN_DEFAULT_VALUE.equals(UserStore.getInstance(this).getUserResponse())) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            setContentView(R.layout.activity_main);
        }
    }
}
