package com.example.android.aaronchat;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.example.android.aaronchat", appContext.getPackageName());
    }

    /**
     * Test message submission by pushing and then searching for the message in DataSnapshot
     */
    public void testPushToAndRetrievalFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("thread");

        final boolean[] successfulPush = {false};
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Message expectedMessage = new Message("AAAATESTUSER", "TESTMESSAGE");
                //Iterates through all previous Message nodes
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    String username = (String) messageSnapshot.child("user").getValue();
                    String message = (String) messageSnapshot.child("message").getValue();
                    Message currentMessage = new Message(username,message);

                    if (currentMessage.equals(expectedMessage)) {
                        successfulPush[0] = true;
                    }
                }
                if (!testRetrieveFromFirebase(dataSnapshot)) {
                    successfulPush[0] = false;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Expect one added child from original
        databaseReference.child("TEST MESSAGE").setValue(new Message("AAAATESTUSER", "TESTMESSAGE"));
        Assert.assertTrue(successfulPush[0]);


    }


    /**
     * Test message retrieval.
     * <p>
     * The last two messages pushed to the database were
     * ("AAAATESTUSER","TESTMESSAGE")
     * ("AAAATESTUSER2","TESTMESSAGE2")
     * <p>
     * So, if we order by user, these should be the first two messages processed.
     * Assert the user and message fields.
     *
     * @param lastSnapshot snapshot
     * @return boolean true if pass
     */
    public boolean testRetrieveFromFirebase(DataSnapshot lastSnapshot) {
        boolean hasMessage1 = false;

        for (DataSnapshot message : lastSnapshot.getChildren()) {
            if (message.child("user").equals("AAAATESTUSER") &&
                    message.child("message").equals("TESTMESSAGE")) {
                hasMessage1 = true;
            }
        }

        if (hasMessage1) {
            return true;
        }
        else return false;
    }
}
