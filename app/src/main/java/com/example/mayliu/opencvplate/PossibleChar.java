package com.example.mayliu.opencvplate;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import java.lang.Math;

/**
 * Created by mayliu on 2018/3/29.
 */

public class PossibleChar {

    public MatOfPoint contour=new MatOfPoint();
    public Rect boundingRect= new Rect();
    public int intCenterX;
    public int intCenterY;
    public double dblDiagonalSize;
    public float dblAspectRatio;

    PossibleChar(MatOfPoint _contour ){
        contour = _contour;
        //http://monkeycoding.com/?tag=boundingrect%E3%80%81minarearect
        boundingRect = Imgproc.boundingRect(contour);
        intCenterX = (boundingRect.x + boundingRect.x + boundingRect.width) / 2;
        intCenterY = (boundingRect.y + boundingRect.y + boundingRect.height) / 2;
        //https://blog.csdn.net/jerrywu145/article/details/52016533
        //对角线
        dblDiagonalSize = Math.sqrt(Math.pow(boundingRect.width, 2) + Math.pow(boundingRect.height, 2));
        dblAspectRatio = (float)boundingRect.width / (float)boundingRect.height;
    }
}
