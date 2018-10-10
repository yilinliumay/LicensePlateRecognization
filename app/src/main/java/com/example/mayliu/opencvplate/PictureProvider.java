package com.example.mayliu.opencvplate;

import android.graphics.Bitmap;

/**
 * Created by dell on 2016/12/17.
 */

public class PictureProvider {
    private Bitmap picture_resource;
    private String photo_time;

    public Bitmap getPicture_resource() {
        return picture_resource;
    }

    public void setPicture_resource(Bitmap picture_resource) {
        this.picture_resource = picture_resource;
    }

    public String getPhoto_time() {
        return photo_time;
    }

    public void setPhoto_time(String photo_time) {
        this.photo_time = photo_time;
    }

    public PictureProvider(Bitmap picture_resource, String photo_time)
    {
        this.setPicture_resource(picture_resource);
        this.setPhoto_time(photo_time);

    }


}
