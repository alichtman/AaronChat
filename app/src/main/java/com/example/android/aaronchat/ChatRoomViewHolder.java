package com.example.android.aaronchat;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by AaronLichtman on 4/10/17.
 */

/**
 * A ChatRoomViewHolder class for our adapter that 'caches' the references to the
 * subviews, so we don't have to look them up each time.
 *
 * Each ChatRoomViewHolder contains a constraintLayout, chatRoomTextView that are displayed,
 * as well as an object reference to the Thread it represents.
 */
class ChatRoomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView chatRoomTextView;
    private Thread thread;

    /**
     * Constructor for MessageViewHolder
     * @param itemView itemView
     */
    ChatRoomViewHolder(View itemView) {
        super(itemView);
        chatRoomTextView = (TextView) itemView.findViewById(R.id.chatRoomTextView);
        //Set onClickListener for recyclerView
        itemView.setOnClickListener(this);
    }

    public TextView getChatRoomTextView() {
        return chatRoomTextView;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    //Makes the compiler happy
    @Override
    public void onClick(View view) {
    }

}
