package com.sean.golfranger;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sean.golfranger.data.Contract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Match Adapter For Matches Activity Layout. Will be fed Data from Rounds Table
 */

public class MatchAdapter extends  RecyclerView.Adapter<MatchAdapter.MatchAdapterViewHolder>{
    private Cursor mCursor;
    private Context mContext;

    public MatchAdapter(Context context) {
          this.mContext = context;
    }

    @Override
    public MatchAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.match_list_item, null);
        MatchAdapterViewHolder viewHolder = new MatchAdapterViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
    }

    class MatchAdapterViewHolder extends RecyclerView.ViewHolder
          implements View.OnClickListener, View.OnLongClickListener{
        TextView p1First, p2First, p3First, p4First, courseName, clubName, date;

        public MatchAdapterViewHolder(View view) {
            super(view);
            this.p1First = (TextView) view.findViewById(R.id.p1First);
            this.p2First = (TextView) view.findViewById(R.id.p2First);
            this.p3First = (TextView) view.findViewById(R.id.p3First);
            this.p4First = (TextView) view.findViewById(R.id.p4First);
            this.courseName = (TextView) view.findViewById(R.id.courseName);
            this.clubName = (TextView) view.findViewById(R.id.clubName);
            this.date = (TextView) view.findViewById(R.id.roundDate);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long roundId = mCursor.getLong(Contract.RoundColumnPosition.ID);
            Intent intent = new Intent(mContext, StartRoundActivity.class);
            intent.putExtra(StartRoundActivity.EXTRA_ROUND_ID, String.valueOf(roundId));
            mContext.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long roundId = mCursor.getLong(Contract.RoundColumnPosition.ID);

            ContentResolver resolver = mContext.getContentResolver();
            resolver.delete(
                  Contract.Rounds.buildDirUri(),
                  Contract.Rounds._ID + "=?",
                  new String[]{String.valueOf(roundId)});
            return true;
        }
    }

    @Override
    public void onBindViewHolder(MatchAdapterViewHolder customViewHolder, int i) {
        mCursor.moveToPosition(i);
        customViewHolder.p1First.setText(
              mCursor.getString(Contract.RoundColumnPosition.P1_FIRST_NAME
              ));
        customViewHolder.p2First.setText(
              mCursor.getString(Contract.RoundColumnPosition.P2_FIRST_NAME
              ));
        customViewHolder.p3First.setText(
              mCursor.getString(Contract.RoundColumnPosition.P3_FIRST_NAME
              ));
        customViewHolder.p4First.setText(
              mCursor.getString(Contract.RoundColumnPosition.P4_FIRST_NAME
              ));
        customViewHolder.clubName.setText(
              mCursor.getString(Contract.RoundColumnPosition.CLUB_NAME
              ));
        customViewHolder.courseName.setText(
              mCursor.getString(Contract.RoundColumnPosition.COURSE_NAME
              ));
        customViewHolder.date.setText(
              getReadableDate(
                mCursor.getLong(Contract.RoundColumnPosition.DATE
                    )));

    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return mCursor;
    }

    private String getReadableDate(long unixtime) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy", Locale.getDefault());

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(unixtime);
        return formatter.format(calendar.getTime());
    }
}
