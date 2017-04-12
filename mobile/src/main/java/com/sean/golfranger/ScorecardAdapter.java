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
 * Scorecard Adapter For Scorecard Fragment Layout. Will be fed Data from Holes Table
 */
class ScorecardAdapter
      extends RecyclerView.Adapter<ScorecardAdapter.ScorecardAdapterViewHolder> {
    private Cursor mCursor;
    private Context mContext;

    ScorecardAdapter(Context context) {
        mContext = context;
    }

    class ScorecardAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView holeNumber, parNumber, p1HoleScore, p2HoleScore, p3HoleScore, p4HoleScore;

        ScorecardAdapterViewHolder(View view) {
            super(view);
            this.holeNumber = (TextView) view.findViewById(R.id.holeNumber);
            this.parNumber = (TextView) view.findViewById(R.id.parNumber);
            this.p1HoleScore = (TextView) view.findViewById(R.id.p1HoleScore);
            this.p2HoleScore = (TextView) view.findViewById(R.id.p2HoleScore);
            this.p3HoleScore = (TextView) view.findViewById(R.id.p3HoleScore);
            this.p4HoleScore = (TextView) view.findViewById(R.id.p4HoleScore);
        }
    }

    @Override
    public ScorecardAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater
              .from(viewGroup.getContext())
              .inflate(R.layout.fragment_scorecard_item, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
              ViewGroup.LayoutParams.WRAP_CONTENT,
              ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new ScorecardAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ScorecardAdapterViewHolder customViewHolder, int i) {
        mCursor.moveToPosition(i);
        customViewHolder.holeNumber.setText(
              mCursor.getString(
                    mCursor.getColumnIndex(
                          Contract.Holes.HOLE_NUMBER
                    )
              ));
        customViewHolder.parNumber.setText(
              mCursor.getString(
                    mCursor.getColumnIndex(
                          Contract.Holes.HOLE_PAR
                    )
              ));
        customViewHolder.p1HoleScore.setText(
              mCursor.getString(
                    mCursor.getColumnIndex(
                          Contract.Holes.P1_SCORE
                    )
              ));
        customViewHolder.p2HoleScore.setText(
              mCursor.getString(
                    mCursor.getColumnIndex(
                          Contract.Holes.P2_SCORE
                    )
              ));
        customViewHolder.p3HoleScore.setText(
              mCursor.getString(
                    mCursor.getColumnIndex(
                          Contract.Holes.P3_SCORE
                    )
              ));
        customViewHolder.p4HoleScore.setText(
              mCursor.getString(
                    mCursor.getColumnIndex(
                        Contract.Holes.P4_SCORE
                    )
              ));
    }

    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
    }

    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return mCursor;
    }
}
