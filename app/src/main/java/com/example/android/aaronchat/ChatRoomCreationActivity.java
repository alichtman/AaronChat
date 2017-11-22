package com.example.android.aaronchat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.android.aaronchat.ChatRoomSelectionActivity.chatRoomNames;

/**
 * This Activity allows the user to enter a chat name to create a new chat room. When a
 * name is entered, transition back to ChatRoomSelectionActivity and snackbar notification to
 * show if successful or not. The new chat room is pushed to Firebase, and the RecyclerView updates.
 */
public class ChatRoomCreationActivity extends AppCompatActivity {
    /**
     * On create, get intent pass and store data. Then wait for user to enter text and press send.
     *
     * @param savedInstanceState Saved Instance State
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_creation);

        final EditText chatRoomEditText = (EditText) findViewById(R.id.chatRoomEditText);
        Button createButton = (Button) findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            /**
             * When the create button is pressed, make sure the user has entered text.
             * Then, create new node under "chatRooms" node.
             * @param view View
             */
            @Override
            public void onClick(View view) {
                String chatName = chatRoomEditText.getText().toString();

                //Setting up transition to ChatRoomSelectionActivity
                Intent output = new Intent();

                //If the chatName is valid, confirm intent to send and then push to Firebase.
                if (isChatRoomNameValid(chatName)) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = database.getReference("chatRooms");

                    //Make first message in the chat room instructional.
                    Message[] messages = new Message[1];
                    Message startMessage = new Message("ADMIN", getString(R.string.first_message));
                    messages[0] = startMessage;

                    //Fetch unique key for chatRoom
                    String key = databaseReference.push().getKey();
                    Log.d("CHAT FIREBASE KEY", key);

                    //Push new thread to Firebase
                    databaseReference.child(key).child("name").setValue(chatName);
                    databaseReference.child(key).child("thread").push().setValue(startMessage);


                    //Attach information for pop-up snackbar
                    output.putExtra("chatRoomName", chatName);
                    output.putExtra("chatRoomKey", key);
                    setResult(1, output);
                }
                //Else pop-up Invalid ChatRoomName notification
                else {
                    setResult(0);
                }
                //Transition back to ChatRoomSelectionActivity
                finish();
            }
        });
    }

    /**
     * Checks if a chat room name is valid. Use running list of used chat
     * names to make sure there are no duplicates.
     *
     * @param chatName EditText object that holds proposed chatName text.
     * @return boolean true if is valid, false otherwise
     */
    private boolean isChatRoomNameValid(String chatName) throws NullPointerException {
        try {
            if (!chatName.trim().equals("")) {
                for (String name : chatRoomNames) {
                    if (chatName.equals(name)) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }
}
