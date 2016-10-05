package com.example.akanksha.smartpix;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by akanksha on 07/07/16.
 */
public class ProductObject implements Parcelable{


    private String id = null;
    private String category = null;
    private String name = null;
    private String brand = null;
    private String img_url = null;  // ImageUrl
    private String price = null;


    ProductObject(String id, String category, String name, String brand, String img_url, String price){
        this.id = id;
        this.category = category;
        this.name = name;
        this.brand = brand;
        this.img_url = img_url;
        this.price = price;

    }

    //All setter method is redundant.
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(category);
        dest.writeString(name);
        dest.writeString(brand);
        dest.writeString(img_url);
        dest.writeString(price);
    }


    protected ProductObject(Parcel in) {
        id = in.readString();
        category = in.readString();
        name = in.readString();
        brand = in.readString();
        img_url = in.readString();
        price = in.readString();
    }

    public static final Creator<ProductObject> CREATOR = new Creator<ProductObject>() {
        @Override
        public ProductObject createFromParcel(Parcel in) {
            return new ProductObject(in);
        }

        @Override
        public ProductObject[] newArray(int size) {
            return new ProductObject[size];
        }
    };





}

