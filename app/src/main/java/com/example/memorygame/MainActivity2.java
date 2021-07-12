package com.example.memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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


    private void startTimer(){
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis,1000) {
            @Override
            public void onTick(long l) {
                mTimeLeftInMillis = l;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                for (int j = 0; j < 12; j++) {
                    String ImageButtonName = "guess" + (j + 1);
                    int resIDImageButton = getResources().getIdentifier(ImageButtonName, "id", getPackageName());
                    ImageButton button = findViewById(resIDImageButton);
                    if(button!=null){
                        button.setOnClickListener(null);
                    }
                    Button completeBtn = findViewById(R.id.completeActivity2);
                    if (completeBtn !=null){
                        completeBtn.setVisibility(View.VISIBLE);
                        completeBtn.setText("Try Again");
                        completeBtn.setOnClickListener(view -> {
                            Intent intent1 = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent1);
                        });
                    }
                }
            }
        }.start();

        mTimerRunning = true;
    }



    private void updateCountDownText(){
        int minutes = (int) (mTimeLeftInMillis/1000/60);
        int seconds = (int) (mTimeLeftInMillis/1000%60);

        String timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);

        mTextViewCountDown.setText(timeLeftFormatted);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        List<ImageButton> tempClicked = new ArrayList<>();

        //For timer
        mTextViewCountDown = findViewById(R.id.timer);

        startTimer();
        updateCountDownText();


        //For each of the button, we should randomly set the tags of the buttons.
        //For now hardcode the bitmaps
        Random randGenerator = new Random();
        List<Integer> listOfBitmaps = new ArrayList<>();
        listOfBitmaps.add(R.drawable.image1);
        listOfBitmaps.add(R.drawable.image2);
        listOfBitmaps.add(R.drawable.image3);
        listOfBitmaps.add(R.drawable.image4);
        listOfBitmaps.add(R.drawable.image5);
        listOfBitmaps.add(R.drawable.image6);
        listOfBitmaps.add(R.drawable.image1);
        listOfBitmaps.add(R.drawable.image2);
        listOfBitmaps.add(R.drawable.image3);
        listOfBitmaps.add(R.drawable.image4);
        listOfBitmaps.add(R.drawable.image5);
        listOfBitmaps.add(R.drawable.image6);

        int totalNum = 12;

        //Main Game Button Interactions
        for (int j = 0; j < 12; j++) {
            String ImageButtonName = "guess" + (j + 1);
            int resIDImageButton = getResources().getIdentifier(ImageButtonName, "id", getPackageName());
            ImageButton button = findViewById(resIDImageButton);
            if(button!=null)
            {
                int generatedIndex = randGenerator.nextInt(totalNum);
                int tag = listOfBitmaps.get(generatedIndex);
                button.setTag(tag);
                button.setImageResource(R.drawable.x);
                listOfBitmaps.remove(generatedIndex);
                totalNum-=1;

                //Onclick Listener
                button.setOnClickListener(view -> {
                        System.out.println("Clicked" + ImageButtonName);
                        if (tempClicked.size() < 2) {
                            if (tempClicked.size() == 1) {
                                //prevent same button clicking
                                if (tempClicked.get(0) == button) {
                                    System.out.println("you have clicked this before");
                                } else {
                                    button.setImageResource((Integer) button.getTag());
                                    button.setTag((Integer) button.getTag());
                                    tempClicked.add(button);

                                    //Checking if match
                                    TextView matchCounter = findViewById(R.id.matches);
                                    if (tempClicked.get(0).getTag().toString().equals(tempClicked.get(1).getTag().toString())) {
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
                                                completeBtn.setOnClickListener(view1 -> {
                                                    Intent intent = new Intent(this,MainActivity.class);
                                                    startActivity(intent);
                                                });
                                            }
                                        }
                                        for (int i = 0; i < 2; i++) {
                                            tempClicked.get(i).setOnClickListener(null);
                                        }
                                        tempClicked.clear();
                                    }
                                    else {
                                        tempClicked.add(button);
                                    }
                                }
                            }
                            else{
                                    button.setImageResource((Integer) button.getTag());
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
                                completeBtn.setOnClickListener(view1 -> {
                                    Intent intent = new Intent(this,MainActivity.class);
                                    startActivity(intent);
                                });
                            }
                        }
                });
            }
        }
    }
}