package com.example.memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity2 extends AppCompatActivity {

    int matchComplete =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        List<ImageButton> tempClicked = new ArrayList<>();

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