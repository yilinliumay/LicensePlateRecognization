package com.example.mayliu.opencvplate;

/**
 * Created by mayliu on 2018/3/27.
 */

public interface Classifier {

    String name();

    Classification recognize(final float[] pixels);
    //Classification recognize(final float[] pixels, int type);
}

