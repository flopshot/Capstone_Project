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
 * Course Adapter For Course Activity Layout. Will be fed Data from Course Table
 */

class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseAdapterViewHolder> {
    private Cursor mCursor;
    private Context mContext;
    final private CourseAdapterOnClickHandler mClickHandler;
    final private CourseAdapterEditClickHandler mEditClickHandler;
    private Boolean doAnimation;
    private Set<String> mNewIds = null;

    CourseAdapter(Context context,
                  Boolean animationBoolean,
          CourseAdapterOnClickHandler clickHandler,
          CourseAdapterEditClickHandler editClickHandler) {
        this.mContext = context;
        this.doAnimation = animationBoolean;
        this.mClickHandler = clickHandler;
        this.mEditClickHandler = editClickHandler;
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

        AnimateUtils.runEnterAnimation(mContext, customViewHolder.itemView, doAnimation,
              AnimateUtils.COURSE_TYPE, i, mCursor.getString(Contract.Courses.COURSEID_POS), mNewIds);

        doAnimation = doAnimation && mCursor.getCount() != i + 1;
    }

    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
    }

    class CourseAdapterViewHolder extends RecyclerView.ViewHolder
          implements View.OnClickListener{
        TextView clubNameView;
        TextView courseNameView;
        ImageButton editButton;

        CourseAdapterViewHolder(View view) {
            super(view);
            this.clubNameView = (TextView) view.findViewById(R.id.clubNameLabel);
            this.courseNameView = (TextView) view.findViewById(R.id.courseNameLabel);
            this.editButton = (ImageButton) view.findViewById(R.id.editButton);
            view.setOnClickListener(this);
            editButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == editButton.getId()) {
                int adapterPosition = getAdapterPosition();
                mCursor.moveToPosition(adapterPosition);
                long courseId = mCursor.getLong(Contract.Courses.COURSEID_POS);
                String courseName = mCursor.getString(Contract.Courses.COURSENAME_POS);
                String clubName = mCursor.getString(Contract.Courses.CLUBNAME_POS);
                int holeCount = mCursor.getInt(Contract.Courses.HOLECNT_POS);
                mEditClickHandler.onEditClick(courseId, courseName, clubName, holeCount);
            } else {
                int adapterPosition = getAdapterPosition();
                mCursor.moveToPosition(adapterPosition);
                long courseId = mCursor.getLong(Contract.Courses.COURSEID_POS);
                mClickHandler.onClick(courseId);
            }
        }
    }

    interface CourseAdapterOnClickHandler {
        void onClick(Long courseId);
    }

    interface CourseAdapterEditClickHandler {
        void onEditClick(Long courseId, String clubName, String courseName, int holeCount);
    }

    void swapCursor(Cursor newCursor) {
        mNewIds = AnimateUtils.newIdsFromCursor(mContext, newCursor);

        mCursor = newCursor;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        if (mCursor.moveToPosition(position)) {
            return mCursor.getLong(Contract.Courses.COURSEID_POS);
        } else {
            return -1;
        }
    }

    public Cursor getCursor() {
        return mCursor;
    }
}