package com.example.memorygame;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.example.memorygame.Interface.FetchImageHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RunnableFuture;

public class MainActivity extends AppCompatActivity  {
    List<ImageButton> buttons;
    Map<Integer, Bitmap> bitmapDict;
    ProgressBar progressBar;
    TextView progressStatus;
    Button playButton;
    Set<Integer> selected = new HashSet<>();
    boolean selectAllowed = false;
    Thread fetchImageThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bitmapDict = new HashMap<Integer, Bitmap>();
        buttons = new ArrayList<>();
        playButton = findViewById(R.id.playBtn);
        progressBar = findViewById(R.id.determinateBar);
        progressStatus = findViewById(R.id.progressStatus);

        // bind each ImageButton
        for (int j = 0; j < 20; j++) {
            String ImageButtonName = "button" + (j + 1);
            int resIDImageButton = getResources().getIdentifier(ImageButtonName, "id", getPackageName());
            ImageButton button = findViewById(resIDImageButton);
            button.setOnClickListener(v -> {
                if (!selectAllowed)
                    return;

                int id = v.getId();

                if (selected.contains(v.getId())) {
                    // TODO: change selected state to false
                    Drawable normalBg = AppCompatResources.getDrawable(this, R.drawable.imageview_grey_border);
                    v.setBackground(normalBg);
                    selected.remove(id);
                    onSelectionChange();
                    Log.d("selected", String.valueOf(selected));
                    return;
                }

                if (selected.size() >= 6) {
                    Log.d("selected", String.valueOf(selected));
                    return;
                }

                // TODO: change selected state to true
                Drawable selectedBg = AppCompatResources.getDrawable(this, R.drawable.imageview_red_border);
                v.setBackground(selectedBg);
                selected.add(id);
                onSelectionChange();
                Log.d("selected", String.valueOf(selected));
                playButton.setOnClickListener(view -> {
                    Intent intent = new Intent(this,MainActivity2.class);
                    startActivity(intent);
                });
            });
            buttons.add(button);
        }

        // bind fetchButton
        Button fetchButton = findViewById(R.id.button);
        fetchButton.setOnClickListener(v -> {
            // clear selection in UI
            for (int id : selected) {
                ImageButton button = findViewById(id);
                Drawable normalBg = AppCompatResources.getDrawable(this, R.drawable.imageview_grey_border);
                button.setBackground(normalBg);
            }
            selected.clear();
            onSelectionChange();
            selectAllowed = false;


            EditText enteredURL = findViewById(R.id.editTextURL);
            String urlString = enteredURL.getText().toString();
            //checks if URL is valid
            if (!URLUtil.isValidUrl(urlString))
                return;

            if (fetchImageThread!=null)
            {
                fetchImageThread.interrupt();
                fetchImageThread=null;
            }

            fetchImageThread= new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Document document = Jsoup.connect(urlString).get();

                        //Get the image sources on the website
                        Elements images = document.select("img[src$=.jpg]");

                        // Only load images if there are more than 20
                        if (images.size() >= 20)
                        {
                            // Locate the src attribute
                            for (int i = 0; i < 20; i++) {
                                if (Thread.interrupted())
                                    return;
                                int finalI = i;
                                String imgSrc = images.get(i).absUrl("src");
                                // Download image from URL
                                InputStream input = new java.net.URL(imgSrc).openStream();
                                // Decode Bitmap
                                Bitmap result=BitmapFactory.decodeStream(input);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // this is okay!
                                        ImageButton button = buttons.get(finalI);
                                        button.setImageBitmap(result);
                                        bitmapDict.put(button.getId(), result);
                                    }
                                });
                                // update progress bar
                                // publishProgress(i + 1);

                                // for debugging
                                Log.d(urlString, "downloaded " + images.get(i).toString());
                            }
                        }
                        else
                            return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });


            fetchImageThread.start();

        });

        // bind playButton
        Button playButton =findViewById(R.id.playBtn);
        playButton.setOnClickListener(v -> {
            String filePath = "selected_images";
            String fileName = "img";

            for (int id : selected) {
                Bitmap bmp = bitmapDict.get(id);
                File mTargetFile = new File(this.getFilesDir(), filePath + "/" + fileName + id + ".bmp");
                writeImageToFile(mTargetFile, bmp);
            }

            Intent intent = new Intent(this, MainActivity2.class);
            String[] imgFiles = selected.stream().map(i -> "img" + i + ".bmp").toArray(String[]::new);

            intent.putExtra("img", imgFiles);
            startActivity(intent);
        });
    }


    //public void onFetchComplete(List<Bitmap> result) {
    //    selectAllowed = true;
    //
    //   if (result.isEmpty()) {
    //        Toast toast = Toast.makeText(getApplicationContext(), "Not enough images found", Toast.LENGTH_SHORT);
    //        toast.show();
    //    } else {
    //        for (int j = 0; j < 20; j++) {
    //            ImageButton button = buttons.get(j);
    //            button.setImageBitmap(result.get(j));
    //            bitmapDict.put(button.getId(), result.get(j));
    //        }
    //    }

    //    progressBar.setProgress(0);
    //    progressBar.setVisibility(View.GONE);
    //    progressStatus.setText("");
    //}

    //@Override
    //public void onFetchCancel() {
    //   progressBar.setProgress(0);
    //   progressBar.setVisibility(View.GONE);
    //   progressStatus.setText("");

    //   for (int i = 0; i < 20; i++) {
    //        String ImageButtonName = "button" + (i + 1);
    //        int resIDImageButton = getResources().getIdentifier(ImageButtonName, "id", getPackageName());
    //        ImageButton button = findViewById(resIDImageButton);
    //        button.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.x));
    // }
    //}

    //@Override
    //public void onProgressUpdate(int currProgress) {
    //    if (progressBar.getVisibility() != View.VISIBLE)
    //        progressBar.setVisibility(View.VISIBLE);
    //
    //       int progressPercent = (int) (((currProgress) / 20.0) * 100);
    //     progressBar.setProgress(progressPercent);
    //   progressStatus.setText(String.format("Downloading %s / 20", currProgress));
    //}

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fetchImageThread != null) {
            fetchImageThread.interrupt();
        }
    }

    protected void onSelectionChange() {
        if (selected.size() == 6) {
            playButton.setVisibility(View.VISIBLE);
            return;
        }

        playButton.setVisibility(View.GONE);
    }

    protected void writeImageToFile(File mTargetFile, Bitmap img) {
        try {
            File parent = mTargetFile.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                throw new IllegalStateException("Couldn't create dir: " + parent);
            }
            FileOutputStream fos = new FileOutputStream(mTargetFile);
            img.compress(Bitmap.CompressFormat.PNG, 100, fos);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}