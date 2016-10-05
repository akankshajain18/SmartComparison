package com.example.akanksha.smartpix;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by akanksha on 08/07/16.
 */



    //@SuppressLint("InflateParams")
    public class StoreAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<StoreObjectWrapper> list;
        private static final String TAG =StoreAdapter.class.getSimpleName();


        public StoreAdapter(Context context, ArrayList<StoreObjectWrapper> list) {
            mContext= context;
            this.list=list;

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {

            Log.d(TAG, "GET View Called : " + position);
            ViewHolder holder;

            StoreObjectWrapper sow = list.get(position);
            final StoreObject so = sow.getStoreDetails();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.product_description_row, null);
                holder.price = (TextView) convertView.findViewById(R.id.tv_price);
                holder.buy = (Button) convertView.findViewById(R.id.btn_buy);
                holder.pic = (ImageView) convertView.findViewById(R.id.iv_Pic);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            String price = "";
            if(so.getPrice()!=null && !so.getPrice().isEmpty())
                price = so.getPrice();

            if (sow.getImagebitmap() != null){
                holder.pic.setImageBitmap(sow.getImagebitmap());
            } else {
                holder.pic.setImageResource(R.drawable.noimg);
            }

            holder.price.setText("Rs." + " " + price);

            holder.buy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String url = list.get(position).getStoreDetails().getStore_url();
                    if(url!=null&&!url.isEmpty()) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                            mContext.startActivity(intent);
                        }
                    }else {
                        Toast.makeText(mContext, "Ooops!!!, This click can not take you any where", Toast.LENGTH_SHORT).show();
                    }


                }
            });




            return convertView;
        }

        public static class ViewHolder {
            Button buy;
            TextView price;
            ImageView pic;
        }

    }


