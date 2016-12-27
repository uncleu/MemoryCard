package com.memorycard.android.memorycardapp;

import java.util.ArrayList;
import java.util.List;

public class CardsGroup {

    private String name;
    private String tab_name;
    private Long lLastModifTimeInMillis;
    private String discription;


    private int progressValue;
    private int mId;
    private int total;
    private List<Card> mCardsList;



    public String getTab_name() {
        return tab_name;
    }

    public void setTab_name(String tab_name) {
        this.tab_name = tab_name;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public Long getlLastModifTimeInMillis() {
        return lLastModifTimeInMillis;
    }

    public void setlLastModifTimeInMillis(Long lLastModifTimeInMillis) {
        this.lLastModifTimeInMillis = lLastModifTimeInMillis;
    }


    public int getProgressValue() {
        return progressValue;
    }

    public void setProgressValue(int progressValue) {
        this.progressValue = progressValue;
    }


    public CardsGroup(){
        mCardsList = new ArrayList<Card>();
    }



    public List<Card> getmCardsList() {
        return mCardsList;
    }

    public void setmCardsList(List<Card> mCardsList) {
        this.mCardsList = mCardsList;
    }



    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotal() {
        this.total = mCardsList.size();
        return this.total;
    }

    public void setTotal(int total) {
        this.total = total;
    }



    public void addCard(Card card){
        mCardsList.add(card);
    }

    public void ChargeCardsList(List<Card> cardslist){
        mCardsList = new ArrayList<>(cardslist);
    }



}
