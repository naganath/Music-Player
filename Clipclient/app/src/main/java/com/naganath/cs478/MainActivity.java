package com.naganath.cs478;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.naganath.cs478.clipclient.R;

public class MainActivity extends AppCompatActivity {


    private Button startService;
    private Button stopService;
    private Button playClip;
    private Button stopClip;
    private Button resumeClip;
    private Button pauseClip;
    private Spinner spinner;
    private Intent intent;
    private IPlayInterface playInterface;
    private Integer selectedItem;
    private BroadcastReceiver bcReceiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            stopActions();
        }
    };



    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            playInterface = IPlayInterface.Stub.asInterface(service);
            String[] playlist = null;
            try {
                playlist = playInterface.getAll();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                    android.R.layout.simple_spinner_item, playlist);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            playInterface = null;
        }
    };

    Spinner.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectedItem = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            selectedItem = null;
        }
    };

    Button.OnClickListener startServiceOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startService.setEnabled(false);
            playClip.setEnabled(true);
            stopService.setEnabled(true);
            startForegroundService(intent);
            bindPlayService();

        }
    };



    Button.OnClickListener stopServiceOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startService.setEnabled(true);
            pauseClip.setEnabled(false);
            resumeClip.setEnabled(false);
            playClip.setEnabled(false);
            stopClip.setEnabled(false);
            stopService.setEnabled(false);
            Toast.makeText(getApplicationContext(), "Clip will stop", Toast.LENGTH_SHORT).show();

            if(playInterface != null)
                unbindService(serviceConnection);
            try {
                playInterface.stop();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            stopService(intent);

        }
    };

    Button.OnClickListener playClipOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(selectedItem == null)
                return;
            pauseClip.setEnabled(true);
            stopClip.setEnabled(true);
            bindPlayService();
            try {
                playInterface.play(selectedItem);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    };

    Button.OnClickListener stopClipOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          stopActions();
        }
    };

    private void stopActions() {
        pauseClip.setEnabled(false);
        resumeClip.setEnabled(false);
        stopClip.setEnabled(false);
        unbindService(serviceConnection);
        try {
            playInterface.stop();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    Button.OnClickListener resumeClipOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            pauseClip.setEnabled(true);
            resumeClip.setEnabled(false);
            try {
                playInterface.resume();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    Button.OnClickListener pauseClipOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            pauseClip.setEnabled(false);
            resumeClip.setEnabled(true );
            try {
                playInterface.pause();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = new Intent(IPlayInterface.class.getName());
        ResolveInfo info = getPackageManager().resolveService(intent, 0);
        intent.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));


        startService = findViewById(R.id.start_service);
        stopService = findViewById(R.id.stop_service);
        playClip = findViewById(R.id.play_button);
        stopClip = findViewById(R.id.stop_button);
        resumeClip = findViewById(R.id.resume_button);
        pauseClip = findViewById(R.id.pause_button);
        spinner = findViewById(R.id.spinner);



        startService.setOnClickListener(startServiceOnClickListener);
        stopService.setOnClickListener(stopServiceOnClickListener);
        playClip.setOnClickListener(playClipOnClickListener);
        stopClip.setOnClickListener(stopClipOnClickListener);
        resumeClip.setOnClickListener(resumeClipOnClickListener);
        pauseClip.setOnClickListener(pauseClipOnClickListener);
        spinner.setOnItemSelectedListener(onItemSelectedListener);

        startService.setEnabled(true);
        stopService.setEnabled(false);
        playClip.setEnabled(false);
        stopClip.setEnabled(false);
        pauseClip.setEnabled(false);
        resumeClip.setEnabled(false);


        IntentFilter filter = new IntentFilter("naganath");
        registerReceiver(bcReceiver, filter);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if(playInterface != null) {
            try {
                playInterface.stop();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
            unbindService(serviceConnection);
    }

    private void bindPlayService() {

        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE );
//        startForegroundService(intent);

    }
}
