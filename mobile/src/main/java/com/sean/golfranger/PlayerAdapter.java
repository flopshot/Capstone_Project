package com.sean.golfranger;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sean.golfranger.data.Contract;
import com.sean.golfranger.utils.AnimateUtils;

import java.util.Set;

/**
 * Player Adapter For Player Activity Layout. Will be fed Data from Player Table
 */
class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerAdapterViewHolder> {
    private Cursor mCursor;
    private Context mContext;
    final private PlayerAdapterOnClickHandler mClickHandler;
    final private PlayerAdapterEditClickHandler mEditClickHandler;
    private Boolean doAnimation;
    private Set<String> mNewIds = null;

    PlayerAdapter(Context context,
          Boolean animationBoolean,
          PlayerAdapterOnClickHandler clickHandler,
          PlayerAdapterEditClickHandler editClickHandler) {
        this.mContext = context;
        this.doAnimation = animationBoolean;
        this.mClickHandler = clickHandler;
        this.mEditClickHandler = editClickHandler;
    }

    @Override
    public PlayerAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.player_list_item, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new PlayerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlayerAdapterViewHolder customViewHolder, int i) {
        mCursor.moveToPosition(i);
        customViewHolder.firstNameView.setText(mCursor.getString(Contract.Players.PLAYERFIRST_POS));
        customViewHolder.lastNameView.setText(mCursor.getString(Contract.Players.PLAYERLAST_POS));

        AnimateUtils.runEnterAnimation(mContext, customViewHolder.itemView, doAnimation,
              AnimateUtils.PLAYER_TYPE, i, mCursor.getString(Contract.Players.PLAYERID_POS), mNewIds);

        doAnimation = doAnimation && mCursor.getCount() != i + 1;
    }

    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
    }

    class PlayerAdapterViewHolder extends RecyclerView.ViewHolder
          implements View.OnClickListener{
        TextView firstNameView;
        TextView lastNameView;
        ImageButton editButton;

        PlayerAdapterViewHolder(View view) {
            super(view);
            this.firstNameView = (TextView) view.findViewById(R.id.firstNameLabel);
            this.lastNameView = (TextView) view.findViewById(R.id.lastNameLabel);
            this.editButton = (ImageButton) view.findViewById(R.id.editButton);
            view.setOnClickListener(this);
            editButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == editButton.getId()) {
                int adapterPosition = getAdapterPosition();
                mCursor.moveToPosition(adapterPosition);
                long playerId = mCursor.getLong(Contract.Players.PLAYERID_POS);
                String firstName = mCursor.getString(Contract.Players.PLAYERFIRST_POS);
                String lastName = mCursor.getString(Contract.Players.PLAYERLAST_POS);
                mEditClickHandler.onEditClick(playerId, firstName, lastName);
            } else {
                int adapterPosition = getAdapterPosition();
                mCursor.moveToPosition(adapterPosition);
                long playerId = mCursor.getLong(Contract.Players.PLAYERID_POS);
                mClickHandler.onClick(playerId);
            }
        }
    }

    interface PlayerAdapterOnClickHandler {
        void onClick(Long courseId);
    }

    interface PlayerAdapterEditClickHandler {
        void onEditClick(Long courseId, String firstName, String lastName);
    }

    void swapCursor(Cursor newCursor) {
        mNewIds = AnimateUtils.newIdsFromCursor(mContext, newCursor);

        mCursor = newCursor;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        if (mCursor.moveToPosition(position)) {
            return mCursor.getLong(Contract.Players.PLAYERID_POS);
        } else {
            return -1;
        }
    }

    public Cursor getCursor() {
        return mCursor;
    }

//    private void runEnterAnimation(View view, int position, String id) {
//        int maxItems = ScreenUtils.getMaxNumListItems(mContext, 0);
//
//        Timber.d("Do Animation: " + doAnimation + " position: " + position + " maxItems: " + maxItems);
//        if ((doAnimation && position <= maxItems) | mNewIds.contains(id)) {
//            view.setTranslationX(ScreenUtils.getScreenWidth());
//            view.animate()
//                  .translationX(0)
//                  .setInterpolator(new DecelerateInterpolator(3.f))
//                  .setDuration(1400)
//                  .setStartDelay(position * 200)
//                  .start();
//
//            if (position == maxItems) {
//                doAnimation = false;
//            }
//        }
//    }
//
//    private Set<Long> newIdsFromCursor(Cursor oldCursor, Cursor newCursor) {
//        Set<Long> oldIds = new HashSet<>();
//        Set<Long> newIds = new HashSet<>();
//        int newCursorCount;
//        int oldCursorCount;
//
//        try {newCursorCount = newCursor.getCount();} catch (NullPointerException e) {return newIds;}
//        try {oldCursorCount = oldCursor.getCount();} catch (NullPointerException e) {oldCursorCount = 0;}
//
//        if (oldCursorCount < newCursorCount) {
//            if (oldCursor != null && oldCursor.moveToFirst()) {
//                do {
//                    oldIds.add(oldCursor.getLong(Contract.Players.PLAYERID_POS));
//                } while (oldCursor.moveToNext());
//            }
//
//            if (newCursor.moveToFirst()) {
//                do {
//                    newIds.add(newCursor.getLong(Contract.Players.PLAYERID_POS));
//                } while (newCursor.moveToNext());
//            }
//
//            newIds.removeAll(oldIds);
//        }
//        return newIds;
//    }
}
// Get Max number of items possible on screen (n)
// Animate the first n items and trigger a global boolean to set to false after 1st animation
// Define a global hash set of ids
// in swapCursor, check if new ids have been added to cursor
// Check new ids and animate if new ids exist
// Scroll To Bottom in onLoadFinished to show newly animated item