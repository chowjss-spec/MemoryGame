package com.example.memorygame;

import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity2 extends AppCompatActivity {

    int matchComplete =0;

    private static final long startTimeInMil = 6000;

    private TextView mTextViewCountDown;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = startTimeInMil;
    private SoundEffect sound;
    private Handler handler = new Handler();


    private int clickedTimes=0;
    private int accurateClickedTimes=0;

    private void startTimer(ArrayList<ImageButton> tempClicked){
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis,1000) {
            @Override
            public void onTick(long l) {
                mTimeLeftInMillis= l;
            }

            @Override
            public void onFinish() {

                for (ImageButton buttons : tempClicked) {
                    buttons.setImageResource(R.drawable.sushi2);
                }
                System.out.println("Don't match");

                tempClicked.clear();
                mTimeLeftInMillis=startTimeInMil;

            }
        }.start();
    }





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
                button.setImageResource(R.drawable.sushi2);
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
                                    clickedTimes+=2;
                                    tempClicked.add(button);

                                    //Checking if match
                                    TextView matchCounter = findViewById(R.id.matches);
                                    if (tempClicked.get(0).getTag().toString().equals(tempClicked.get(1).getTag().toString())) {
                                        sound.correctMatch();
                                        accurateClickedTimes+=2;
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
                                                String seconds=getChronometerSeconds(chronoTimer);
                                                double accuracy=accurateClickedTimes*100/clickedTimes;
                                                Toast.makeText(getApplicationContext(),"You only used "+seconds+" seconds and clicked "+clickedTimes+" times to win, the accuracy is "+accuracy+"% nice! Now please click COMPLETE~",Toast.LENGTH_LONG).show();
                                                clickedTimes=0;
                                                accurateClickedTimes=0;
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
                                        startTimer((ArrayList<ImageButton>) tempClicked);
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
                                buttons.setImageResource(R.drawable.sushi2);
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
                                String seconds1=getChronometerSeconds(chronoTimer);
                                double accuracy=accurateClickedTimes*100/clickedTimes;
                                Toast.makeText(getApplicationContext(),"You only used "+seconds1+" seconds and clicked "+clickedTimes+" times to win, the accuracy is "+accuracy+"% nice! Now please click COMPLETE~",Toast.LENGTH_LONG).show();
                                clickedTimes=0;
                                accurateClickedTimes=0;
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
    public  static String getChronometerSeconds(Chronometer cmt) {
        int totalss = 0;
        String string = cmt.getText().toString();
        if(string.length()==7){

            String[] split = string.split(":");
            String string2 = split[0];
            int hour = Integer.parseInt(string2);
            int Hours =hour*3600;
            String string3 = split[1];
            int min = Integer.parseInt(string3);
            int Mins =min*60;
            int  SS =Integer.parseInt(split[2]);
            totalss = Hours+Mins+SS;
            return String.valueOf(totalss);
        }

        else if(string.length()==5){

            String[] split = string.split(":");
            String string3 = split[0];
            int min = Integer.parseInt(string3);
            int Mins =min*60;
            int  SS =Integer.parseInt(split[1]);

            totalss =Mins+SS;
            return String.valueOf(totalss);
        }
        return String.valueOf(totalss);
    }



}