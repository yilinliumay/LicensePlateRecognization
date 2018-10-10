package com.example.mayliu.opencvplate;

import org.opencv.core.Mat;
import org.opencv.core.RotatedRect;

/**
 * Created by mayliu on 2018/3/31.
 */

public class PossiblePlate {

    // member variables ///////////////////////////////////////////////////////////////////////////
    public Mat imgPlate;
    public Mat imgGrayscale=new Mat();
    public Mat imgThresh=new Mat();

    public RotatedRect rrLocationOfPlateInScene =new RotatedRect();

    public String strChars;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    static boolean sortDescendingByNumberOfChars(PossiblePlate ppLeft, PossiblePlate ppRight) {
        return(ppLeft.strChars.length() > ppRight.strChars.length());
    }
}
