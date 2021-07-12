package com.example.memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.FileUtils;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

public class MainActivity2 extends AppCompatActivity {

    int matchComplete =0;

    private static final long startTimeInMil = 60000;

    private TextView mTextViewCountDown;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = startTimeInMil;
    private SoundEffect sound;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        List<ImageButton> tempClicked = new ArrayList<>();
        sound = new SoundEffect(this);

        //For timer
        mTextViewCountDown = findViewById(R.id.timer);

        //For Chronometer
        Chronometer chronoTimer = (Chronometer) findViewById(R.id.chronoTimer);
        chronoTimer.start();

        //For each of the button, we should randomly set the tags of the buttons.
        //For now hardcode the bitmaps
        Random randGenerator = new Random();
        List<Bitmap> listOfBitmaps = new ArrayList<>();
//        listOfBitmaps.add(R.drawable.image1);
//        listOfBitmaps.add(R.drawable.image2);
//        listOfBitmaps.add(R.drawable.image3);
//        listOfBitmaps.add(R.drawable.image4);
//        listOfBitmaps.add(R.drawable.image5);
//        listOfBitmaps.add(R.drawable.image6);
//        listOfBitmaps.add(R.drawable.image1);
//        listOfBitmaps.add(R.drawable.image2);
//        listOfBitmaps.add(R.drawable.image3);
//        listOfBitmaps.add(R.drawable.image4);
//        listOfBitmaps.add(R.drawable.image5);
//        listOfBitmaps.add(R.drawable.image6);
//

        Intent intentM = new Intent(this, Music.class);
        intentM.setAction("Game_Music");
        startService(intentM);

        Intent activity1Intent = getIntent();
        String[] directory = activity1Intent.getStringArrayExtra("img");
        String filePath = "selected_images";
        for (String direct : directory ) {
            File mTargetFile = new File(this.getFilesDir(), filePath + "/" + direct);

            try {
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(mTargetFile));
                listOfBitmaps.add(b);
                listOfBitmaps.add(b);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }



        int totalNum = 12;

        //Main Game Button Interactions
        for (int j = 0; j < 12; j++) {
            String ImageButtonName = "guess" + (j + 1);
            int resIDImageButton = getResources().getIdentifier(ImageButtonName, "id", getPackageName());
            ImageButton button = findViewById(resIDImageButton);
            if(button!=null)
            {
                int generatedIndex = randGenerator.nextInt(totalNum);
                Bitmap tag = listOfBitmaps.get(generatedIndex);
                button.setTag(tag);
                button.setImageResource(R.drawable.x);
                listOfBitmaps.remove(generatedIndex);
                totalNum-=1;

                //Onclick Listener
                button.setOnClickListener(view -> {
                        sound.clickSelect();
                        System.out.println("Clicked" + ImageButtonName);
                        if (tempClicked.size() < 2) {
                            if (tempClicked.size() == 1) {
                                //prevent same button clicking
                                if (tempClicked.get(0) == button) {
                                    System.out.println("you have clicked this before");
                                } else {
                                    button.setImageBitmap((Bitmap) button.getTag());
//                                    button.setTag((Integer) button.getTag());
                                    tempClicked.add(button);

                                    //Checking if match
                                    TextView matchCounter = findViewById(R.id.matches);
                                    if (tempClicked.get(0).getTag().toString().equals(tempClicked.get(1).getTag().toString())) {
                                        sound.correctMatch();
                                        System.out.println("match");
                                        matchComplete +=1;
                                        if(matchComplete<=5){
                                            matchCounter.setText(new StringBuilder().append(matchComplete).append("/6 Match").toString());
                                        }
                                        else{
                                            Button completeBtn = findViewById(R.id.completeActivity2);
                                            if (completeBtn !=null){
                                                completeBtn.setVisibility(View.VISIBLE);
                                                matchCounter.setText(new StringBuilder().append(matchComplete).append("/6 Match").toString());
                                                chronoTimer.stop();
                                                completeBtn.setOnClickListener(view1 -> {
                                                    sound.completeMatch();
                                                    Intent intent = new Intent(this,MainActivity.class);
                                                    File dir = new File (this.getFilesDir(), filePath);
                                                    if (dir.isDirectory())
                                                    {
                                                        String[] children = dir.list();
                                                        for (int i = 0; i < children.length; i++)
                                                        {
                                                            new File(dir, children[i]).delete();
                                                        }
                                                    }
                                                    handler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            // Go back to main page after 3 seconds
                                                            // After sound effect
                                                            stopService(intentM);
                                                            startActivity(intent);
                                                        }
                                                    }, 3000);
                                                });
                                            }
                                        }
                                        for (int i = 0; i < 2; i++) {
                                            tempClicked.get(i).setOnClickListener(null);
                                        }
                                        tempClicked.clear();
                                    }
                                    else {
                                        sound.incorrectMatch();
                                        tempClicked.add(button);
                                    }
                                }
                            }
                            else{
                                    button.setImageBitmap((Bitmap) button.getTag());
                                    tempClicked.add(button);
                                }
                        }
                        else {
                            for (ImageButton buttons : tempClicked) {
                                buttons.setImageResource(R.drawable.x);
                            }
                            System.out.println("Don't match");
                            tempClicked.clear();
                        }

                        System.out.println((tempClicked.size()));
                        if(matchComplete==5 && tempClicked.size()==2){
                            Button completeBtn = findViewById(R.id.completeActivity2);
                            if (completeBtn !=null){
                                completeBtn.setVisibility(View.VISIBLE);
                                TextView matchCounter = findViewById(R.id.matches);
                                matchCounter.setText("6/6 Match");
                                chronoTimer.stop();
                                completeBtn.setOnClickListener(view1 -> {
                                    sound.completeMatch();
                                    Intent intent = new Intent(this,MainActivity.class);
                                    File dir = new File (this.getFilesDir(), filePath);
                                    if (dir.isDirectory())
                                    {
                                        String[] children = dir.list();
                                        for (int i = 0; i < children.length; i++)
                                        {
                                            new File(dir, children[i]).delete();
                                        }
                                    }
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Go back to main page after 3 seconds
                                            // After sound effect
                                            stopService(intentM);
                                            startActivity(intent);
                                        }
                                    }, 3000);
                                });
                            }
                        }
                });
            }
        }
    }
}