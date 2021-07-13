package com.example.memorygame;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class Music extends Service {

    MediaPlayer songs;

    public Music() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent.getAction();

        if (action != null) {
            switch (action) {
                case "Game_Music":
                    songs = MediaPlayer.create(this, R.raw.monkey);
                    songs.setLooping(true);
                    songs.setOnPreparedListener(mp -> songs.start());
                    break;

                case "Main_Music":
                    songs = MediaPlayer.create(this, R.raw.komiku);
                    songs.setLooping(true);
                    songs.setVolume(0.4f, 0.4f);
                    songs.setOnPreparedListener(mp -> songs.start());
                    break;
            }
        }

         return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (songs != null) {
            songs.stop();
            songs.reset();
            songs.release();
            songs = null;
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}