package com.example.akanksha.smartpix;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by akanksha on 08/07/16.
 */
public class StoreObjectWrapper {
    private StoreObject storeObject;
    private Bitmap imageBitmap = null;
    private StoreAdapter custA;

    public StoreObjectWrapper(StoreObject so) {
        this.storeObject = so;
        // TO BE LOADED LATER - OR CAN SET TO A DEFAULT IMAGE
        this.imageBitmap = null;
    }

    public StoreObject getStoreDetails() {
        return storeObject;
    }

    public Bitmap getImagebitmap() {
        return imageBitmap;
    }

    public StoreAdapter getAdapter() {
        return custA;
    }

    public void setAdapter(StoreAdapter sta) {
        this.custA = sta;
    }

    public void loadImage(StoreAdapter sta) {
        // HOLD A REFERENCE TO THE ADAPTER
        this.custA = sta;
        if (storeObject.getLogo()!= null && !storeObject.getLogo().equals("")) {
            new ImageLoadTask().execute(storeObject.getLogo());
        }
    }

    // ASYNC TASK TO AVOID CHOKING UP UI THREAD
    private class ImageLoadTask extends AsyncTask<String, String, Bitmap> {

        @Override
        protected void onPreExecute() {
            Log.i("ImageLoadTask", "Loading image...");
        }

        // PARAM[0] IS IMG URL
        protected Bitmap doInBackground(String... param) {
            Log.i("ImageLoadTask", "Attempting to load image URL: " + param[0]);
            try {
                Bitmap b = NetwrokUtil.getBitmapFromURL(param[0]);
                return b;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(Bitmap ret) {
            if (ret != null) {
                Log.i("ImageLoadTask", "Successfully loaded " + storeObject.getStore_name() + " image");
                imageBitmap = ret;
                if (custA != null) {
                    // WHEN IMAGE IS LOADED NOTIFY THE ADAPTER
                    custA.notifyDataSetChanged();
                }
            } else {
                Log.e("ImageLoadTask", "Failed to load " + storeObject.getStore_name() + " image");
            }
        }
    }
}
