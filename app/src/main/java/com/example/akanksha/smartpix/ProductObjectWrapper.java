package com.example.akanksha.smartpix;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by akanksha on 07/07/16.
 */
public class ProductObjectWrapper {


    private ProductObject productObject;
    private Bitmap imageBitmap = null;
    private CustomAdapter custA;

        public ProductObjectWrapper(ProductObject po) {
            this.productObject = po;
            // TO BE LOADED LATER - OR CAN SET TO A DEFAULT IMAGE
            this.imageBitmap = null;
        }

        public ProductObject getProductDetail() {
            return productObject;
        }

        public Bitmap getImagebitmap() {
            return imageBitmap;
        }

        public CustomAdapter getAdapter() {
            return custA;
        }

        public void setAdapter(CustomAdapter sta) {
            this.custA = sta;
        }

        public void loadImage(CustomAdapter sta) {
            // HOLD A REFERENCE TO THE ADAPTER
            this.custA = sta;
            if (productObject.getImg_url()!= null && !productObject.getImg_url().equals("")) {
                new ImageLoadTask().execute(productObject.getImg_url());
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
                    Log.i("ImageLoadTask", "Successfully loaded " + productObject.getId() + " image");
                    imageBitmap = ret;
                    if (custA != null) {
                        // WHEN IMAGE IS LOADED NOTIFY THE ADAPTER
                        custA.notifyDataSetChanged();
                    }
                } else {
                    Log.e("ImageLoadTask", "Failed to load " + productObject.getId() + " image");
                }
            }
        }
    }


