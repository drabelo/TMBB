package com.dexafree.materialList.cards;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.dexafree.materialList.events.BusProvider;

/**
 * The SimpleCard defines a title, description and (if it is set on the layout) an image.
 */
public abstract class SimpleCard extends BasicCard {
    private String mTitle;
    private int mTitleColor = -1;
    private String mDescription;
    private int mDescriptionColor = -1;
    private Drawable mDrawable;
    private String secret;

    public SimpleCard(Context context) {
        super(context);
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
        BusProvider.dataSetChanged();
    }

    public void setSecret(String secret){this.secret= secret;}

    public String getSecret() {
        return secret;
    }


    public void setTitle(int titleId) {
        setTitle(getString(titleId));
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
        BusProvider.dataSetChanged();
    }

    public void setDescription(int descriptionId) {
        setDescription(getString(descriptionId));
    }

    public Drawable getDrawable() {
        return mDrawable;
    }

    public void setDrawable(Drawable drawable) {
        this.mDrawable = drawable;
        BusProvider.dataSetChanged();
    }

    public void setDrawable(int drawableId) {
        setDrawable(getResources().getDrawable(drawableId));
    }

    public int getTitleColor() {
        return mTitleColor;
    }

    public void setTitleColor(int color) {
        this.mTitleColor = color;
        BusProvider.dataSetChanged();
    }

    public void setTitleColorRes(int colorId) {
        setTitleColor(getResources().getColor(colorId));
    }

    public int getDescriptionColor() {
        return mDescriptionColor;
    }

    public void setDescriptionColor(int color) {
        this.mDescriptionColor = color;
        BusProvider.dataSetChanged();
    }

    public void setDescriptionColorRes(int colorId) {
        setDescriptionColor(getResources().getColor(colorId));
    }
}
