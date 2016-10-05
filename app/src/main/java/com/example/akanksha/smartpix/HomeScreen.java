package com.example.akanksha.smartpix;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeScreen extends Activity implements AdapterView.OnItemClickListener {
    ProgressDialog prgDialog;
    Context mContext;
    ListView list;
    ArrayAdapter<String> adapter;
    public static final String TAG = HomeScreen.class.getSimpleName();
    ArrayList<String> categoryList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = HomeScreen.this;
        prgDialog = new ProgressDialog(mContext);
        // Set Cancelable as False
        prgDialog.setCancelable(false);
        if(!NetwrokUtil.isConnected(mContext)){
            Toast.makeText(mContext, getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
            finishThisActivity();
            return;
        }
        setContentView(R.layout.activity_home_screen);
        this.getActionBar().setDisplayHomeAsUpEnabled(true);
        prgDialog.setMessage(getString(R.string.fetch_data));
        prgDialog.show();
        makeHTTPCall();
        list = (ListView) findViewById(R.id.list);
        list.setOnItemClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
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


    // Make Http call to upload Image to Java server
    public void makeHTTPCall() {

        AsyncHttpClient client = new AsyncHttpClient();
       String url = NetwrokUtil.getAllCategory();
        Log.i("ABCDDD", url);
        client.get(this, url, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http
            // response code '200'
            @Override
            public void onSuccess(String response) {
                // Hide Progress Dialog
                if(prgDialog!=null)
                     prgDialog.dismiss();
                //Get all values in string array
                JSONArray categoryArray = new JSONArray();
                categoryList = new ArrayList<String>();
                try {
                    JSONObject jobject = new JSONObject(response.toString());
                    String requestStatus = jobject.optString("request_status");
                    if ("success".equalsIgnoreCase(requestStatus)) {
                        categoryArray = jobject.getJSONArray("request_result");
                        Gson gson = new GsonBuilder().create();
                        categoryList = gson.fromJson(categoryArray.toString(), ArrayList.class);
                        list = (ListView) findViewById(R.id.list);
                        adapter = new ArrayAdapter<String>(mContext, R.layout.home_screen_row, categoryList);// in place of null add array
                        list.setAdapter(adapter);

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
                    Log.d(TAG, "HTTP Status code : "
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
            i.putExtra("position" , categoryList.get(position));
            i.setClass(mContext, CategoryDetail.class);
        startActivity(i);

        //Do api call with selected category and load second activity.


    }


    public void finishThisActivity(){
        finish();
    }
}
