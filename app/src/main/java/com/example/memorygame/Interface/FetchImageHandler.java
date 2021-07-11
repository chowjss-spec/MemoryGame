package com.example.memorygame.Interface;

import android.graphics.Bitmap;

import java.util.List;

public interface FetchImageHandler {
    void onFetchComplete(List<Bitmap> result);
    void onFetchCancel();
    void onProgressUpdate(int currProgress);
}
