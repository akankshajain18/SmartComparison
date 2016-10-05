package com.example.akanksha.smartpix;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

//@SuppressLint("InflateParams")
public class CustomAdapter extends BaseAdapter{
    private Context mContext;
    private ArrayList<ProductObjectWrapper> list;
    private static final String TAG =CustomAdapter.class.getSimpleName();


    public CustomAdapter(Context context, ArrayList<ProductObjectWrapper> list) {
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

        ProductObjectWrapper pow = list.get(position);
        final ProductObject po = pow.getProductDetail();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.category_detail_row, null);
            holder.price = (TextView) convertView.findViewById(R.id.tv_price);
            holder.title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.pic = (ImageView) convertView.findViewById(R.id.iv_Pic);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String title = "";
        if(po.getName()!=null && !po.getName().isEmpty())
            title = po.getName();

        String price = "";
        if(po.getPrice()!=null && !po.getPrice().isEmpty())
            price = po.getPrice();

        if ( pow.getImagebitmap() != null){
            holder.pic.setImageBitmap(pow.getImagebitmap());
        } else {
            holder.pic.setImageResource(R.drawable.noimg);
        }

        holder.title.setText(title);
        holder.price.setText("Rs." + " " + price);




        return convertView;
    }

    public static class ViewHolder {
        TextView title;
        TextView price;
        ImageView pic;
    }

}
