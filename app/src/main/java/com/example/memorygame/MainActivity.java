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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
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

        bitmapDict = new HashMap<>();
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

                Drawable selectedBg = AppCompatResources.getDrawable(this, R.drawable.imageview_red_border);
                v.setBackground(selectedBg);
                selected.add(id);
                onSelectionChange();
                Log.d("selected", String.valueOf(selected));
            });
            buttons.add(button);
        }

        // bind playButton
        Button playButton = findViewById(R.id.playBtn);
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

        // bind fetchButton
        Button fetchButton = findViewById(R.id.button);
        fetchButton.setOnClickListener(v -> {

            EditText enteredURL = findViewById(R.id.editTextURL);
            String urlString = enteredURL.getText().toString();

            //checks if URL is valid
            if (!URLUtil.isValidUrl(urlString)) {
                Toast.makeText(getApplicationContext(), "Please enter a valid URL", Toast.LENGTH_SHORT).show();
                return;
            }

            if (fetchImageThread != null) {
                fetchImageThread.interrupt();
            }

            fetchImageThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(() -> onStartFetch());
                    try {
                        Document document = Jsoup.connect(urlString).get();

                        //Get the image sources on the website
                        Elements images = document.select("img[src$=.jpg]");

                        Log.d("url", urlString);
                        Log.d("images", String.valueOf(images.size()));
                        // Only load images if there are more than 20
                        if (images.size() >= 20) {
                            // Locate the src attribute
                            for (int i = 0; i < 20; i++) {
                                if (Thread.interrupted()) {
                                    break;
                                }

                                final int finalI = i;
                                String imgSrc = images.get(i).absUrl("src");
                                // Download image from URL
                                InputStream input = new java.net.URL(imgSrc).openStream();
                                // Decode Bitmap
                                Bitmap result = BitmapFactory.decodeStream(input);
                                runOnUiThread(() -> {
                                    // this is okay!
                                    ImageButton button = buttons.get(finalI);
                                    button.setImageBitmap(result);
                                    bitmapDict.put(button.getId(), result);
                                });
                                // update progress bar
                                runOnUiThread(() -> {
                                    onProgressUpdate(finalI + 1);
                                });
                            }
                            runOnUiThread(() -> {
                                onFetchComplete();
                            });
                            return;
                        }

                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "Unable to fetch enough images", Toast.LENGTH_SHORT).show();
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            fetchImageThread.start();

        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fetchImageThread != null) {
            fetchImageThread.interrupt();
        }
    }

    public void onStartFetch() {
        // clear selection in UI
        for (int id : selected) {
            ImageButton button = findViewById(id);
            Drawable normalBg = AppCompatResources.getDrawable(this, R.drawable.imageview_grey_border);
            button.setBackground(normalBg);
        }
        selected.clear();
        onSelectionChange();
        selectAllowed = false;

        progressBar.setProgress(0);
        progressBar.setVisibility(View.GONE);
        progressStatus.setText("");
    }

    public void onFetchComplete() {
        selectAllowed = true;

        progressBar.setProgress(0);
        progressBar.setVisibility(View.GONE);
        progressStatus.setText("");
        fetchImageThread = null;
    }

    public void onProgressUpdate(int currProgress) {
        if (progressBar.getVisibility() != View.VISIBLE)
            progressBar.setVisibility(View.VISIBLE);

        int progressPercent = (int) (((currProgress) / 20.0) * 100);
        progressBar.setProgress(progressPercent);
        progressStatus.setText(String.format("Downloading %s / 20", currProgress));
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}