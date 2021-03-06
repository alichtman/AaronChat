package com.example.android.aaronchat;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by AaronLichtman on 4/10/17.
 */

/**
 * A MessageViewHolder class for our adapter that 'caches' the references to the
 * subviews, so we don't have to look them up each time.
 *
 * Each MessageViewHolder contains a constraintLayout, titleView, languageView and imageView that are displayed,
 * as well as an object reference to the Movie it represents.
 */
class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView usernameTextView;
    private TextView messageTextView;
    private ImageView profilePictureImageView;
    private ConstraintLayout constraintLayout;
    private Message message;

    /**
     * Constructor for MessageViewHolder
     * @param itemView itemView
     */
    MessageViewHolder(View itemView) {
        super(itemView);
        usernameTextView = (TextView) itemView.findViewById(R.id.usernameTextView);
        messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
        profilePictureImageView = (ImageView) itemView.findViewById(R.id.profileImageView);
        constraintLayout = (ConstraintLayout) itemView.findViewById(R.id.messageConstraintLayout);

        //Set onClickListener for recyclerView
        itemView.setOnClickListener(this);
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    ConstraintLayout getConstraintLayout() {
        return constraintLayout;
    }

    TextView getUsernameTextView() {
        return usernameTextView;
    }

    TextView getMessageTextView() {
        return messageTextView;
    }

    ImageView getProfilePictureImageView() {
        return profilePictureImageView;
    }

    //Makes the compiler happy.
    @Override
    public void onClick(View view) {

    }
}
