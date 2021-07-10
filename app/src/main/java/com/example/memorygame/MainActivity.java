package com.example.memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    Content myContent;
    ImageButton buttons[];
    Bitmap bitmap[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button fetchButton=findViewById(R.id.button);
        fetchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap=new Bitmap[20];
                buttons=new ImageButton[20];
                if (myContent!=null) {
                    myContent.cancel(true);
                }
                myContent=new Content();
                EditText enteredURL=findViewById(R.id.editTextURL);
                String URL=enteredURL.getText().toString();
                myContent.execute(URL);
            }
        });
    }
    public class Content extends AsyncTask<String, Void, Void> {
        private AsyncTask<String, Void, Void> updateTask = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(String...Strings) {
            try {
                //Connect to the website
                Document document = Jsoup.connect(Strings[0]).get();

                //Get the logo source of the website
                Elements images = document.select("img[src$=.jpg]");
                // Locate the src attribute
                for (int i=0;i<20;i++)
                {
                    if (isCancelled())
                        break;
                    String imgSrc = images.get(i).absUrl("src");
                    // Download image from URL
                    InputStream input = new java.net.URL(imgSrc).openStream();
                    // Decode Bitmap
                    bitmap[i] = BitmapFactory.decodeStream(input);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            for (int j = 0; j < 20; j++) {
                String ImageButtonName = "button" + (j + 1);
                int resIDImageButton = getResources().getIdentifier(ImageButtonName, "id", getPackageName());
                buttons[j]=findViewById(resIDImageButton);
                buttons[j].setImageBitmap(bitmap[j]);
            }
        }
    }
}