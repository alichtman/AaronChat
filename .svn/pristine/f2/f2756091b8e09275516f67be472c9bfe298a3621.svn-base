package com.example.android.aaronchat;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by AaronLichtman on 4/10/17.
 *
 * {
 * "user": "AaronLichtman",
 * "message": "What's up guys?",
 * }
 */

class Message implements Parcelable {

    private String user;
    private String message;

    Message(String user, String message) {
        this.user = user;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    String getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "User: " + this.user + ". Message: " + this.message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.user);
        dest.writeString(this.message);
    }

    Message(Parcel in) {
        this.user = in.readString();
        this.message = in.readString();
    }

    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel source) {
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}
