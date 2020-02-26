package com.naganath.cs478;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.naganath.cs478.clipserver.R;

import java.util.Arrays;
import java.util.List;


public class PlayService extends Service {

    private Notification notification;
    private final  int notificationId = 1;
    private static final String TAG = "PlayService";
    private static String CHANNEL_ID = "NC1";
    private MediaPlayer mediaPlayer;
    private final List<Integer> playList = Arrays.asList(R.raw.music1, R.raw.music2, R.raw.music3,
            R.raw.music4, R.raw.music5);


    public PlayService() {
    }

    private final IPlayInterface.Stub binder = new IPlayInterface.Stub() {
        @Override
        public void play(int index) throws RemoteException {
            Log.e(TAG, " inside play");
            if(mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            mediaPlayer = MediaPlayer.create(getApplicationContext(), playList.get(index));
            mediaPlayer.start();
//            mediaPlayer.setDataSource();
//            mediaPlayer.


            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    Log.e(TAG, " clip finished");
                    Intent intent = new Intent("naganath");
                    sendBroadcast(intent);
                }
            });
        }

        @Override
        public void pause() throws RemoteException {
            Log.e(TAG, " inside pause");
            mediaPlayer.pause();

        }

        @Override
        public void resume() throws RemoteException {
            Log.e(TAG, " inside resume");
            mediaPlayer.start();

        }

        @Override
        public void stop() throws RemoteException {
            // unbind
            mediaPlayer.stop();

            Log.e(TAG, " inside stop");
        }

        @Override
        public String[] getAll() throws RemoteException {
            Log.e(TAG, " inside getAll");
            return new String[] {"music 1", "music 2", "music 3", "music 4", "music 5"};
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, " inside bind");
        return binder;
//        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.createNotificationChannel();

        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.image2)
                .setContentTitle("Play Service 1")
                .setContentText("Play Service Desc")
                .build();

        mediaPlayer = MediaPlayer.create(this, playList.get(0));
        mediaPlayer.setLooping(false);
        Log.e(TAG,"inside oncreate");




    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Play Service", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription( "This works ");
            channel.setSound(null, null);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startForeground(notificationId, notification);
        Log.e(TAG, " inside start command");
//        mediaPlayer.start();
        Log.e(TAG, " after music");
        return Service.START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        Log.e(TAG, "inside on destroy method.");
    }




}
