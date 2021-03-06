package com.example.android.aaronchat;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by AaronLichtman on 4/10/17.
 * DataSnapshot {
 *      key = -Ki7pdI9OfwtzdFRpCyK, value = {
 *          name=CHAT ROOM 1,
 *          thread={
 *                  m2={message=asf, user=bb},
 *                  m1={message=hey there, user=AA}
 *          }
 *      }
 *}
 *
 *
 */

class Thread implements Parcelable{

    private String name;
    private Message[] messages;

    Thread(String name, Message[] messages) {
        this.name = name;
        this.messages = messages;
    }

    private Thread(Parcel in) {
        name = in.readString();
        messages = in.createTypedArray(Message.CREATOR);
    }

    public static final Creator<Thread> CREATOR = new Creator<Thread>() {
        @Override
        public Thread createFromParcel(Parcel in) {
            return new Thread(in);
        }

        @Override
        public Thread[] newArray(int size) {
            return new Thread[size];
        }
    };

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    Message[] getMessages() {
        return messages;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeTypedArray(messages, i);
    }
}
