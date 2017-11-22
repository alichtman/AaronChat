package com.example.android.aaronchat;

/**
 * Created by AaronLichtman on 4/10/17.
 */

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import static com.example.android.aaronchat.R.drawable.ic_user;

/**
 * The ChatMessagesAdapter class allows the RecyclerView to work with Firebase.
 */
class ChatMessagesAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    //Message Adapter has a private Message Array that stores the messages.
    private static Message[] messages;
    private static Map<String, Integer> userToHexColor = new HashMap<>();

    ChatMessagesAdapter(Message[] messages) {
        ChatMessagesAdapter.messages = messages;
    }

    void setMessages(Message[] messages) {
        ChatMessagesAdapter.messages = messages;
    }

    /**
     * This function is called only enough times to cover the screen with views.  After
     * that point, it recycles the views when scrolling is done.
     *
     * @param parent   the parent object being drawn from (our RecyclerView)
     * @param viewType unused in our function (enables having different kinds of views in the same RecyclerView)
     * @return the new MessageViewHolder we allocate
     */
    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater converts: layout XML resource --> a View object.
        return new MessageViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.message_item, parent, false));
    }

    /**
     * This function gets called each time a MessageViewHolder needs to hold data for a different
     * position in the list.  We don't need to create any views (because we're recycling), but
     * we do need to update the contents in the views.
     *
     * @param holder   the MessageViewHolder that knows about the Views we need to update
     * @param position the index into the array of Messages
     */
    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        //Set message of the holder from the array of previously sent messages.
        holder.setMessage(messages[position]);

        Message message = holder.getMessage();

        //If the message isn't null
        if (isNonNullMessage(message)) {

            String messageUsername = messages[position].getUser();

            /**
             * Look up user in the userToHexColor Map to see if the user is already associated with
             * a color, if not, assign this user the next color.
             */
            //No established user -> color maps yet
            if (userToHexColor.size() == 0) {
                userToHexColor.put(messageUsername, 0);
            }
            //At least one user in the map, if user isn't already matched, add to the map
            else if (!userToHexColor.containsKey(messageUsername) && userToHexColor.size() < 6) {
                userToHexColor.put(messageUsername, userToHexColor.size());
            }
            else {
                Log.d("ERR","Insufficient Color Resources");
            }

            //Update holder textViews
            holder.getUsernameTextView().setText(message.getUser());
            holder.getMessageTextView().setText(message.getMessage());

            //Load image into view with Picasso
            ImageView profileImageView = holder.getProfilePictureImageView();
            Context context = profileImageView.getContext();
            //Resize to fit, maintaining aspect ratio
            Picasso.with(context).load(ic_user).fit().centerInside().into(profileImageView);

            //Change background color
            switch (userToHexColor.get(message.getUser())) {
                case 0:
                    holder.getConstraintLayout().setBackgroundColor(ContextCompat.getColor(context, R.color.user0));
                    break;
                case 1:
                    holder.getConstraintLayout().setBackgroundColor(ContextCompat.getColor(context, R.color.user1));
                    break;
                case 2:
                    holder.getConstraintLayout().setBackgroundColor(ContextCompat.getColor(context, R.color.user2));
                    break;
                case 3:
                    holder.getConstraintLayout().setBackgroundColor(ContextCompat.getColor(context, R.color.user3));
                    break;
                case 4:
                    holder.getConstraintLayout().setBackgroundColor(ContextCompat.getColor(context, R.color.user4));
                    break;
                case 5:
                    holder.getConstraintLayout().setBackgroundColor(ContextCompat.getColor(context, R.color.user5));
                    break;
                default:
                    Log.d("ERR","Change Background Color");
            }
        }
    }


    /**
     * Returns false if a message contains null for any of its properties
     *
     * @param message Message object
     * @return true if a message has all non-null properties, false otherwise.
     * @throws NullPointerException Possible that the message contains null references, so return
     *                              false in that case.
     */
    private boolean isNonNullMessage(Message message) throws NullPointerException {
        try {
            if (message.getUser() != null && message.getMessage() != null) {
                return true;
            } else return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     * RecyclerView wants to know how many list items there are, so it knows when it gets to the
     * end of the array and should stop.
     *
     * @return the number of messages in the array.
     */
    @Override
    public int getItemCount() {
        if (messages == null) {
            return 0;
        }
        return messages.length;
    }
}

