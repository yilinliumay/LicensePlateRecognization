package com.example.mayliu.opencvplate;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan;
import static java.lang.Math.pow;

/**
 * Created by mayliu on 2018/4/3.
 */

public class Calculate {

    public double distanceBetweenChars(PossibleChar firstChar, PossibleChar secondChar) {
        int intX = abs(firstChar.intCenterX - secondChar.intCenterX);
        int intY = abs(firstChar.intCenterY - secondChar.intCenterY);
        return(Math.sqrt(pow(intX, 2) + pow(intY, 2)));
    }

    public double angleBetweenChars(PossibleChar firstChar, PossibleChar secondChar) {
        double dblAdj = abs(firstChar.intCenterX - secondChar.intCenterX);
        double dblOpp = abs(firstChar.intCenterY - secondChar.intCenterY);
        double dblAngleInRad = atan(dblOpp / dblAdj);
        double dblAngleInDeg = dblAngleInRad * (180.0 / PI);
        return(dblAngleInDeg);
    }
}
