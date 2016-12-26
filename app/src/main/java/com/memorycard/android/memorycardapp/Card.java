package com.memorycard.android.memorycardapp;

/**
 * Created by yina on 2016/12/17.
 */

public class Card {


    private String mblobQuestion;
    private String mtxtQuestion;
    private String mblobAnswer;
    private String mtxtAnswer;


    private String mtabName;
    private int mDifficultyScore;
    private int mId;
    private int mGroupId;
    private int mDay;
    private CardsGroup mCardsGroup;



    public String getMtxtQuestion() {
        return mtxtQuestion;
    }

    public void setMtxtQuestion(String mtxtQuestion) {
        this.mtxtQuestion = mtxtQuestion;
    }

    public String getMblobAnswer() {
        return mblobAnswer;
    }

    public void setMblobAnswer(String mblobAnswer) {
        this.mblobAnswer = mblobAnswer;
    }

    public String getMblobQuestion() {
        return mblobQuestion;
    }

    public void setMblobQuestion(String mblobQuestion) {
        this.mblobQuestion = mblobQuestion;
        this.mblobQuestion = mblobQuestion;
    }

    public CardsGroup getmCardsGroup() {
        return mCardsGroup;
    }

    public void setmCardsGroup(CardsGroup mCardsGroup) {
        this.mCardsGroup = mCardsGroup;
    }

    public int getmDay() {
        return mDay;
    }

    public void setmDay(int mDay) {
        this.mDay = mDay;
    }

    public int getmDifficultyScore() {
        return mDifficultyScore;
    }

    public void setmDifficultyScore(int mDifficultyScore) {
        this.mDifficultyScore = mDifficultyScore;
    }

    public int getmGroupId() {
        return mGroupId;
    }

    public void setmGroupId(int mGroupId) {
        this.mGroupId = mGroupId;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getMtxtAnswer() {
        return mtxtAnswer;
    }

    public void setMtxtAnswer(String mtxtAnswer) {
        this.mtxtAnswer = mtxtAnswer;
    }


    public String getMtabName() {
        return mtabName;
    }

    public void setMtabName(String mtabName) {
        this.mtabName = mtabName;
    }

}
