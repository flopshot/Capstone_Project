package com.sean.golfranger;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sean.golfranger.data.Contract;
import com.sean.golfranger.utils.AnimateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Set;

/**
 * Match Adapter For Matches Activity Layout. Will be fed Data from Rounds Table
 */

class MatchAdapter extends  RecyclerView.Adapter<MatchAdapter.MatchAdapterViewHolder>{
    private Cursor mCursor;
    private Context mContext;
    private Boolean doAnimation;
    private Set<String> mNewIds = null;

    MatchAdapter(Context context,Boolean animationBoolean) {
          this.mContext = context;
          this.doAnimation = animationBoolean;
    }

    @Override
    public MatchAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.match_list_item, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new MatchAdapterViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
    }

    class MatchAdapterViewHolder extends RecyclerView.ViewHolder
          implements View.OnClickListener{
        TextView p1First, p2First, p3First, p4First, courseName, clubName, date;

        MatchAdapterViewHolder(View view) {
            super(view);
            this.p1First = (TextView) view.findViewById(R.id.p1First);
            this.p2First = (TextView) view.findViewById(R.id.p2First);
            this.p3First = (TextView) view.findViewById(R.id.p3First);
            this.p4First = (TextView) view.findViewById(R.id.p4First);
            this.courseName = (TextView) view.findViewById(R.id.courseName);
            this.clubName = (TextView) view.findViewById(R.id.clubName);
            this.date = (TextView) view.findViewById(R.id.roundDate);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long roundId = mCursor.getLong(Contract.MatchesView.ROUNDID_COL_INDEX);
            Intent intent = new Intent(mContext, StartRoundActivity.class);
            intent.putExtra(StartRoundActivity.EXTRA_ROUND_ID, String.valueOf(roundId));
            mContext.startActivity(intent);
        }
    }

    @Override
    public void onBindViewHolder(MatchAdapterViewHolder customViewHolder, int i) {
        mCursor.moveToPosition(i);
        customViewHolder.p1First.setText(
              mCursor.getString(Contract.MatchesView.P1_FIRST_COL_INDEX
              ));
        customViewHolder.p2First.setText(
              mCursor.getString(Contract.MatchesView.P2_FIRST_COL_INDEX
              ));
        customViewHolder.p3First.setText(
              mCursor.getString(Contract.MatchesView.P3_FIRST_COL_INDEX
              ));
        customViewHolder.p4First.setText(
              mCursor.getString(Contract.MatchesView.P4_FIRST_COL_INDEX
              ));
        customViewHolder.clubName.setText(
              mCursor.getString(Contract.MatchesView.CLUBNAME_COL_INDEX
              ));
        customViewHolder.courseName.setText(
              mCursor.getString(Contract.MatchesView.COURSENAME_COL_INDEX
              ));
        customViewHolder.date.setText(
              getReadableDate(
                mCursor.getLong(Contract.MatchesView.START_DATE
              )));

        AnimateUtils.runEnterAnimation(mContext, customViewHolder.itemView, doAnimation,
              AnimateUtils.MATCH_TYPE, i, mCursor.getString(Contract.MatchesView.ROUNDID_COL_INDEX), mNewIds);

        doAnimation = doAnimation && mCursor.getCount() != i + 1;
    }

    void swapCursor(Cursor newCursor) {
        mNewIds = AnimateUtils.newIdsFromCursor(mContext, newCursor);

        mCursor = newCursor;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        if (mCursor.moveToPosition(position)) {
            return mCursor.getLong(Contract.MatchesView.ROUNDID_COL_INDEX);
        } else {
            return -1;
        }
    }

    public Cursor getCursor() {
        return mCursor;
    }

    private String getReadableDate(long unixtime) {
        SimpleDateFormat formatter =
              new SimpleDateFormat(mContext.getString(R.string.dateFormat), Locale.getDefault());

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(unixtime);
        return formatter.format(calendar.getTime());
    }
}
