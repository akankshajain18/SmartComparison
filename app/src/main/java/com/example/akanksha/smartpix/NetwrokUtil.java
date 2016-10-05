package com.example.akanksha.smartpix;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by akanksha on 07/07/16.
 */
public class NetwrokUtil {


    public static final String base_url="http://api.smartprix.com/";
    public static final String sample_v="simple/v1";
    public static final String keyValue = "NVgien7bb7P5Gsc8DWqc";


    public static String addQueryParameter(String name, String value, boolean isFirst){
        StringBuilder sb = new StringBuilder();
        if(isFirst){
            sb.append("?");
            sb.append(name);
            sb.append("=");
            sb.append(value);
        }else{
            sb.append("&");
            sb.append(name);
            sb.append("=");
            sb.append(value);
        }

        return sb.toString();
    }

    //Query 3: http://api.smartprix.com/simple/v1?type=categories&key=NVgien7bb7P5Gsc8DWqc&indent=1
    public static String getAllCategory(){
        StringBuilder sb = new StringBuilder(base_url);
        sb.append(sample_v);
        sb.append(addQueryParameter("type", "categories", true));
        sb.append(addQueryParameter("key", keyValue, false));
        sb.append(addQueryParameter("indent", 1+"", false));

        return sb.toString();
    }


    //Query 1: http://api.smartprix.com/simple/v1?type=product_full&key=NVgien7bb7P5Gsc8DWqc&id=2179&indent=1
    public static String getProductDetailPrice(String id){
        StringBuilder sb = new StringBuilder(base_url);
        sb.append(sample_v);
        sb.append(addQueryParameter("type", "product_full", true));
        sb.append(addQueryParameter("key", keyValue, false));
        sb.append(addQueryParameter("id", id, false));
        sb.append(addQueryParameter("indent", 1+"", false));

        return sb.toString();
    }



    //http://api.smartprix.com/simple/v1?type=search&key=NVgien7bb7P5Gsc8DWqc&category=Mobiles&q=3g&indent=1
    public static String getSearchResultForCategory(String category, int count, int startindex){
        StringBuilder sb = new StringBuilder(base_url);
        sb.append(sample_v);
        sb.append(addQueryParameter("type", "search", true));
        sb.append(addQueryParameter("key", keyValue, false));
        sb.append(addQueryParameter("category", category, false));
        sb.append(addQueryParameter("rows", count+"", false));
        sb.append(addQueryParameter("start", startindex+"", false));
        sb.append(addQueryParameter("indent", 1+"", false));

        return sb.toString();
    }




    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
        public static boolean isConnected(Context context)
        {
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo network = cm.getActiveNetworkInfo();
            if (network!=null&&network.isConnected()){
                return true;
            }

            return false;
        }

}
