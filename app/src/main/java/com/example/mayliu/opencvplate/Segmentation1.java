package com.example.mayliu.opencvplate;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static org.opencv.core.CvType.CV_8UC3;

/**
 * Created by mayliu on 2018/4/4.
 */

public class Segmentation1 {

    public Mat imgGrayscale=new Mat(); //hui
    public Mat imgThresh=new Mat();//bian
    public Mat findPossible;//去掉一些细碎的结果
    public Mat matching;
    public Mat imgThreshColor= new Mat();//分割好的结果


    public int intLenOfLongestVectorOfChars = 0;
    public int intIndexOfLongestVectorOfChars = 0;
    ArrayList<Mat> imgCropped;//最终分割好的所有字符
    Bitmap bmp;
    String typea;


    List<PossiblePlate> detectCharsInPlates(List<PossiblePlate> vectorOfPossiblePlates) {
        int intPlateCounter = 0;                // this is only for showing steps
        Mat imgContours = new Mat();

        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        //如果没有车牌被检测到
        if (vectorOfPossiblePlates.isEmpty()) {
            System.out.println("no plates");
            return(vectorOfPossiblePlates);

        }

        // 如果有车牌被检测到
        // int i=-1;

        int longestPlate=0;
        PossiblePlate Lp=new PossiblePlate();

        for (PossiblePlate possiblePlate : vectorOfPossiblePlates) {
            if(possiblePlate.rrLocationOfPlateInScene.boundingRect().width>longestPlate){
                longestPlate=possiblePlate.rrLocationOfPlateInScene.boundingRect().width;
                Lp=possiblePlate;
            }
        }


        PossiblePlate possiblePlate=Lp;


        //for (PossiblePlate possiblePlate : vectorOfPossiblePlates) {


        //预处理
        bmp = Bitmap.createBitmap(possiblePlate.imgPlate.cols(), possiblePlate.imgPlate.rows(),
                Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(possiblePlate.imgPlate, bmp);
        Preprocess pre = new Preprocess(bmp);
        pre.TurntoGray();
        imgGrayscale = pre.getImgGray();
        pre.maximizeContrast();
        pre.GaussianBlur();
        pre.adaptiveThreshold();
        imgThresh = pre.getImgThresh();

        possiblePlate.imgThresh = imgThresh;
        possiblePlate.imgGrayscale = imgGrayscale;

        // upscale size by 60% for better viewing and character recognition
        Size sz = possiblePlate.imgThresh.size();
        Imgproc.resize(possiblePlate.imgThresh, possiblePlate.imgThresh, new Size(sz.width * 1.6, sz.height * 1.6));

        // threshold again to eliminate any gray areas
        Imgproc.threshold(possiblePlate.imgThresh, possiblePlate.imgThresh, 0.0, 255.0, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);


        //画出去掉细碎的车牌
        ArrayList<PossibleChar> vectorOfPossibleCharsInPlate = new findPossibleChars().findPossibleCharsInPlate(possiblePlate.imgGrayscale, possiblePlate.imgThresh);
        imgContours = new Mat(possiblePlate.imgThresh.size(), CV_8UC3, new Scalar(0, 0, 0));
        contours.clear();
        for (PossibleChar possibleChar : vectorOfPossibleCharsInPlate) {
            contours.add(possibleChar.contour);
        }
        Imgproc.drawContours(imgContours, contours, -1, new Scalar(0, 255, 0),5);
        findPossible = imgContours;


        //画出分好组的车牌
        ArrayList<ArrayList<PossibleChar>> vectorOfVectorsOfMatchingCharsInPlate = new findMatchingChars().findVectorOfVectorsOfMatchingChars(vectorOfPossibleCharsInPlate);
        imgContours = new Mat(possiblePlate.imgThresh.size(), CV_8UC3, new Scalar(0, 0, 0));
        contours.clear();
        for (ArrayList<PossibleChar> vectorOfMatchingChars : vectorOfVectorsOfMatchingCharsInPlate) {
            Random random = new java.util.Random();
            int intRandomBlue = random.nextInt(256);
            int intRandomGreen = random.nextInt(256);
            int intRandomRed = random.nextInt(256);

            for (PossibleChar matchingChar : vectorOfMatchingChars) {
                contours.add(matchingChar.contour);
            }
            Imgproc.drawContours(imgContours, contours, -1, new Scalar((double) intRandomBlue, (double) intRandomGreen, (double) intRandomRed),5);
        }
        matching = imgContours;


        //如果这个牌子里的字母无法被分成几组 则换下一个车牌
//            if (vectorOfVectorsOfMatchingCharsInPlate.size() == 0) {
//                possiblePlate.strChars = "";
//                continue;
//            }


        //做进一步处理 先忽略
        for (ArrayList<PossibleChar> vectorOfMatchingChars : vectorOfVectorsOfMatchingCharsInPlate) {                                         // for each vector of matching chars in the current plate
            Collections.sort(vectorOfMatchingChars,
                    new Comparator<PossibleChar>() {
                        public int compare(PossibleChar o1, PossibleChar o2) {
                            if (o1.intCenterX > o2.intCenterX) return 1;
                            else return -1;
                        }
                    });  // sort the chars left to right
            vectorOfMatchingChars = removeInnerOverlappingChars(vectorOfMatchingChars);                                     // and eliminate any overlapping chars
        }


        //找到最长的那个车牌
        for (int i = 0; i < vectorOfVectorsOfMatchingCharsInPlate.size(); i++) {
            if (vectorOfVectorsOfMatchingCharsInPlate.get(i).size()>intLenOfLongestVectorOfChars) {
                intLenOfLongestVectorOfChars = vectorOfVectorsOfMatchingCharsInPlate.get(i).size();
                intIndexOfLongestVectorOfChars = i;
                ArrayList<PossibleChar> longestVectorOfMatchingCharsInPlate = vectorOfVectorsOfMatchingCharsInPlate.get(intIndexOfLongestVectorOfChars);

                // SHOW_STEPS
                imgContours = new Mat(possiblePlate.imgThresh.size(), CV_8UC3, new Scalar(0, 0, 0));
                contours.clear();
                for (PossibleChar matchingChar : longestVectorOfMatchingCharsInPlate) {
                    contours.add(matchingChar.contour);
                }
                Imgproc.drawContours(imgContours, contours, -1, new Scalar(255, 255, 255));


                System.out.println("possiblePlate.imgThresh"+possiblePlate.imgThresh.size());
                possiblePlate.strChars = recognizeCharsInPlate(possiblePlate.imgThresh, longestVectorOfMatchingCharsInPlate);
            }
        }
        System.out.println("longest"+intLenOfLongestVectorOfChars);


        // suppose that the longest vector of matching chars within the plate is the actual vector of chars


        System.out.println("chars found in plate number "+intPlateCounter +" = " +possiblePlate.strChars);

        intPlateCounter++;



        //Imgproc.cvtColor(possiblePlate.imgThresh, imgThreshColor, Imgproc.COLOR_GRAY2BGR);

        // }
        return(vectorOfPossiblePlates);
    }



    public  ArrayList<PossibleChar> removeInnerOverlappingChars(ArrayList<PossibleChar> vectorOfMatchingChars) {

        double MIN_DIAG_SIZE_MULTIPLE_AWAY = 0.3;
        double MAX_DIAG_SIZE_MULTIPLE_AWAY = 5.0;
        ArrayList <PossibleChar> vectorOfMatchingCharsWithInnerCharRemoved=vectorOfMatchingChars;
        List delList = new ArrayList();
        for (PossibleChar currentChar : vectorOfMatchingChars) {
            for (PossibleChar otherChar : vectorOfMatchingChars) {
                if (currentChar != otherChar) {                         // if current char and other char are not the same char . . .
                    // if current char and other char have center points at almost the same location . . .
                    if (new Calculate().distanceBetweenChars(currentChar, otherChar) < (currentChar.dblDiagonalSize * MIN_DIAG_SIZE_MULTIPLE_AWAY)) {
                        // if we get in here we have found overlapping chars
                        // next we identify which char is smaller, then if that char was not already removed on a previous pass, remove it

                        // if current char is smaller than other char

                        if (currentChar.boundingRect.area() < otherChar.boundingRect.area()) {
                            // look for char in vector with an iterator
                            for (PossibleChar possChar : vectorOfMatchingCharsWithInnerCharRemoved) {
                                if(possChar== currentChar){
                                    delList.add(possChar);
                                }
                            }

                        }
                        else {        // else if other char is smaller than current char
                            for (PossibleChar possChar : vectorOfMatchingCharsWithInnerCharRemoved) {
                                if(possChar== otherChar){
                                    delList.add(possChar);
                                }
                            }
                        }


                    }
                }
            }
        }

        vectorOfMatchingCharsWithInnerCharRemoved.removeAll(delList);

        return(vectorOfMatchingCharsWithInnerCharRemoved);
    }





    String recognizeCharsInPlate(Mat imgThresh,ArrayList<PossibleChar> vectorOfMatchingChars) {
        String strChars="";               // this will be the return value, the chars in the lic plate
        imgCropped=new ArrayList<>();
        Imgproc.cvtColor(imgThresh, imgThreshColor, Imgproc.COLOR_GRAY2BGR);
        int i=0;

        for(PossibleChar currentChar:vectorOfMatchingChars){
            if(vectorOfMatchingChars.size()!=8) {
                if (i == 0) {
                    if (vectorOfMatchingChars.size() == 6) {
                        Imgproc.rectangle(imgThreshColor, new Point(currentChar.boundingRect.x - 1.25 * currentChar.boundingRect.width, currentChar.boundingRect.y), new Point(currentChar.boundingRect.x + currentChar.boundingRect.width - 1.2 * currentChar.boundingRect.width, currentChar.boundingRect.y + currentChar.boundingRect.height), new Scalar(0, 255, 0), 5);

                        Rect rect = new Rect((int) Math.pow(currentChar.boundingRect.x - 1.25 * currentChar.boundingRect.width, 1), currentChar.boundingRect.y, (int) Math.pow(1.05 * currentChar.boundingRect.width, 1), currentChar.boundingRect.height);
                        //  System.out.println(currentChar.boundingRect.x + " " + currentChar.boundingRect.y + " " + currentChar.boundingRect.width + " " + currentChar.boundingRect.height);
                        Mat test = imgThreshColor.submat(rect);
                        imgCropped.add(test);

                        Imgproc.rectangle(imgThreshColor, new Point(currentChar.boundingRect.x, currentChar.boundingRect.y), new Point(currentChar.boundingRect.x + currentChar.boundingRect.width, currentChar.boundingRect.y + currentChar.boundingRect.height), new Scalar(0, 255, 0), 5);
                        Rect rect2 = new Rect(currentChar.boundingRect.x, currentChar.boundingRect.y, currentChar.boundingRect.width, currentChar.boundingRect.height);
                        System.out.println(currentChar.boundingRect.x + " " + currentChar.boundingRect.y + " " + currentChar.boundingRect.width + " " + currentChar.boundingRect.height);
                        Mat test2 = imgThreshColor.submat(rect2);
                        imgCropped.add(test2);
                    }
                    i++;

                } else if (i == 1 && vectorOfMatchingChars.size() == 7) {
                    Imgproc.rectangle(imgThreshColor, new Point(currentChar.boundingRect.x - 1.25 * currentChar.boundingRect.width, currentChar.boundingRect.y), new Point(currentChar.boundingRect.x + currentChar.boundingRect.width - 1.2 * currentChar.boundingRect.width, currentChar.boundingRect.y + currentChar.boundingRect.height), new Scalar(0, 255, 0), 5);

                    Rect rect = new Rect((int) Math.pow(currentChar.boundingRect.x - 1.25 * currentChar.boundingRect.width, 1), currentChar.boundingRect.y, (int) Math.pow(1.05 * currentChar.boundingRect.width, 1), currentChar.boundingRect.height);
                    //  System.out.println(currentChar.boundingRect.x + " " + currentChar.boundingRect.y + " " + currentChar.boundingRect.width + " " + currentChar.boundingRect.height);
                    Mat test = imgThreshColor.submat(rect);
                    imgCropped.add(test);

                    Imgproc.rectangle(imgThreshColor, new Point(currentChar.boundingRect.x, currentChar.boundingRect.y), new Point(currentChar.boundingRect.x + currentChar.boundingRect.width, currentChar.boundingRect.y + currentChar.boundingRect.height), new Scalar(0, 255, 0), 5);
                    Rect rect2 = new Rect(currentChar.boundingRect.x, currentChar.boundingRect.y, currentChar.boundingRect.width, currentChar.boundingRect.height);
                    System.out.println(currentChar.boundingRect.x + " " + currentChar.boundingRect.y + " " + currentChar.boundingRect.width + " " + currentChar.boundingRect.height);
                    Mat test2 = imgThreshColor.submat(rect2);
                    imgCropped.add(test2);
                    i++;
                } else if (i == vectorOfMatchingChars.size() - 2) {
                    Imgproc.rectangle(imgThreshColor, new Point(currentChar.boundingRect.x - 1.25 * currentChar.boundingRect.width, currentChar.boundingRect.y), new Point(currentChar.boundingRect.x + currentChar.boundingRect.width - 1.2 * currentChar.boundingRect.width, currentChar.boundingRect.y + currentChar.boundingRect.height), new Scalar(0, 255, 0), 5);

                    Rect rect = new Rect((int) Math.pow(currentChar.boundingRect.x - 1.25 * currentChar.boundingRect.width, 1), currentChar.boundingRect.y, (int) Math.pow(1.05 * currentChar.boundingRect.width, 1), currentChar.boundingRect.height);
                    //  System.out.println(currentChar.boundingRect.x + " " + currentChar.boundingRect.y + " " + currentChar.boundingRect.width + " " + currentChar.boundingRect.height);
                    Mat test = imgThreshColor.submat(rect);
                    imgCropped.add(test);

                    Imgproc.rectangle(imgThreshColor, new Point(currentChar.boundingRect.x, currentChar.boundingRect.y), new Point(currentChar.boundingRect.x + currentChar.boundingRect.width, currentChar.boundingRect.y + currentChar.boundingRect.height), new Scalar(0, 255, 0), 5);
                    Rect rect2 = new Rect(currentChar.boundingRect.x, currentChar.boundingRect.y, currentChar.boundingRect.width, currentChar.boundingRect.height);
                    System.out.println(currentChar.boundingRect.x + " " + currentChar.boundingRect.y + " " + currentChar.boundingRect.width + " " + currentChar.boundingRect.height);
                    Mat test2 = imgThreshColor.submat(rect2);
                    imgCropped.add(test2);
                    i++;
                } else {
                    Imgproc.rectangle(imgThreshColor, new Point(currentChar.boundingRect.x, currentChar.boundingRect.y), new Point(currentChar.boundingRect.x + currentChar.boundingRect.width, currentChar.boundingRect.y + currentChar.boundingRect.height), new Scalar(0, 255, 0), 5);
                    Rect rect3 = new Rect(currentChar.boundingRect.x, currentChar.boundingRect.y, currentChar.boundingRect.width, currentChar.boundingRect.height);
                    System.out.println(currentChar.boundingRect.x + " " + currentChar.boundingRect.y + " " + currentChar.boundingRect.width + " " + currentChar.boundingRect.height);
                    Mat test3 = imgThreshColor.submat(rect3);
                    imgCropped.add(test3);
                    i++;
                }
            }
            else{
                if(i==0){i++;}
                else if(i==1){
                    Imgproc.rectangle(imgThreshColor, new Point(currentChar.boundingRect.x - 1.25 * currentChar.boundingRect.width, currentChar.boundingRect.y), new Point(currentChar.boundingRect.x + currentChar.boundingRect.width - 1.2 * currentChar.boundingRect.width, currentChar.boundingRect.y + currentChar.boundingRect.height), new Scalar(0, 255, 0), 5);

                    Rect rect = new Rect((int) Math.pow(currentChar.boundingRect.x - 1.25 * currentChar.boundingRect.width, 1), currentChar.boundingRect.y, (int) Math.pow(1.05 * currentChar.boundingRect.width, 1), currentChar.boundingRect.height);
                    //  System.out.println(currentChar.boundingRect.x + " " + currentChar.boundingRect.y + " " + currentChar.boundingRect.width + " " + currentChar.boundingRect.height);
                    Mat test = imgThreshColor.submat(rect);
                    imgCropped.add(test);

                    Imgproc.rectangle(imgThreshColor, new Point(currentChar.boundingRect.x, currentChar.boundingRect.y), new Point(currentChar.boundingRect.x + currentChar.boundingRect.width, currentChar.boundingRect.y + currentChar.boundingRect.height), new Scalar(0, 255, 0), 5);
                    Rect rect2 = new Rect(currentChar.boundingRect.x, currentChar.boundingRect.y, currentChar.boundingRect.width, currentChar.boundingRect.height);
                    System.out.println(currentChar.boundingRect.x + " " + currentChar.boundingRect.y + " " + currentChar.boundingRect.width + " " + currentChar.boundingRect.height);
                    Mat test2 = imgThreshColor.submat(rect2);
                    imgCropped.add(test2);
                    i++;
                }
                else {
                    Imgproc.rectangle(imgThreshColor, new Point(currentChar.boundingRect.x, currentChar.boundingRect.y), new Point(currentChar.boundingRect.x + currentChar.boundingRect.width, currentChar.boundingRect.y + currentChar.boundingRect.height), new Scalar(0, 255, 0), 5);
                    Rect rect3 = new Rect(currentChar.boundingRect.x, currentChar.boundingRect.y, currentChar.boundingRect.width, currentChar.boundingRect.height);
                    System.out.println(currentChar.boundingRect.x + " " + currentChar.boundingRect.y + " " + currentChar.boundingRect.width + " " + currentChar.boundingRect.height);
                    Mat test3 = imgThreshColor.submat(rect3);
                    imgCropped.add(test3);
                    i++;
                }
            }

        }

        System.out.println("size"+imgCropped.size());
        return(strChars);               // return result
    }

}
