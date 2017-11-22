package com.example.android.aaronchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * This Activity allows the user to enter a message and if a valid message has been entered,
 * perform Intent transition to the ChatMessageSelectionActivity with the Message as a Parcelable Extra. The
 * Message is pushed to Firebase, and the RecyclerView needs to update.
 */
public class MessageEntryActivity extends AppCompatActivity {

    private static String username;

    /**
     * On create, get intent pass and store data. Then wait for user to enter text and press send.
     *
     * @param savedInstanceState Saved Instance State
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_entry);

        //Extract username and chatKey from Intent
        Bundle intentExtras = getIntent().getExtras();
        username = intentExtras.get("user").toString();
        final String chatKey = intentExtras.get("chatKey").toString();

        /**
         * Onclick Listener for sendButton creates Message from input and validates it. If valid,
         * push to Firebase and prepare Snackbar. Else, just prepare snackbar.
         */
        Button sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            /**
             * When the send button is pressed, make sure the user has entered text.
             * Now, we need to push message to Firebase.
             * @param view View
             */
            @Override
            public void onClick(View view) {
                EditText messageEditText = (EditText) findViewById(R.id.messageEditText);
                String messageText = messageEditText.getText().toString();
                Message message = new Message(username, messageText);

                Intent output = new Intent();

                //If the message is valid, confirm intent to send and then push to Firebase.
                if (isMessageValid(message)) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    //Push message to correct chat room
                    DatabaseReference databaseReference = database.getReference("chatRooms").child(chatKey).child("thread");
                    databaseReference.push().setValue(message);
                    setResult(1, output);
                }
                else setResult(0, output);
                //Transition back to ChatRoomPage
                finish();
            }
        });
    }

    /**
     * Checks if a message is valid.
     * @param message EditText object that holds message text.
     * @return boolean true if valid
     */
    private boolean isMessageValid(Message message) throws NullPointerException {
        //If any of the properties of the message are null, invalidate it
        try {
            if (message.getUser() != null && !message.getMessage().trim().equals("")) {
                return true;
            }
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }
}
