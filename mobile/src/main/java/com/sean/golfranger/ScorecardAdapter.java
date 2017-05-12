package com.sean.golfranger;

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
              mCursor.getString(Contract.ScorecardView.HOLENUMBER_POS)
              );
        customViewHolder.parNumber.setText(
              nullOutZeroScore(mCursor.getString(Contract.ScorecardView.HOLEPAR_POS))
        );
        customViewHolder.p1HoleScore.setText(
              nullOutZeroScore(mCursor.getString(Contract.ScorecardView.P1SCORE_POS))
        );
        customViewHolder.p2HoleScore.setText(
              nullOutZeroScore(mCursor.getString(Contract.ScorecardView.P2SCORE_POS))
        );
        customViewHolder.p3HoleScore.setText(
              nullOutZeroScore(mCursor.getString(Contract.ScorecardView.P3SCORE_POS))
        );
        customViewHolder.p4HoleScore.setText(
              nullOutZeroScore(mCursor.getString(Contract.ScorecardView.P4SCORE_POS))
        );
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

    private String nullOutZeroScore(String score) {
        if (score != null && !score.equals("0")) {
            return score;
        } else {
            return null;
        }
    }
}
