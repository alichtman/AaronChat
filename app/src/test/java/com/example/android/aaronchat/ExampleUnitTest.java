package com.example.android.aaronchat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    private void testFirebase() throws Exception {
        //Set up database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("thread");

        //Test push functionality
        DataSnapshot data = testPushToFirebase(databaseReference);

        //Test retrieval functionality
        testRetrieveFromFirebase(data);
    }


    /**
     * Test message retrieval.
     *
     * The last two messages pushed to the database were
     *          ("AAAATESTUSER","TESTMESSAGE")
     *          ("AAAATESTUSER2","TESTMESSAGE2")
     *
     * So, if we order by user, these should be the first two messages processed.
     * Assert the user and message fields.
     * @param lastSnapshot snapshot
     */
    private void testRetrieveFromFirebase(DataSnapshot lastSnapshot) {
        boolean hasMessage1 = false;
        boolean hasMessage2 = false;

        for (DataSnapshot message : lastSnapshot.getChildren()) {
            if (message.child("user").equals("AAAATESTUSER") &&
                    message.child("message").equals("TESTMESSAGE")) {
                hasMessage1 = true;
            }

            if (message.child("user").equals("AAAATESTUSER2") &&
                    message.child("message").equals("TESTMESSAGE2")) {
                hasMessage1 = true;
            }
        }

        Assert.assertTrue(hasMessage1 && hasMessage2);
    }

    /**
     * Test message submission by incrementing messageCount on addChildEvent in the database
     * @param databaseReference Database Reference to node "thread"
     */
    private DataSnapshot testPushToFirebase(DatabaseReference databaseReference) {
        final int[] messageCount = {0};

        final DataSnapshot[] data = new DataSnapshot[1];

        //Push new message
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Message newMessage = dataSnapshot.getValue(Message.class);
                System.out.println("User: " + newMessage.getUser());
                System.out.println("Message: " + newMessage.getMessage());
                messageCount[0]++;

                data[0] = dataSnapshot;
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        //Expect one added child from original
        databaseReference.push().setValue(new Message("AAAATESTUSER","TESTMESSAGE"));
        int[] expected = {1};
        Assert.assertArrayEquals(expected, messageCount);

        //Expect two added children from original
        databaseReference.push().setValue(new Message("AAAATESTUSER2","TESTMESSAGE2"));
        expected[0]++;
        Assert.assertArrayEquals(expected, messageCount);

        return data[0];
    }
}