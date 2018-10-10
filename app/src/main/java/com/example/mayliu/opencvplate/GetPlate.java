package com.example.mayliu.opencvplate;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static java.lang.Math.PI;
import static java.lang.Math.asin;

/**
 * Created by mayliu on 2018/4/3.
 */

public class GetPlate {

    public  ArrayList<ArrayList<PossibleChar>> vectorOfVectorsOfMatchingCharsInScene;
    public Mat image;
    public Mat secondImgContours;

    public ArrayList<PossiblePlate> vectorOfPossiblePlates=new ArrayList<PossiblePlate>();

    GetPlate(ArrayList<ArrayList<PossibleChar>> vectorOfVectorsOfMatchingCharsInScene,Mat image,Mat secondImgContours){
        this.vectorOfVectorsOfMatchingCharsInScene=vectorOfVectorsOfMatchingCharsInScene;
        this.image=image;
        this.secondImgContours=secondImgContours;
    }


    public void getPossiblePlate(){
        for (ArrayList<PossibleChar> vectorOfMatchingChars : vectorOfVectorsOfMatchingCharsInScene) {

//            // for each group of matching chars
            PossiblePlate possiblePlate = extractPlate(image, vectorOfMatchingChars);        // 对于其中一群提取车牌
            if (possiblePlate.imgPlate!=null) {
                vectorOfPossiblePlates.add(possiblePlate);
            }
           // break;

        }
        System.out.println(vectorOfPossiblePlates.size()+" possible plates found");

        for (int i = 0; i <vectorOfPossiblePlates.size(); i++) {
            Point p2fRectPoints[] = new Point[4];

           vectorOfPossiblePlates.get(i).rrLocationOfPlateInScene.points(p2fRectPoints);

            for (int j = 0; j < 4; j++) {
            if(p2fRectPoints[j].x>0 && p2fRectPoints[j].y>0 && p2fRectPoints[(j + 1) % 4].x>0 &&p2fRectPoints[(j + 1) % 4].y>0)
               Imgproc.line(secondImgContours, p2fRectPoints[j], p2fRectPoints[(j + 1) % 4], new Scalar(255, 0, 0), 2);
         //       System.out.println(p2fRectPoints[j].x);
//                System.out.println(p2fRectPoints[j].y);


            }
        }

    }



    public PossiblePlate extractPlate(Mat imgOriginal, ArrayList<PossibleChar> vectorOfMatchingChars) {
        double PLATE_WIDTH_PADDING_FACTOR = 1.5;
        double PLATE_HEIGHT_PADDING_FACTOR = 1.5;
        PossiblePlate possiblePlate=new PossiblePlate();            // this will be the return value
        //将intCenterX从小到大排列
        Collections.sort(vectorOfMatchingChars,
                new Comparator<PossibleChar>() {
                    public int compare(PossibleChar o1, PossibleChar o2) {
                        if(o1.intCenterX>o2.intCenterX) return 1;
                        else return -1;
                    }
                });




        // calculate the center point of the plate
        double dblPlateCenterX = (double)(vectorOfMatchingChars.get(0).intCenterX + vectorOfMatchingChars.get(vectorOfMatchingChars.size() - 1).intCenterX) / 2.0;
        double dblPlateCenterY = (double)(vectorOfMatchingChars.get(0).intCenterY + vectorOfMatchingChars.get(vectorOfMatchingChars.size() - 1).intCenterY) / 2.0;
        Point p2dPlateCenter=new Point(dblPlateCenterX, dblPlateCenterY);

        // calculate plate width and height
        int intPlateWidth = (int)((vectorOfMatchingChars.get(vectorOfMatchingChars.size() - 1).boundingRect.x + vectorOfMatchingChars.get(vectorOfMatchingChars.size() - 1).boundingRect.width - vectorOfMatchingChars.get(0).boundingRect.x) * PLATE_WIDTH_PADDING_FACTOR);

        double intTotalOfCharHeights = 0;

        for (PossibleChar matchingChar : vectorOfMatchingChars) {
            intTotalOfCharHeights = intTotalOfCharHeights + matchingChar.boundingRect.height;
        }

        double dblAverageCharHeight = (double)intTotalOfCharHeights / vectorOfMatchingChars.size();

        int intPlateHeight = (int)(dblAverageCharHeight * PLATE_HEIGHT_PADDING_FACTOR);

        // calculate correction angle of plate region
        double dblOpposite = vectorOfMatchingChars.get(vectorOfMatchingChars.size() - 1).intCenterY - vectorOfMatchingChars.get(1).intCenterY;
        Calculate ca=new Calculate();
        double dblHypotenuse = ca.distanceBetweenChars(vectorOfMatchingChars.get(1), vectorOfMatchingChars.get(vectorOfMatchingChars.size() - 1));
        double dblCorrectionAngleInRad = asin(dblOpposite / dblHypotenuse);
        double dblCorrectionAngleInDeg = dblCorrectionAngleInRad * (180.0 / PI);

        // assign rotated rect member variable of possible plate
        possiblePlate.rrLocationOfPlateInScene = new RotatedRect(p2dPlateCenter, new Size((float)intPlateWidth, (float)intPlateHeight), (float)dblCorrectionAngleInDeg);

        // Mat rotationMatrix;             // final steps are to perform the actual rotation
        Mat imgRotated=new Mat(imgOriginal.rows(), imgOriginal.cols(), imgOriginal.type());


        // get the rotation matrix for our calculated correction angle
        Mat rotationMatrix = Imgproc.getRotationMatrix2D(p2dPlateCenter, dblCorrectionAngleInDeg, 1.0);

        // rotate the entire image
        Imgproc.warpAffine(imgOriginal, imgRotated, rotationMatrix, imgRotated.size());

        // crop out the actual plate portion of the rotated image
        //从原图像中提取提取一个感兴趣的矩形区域图像
        // Imgproc.getRectSubPix(imgRotated, possiblePlate.rrLocationOfPlateInScene.size, possiblePlate.rrLocationOfPlateInScene.center, imgCropped,-1);
       double x=possiblePlate.rrLocationOfPlateInScene.center.x-possiblePlate.rrLocationOfPlateInScene.size.width/2;
       double y=possiblePlate.rrLocationOfPlateInScene.center.y-possiblePlate.rrLocationOfPlateInScene.size.height/2;
       double width=possiblePlate.rrLocationOfPlateInScene.size.width;
       double height=possiblePlate.rrLocationOfPlateInScene.size.height;

        double imgRotatedwidth=imgRotated.size().width;
        double imgRotatedheight=imgRotated.size().height;

        if(x>0 && y>0 ) {
            Rect rect = new Rect((int)(possiblePlate.rrLocationOfPlateInScene.center.x-possiblePlate.rrLocationOfPlateInScene.size.width/2), (int)(possiblePlate.rrLocationOfPlateInScene.center.y-possiblePlate.rrLocationOfPlateInScene.size.height/2),(int)possiblePlate.rrLocationOfPlateInScene.size.width ,(int)possiblePlate.rrLocationOfPlateInScene.size.height); // 设置矩形ROI的位置
            Mat imgCropped= new Mat();
            imgCropped = imgRotated.submat(rect);
            possiblePlate.imgPlate = imgCropped;
        }
        else{
            possiblePlate.imgPlate=null;
        }

       // else possiblePlate.imgPlate=null;
                    // copy the cropped plate image into the applicable member variable of the possible plate

        return(possiblePlate);
    }
}
