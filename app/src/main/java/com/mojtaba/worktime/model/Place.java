package com.mojtaba.worktime.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mojtaba on 6/4/15.
 */
public class Place implements Parcelable {

    private long id;
    private String name;
    private String googleAddress;
    private String description;
    private double latitude;
    private double longitude;

    public Place()
    {}
    public Place(String name,long id)
    {
        this.name = name;
        this.id = id;
    }

    public Place(Parcel in)
    {
        id=in.readLong();
        name= in.readString();
        description = in.readString();
        googleAddress = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGoogleAddress(String googleAddress) {
        this.googleAddress = googleAddress;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGoogleAddress() {
        return googleAddress;
    }

    public String getDescription() {
        return description;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {

        return name +"\n" + googleAddress;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeString(googleAddress);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        public Place[] newArray(int size) {
            return new Place[size];
        }
    };
}
