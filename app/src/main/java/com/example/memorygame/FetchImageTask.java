package com.example.memorygame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.example.memorygame.Interface.FetchImageHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FetchImageTask extends AsyncTask<URL, Integer, List<Bitmap>> {
    private final FetchImageHandler handler;

    public FetchImageTask(FetchImageHandler handler) {
        super();
        this.handler = handler;
    }


//    private AsyncTask<String, Void, Void> updateTask = null;

    @Override
    protected List<Bitmap> doInBackground(URL... urls) {
        List<Bitmap> bitmaps = new ArrayList<>();

        String urlString = urls[0].toString();
        try {
            //Connect to the website
            Document document = Jsoup.connect(urlString).get();

            //Get the logo source of the website
            Elements images = document.select("img[src$=.jpg]");

            // TODO: handle this exception
            if (images.size() < 20)
                throw new Exception("not enough images in URL");

            // Locate the src attribute
            for (int i = 0; i < 20; i++) {
                if (isCancelled())
                    return null;
                String imgSrc = images.get(i).absUrl("src");
                // Download image from URL
                InputStream input = new java.net.URL(imgSrc).openStream();
                // Decode Bitmap
                bitmaps.add(BitmapFactory.decodeStream(input));

                // update progress bar
                publishProgress(i + 1);

                // for debugging
                Log.d(urlString, "downloaded " + images.get(i).toString());
            }


        } catch (IOException e) {
            Log.d("exception", "IOException reading bitmap");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmaps;
    }

    @Override
    protected void onPostExecute(List<Bitmap> result) {
        handler.onFetchComplete(result);
    }

    @Override
    protected void onCancelled() {
        handler.onFetchCancel();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int currProgress = values[0];
        handler.onProgressUpdate(currProgress);
    }
}