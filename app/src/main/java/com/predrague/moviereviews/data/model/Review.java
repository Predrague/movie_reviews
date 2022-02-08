package com.predrague.moviereviews.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Review implements Parcelable {
    @SerializedName("display_title")
    private final String displayTitle;
    @SerializedName("mpaa_rating")
    private final String mpaaRating;
    @SerializedName("critics_pick")
    private final int criticsPick;
    @SerializedName("byline")
    private final String author;
    @SerializedName("headline")
    private final String headline;
    @SerializedName("summary_short")
    private final String summaryShort;
    @SerializedName("publication_date")
    private final String publicationDateString;
    @SerializedName("link")
    private final Link link;


    protected Review(Parcel in) {
        displayTitle = in.readString();
        mpaaRating = in.readString();
        criticsPick = in.readInt();
        author = in.readString();
        headline = in.readString();
        summaryShort = in.readString();
        publicationDateString = in.readString();
        link = in.readParcelable(Link.class.getClassLoader());
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(displayTitle);
        parcel.writeString(mpaaRating);
        parcel.writeInt(criticsPick);
        parcel.writeString(author);
        parcel.writeString(headline);
        parcel.writeString(summaryShort);
        parcel.writeString(publicationDateString);
        parcel.writeParcelable(link, i);
    }

    public String getDisplayTitle() {
        return displayTitle;
    }

    public String getMpaaRating() {
        return mpaaRating;
    }

    public int getCriticsPick() {
        return criticsPick;
    }

    public String getAuthor() {
        return author;
    }

    public String getHeadline() {
        return headline;
    }

    public String getSummaryShort() {
        return summaryShort;
    }

    public String getPublicationDateString() {
        return publicationDateString;
    }

    public Link getLink() {
        return link;
    }


    @Override
    public String toString() {
        return "Review{" +
                "displayTitle='" + displayTitle + '\'' +
                ", mpaaRating='" + mpaaRating + '\'' +
                ", criticsPick=" + criticsPick +
                ", author='" + author + '\'' +
                ", headline='" + headline + '\'' +
                ", summaryShort='" + summaryShort + '\'' +
                ", publicationDateString='" + publicationDateString + '\'' +
                ", link=" + link +
                '}';
    }
}
