package com.sean.golfranger;

import android.app.Dialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.sean.golfranger.data.Contract;
import com.sean.golfranger.utils.DialogUtils;
import com.sean.golfranger.utils.SharedPrefUtils;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import timber.log.Timber;

/**
 * UI displays and updates data of each hole
 */
public class HoleFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    EditText[] editTexts;
    Integer mFocusedEditTextIndex = null;
    CheckBox[] checkBoxes;
    Button holeButton;
    TextView p1Initials, p2Initials, p3Initials, p4Initials;
    private static LoaderManager sLoaderManager;
    private static LoaderManager.LoaderCallbacks sLoaderCallback;
    private static final String HOLE_NUMBER_EXTRA = "HoleFragmentHoleNumExtra";
    private static final int HOLE_LOADER = 101;

    private static final int[] EDIT_TEXT_IDS =
          new int[] {R.id.distanceValue,R.id.parValue,R.id.p1Score, R.id.p2Score, R.id.p3Score,
                R.id.p4Score, R.id.p1Putts, R.id.p2Putts, R.id.p3Putts, R.id.p4Putts,
                R.id.p1Penalties, R.id.p2Penalties, R.id.p3Penalties, R.id.p4Penalties
                ,R.id.p1Sand, R.id.p2Sand, R.id.p3Sand, R.id.p4Sand};
    private static final String[] EDIT_TEXT_DB_COLUMNS =
          new String[] {Contract.Holes.HOLE_DISTANCE, Contract.Holes.HOLE_PAR, Contract.Holes.P1_SCORE,
                Contract.Holes.P2_SCORE, Contract.Holes.P3_SCORE, Contract.Holes.P4_SCORE,
                Contract.Holes.P1_PUTTS, Contract.Holes.P2_PUTTS, Contract.Holes.P3_PUTTS,
                Contract.Holes.P4_PUTTS, Contract.Holes.P1_PENALTIES, Contract.Holes.P2_PENALTIES,
                Contract.Holes.P3_PENALTIES, Contract.Holes.P4_PENALTIES, Contract.Holes.P1_SAND,
                Contract.Holes.P2_SAND, Contract.Holes.P3_SAND, Contract.Holes.P4_SAND};

    private static final int[] CHECKBOX_IDS =
          new int[] {R.id.p1Gir, R.id.p2Gir,R.id.p3Gir, R.id.p4Gir
                ,R.id.p1Fir, R.id.p2Fir, R.id.p3Fir, R.id.p4Fir};
    private static final String[] CHECKBOX_DB_COLUMNS =
          new String[] {Contract.Holes.P1_GIR, Contract.Holes.P2_GIR,
                Contract.Holes.P3_GIR, Contract.Holes.P4_GIR, Contract.Holes.P1_FIR,
                Contract.Holes.P2_FIR, Contract.Holes.P3_FIR, Contract.Holes.P4_FIR};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        sLoaderManager = getLoaderManager();
        sLoaderCallback = this;
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            savedInstanceState = new Bundle();
            savedInstanceState.putString(HOLE_NUMBER_EXTRA, "1");
        }
        sLoaderManager.initLoader(HOLE_LOADER, savedInstanceState, sLoaderCallback);

        KeyboardVisibilityEvent.registerEventListener(
              getActivity(),
              new KeyboardVisibilityEventListener() {
                  @Override
                  public void onVisibilityChanged(boolean isOpen) {
                      if (!isOpen && mFocusedEditTextIndex != null) {
                          ContentValues values = new ContentValues();
                          values.put(EDIT_TEXT_DB_COLUMNS[mFocusedEditTextIndex],
                                editTexts[mFocusedEditTextIndex].getText().toString().trim());
                          String roundId = SharedPrefUtils.getCurrentRoundId(getActivity());
                          String rawHoleNumber = holeButton.getText().toString().trim();
                          String holeNum = rawHoleNumber.substring(0,rawHoleNumber.length()-1).trim();
                          Timber.d("HoleNumber: " + holeNum);
                          ContentResolver resolver = getActivity().getContentResolver();
                          resolver.update(
                                Contract.Holes.buildDirUri(),
                                values,
                                Contract.Holes.ROUND_ID + "=? AND " + Contract.Holes.HOLE_NUMBER + "=?",
                                new String[] {roundId, holeNum}
                          );
                      }
                  }
              });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String rawNumber = holeButton.getText().toString();
        String number = rawNumber.substring(0, rawNumber.length()-1);
        outState.putString(HOLE_NUMBER_EXTRA, number);

        if (mFocusedEditTextIndex != null){
            ContentValues values = new ContentValues();
            values.put(EDIT_TEXT_DB_COLUMNS[mFocusedEditTextIndex],
                  editTexts[mFocusedEditTextIndex].getText().toString().trim());
            String roundId = SharedPrefUtils.getCurrentRoundId(getActivity());
            Timber.d("HoleNumber: " + number);
            ContentResolver resolver = getActivity().getContentResolver();
            resolver.update(
                  Contract.Holes.buildDirUri(),
                  values,
                  Contract.Holes.ROUND_ID + "=? AND " + Contract.Holes.HOLE_NUMBER + "=?",
                  new String[] {roundId, number}
            );
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            savedInstanceState = new Bundle();
            savedInstanceState.putString(HOLE_NUMBER_EXTRA, "1");
        }

        final String holeNumber = savedInstanceState.getString(HOLE_NUMBER_EXTRA);
        String holeNumberViewText = holeNumber + getString(R.string.dropDownIcon);

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_hole, container, false);
        holeButton = (Button) rootView.findViewById(R.id.holeValue);
        p1Initials = (TextView) rootView.findViewById(R.id.p1Name);
        p2Initials = (TextView) rootView.findViewById(R.id.p2Name);
        p3Initials = (TextView) rootView.findViewById(R.id.p3Name);
        p4Initials = (TextView) rootView.findViewById(R.id.p4Name);
        holeButton.setText(holeNumberViewText);
        holeButton.setOnClickListener(mButtonClickListener);

        editTexts = new EditText[EDIT_TEXT_IDS.length];
        checkBoxes = new CheckBox[CHECKBOX_IDS.length];

        for (int i = 0; i < EDIT_TEXT_IDS.length; i++) {
            final int k = i;
            editTexts[i] = (EditText) rootView.findViewById(EDIT_TEXT_IDS[i]);
            editTexts[i].setSelectAllOnFocus(true);
            editTexts[i].setOnTouchListener(new View.OnTouchListener(){
                public boolean onTouch(View v, MotionEvent me) {
                mFocusedEditTextIndex = k;
                return false;
                }
            });
        }

        for (int l = 0; l < CHECKBOX_IDS.length; l++) {
            checkBoxes[l] = (CheckBox) rootView.findViewById(CHECKBOX_IDS[l]);
        }
        setCheckBoxAndEditTextListeners(holeNumber);
        return rootView;
    }

    @Override
    public void onResume() {
        String rawHoleNumber = holeButton.getText().toString().trim();
        String holeNum = rawHoleNumber.substring(0,rawHoleNumber.length()-1).trim();
        Timber.d("REINIT FROM ROTATION HOLE: " + holeNum);
        Bundle loaderBundle = new Bundle();
              loaderBundle.putString(HOLE_NUMBER_EXTRA, holeNum);
        sLoaderManager.restartLoader(HOLE_LOADER, loaderBundle, sLoaderCallback);
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Context context = getActivity().getApplicationContext();
            String holeNumber = bundle.getString(HOLE_NUMBER_EXTRA);
            Timber.d("Hole Number in Loader: " + holeNumber);
            return new CursorLoader(context,
                  Contract.HoleView.buildDirUri(),
                  null,
                  Contract.HoleView.ROUND_ID + "=? AND " + Contract.HoleView.HOLE_NUM + "=?",
                  new String[] {SharedPrefUtils.getCurrentRoundId(context), holeNumber},
                  null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()){
            for (int i = 0; i < editTexts.length; i++) {
                //Cursor/View Logic is Highly Coupled with Column Indices of HoleView
                String str = String.valueOf(cursor.getLong(i+Contract.HoleView.HOLEDISTANCE_POS));
                editTexts[i].setText(str);
            }
            for (int j = 0; j < checkBoxes.length; j++) {
                //Cursor/View Logic is Highly Coupled with Column Indices of HoleView
                long bool = cursor.getLong(j+Contract.HoleView.P1GIR_POS);
                checkBoxes[j].setChecked(bool == 1);
            }

            String initials;
            initials = cursor.getString(Contract.HoleView.P1INITIALS_POS);
            if (initials != null) {
                p1Initials.setText(initials);
            }
            initials = cursor.getString(Contract.HoleView.P2INITIALS_POS);
            if (initials != null) {
                p2Initials.setText(initials);
            }
            initials = cursor.getString(Contract.HoleView.P3INITIALS_POS);
            if (initials != null) {
                p3Initials.setText(initials);
            }
            initials = cursor.getString(Contract.HoleView.P4INITIALS_POS);
            if (initials != null) {
                p4Initials.setText(initials);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private OnClickListener mButtonClickListener = new OnClickListener() {
        public void onClick(View v) {
            // custom dialog
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.radio_button_holes);
            RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radio_group_hole);

            dialog.show();
            DialogUtils.doKeepDialog(dialog);
            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    int childCount = group.getChildCount();
                    for (int x = 0; x < childCount; x++) {
                        RadioButton btn = (RadioButton) group.getChildAt(x);
                        if (btn.getId() == checkedId) {
                            String rawHoleNumber = btn.getText().toString();
                            String holeNum = rawHoleNumber.substring(rawHoleNumber.length()-2).trim();
                            Timber.d("selected Hole-> " + holeNum);
                            Bundle loaderBundle = new Bundle();
                            loaderBundle.putString(HOLE_NUMBER_EXTRA, holeNum);
                            String holeNumViewText = (holeNum + getString(R.string.dropDownIcon)).trim();
                            holeButton.setText(holeNumViewText);
                            sLoaderManager.restartLoader(HOLE_LOADER, loaderBundle, sLoaderCallback);

                            setCheckBoxAndEditTextListeners(holeNum);
                        }
                    }
                    dialog.dismiss();
                }
            });
        }
    };

    private void setCheckBoxAndEditTextListeners(final String holeNumber) {
        for (int j = 0; j < editTexts.length; j++) {
            final int k = j;
            editTexts[j].setOnFocusChangeListener(null);
            editTexts[j].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (!hasFocus) {
                        ContentValues values = new ContentValues();
                        values.put(EDIT_TEXT_DB_COLUMNS[k], editTexts[k].getText().toString().trim());
                        String roundId = SharedPrefUtils.getCurrentRoundId(getActivity());
                        Timber.d("HoleNumber: " + holeNumber);
                        ContentResolver resolver = getActivity().getContentResolver();
                        resolver.update(
                              Contract.Holes.buildDirUri(),
                              values,
                              Contract.Holes.ROUND_ID + "=? AND " + Contract.Holes.HOLE_NUMBER + "=?",
                              new String[] {roundId, holeNumber}
                        );
                        values.clear();
                    }
                }
            });
        }

        for (int m = 0; m < checkBoxes.length; m++) {
            final int n = m;
            checkBoxes[m].setOnTouchListener(null);
            checkBoxes[m].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    ContentValues values = new ContentValues();
                    int boolInt = (checkBoxes[n].isChecked()) ? 0 : 1;
                    values.put(CHECKBOX_DB_COLUMNS[n], String.valueOf(boolInt));
                    ContentResolver resolver = getActivity().getContentResolver();
                    Timber.d("Saved a checkmark: " + String.valueOf(boolInt)+" for hole " + holeNumber);
                    resolver.update(
                          Contract.Holes.buildDirUri(),
                          values,
                          Contract.Holes.ROUND_ID + "=? AND " + Contract.Holes.HOLE_NUMBER + "=?",
                          new String[] {SharedPrefUtils.getCurrentRoundId(getActivity()), holeNumber}
                    );
                    return false;
                }
                //                @Override
//                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                    ContentValues values = new ContentValues();
//                    int boolInt = (isChecked) ? 0 : 1;
//                    values.put(CHECKBOX_DB_COLUMNS[n], String.valueOf(boolInt));
//                    ContentResolver resolver = getActivity().getContentResolver();
//                    Timber.d("Saved a checkmark: " + String.valueOf(boolInt)+" for hole " + holeNumber);
//                    resolver.update(
//                          Contract.Holes.buildDirUri(),
//                          values,
//                          Contract.Holes.ROUND_ID + "=? AND " + Contract.Holes.HOLE_NUMBER + "=?",
//                          new String[] {SharedPrefUtils.getCurrentRoundId(getActivity()), holeNumber}
//                    );
//                }
            });
        }
    }
}