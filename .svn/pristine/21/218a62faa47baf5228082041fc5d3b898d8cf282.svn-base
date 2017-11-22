package com.example.android.aaronchat;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatMessageSelectionActivity extends AppCompatActivity {

    /**
     * Get information from Intent pass, create RecyclerView for messages. If there are already
     * messages there, load and display. If none, do nothing.
     *
     * @param savedInstanceState Saved Instance State
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        //Catch username and chatName selected
        final Intent intent = getIntent();
        final String user = intent.getStringExtra("user");
        final String chatName = intent.getStringExtra("chatName");
        final String chatKey = intent.getStringExtra("chatKey");
        Log.d("chatName", chatName);

        //Declare floating action button and menu
        com.github.clans.fab.FloatingActionButton floatingActionButton =
                (com.github.clans.fab.FloatingActionButton) findViewById(R.id.floatingActionButton);

        final com.github.clans.fab.FloatingActionMenu floatingActionMenu =
                (com.github.clans.fab.FloatingActionMenu) findViewById(R.id.floatingActionsMenu);


        /**
         * Floating action button added to the floating actions menu.
         * When this button is clicked, transition to the ChatRoomCreationActivity
         *
         * Using   https://github.com/Clans/FloatingActionButton
         */
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("INTENT", "MessageEntryActivity");
                Context context = view.getContext();
                floatingActionMenu.close(true);

                /**
                 * Information needed is:
                 *      Username
                 *      Chat Key
                 */
                Intent messageEntryIntent = new Intent(context, MessageEntryActivity.class);
                messageEntryIntent.putExtra("user", user);
                messageEntryIntent.putExtra("chatKey", chatKey);
                startActivityForResult(messageEntryIntent, 0);
            }
        });

        //Sets toolbar as the app bar for the activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(chatName);

        //On click navigation icon in toolbar, go back to ChatRoomSelectionActivity
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Debugging
                Log.d("INTENT", "To ChatRoomSelectionActivity");
                Context context = view.getContext();
                //Intent constructor's parameters are --> (Context to go from, Activity to go to)
                Intent intent = new Intent(context, ChatRoomSelectionActivity.class);
                intent.putExtra("user", user);
                context.startActivity(intent);
            }
        });

        //Set layout manager for recyclerView
        RecyclerView messagesRecyclerView = (RecyclerView) findViewById(R.id.messagesRecyclerView);
        messagesRecyclerView.setHasFixedSize(true);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        //Create array to hold previous messages
        final Message[] tempMessageArr = new Message[15];

        //Connect chatMessagesAdapter to messagesRecyclerView
        final ChatMessagesAdapter chatMessagesAdapter = new ChatMessagesAdapter(tempMessageArr);
        messagesRecyclerView.setAdapter(chatMessagesAdapter);

        //Gets instance of Firebase database and establish reference as "thread"
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference messagesInDatabase = database.getReference("chatRooms");

        /**
         * https://firebase.google.com/docs/database/android/lists-of-data
         *
         * Attaching a ValueEventListener to a list of data will return the entire list of data as
         * a single DataSnapshot, which you can then loop over to access individual children.
         * Even when there is only a single match for the query, the snapshot is still a list;
         * it just contains a single item. To access the item, you need to loop over the result
         *
         * ---------
         * Attaching it to messagesInDatabase, which already has the reference "Thread", so the
         * list returned in dataSnapshot is going to be a list of Message Objects.
         * ---------
         *
         */
        messagesInDatabase.addValueEventListener(new ValueEventListener() {
            /**
             * Pull previous messages from Firebase
             * Reads the database in real time.
             * @param dataSnapshot data returned from Firebase
             */
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Initialize Message Array
                Message[] pastMessages = new Message[0];
                ArrayList<Message> messageArrayList = new ArrayList<>();

                for (DataSnapshot threadSnapshot : dataSnapshot.getChildren()) {
                    Log.d("ATTN: ThreadSnapshot", threadSnapshot.toString());
                    if (threadSnapshot.child("name").getValue().equals(chatName)) {
                        Log.d("ATTN:ChatName", threadSnapshot.child("name").getValue().toString());
                        Log.d("ATTN:Contents", threadSnapshot.child("thread").getValue().toString());

                        //Collect all messages and assign to pastMessages
                        for (DataSnapshot messagesInThread : threadSnapshot.child("thread").getChildren()) {
                            String user = messagesInThread.child("user").getValue().toString();
                            String messageText = messagesInThread.child("message").getValue().toString();
                            Message nextMessage = new Message(user , messageText);
                            Log.d("NEXT MESSAGE", nextMessage.toString());
                            messageArrayList.add(nextMessage);
                        }
                        pastMessages = new Message[messageArrayList.size()];
                        pastMessages = messageArrayList.toArray(pastMessages);
                    }
                }
                //Poke ChatMessagesAdapter
                chatMessagesAdapter.setMessages(pastMessages);
                chatMessagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Error", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    /**
     * When returning from the chat message entry screen, if successful result code returned, display
     * success message, if failed result code returned, show failed code.
     * @param requestCode Determines request type. Default either 1 or 0?
     * @param resultCode Result code returned from other activity
     * @param data Intent pass back from activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        //Check which request we're responding to
        if (requestCode == 0) {
            // Make sure the request was successful
            switch (resultCode) {
                //Failed chat creation.
                case RESULT_CANCELED:
                    Snackbar.make(relativeLayout, "Failed to send message.",
                            Snackbar.LENGTH_SHORT).show();
                    break;
                //Successful chat creation, chatName should be collected, pop success snackbar
                case 1:
                    Snackbar.make(relativeLayout, "Message sent.", Snackbar.LENGTH_SHORT).show();
                    break;
                //Failed project.
                default:
                    Snackbar.make(relativeLayout, "Something went wrong. Help. Please.",
                            Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}
