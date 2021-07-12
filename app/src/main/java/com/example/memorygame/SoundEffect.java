package com.example.memorygame;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

public class SoundEffect {
    private final SoundPool soundPool;
    private final int click;
    private final int correctMatch;
    private final int incorrectMatch;
    private final int matchCompleted;

    public SoundEffect(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttr = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder().setAudioAttributes(audioAttr).setMaxStreams(2).build();
        } else {
            soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        }

        click = soundPool.load(context, R.raw.click, 2);
        correctMatch = soundPool.load(context, R.raw.correctmatch, 1);
        incorrectMatch = soundPool.load(context, R.raw.incorrectmatch, 1);
        matchCompleted = soundPool.load(context, R.raw.matchcompleted, 1);
    }

    public void clickSelect() {
        soundPool.play(click, 1.0f, 1.0f, 2, 0, 1.0f);
    }

    public void correctMatch() {
        soundPool.play(correctMatch, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void incorrectMatch() {
        soundPool.play(incorrectMatch, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void completeMatch() {
        soundPool.play(matchCompleted, 1.0f, 1.0f, 1, 0, 1.0f);
    }
}
