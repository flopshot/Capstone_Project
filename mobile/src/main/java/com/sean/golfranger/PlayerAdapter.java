package com.sean.golfranger;


import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sean.golfranger.data.Contract;


/**
 * Player Adapter For Player Activity Layout. Will be fed Data from Player Table
 */

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerAdapterViewHolder>{
    private Cursor mCursor;
    private Context mContext;
    final private PlayerAdapterOnClickHandler mClickHandler;
    final private PlayerAdapterLongClickHandler mLongClickHandler;

    public PlayerAdapter(
          Context context,
          PlayerAdapterOnClickHandler clickHandler,
          PlayerAdapterLongClickHandler longClickHandler) {
        this.mContext = context;
        this.mClickHandler = clickHandler;
        this.mLongClickHandler = longClickHandler;
    }

    @Override
    public PlayerAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.player_list_item, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        PlayerAdapterViewHolder viewHolder = new PlayerAdapterViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PlayerAdapterViewHolder customViewHolder, int i) {
        mCursor.moveToPosition(i);
        customViewHolder.firstNameView.setText(
              mCursor.getString(Contract.PlayerColumnPosition.FIRST_NAME
              ));
        customViewHolder.lastNameView.setText(
              mCursor.getString(Contract.PlayerColumnPosition.LAST_NAME
              ));
    }

    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
    }

    class PlayerAdapterViewHolder extends RecyclerView.ViewHolder
          implements View.OnClickListener, View.OnLongClickListener{
        protected TextView firstNameView;
        protected TextView lastNameView;

        public PlayerAdapterViewHolder(View view) {
            super(view);
            this.firstNameView = (TextView) view.findViewById(R.id.firstNameLabel);
            this.lastNameView = (TextView) view.findViewById(R.id.lastNameLabel);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long playerId = mCursor.getLong(Contract.PlayerColumnPosition.ID);
            String firstName = mCursor.getString(Contract.PlayerColumnPosition.FIRST_NAME);
            String lastName = mCursor.getString(Contract.PlayerColumnPosition.LAST_NAME);
            mClickHandler.onClick(playerId, firstName, lastName);
        }

        @Override
        public boolean onLongClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long playerId = mCursor.getLong(Contract.PlayerColumnPosition.ID);
            String firstName = mCursor.getString(Contract.PlayerColumnPosition.FIRST_NAME);
            String lastName = mCursor.getString(Contract.PlayerColumnPosition.LAST_NAME);
            mLongClickHandler.onLongClick(playerId, firstName, lastName);
            return true;
        }
    }

    public static interface PlayerAdapterOnClickHandler {
        void onClick(Long courseId, String firstName, String lastName);
    }

    public static interface PlayerAdapterLongClickHandler {
        void onLongClick(Long courseId, String firstName, String lastName);
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return mCursor;
    }
}
