package com.campustechng.aminu.idpenrollment.models;

import android.graphics.Bitmap;

/**
 * Created by Muhammad Amin on 4/4/2017.
 */

public class HomeCardView {
    private Bitmap thumbnail;
    private String description;

    public HomeCardView(Bitmap thumbnail, String description) {
        this.thumbnail = thumbnail;
        this.description = description;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public String getDescription() {
        return description;
    }
}
