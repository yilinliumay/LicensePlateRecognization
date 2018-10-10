package com.example.mayliu.opencvplate;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by mayliu on 2018/4/3.
 */

public class Preprocess {

    private Bitmap srcimage;
    private Mat imgGray= new Mat();
    private Mat imgMaxContrastGrayscale;
    private Mat imgBlurred;
    private Mat imgThresh;
    public Mat imgTopHat=new Mat();
    public  Mat imgBlackHat=new Mat();
    public Mat imgGrayscalePlusTopHat=new Mat();


    Preprocess(Bitmap srcimage){
        this.srcimage=srcimage;
    }

    public void TurntoGray() {
        Mat srcMat = new Mat();
        Utils.bitmapToMat(srcimage, srcMat);
        Mat src = new Mat();
        //用来存放三色通道
        List<Mat> hsv_channel = new ArrayList<Mat>();
        //RGB->HSV
        Imgproc.cvtColor(srcMat, src, Imgproc.COLOR_BGR2HSV);
        //分解H,S,V
        Core.split(src, hsv_channel);
        //提取V
        imgGray=hsv_channel.get(2);
    }

    public Mat Manage(){
        Mat srcMat = new Mat();
        Mat dst=new Mat();
        Utils.bitmapToMat(srcimage, srcMat);
        Mat hsv = new Mat();
        Mat finalsrc=new Mat();
        //用来存放三色通道
        List<Mat> hsv_channel = new ArrayList<Mat>();
        //RGB->HSV
        Imgproc.cvtColor(srcMat, hsv, Imgproc.COLOR_RGB2HSV);
        //分解H,S,V
        Core.split(hsv, hsv_channel);
        dst=hsv_channel.get(0);

       // Mat skinMask = new Mat(src.rows(), src.cols(), CvType.CV_8U, new Scalar(3));
       Core.inRange(hsv, new Scalar(100, 100, 80), new Scalar(120, 255, 255), dst);
        Core.bitwise_not(dst,dst);

        return dst;
    }




    public void maximizeContrast(){


        imgMaxContrastGrayscale=new Mat();
        Mat structElement = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT,
                new Size(3, 3), new Point(-1, -1));
        Imgproc.morphologyEx(imgGray, imgTopHat, Imgproc.MORPH_TOPHAT, structElement);
        Imgproc.morphologyEx(imgGray, imgBlackHat, Imgproc.MORPH_BLACKHAT, structElement);
        Core.add(imgGray,imgTopHat,imgGrayscalePlusTopHat);
        Core.subtract(imgGrayscalePlusTopHat,imgBlackHat,imgMaxContrastGrayscale);
        Core.add(imgMaxContrastGrayscale,imgTopHat,imgMaxContrastGrayscale);
        Core.subtract(imgMaxContrastGrayscale,imgBlackHat,imgMaxContrastGrayscale);

    }

    public void GaussianBlur(){
        imgBlurred=new Mat();
        Imgproc.GaussianBlur(imgMaxContrastGrayscale, imgBlurred, new Size(5, 5),0);

    }

    //https://blog.csdn.net/jianguo_cui/article/details/7380784
    public void adaptiveThreshold(){
        imgThresh=new Mat();
        Imgproc.adaptiveThreshold(imgBlurred, imgThresh, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 55, 7);
       // Imgproc.threshold(imgBlurred, imgThresh, 0.0, 255.0, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

    }

    public Mat getImgGray(){
        return imgGray;
    }
    public Mat getImgMaxContrastGrayscale(){
        return imgMaxContrastGrayscale;
    }
    public Mat getImgBlurred(){
        return imgBlurred;
    }
    public Mat getImgThresh(){
        return imgThresh;
    }





}
