package com.example.android.aaronchat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatRoomSelectionActivity extends AppCompatActivity {

    public static final ArrayList<String> chatRoomNames = new ArrayList<>();
    public static final ArrayList<String> chatRoomKeys = new ArrayList<>();
    public static String user;

    /**
     * Get information from Intent pass, create RecyclerView for messages. If there are already
     * chat rooms there, load and display. If none, display
     *                     "Nothing's here :(
     *                      Tap the Plus to start chatting!" on a grey screen
     *
     * @param savedInstanceState Saved Instance State
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_chat_room);

        user = getIntent().getStringExtra("user");

        //Something has gone very wrong if the user is signed in w/ default profile.
        if (user.equals("default")) {
            finish();
        }

        //Declare floating action button and menu
        final com.github.clans.fab.FloatingActionMenu floatingActionMenu =
                (com.github.clans.fab.FloatingActionMenu) findViewById(R.id.floatingActionsMenu);
        final com.github.clans.fab.FloatingActionButton floatingActionButton =
                (com.github.clans.fab.FloatingActionButton) findViewById(R.id.floatingActionButton);
        /**
         * Floating action button added to the floating actions menu.
         * When this button is clicked, transition to the ChatRoomCreationActivity
         *
         * Using   https://github.com/Clans/FloatingActionButton
         */
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("INTENT", "To ChatRoomCreationActivity");
                Context context = view.getContext();
                floatingActionMenu.close(true);

                //StartActivityForResult allows the app to return to this activity when finished
                //Arbitrary requestCode of 0 is arbitrary.
                Intent chatRoomCreationIntent = new Intent(context, ChatRoomCreationActivity.class);
                startActivityForResult(chatRoomCreationIntent, 0);
            }
        });

        //Sets toolbar as the app bar for the activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Set layout manager for recyclerView
        final RecyclerView threadRecyclerView = (RecyclerView) findViewById(R.id.threadRecyclerView);
        threadRecyclerView.setHasFixedSize(true);
        threadRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        //Connect chatMessagesAdapter to threadsRecyclerView
        final Thread[] tempThreadArr = new Thread[9];
        final ChatRoomAdapter chatRoomAdapter = new ChatRoomAdapter(tempThreadArr);
        threadRecyclerView.setAdapter(chatRoomAdapter);

        //Gets instance of Firebase database and establish reference as "chatRooms"
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference chatRoomsInDatabase = database.getReference("chatRooms");

        /**
         * https://firebase.google.com/docs/database/android/lists-of-data
         *
         * Attaching a ValueEventListener to a list of data will return the entire list of data as
         * a single DataSnapshot, which you can then loop over to access individual children.
         * Even when there is only a single match for the query, the snapshot is still a list;
         * it just contains a single item. To access the item, you need to loop over the result
         *
         * ---------
         * Attaching it to chatRoomsInDatabase, which already has the reference "chatRooms", so the
         * list returned in dataSnapshot is going to be a list of Message Objects.
         * ---------
         *
         * DataSnapshot {
         *      key = -Ki7pdI9OfwtzdFRpCyK, value = {
         *          name=CHAT ROOM 1,
         *          thread={
         *                  m2={message=asf, user=bb},
         *                  m1={message=hey there, user=AA}
         *          }
         *      }
         *}
         */
        chatRoomsInDatabase.addValueEventListener(new ValueEventListener() {
            /**
             * Pull all chat rooms from Firebase and then reads the database in real time.
             * @param dataSnapshot data returned from Firebase database
             */
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Relist the chatNames and chatNameKeys
                chatRoomNames.clear();
                chatRoomKeys.clear();

                Thread[] threads = new Thread[(int) dataSnapshot.getChildrenCount()];
                int currentIndex = 0;
                //Iterates through all child nodes of chatRooms and adds them to array
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    //Pull chatName and put placeholder for messages from Firebase
                    String chatName = messageSnapshot.child("name").getValue().toString();
                    String chatKey = messageSnapshot.getKey();
                    Log.d("CHATROOMNAME", chatName);
                    Log.d("CHATROOMKEY", chatKey);
                    Message[] messagesInChat = new Message[(int)messageSnapshot.getChildrenCount()];
                    threads[currentIndex] = new Thread(chatName, messagesInChat);
                    chatRoomNames.add(chatName);
                    chatRoomKeys.add(chatKey);
                    currentIndex++;
                }
                //Poke chatRoomAdapter
                chatRoomAdapter.setThreads(threads);
                chatRoomAdapter.notifyDataSetChanged();

                //Set onClickListener for each item in the threadRecyclerView.
                //Fixes the 'adding chat room without onClickListener' bug.
                threadRecyclerView.addOnItemTouchListener(
                        new RecyclerItemClickListener(getBaseContext(), new OnItemClickListener()));
            }

            @Override
            /**
             * OnCancelled post error
             */
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Error", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }



    /**
     * When activity resumes, check for successful creation of new chat and post snackbar
     * notification.
     * @param requestCode Only request code is 0.
     * @param resultCode Result code of 0 (RESULT_CANCELLED) indicates a failed chat creation.
     *                   Failed snackbar
     *                   Result code of 1 indicates a successful chat creation, in which case
     *                   chatName should be collected from the data. Success snackbar
     * @param data Contains new chatName on successful creation
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
            switch (resultCode) {
                //Failed chat creation.
                case RESULT_CANCELED:
                    Snackbar.make(coordinatorLayout, "Failed to create new chat.", Snackbar.LENGTH_SHORT).show();
                    break;
                //Successful chat creation, chatName should be collected, pop success snackbar
                case 1:
                    Bundle dataExtras = data.getExtras();
                    String chatRoomName = dataExtras.getString("chatRoomName");
                    String chatRoomKey = dataExtras.getString("chatRoomKey");
                    Snackbar.make(coordinatorLayout, "\"" + chatRoomName + "\" successfully created.", Snackbar.LENGTH_SHORT).show();
                    chatRoomNames.add(chatRoomName);
                    chatRoomKeys.add(chatRoomKey);
                    break;
                //Failed project.
                default:
                    Snackbar.make(coordinatorLayout, "Something went wrong. Help. Please.", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * On item click in recyclerView, go to chatRoomActivity. Pass along the position of the element
     * clicked in Intent.
     */
    private class OnItemClickListener extends RecyclerItemClickListener.SimpleOnItemClickListener {
        @Override
        public void onItemClick(View childView, int position) {
            Intent intent = new Intent(getBaseContext(), ChatMessageSelectionActivity.class);
            intent.putExtra("chatName", chatRoomNames.get(position));
            intent.putExtra("chatKey", chatRoomKeys.get(position));
            intent.putExtra("user", user);
            startActivity(intent);
        }
    }
}


