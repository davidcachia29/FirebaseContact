package com.example.davidCachiaTask3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.davidCachiaTask3.R;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    public boolean connected = false;
    ConstraintLayout constraintLayout;
    Button getContacts;
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getContacts =findViewById(R.id.goTOCONTACTS);
        View parentLayout=findViewById(android.R.id.content);
        Snackbar snackbar=Snackbar.make(parentLayout,"NOT Connected",Snackbar.LENGTH_INDEFINITE);


        broadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(connectionCheck(context))
                {
                    Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_LONG).show();
                    snackbar.dismiss();
                    getContacts.setEnabled(true);
                }
                else
                    {
                        snackbar.show();
                        getContacts.setEnabled(false);
                    }
            }
        };
        registerReceiver();


        getContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, getContacts.class));
            }
        });

    }

    protected void registerReceiver()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N)
        {
            registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }

    }

    protected void removeReceiver()
    {
        try
        {
            unregisterReceiver(broadcastReceiver);
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeReceiver();
    }

    public boolean connectionCheck(Context context)
    {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            return (nInfo!=null && nInfo.isConnected());

        } catch (Exception e) {
            Log.e("Connection not made", e.getMessage());
            return false;
        }

    }


}