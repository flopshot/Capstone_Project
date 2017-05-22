package com.sean.golfranger;


import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sean.golfranger.data.Contract;


/**
 * Player Adapter For Player Activity Layout. Will be fed Data from Player Table
 */

class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerAdapterViewHolder>{
    private Cursor mCursor;
    final private PlayerAdapterOnClickHandler mClickHandler;
    final private PlayerAdapterEditClickHandler mLongClickHandler;

    PlayerAdapter(
          PlayerAdapterOnClickHandler clickHandler,
          PlayerAdapterEditClickHandler longClickHandler) {
        this.mClickHandler = clickHandler;
        this.mLongClickHandler = longClickHandler;
    }

    @Override
    public PlayerAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.player_list_item1, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new PlayerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlayerAdapterViewHolder customViewHolder, int i) {
        mCursor.moveToPosition(i);
        customViewHolder.firstNameView.setText(mCursor.getString(Contract.Players.PLAYERFIRST_POS));
        customViewHolder.lastNameView.setText(mCursor.getString(Contract.Players.PLAYERLAST_POS));
    }

    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
    }

    class PlayerAdapterViewHolder extends RecyclerView.ViewHolder
          implements View.OnClickListener, View.OnLongClickListener{
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
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == editButton.getId()) {
                int adapterPosition = getAdapterPosition();
                mCursor.moveToPosition(adapterPosition);
                long playerId = mCursor.getLong(Contract.Players.PLAYERID_POS);
                String firstName = mCursor.getString(Contract.Players.PLAYERFIRST_POS);
                String lastName = mCursor.getString(Contract.Players.PLAYERLAST_POS);
                mLongClickHandler.onEditClick(playerId, firstName, lastName);
            } else {
                int adapterPosition = getAdapterPosition();
                mCursor.moveToPosition(adapterPosition);
                long playerId = mCursor.getLong(Contract.Players.PLAYERID_POS);
                mClickHandler.onClick(playerId);
            }
        }

        @Override
        public boolean onLongClick(View view) {

            return true;
        }
    }

    interface PlayerAdapterOnClickHandler {
        void onClick(Long courseId);
    }

    interface PlayerAdapterEditClickHandler {
        void onEditClick(Long courseId, String firstName, String lastName);
    }

    void swapCursor(Cursor newCursor) {
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
}
