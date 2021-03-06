package com.example.android.aaronchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Code based off https://github.com/firebase/quickstart-android/blob/master/auth/app/src
 *
 *
 * Incorporated firebase authentication in Google Authentication
 * https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md
 */
public class GoogleFirebaseAuth extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private static String username = "default";

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private GoogleApiClient googleApiClient;
    private TextView statusTextView;
    private TextView detailTextView;

    /**
     * On create, set the layout to that of activity_sign_in, set listeners for the
     * buttons that can be clicked, configure GoogleFirebaseAuth to request the user information, and
     * make the sign in button look pretty.
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        statusTextView = (TextView) findViewById(R.id.status);
        detailTextView = (TextView) findViewById(R.id.detail);
        TextView developedByTextView = (TextView) findViewById(R.id.developedBy);
        developedByTextView.setText(R.string.developerCredentials);

        auth = FirebaseAuth.getInstance();

        //Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.chat_room_selection_button).setOnClickListener(this);

        // ---- CONFIGURE SIGN IN -----
        //Configure sign-in to request the user's ID, email address, and basic
        //profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(GoogleFirebaseAuth.this.getResources().getString(R.string.server_client_id))
                .requestEmail()
                .build();

        // ---- BUILD CLIENT FOR CONNECTION TO GOOGLE SIGN IN API ----
        //Build a GoogleApiClient with access to the Google Sign-In API and the
        //options specified by gso.
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /*FragmentActivity*/, this /*OnConnectionFailedListener*/)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //Set dimensions of the sign-in button.
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        //Listener that responds to changes in user's sign-in state
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("FirebaseAuth", "onAuthStateChanged:signed_in:" + user.getUid());
                    username = user.getDisplayName();
                    Log.d("Username", username);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                updateUI(user);
            }
        };
    }

    /**
     * GoogleFirebaseAuth method. Adds AuthStateListener to FirebaseAuth object.
     */
    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    /**
     * GoogleFirebaseAuth method. Removes AuthStateListener from FirebaseAuth object.
     */
    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    /**
     * After a user successfully signs in, get an ID token from the GoogleSignInAccount object,
     * exchange it for a Firebase credential, and authenticate with Firebase using the
     * Firebase credential
     * @param acct Google account details
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "FirebaseAuthWithGoogle:" + acct.getId());

        username = acct.getDisplayName();
        statusTextView.setText(getString(R.string.signed_in_fmt, username));

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(GoogleFirebaseAuth.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * If result returned from GoogleSignInApi.getSignInIntent(...) is valid, authenticate.
     * @param requestCode request integer for sign in request
     * @param resultCode result integer for sign in request
     * @param data intent pass
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                updateUI(null);
            }
        }
    }

    /**
     * Sign out process.
     */
    private void signOut() {
        // Firebase sign out
        auth.signOut();
        username = null;

        // Google sign out
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateUI(null);
                    }
                });
    }

    /**
     * Sign out of Firebase and revoke access from GoogleSignInApi so Cached Google
     * Users aren't auto-signed in.
     */
    private void revokeAccess() {
        // Firebase sign out
        auth.signOut();

        // Google revoke access
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateUI(null);
                    }
                });
    }

    /**
     * If connection fails, log the failure.
     * @param connectionResult Bundle of information related to the connection
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Unresolvable error occurred and Google APIs (including Sign-In) will not be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Updates the UI to one of two states, dependent on whether the user is signed in or not.
     * @param user Firebase user
     */
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            statusTextView.setText(getString(R.string.google_status_fmt, user.getEmail()));
            detailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_connect).setVisibility(View.VISIBLE);

        } else {
            statusTextView.setText(R.string.signed_out);
            detailTextView.setText(null);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_connect).setVisibility(View.GONE);
        }
    }

    /**
     * OnClick method for the sign in button, sign out button and connect button.
     * @param v View
     */
    @Override
    public void onClick(View v) {
        int i = v.getId();
        //Sign In
        if (i == R.id.sign_in_button) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else if (i == R.id.sign_out_button) {
            signOut();
            revokeAccess();
        } else if (i == R.id.chat_room_selection_button) {
            transferToChatRoomSelectionActivity();
        }
    }

    /**
     * Connect user to chat room selection Activity. Pass username in Intent to ChatMessageSelectionActivity.
     */
    private void transferToChatRoomSelectionActivity() {
        //Intent constructor's parameters are --> (Context of origin, Activity to go to)
        Intent intent = new Intent(this, ChatRoomSelectionActivity.class);
        intent.putExtra("user", username);
        startActivity(intent);
    }

}