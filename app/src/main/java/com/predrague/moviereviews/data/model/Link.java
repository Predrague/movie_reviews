package com.predrague.moviereviews.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Link implements Parcelable {
    @SerializedName("url")
    private final String url;
    @SerializedName("suggested_link_text")
    private final String suggestedLinkText;

    protected Link(Parcel in) {
        url = in.readString();
        suggestedLinkText = in.readString();
    }

    public static final Creator<Link> CREATOR = new Creator<Link>() {
        @Override
        public Link createFromParcel(Parcel in) {
            return new Link(in);
        }

        @Override
        public Link[] newArray(int size) {
            return new Link[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(url);
        parcel.writeString(suggestedLinkText);
    }

    public String getUrl() {
        return url;
    }

    public String getSuggestedLinkText() {
        return suggestedLinkText;
    }

    @Override
    public String toString() {
        return "Link{" +
                "url='" + url + '\'' +
                ", suggestedLinkText='" + suggestedLinkText + '\'' +
                '}';
    }
}
