package com.example.mayliu.opencvplate;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

import static org.opencv.core.CvType.CV_8UC3;

/**
 * Created by mayliu on 2018/4/3.
 */

public class findPossibleChars {

    public ArrayList<PossibleChar> findPossibleCharsInScene(Mat imgThresh){
        //返回值 返回可能是车牌的 char
        ArrayList<PossibleChar> vectorOfPossibleChars=new ArrayList<PossibleChar>();

        //https://docs.opencv.org/2.4/doc/tutorials/core/basic_geometric_drawing/basic_geometric_drawing.html
        Mat imgContours=new Mat(imgThresh.size(), CV_8UC3, new Scalar(0, 0, 0));//BLACK
        int intCountOfPossibleChars = 0;


        Mat imgThreshCopy = imgThresh.clone();

        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(imgThreshCopy, contours, new Mat(),
                Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        for (int i = 0;i < contours.size();i++) {


            //https://blog.csdn.net/qq_18343569/article/details/47999257 对图像轮廓点进行多边形拟合
            //https://stackoverflow.com/questions/36882329/how-to-draw-rectangle-around-contours-in-opencv-android
//            MatOfPoint2f approxCurve = new MatOfPoint2f();
//            MatOfPoint2f contour2f = new MatOfPoint2f( contours.get(i).toArray() );
//            double approxDistance = Imgproc.arcLength(contour2f, true)*0.02;
//            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);
//            MatOfPoint points = new MatOfPoint( approxCurve.toArray() );
            //PossibleChar possibleChar= new PossibleChar(points);
            PossibleChar possibleChar= new PossibleChar(contours.get(i));


            if (checkIfPossibleChar(possibleChar)) {                // if contour is a possible char, note this does not compare to other chars (yet) . . .
                intCountOfPossibleChars++;                          // increment count of possible chars
                vectorOfPossibleChars.add(possibleChar);      // and add to vector of possible chars
            }
        }
        System.out.println("图片中的轮廓个数 ："+contours.size());
        System.out.println("第一步：初步筛选剩下的轮廓个数 ："+intCountOfPossibleChars);
        return(vectorOfPossibleChars);

    }


    public ArrayList<PossibleChar> findPossibleCharsInPlate(Mat imgGrayscale, Mat imgThresh) {
        ArrayList<PossibleChar> vectorOfPossibleChars=new ArrayList<>();                            // this will be the return value

        Mat imgThreshCopy;

        ArrayList<MatOfPoint> contours= new ArrayList<>();

        imgThreshCopy = imgThresh.clone();				// make a copy of the thresh image, this in necessary b/c findContours modifies the image

        Imgproc.findContours(imgThreshCopy, contours, new Mat(),Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);        // find all contours in plate

        for (MatOfPoint contour : contours) {                            // for each contour
            PossibleChar possibleChar= new PossibleChar(contour);

            if (checkIfPossibleChar1(possibleChar)) {                // if contour is a possible char, note this does not compare to other chars (yet) . . .
                vectorOfPossibleChars.add(possibleChar);      // add to vector of possible chars
            }
        }

        return(vectorOfPossibleChars);
    }



    public boolean checkIfPossibleChar(PossibleChar possibleChar){
        // this function is a 'first pass' that does a rough check on a contour to see if it could be a char,
        // note that we are not (yet) comparing the char to other chars to look for a group
        int MIN_PIXEL_AREA = 80;
        int MIN_PIXEL_HEIGHT = 8;
        double MAX_ASPECT_RATIO = 1.0;
        int MIN_PIXEL_WIDTH = 2;
        double MIN_ASPECT_RATIO = 0.25;


        if (possibleChar.boundingRect.area() > MIN_PIXEL_AREA &&
                possibleChar.boundingRect.width > MIN_PIXEL_WIDTH && possibleChar.boundingRect.height > MIN_PIXEL_HEIGHT &&
                MIN_ASPECT_RATIO < possibleChar.dblAspectRatio && possibleChar.dblAspectRatio < MAX_ASPECT_RATIO) {
            return(true);
        } else {
            return(false);
        }
    }
    public boolean checkIfPossibleChar1(PossibleChar possibleChar){
        // this function is a 'first pass' that does a rough check on a contour to see if it could be a char,
        // note that we are not (yet) comparing the char to other chars to look for a group
        int MIN_PIXEL_AREA = 80;
        int MIN_PIXEL_HEIGHT = 8;
        double MAX_ASPECT_RATIO = 1.0;
        int MIN_PIXEL_WIDTH = 2;
        double MIN_ASPECT_RATIO = 0.25;


        if (possibleChar.boundingRect.area() > MIN_PIXEL_AREA &&
                possibleChar.boundingRect.width > MIN_PIXEL_WIDTH && possibleChar.boundingRect.height > MIN_PIXEL_HEIGHT &&
                MIN_ASPECT_RATIO < possibleChar.dblAspectRatio && possibleChar.dblAspectRatio < MAX_ASPECT_RATIO) {
            return(true);
        } else {
            return(false);
        }
    }
}
