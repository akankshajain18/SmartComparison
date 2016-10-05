package com.example.akanksha.smartpix;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class ProductComparision extends Activity{



        private static final String TAG = CategoryDetail.class.getSimpleName();
        private Context mContext;
        ListView l = null;
        TextView bestPrice = null;
        TextView availableStore = null;
        private StoreAdapter storeA;
        String id= "";
        ProgressDialog prgDialog;
        ArrayList<StoreObjectWrapper> list_store = null;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_product_description);
            mContext=ProductComparision.this;
            this.getActionBar().setDisplayHomeAsUpEnabled(true);
            TextView productName = (TextView) findViewById(R.id.tv_category);
            productName.setVisibility(View.VISIBLE);
            String name = getIntent().getStringExtra("name");
            productName.setText(name);
            if(!NetwrokUtil.isConnected(mContext)){
                Toast.makeText(mContext, getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                finishThisActivity();
                return;
            }
            bestPrice = (TextView) findViewById(R.id.tv_bestprice);
            availableStore = (TextView) findViewById(R.id.tv_avail);
            id = getIntent().getStringExtra("id");
            l = (ListView)findViewById(R.id.list);

            prgDialog = new ProgressDialog(mContext);
            // Set Cancelable as False
            prgDialog.setCancelable(false);
            prgDialog.setMessage(getString(R.string.fetch_data));
            prgDialog.show();
            makeHTTPCall(id);
            //l.setOnItemClickListener(this);

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


        public void makeHTTPCall(String id) {
            AsyncHttpClient client = new AsyncHttpClient();
            String url = NetwrokUtil.getProductDetailPrice(id);
            client.get(this, url, new AsyncHttpResponseHandler() {
                // When the response returned by REST has Http
                // response code '200'
                @Override
                public void onSuccess(String response) {

                    if(prgDialog!=null)
                        prgDialog.dismiss();
                    //Get all values in string array
                    JSONObject stObject = new JSONObject();
                    JSONArray stArray = new JSONArray();
                    try {
                        JSONObject jobject = new JSONObject(response.toString());
                        String requestStatus = jobject.optString("request_status");
                        if ("success".equalsIgnoreCase(requestStatus)) {
                            stObject = jobject.getJSONObject("request_result");
                            stArray = stObject.getJSONArray("prices");
                            Gson gson = new GsonBuilder().create();
                            long lowest_price = 0L;
                            String store_name="";
                                StoreObject[] so = gson.fromJson(stArray.toString(), StoreObject[].class);
                                list_store = new ArrayList<StoreObjectWrapper>();
                                for (int i = 0; i < so.length; i++) {
                                    int price =Integer.parseInt(so[i].getPrice());
                                    if(lowest_price>price||lowest_price==0L){
                                        lowest_price = price;
                                        store_name = so[i].getStore_name();
                                    }
                                    list_store.add(new StoreObjectWrapper(so[i]));
                                }
                                setBestPrice(lowest_price, store_name);
                                storeA = new StoreAdapter(mContext, list_store);
                                l.setAdapter(storeA);
                                for (StoreObjectWrapper sw : list_store) {
                                    // START LOADING IMAGES FOR EACH STORY
                                    sw.loadImage(storeA);
                                }
                            //}else{
                             // Toast.makeText(mContext, "Store information is not available for selected product", Toast.LENGTH_SHORT).show();
                            //}


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
                                + statusCode);

                        System.out.println("Error : " + error);
                        System.out.println("Content : " + content);
                    }

                    finishThisActivity();
                }
            });
        }


    public void setBestPrice(long price, String storeName) {
        if(price==0){
            bestPrice.setText(getString(R.string.notavailable));
            availableStore.setVisibility(View.GONE);
        }else {
            if (bestPrice != null)
                bestPrice.setText(getString(R.string.best_price, price));
            if (availableStore != null)
                availableStore.setText(getString(R.string.available, storeName));
        }
    }

    public void finishThisActivity(){
            finish();
        }



}
