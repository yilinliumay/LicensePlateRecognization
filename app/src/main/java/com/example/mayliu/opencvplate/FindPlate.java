package com.example.mayliu.opencvplate;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Random;

import static org.opencv.core.CvType.CV_8UC3;

/**
 * Created by mayliu on 2018/4/3.
 */

public class FindPlate {

    private Mat imgThresh;
    private ArrayList<PossibleChar> vectorOfPossibleCharsInScene;
    private ArrayList<ArrayList<PossibleChar>> vectorOfVectorsOfMatchingCharsInScene;


    public ArrayList<PossibleChar> getVectorOfPossibleCharsInScene() {
        return vectorOfPossibleCharsInScene;
    }

    public void setVectorOfPossibleCharsInScene(ArrayList<PossibleChar> vectorOfPossibleCharsInScene) {
        this.vectorOfPossibleCharsInScene = vectorOfPossibleCharsInScene;
    }

    public ArrayList<ArrayList<PossibleChar>> getVectorOfVectorsOfMatchingCharsInScene() {
        return vectorOfVectorsOfMatchingCharsInScene;
    }

    public void setVectorOfVectorsOfMatchingCharsInScene(ArrayList<ArrayList<PossibleChar>> vectorOfVectorsOfMatchingCharsInScene) {
        this.vectorOfVectorsOfMatchingCharsInScene = vectorOfVectorsOfMatchingCharsInScene;
    }




    FindPlate(Mat imgThresh){
        this.imgThresh=imgThresh;
    }

    public Mat findAllPossibleChars(){ //先去掉一些细碎的

        findPossibleChars fbc=new findPossibleChars();



        vectorOfPossibleCharsInScene = fbc.findPossibleCharsInScene(imgThresh); //所有轮廓


        Mat imgContours = new Mat(imgThresh.size(), CV_8UC3, new Scalar(0, 0, 0));
        ArrayList<MatOfPoint> contours= new ArrayList<MatOfPoint>();

        for (PossibleChar possibleChar : vectorOfPossibleCharsInScene) {
            contours.add(possibleChar.contour);
        }
        //画出所有经过第一遍筛选的值
        Imgproc.drawContours(imgContours, contours, -1, new Scalar(0, 355, 50),5);
        return imgContours;
    }

    public Mat findByGroup() {

        findMatchingChars fmc=new findMatchingChars();
        vectorOfVectorsOfMatchingCharsInScene = fmc.findVectorOfVectorsOfMatchingChars(vectorOfPossibleCharsInScene);

        System.out.println("第二歩：获得的轮廓集个数：" + vectorOfVectorsOfMatchingCharsInScene.size());
        Mat secondImgContours = new Mat(imgThresh.size(), CV_8UC3, new Scalar(0, 0, 0));//BLACK
        for (ArrayList<PossibleChar> vectorOfMatchingChars : vectorOfVectorsOfMatchingCharsInScene) {
            // int intRandomBlue = rng.uniform(0, 256);
            //ArrayList<PossibleChar> vectorOfMatchingChars=vectorOfVectorsOfMatchingCharsInScene.get(0);
            System.out.println(vectorOfMatchingChars.size());
            Random random = new java.util.Random();
            int intRandomBlue = random.nextInt(256);
            int intRandomGreen = random.nextInt(256);
            int intRandomRed = random.nextInt(256);

            ArrayList<MatOfPoint> secondContours = new ArrayList<MatOfPoint>();

            for (PossibleChar matchingChar : vectorOfMatchingChars) {
                secondContours.add(matchingChar.contour);
            }

            Imgproc.drawContours(secondImgContours, secondContours, -1, new Scalar((double) intRandomBlue, (double) intRandomGreen, (double) intRandomRed), 3);

        }
        return secondImgContours;
    }
}
