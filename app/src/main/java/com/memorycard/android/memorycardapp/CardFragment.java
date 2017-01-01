package com.memorycard.android.memorycardapp;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.database.DatabaseUtilsCompat;
import android.support.v4.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import utilities.DataBaseManager;
import utilities.ImageUtilities;
import utilities.SettingsUtilities;
import utilities.TimeUtilities;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CardFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String QUESTION = "txt_question";
    private static final String ANSWER = "txt_answer";

    // TODO: Rename and change types of parameters
    private String tabName;
    private String tabDescription;
    private Context context;
    private Card mCard;
    private int position;
    private TextView txtQuestion;
    private EditText txtAnswer;
    private String rightAnswer;
    private CheckBox isDifficult;
    private TextView txtResult;
    private TextView timeCountDown;
    private DataBaseManager dbmanager;
    private CountDownTimer countDownTimer=null;
    private ImageView imageView;

    private OnFragmentInteractionListener mListener;

    public CardFragment() {
        // Required empty public constructor
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            if(CardsManagerActivity.isCountdownTimer){
                countDownTimer = new CountDownTimer(CardsManagerActivity.countdownValue, 1000) {

                    public void onTick(long millisUntilFinished) {
                        timeCountDown.setText("seconds remaining: " + millisUntilFinished / 1000);
                    }

                    public void onFinish() {
                        timeCountDown.setText("done!");
                        if (mListener != null) {
                            mListener.onFragmentInteraction(Uri.parse("content://"
                                    + "com.memorycard.android.memorycardapp"
                                    + "/countdowntimer"));
                        }

                    }
                };
                countDownTimer.start();
            }
        }else{
            // fragment is no longer visible
            if (countDownTimer !=null){
                countDownTimer.cancel();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        dbmanager = DataBaseManager.getDbManager(context);

/*        if (getArguments() != null) {
            txtQuestion.setText(getArguments().getString(QUESTION));
            txtAnswer.setText(getArguments().getString(ANSWER));
        }*/
    }

    public static CardFragment newInstance(int position){
        CardFragment fragment = new CardFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position",position);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(getArguments() != null){
            this.position = getArguments().getInt("position");
            this.mCard = CardsManagerActivity.getCardFromCardsList(this.position);
        }

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_card, container, false);
        txtAnswer = (EditText)v.findViewById(R.id.card_txt_answer);
        txtQuestion = (TextView)v.findViewById(R.id.card_txt_question);
        Button checkButton = (Button)v.findViewById(R.id.check_button);
        isDifficult = (CheckBox)v.findViewById(R.id.is_difficult);
        txtResult = (TextView)v.findViewById(R.id.txt_result);
        timeCountDown = (TextView)v.findViewById(R.id.time_count_down);

        //initialisation
        //txtAnswer.setText(mCard.getMtxtAnswer());
        rightAnswer = mCard.getMtxtAnswer();
        txtQuestion.setText(mCard.getMtxtQuestion());

        //image
        imageView = (ImageView)v.findViewById(R.id.image_question);
        byte[] bytes =mCard.getMblobQuestion();
        imageView.setImageBitmap(ImageUtilities.BytesToBimap(bytes));



        if(mCard.getmDifficultyScore()>0)
                    isDifficult.setChecked(true);

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(rightAnswer != null && rightAnswer.equals(txtAnswer.getText().toString())){
                    txtResult.setText("Congratulations!");
                    txtResult.setTextColor(Color.rgb(0, 238, 118));
                    CardsManagerActivity.correctResponse++; //test
                }
                else{
                    txtResult.setText("Try again!");
                    txtResult.setTextColor(Color.rgb(255, 64, 64));
                }

                dbmanager.updateCard(mCard);
            }
        });

        isDifficult.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                if(isDifficult.isChecked())
                {
                    Toast.makeText(context, "is difficult!",
                            Toast.LENGTH_SHORT).show();
                    mCard.setmDifficultyScore(1);
                }
                else
                {
                    Toast.makeText(context, "is not difficult anymore",
                            Toast.LENGTH_SHORT).show();
                            mCard.setmDifficultyScore(0);
                }

                dbmanager.updateCard(mCard);
            }

        });



        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (countDownTimer != null)
                countDownTimer.cancel();

    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
