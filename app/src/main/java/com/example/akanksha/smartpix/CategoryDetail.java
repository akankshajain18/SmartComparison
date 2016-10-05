package com.example.akanksha.smartpix;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CategoryDetail extends Activity implements AdapterView.OnItemClickListener {

    private static final String TAG = CategoryDetail.class.getSimpleName();
    private Context mContext;
    ListView l = null;
    HorizontalScrollView belowSrollBar = null;
    private CustomAdapter customA;
    String category= "";
    ArrayList<ProductObjectWrapper> list_product = null;
    public int TOTAL_LIST_ITEMS = 0;
    public int NUM_ITEMS_PAGE   = 50;
    private int noOfBtns;
    private Button[] btns;
    ProgressDialog prgDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        mContext=CategoryDetail.this;
        this.getActionBar().setDisplayHomeAsUpEnabled(true);
        TextView categoryName = (TextView) findViewById(R.id.tv_category);
        categoryName.setVisibility(View.VISIBLE);
        category = getIntent().getStringExtra("position");
        categoryName.setText(category);
        belowSrollBar = (HorizontalScrollView) findViewById(R.id.view_horizontal);
        belowSrollBar.setVisibility(View.VISIBLE);
        l = (ListView)findViewById(R.id.list);
        if(!NetwrokUtil.isConnected(mContext)){
            Toast.makeText(mContext, getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
            finishThisActivity();
            return;
        }
        prgDialog = new ProgressDialog(mContext);
        // Set Cancelable as False
        prgDialog.setCancelable(false);
        prgDialog.setMessage(getString(R.string.fetch_data));
        prgDialog.show();
        makeHTTPCall(category);
        l.setOnItemClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        else if(id==android.R.id.home)
        {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void makeHTTPCall(String category) {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = NetwrokUtil.getSearchResultForCategory(category, NUM_ITEMS_PAGE, 0);
        client.get(this,url, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http
            // response code '200'
            @Override
            public void onSuccess(String response) {

                if(prgDialog!=null)
                    prgDialog.dismiss();
                //Get all values in string array
                JSONObject categoryObject = new JSONObject();
                JSONArray categoryArray = new JSONArray();
                try {
                    JSONObject jobject = new JSONObject(response.toString());
                    String requestStatus = jobject.optString("request_status");
                    if ("success".equalsIgnoreCase(requestStatus)) {
                        categoryObject = jobject.getJSONObject("request_result");
                        TOTAL_LIST_ITEMS = categoryObject.getInt("results_count_total");
                        categoryArray = categoryObject.getJSONArray("results");

                        Gson gson = new GsonBuilder().create();
                        ProductObject[] po = gson.fromJson(categoryArray.toString(), ProductObject[].class);
                        list_product = new  ArrayList<ProductObjectWrapper>();
                        for(int i =0;i<po.length;i++) {
                            list_product.add(new ProductObjectWrapper(po[i]));
                        }
                        customA = new CustomAdapter(mContext, list_product);
                        l.setAdapter(customA);
                        for (ProductObjectWrapper sw : list_product) {
                            // START LOADING IMAGES FOR EACH STORY
                            sw.loadImage(customA);
                        }
                        Btnfooter();

                    } else if ("Fail".equalsIgnoreCase(requestStatus)) { //Fail case
                        String error = jobject.optString("request_error");
                        Log.d(TAG, error);
                        Toast.makeText(mContext,
                                error,
                                Toast.LENGTH_SHORT).show();
                        finishThisActivity();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // When the response returned by REST has Http
            // response code other than '200' such as '404',
            // '500' or '403' etc
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {

                if(prgDialog!=null)
                    prgDialog.dismiss();
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(mContext,
                            "Resource is not found",
                            Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(),
                            "Server error",
                            Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Log.d(TAG,"HTTP Status code : "
                            + statusCode );


                    System.out.println("Error : " + error);
                    System.out.println("Content : " + content);
                }

                finishThisActivity();
            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        Intent i = new Intent();
        i.putExtra("id" , list_product.get(position).getProductDetail().getId());
        i.putExtra("name", list_product.get(position).getProductDetail().getName());
        i.setClass(mContext, ProductComparision.class);
        startActivity(i);

        //Do api call with selected category and load second activity.


    }


    private void Btnfooter()
    {
        int val = TOTAL_LIST_ITEMS%NUM_ITEMS_PAGE;
        val = val==0?0:1;
        noOfBtns=TOTAL_LIST_ITEMS/NUM_ITEMS_PAGE+val;

        LinearLayout ll = (LinearLayout)findViewById(R.id.ll_btnoverlay);

        btns =new Button[noOfBtns];

        for(int i=0;i<noOfBtns;i++)
        {
            btns[i] =   new Button(this);
            btns[i].setBackgroundColor(getResources().getColor(android.R.color.transparent));
            btns[i].setText("" + (i + 1));

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ll.addView(btns[i], lp);

            final int j = i;
            btns[j].setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    loadList(j);
                    CheckBtnBackGroud(j);
                }
            });

        }
        btns[0].setTextColor(getResources().getColor(android.R.color.holo_purple));

    }


    @SuppressLint("NewApi")
    private void CheckBtnBackGroud(int index)
    {
        //title.setText("Page "+(index+1)+" of "+noOfBtns);
        for(int i=0;i<noOfBtns;i++){
            if(i==index) {
                btns[i].setTextColor(getResources().getColor(android.R.color.holo_purple));
            } else{
                btns[i].setTextColor(getResources().getColor(android.R.color.black));
            }
        }

    }

    /**
     * Method for loading data in listview
     * @param number
     */
    private void loadList(int number)
    {

        int start = number * NUM_ITEMS_PAGE;
        loadPages(category, start);

    }

    public void loadPages(String category, int startIndex) {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = NetwrokUtil.getSearchResultForCategory(category, NUM_ITEMS_PAGE, startIndex);
        client.get(this,url, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http
            // response code '200'
            @Override
            public void onSuccess(String response) {

                //Get all values in string array
                JSONObject categoryObject = new JSONObject();
                JSONArray categoryArray = new JSONArray();
                try {
                    JSONObject jobject = new JSONObject(response.toString());
                    String requestStatus = jobject.optString("request_status");
                    if ("success".equalsIgnoreCase(requestStatus)) {
                        categoryObject = jobject.getJSONObject("request_result");
                        categoryArray = categoryObject.getJSONArray("results");

                        Gson gson = new GsonBuilder().create();
                        ProductObject[] po = gson.fromJson(categoryArray.toString(), ProductObject[].class);
                        list_product = new  ArrayList<ProductObjectWrapper>();
                        for(int i =0;i<po.length;i++) {
                            list_product.add(new ProductObjectWrapper(po[i]));
                        }
                        customA = new CustomAdapter(mContext, list_product);
                        l.setAdapter(customA);
                        for (ProductObjectWrapper sw : list_product) {
                            // START LOADING IMAGES FOR EACH STORY
                            sw.loadImage(customA);
                        }


                    } else if ("Fail".equalsIgnoreCase(requestStatus)) { //Fail case
                        String error = jobject.optString("request_error");
                        Log.d(TAG, error);
                        Toast.makeText(mContext,
                                error,
                                Toast.LENGTH_SHORT).show();
                        finishThisActivity();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // When the response returned by REST has Http
            // response code other than '200' such as '404',
            // '500' or '403' etc
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {

                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(mContext,
                            "Resource is not found",
                            Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(),
                            "Server error",
                            Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Log.d(TAG,"HTTP Status code : "
                            + statusCode );
                    System.out.println("Error : " + error);
                    System.out.println("Content : " + content);
                }

                finishThisActivity();
            }
        });
    }

    public void finishThisActivity(){
        finish();
    }

}
