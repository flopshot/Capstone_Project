package com.sean.golfranger;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sean.golfranger.data.Contract;

/**
 * Course Adapter For Course Activity Layout. Will be fed Data from Course Table
 */

class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseAdapterViewHolder> {
    private Cursor mCursor;
    final private CourseAdapterOnClickHandler mClickHandler;
    final private CourseAdapterLongClickHandler mLongClickHandler;

    CourseAdapter(
          CourseAdapterOnClickHandler clickHandler,
          CourseAdapterLongClickHandler longClickHandler) {
        this.mClickHandler = clickHandler;
        this.mLongClickHandler = longClickHandler;
    }

    @Override
    public CourseAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.course_list_item, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new CourseAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CourseAdapterViewHolder customViewHolder, int i) {
        mCursor.moveToPosition(i);
        customViewHolder.courseNameView.setText(
              mCursor.getString(Contract.Courses.COURSENAME_POS
        ));
        customViewHolder.clubNameView.setText(
              mCursor.getString(Contract.Courses.CLUBNAME_POS
        ));
    }

    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
    }

    class CourseAdapterViewHolder extends RecyclerView.ViewHolder
          implements View.OnClickListener, View.OnLongClickListener{
        TextView clubNameView;
        TextView courseNameView;

        CourseAdapterViewHolder(View view) {
            super(view);
            this.clubNameView = (TextView) view.findViewById(R.id.clubNameLabel);
            this.courseNameView = (TextView) view.findViewById(R.id.courseNameLabel);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long courseId = mCursor.getLong(Contract.Courses.COURSEID_POS);
            String courseName = mCursor.getString(Contract.Courses.COURSENAME_POS);
            String clubName = mCursor.getString(Contract.Courses.CLUBNAME_POS);
            mClickHandler.onClick(courseId, courseName, clubName);
        }

        @Override
        public boolean onLongClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long courseId = mCursor.getLong(Contract.Courses.COURSEID_POS);
            String courseName = mCursor.getString(Contract.Courses.COURSENAME_POS);
            String clubName = mCursor.getString(Contract.Courses.CLUBNAME_POS);
            mLongClickHandler.onLongClick(courseId, courseName, clubName);
            return true;
        }
    }

    interface CourseAdapterOnClickHandler {
        void onClick(Long courseId, String clubName, String courseName);
    }

    interface CourseAdapterLongClickHandler {
        void onLongClick(Long courseId, String clubName, String courseName);
    }

    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return mCursor;
    }
}