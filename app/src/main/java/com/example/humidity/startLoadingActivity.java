
package com.example.humidity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class startLoadingActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private Socket socket;
    private Boolean isServerExist=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_loading);


    }




}


