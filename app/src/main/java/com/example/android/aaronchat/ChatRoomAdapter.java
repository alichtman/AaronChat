package com.example.android.aaronchat;

/**
 * Created by AaronLichtman on 4/10/17.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;


/**
 * The ChatRoomAdapter class allows the RecyclerView to work with Firebase, keeping track of the
 * available chat rooms.
 */
class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomViewHolder> {

    //Chat Room Adapter has a private Thread Array that stores the threads.
    private static Thread[] threads;

    ChatRoomAdapter(Thread[] threads) {
        ChatRoomAdapter.threads = threads;
    }

    void setThreads(Thread[] threads) {
        ChatRoomAdapter.threads = threads;
    }

    /**
     * This function is called only enough times to cover the screen with views.  After
     * that point, it recycles the views when scrolling is done.
     *
     * @param parent   the parent object being drawn from (our RecyclerView)
     * @param viewType unused in our function (enables having different kinds of views in the
     *                 same RecyclerView)
     * @return the new MessageViewHolder we allocate
     */
    @Override
    public ChatRoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater converts: layout XML resource --> a View object.
        ChatRoomViewHolder chatRoomViewHolder =
                new ChatRoomViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.chat_room_item, parent, false));
        return chatRoomViewHolder;
    }

    /**
     * This function gets called each time a ChatRoomViewHolder needs to hold data for a different
     * position in the list.  We don't need to create any views (because we're recycling), but
     * we do need to update the contents in the views.
     *
     * @param holder   the ChatRoomViewHolder that knows about the Views we need to update
     * @param position the index into the array of Messages
     */
    @Override
    public void onBindViewHolder(ChatRoomViewHolder holder, int position) {
        //Set thread of the holder from the array of threads.
        holder.setThread(threads[position]);
        Thread thread = holder.getThread();
        //If the thread isn't null
        if (isNonNullThread(thread)) {
            //Update chat room names
            String threadName = thread.getName();
            holder.getChatRoomTextView().setText(threadName);
        }
    }


    /**
     * Returns false if a thread contains null for its name or has no messages
     *
     * @param thread Message object
     * @return true if a thread has all non-null properties, false otherwise.
     * @throws NullPointerException Possible that the thread contains null references, so return
     *                              false in that case.
     */
    private boolean isNonNullThread(Thread thread) throws NullPointerException {
        try {
            if (thread.getMessages().length != 0 && thread.getName() != null) {
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
        if (threads == null) {
            return 0;
        }
        return threads.length;
    }
}

