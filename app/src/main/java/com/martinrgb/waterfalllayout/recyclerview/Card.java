package com.martinrgb.waterfalllayout.recyclerview;

/**
 * Created by lcodecore on 2016/12/7.
 */

public class Card {
    public String name;
    public int imgSrc;
    public int iconSrc;

    public int tagNum;

    public Card(String name, int imgSrc,int iconSrc,int tagNum) {
        this.name = name;
        this.imgSrc = imgSrc;
        this.iconSrc = iconSrc;
        this.tagNum = tagNum;
    }

    public Card(int imgSrc) {
        this.name = null;
        this.imgSrc = imgSrc;
        this.iconSrc = 0;
    }


    public int getTagNum() {
        return tagNum;
    }



}
