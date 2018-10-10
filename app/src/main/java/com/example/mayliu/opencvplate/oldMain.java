package com.example.mayliu.opencvplate;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class oldMain extends Activity {
}

   /* Button btnProcess;
    Bitmap srcBitmap;
    Bitmap grayBitmap;
    Bitmap cannyBitmap;
    Bitmap erodeBitmap;
    Bitmap blurBitmap;
    Bitmap cBitmap;
    ImageView imgHuaishi;
    Button btn_gray_process;
    Button btn_canny_process;
    Button btn_erode_process;
    Button btn_contour_process;
    Mat grayMat;
    Mat edges;
    Mat dst;
    Mat srcMat;
    private static boolean flag = true;
    private static boolean isFirst = true;
    private static boolean flag1 = true;
    private static boolean isFirst1 = true;
    private static boolean flag2 = true;
    private static boolean isFirst2 = true;
    private static boolean flag3 = true;
    private static boolean isFirst3 = true;
    private static final String TAG = "MainActivity";

    //OpenCV库加载并初始化成功后的回调函数
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            // TODO Auto-generated method stub
            switch (status){
                case BaseLoaderCallback.SUCCESS:
                    Log.i(TAG, "成功加载");
                    break;
                default:
                    super.onManagerConnected(status);
                    Log.i(TAG, "加载失败");
                    break;
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        this.setTitle("应用NDK转换灰度图");
        btnProcess = (Button) findViewById(R.id.btn_gray_process);
        btn_canny_process = (Button) findViewById(R.id.btn_canny_process);
        btn_erode_process = (Button) findViewById(R.id.btn_erode_process);
        btn_contour_process = (Button) findViewById(R.id.btn_contour_process);
        imgHuaishi = (ImageView) findViewById(R.id.img_huaishi);
        srcBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.car1);
        imgHuaishi.setImageBitmap(srcBitmap);
        btnProcess.setOnClickListener(new ProcessClickListener());
        btn_canny_process .setOnClickListener(new CannyProcessClickListener());
        btn_erode_process .setOnClickListener(new ErodeProcessClickListener());
        btn_contour_process .setOnClickListener(new ContourProcessClickListener());

    }


    //@Override
    // public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    //getMenuInflater().inflate(R.menu.activity_main, menu);
    //return true;
    // }

    public void initUI(){
        btnProcess = (Button)findViewById(R.id.btn_gray_process);
        imgHuaishi = (ImageView)findViewById(R.id.img_huaishi);
        Log.i(TAG, "initUI sucess...");

    }

    public void procSrc2Gray(){
        srcMat = new Mat();
        Utils.bitmapToMat(srcBitmap, srcMat);
        Mat rgbMat = new Mat();
        grayMat = new Mat();
        grayBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Config.RGB_565);
        Utils.bitmapToMat(srcBitmap, rgbMat);//convert original bitmap to Mat, R G B.
        Imgproc.cvtColor(rgbMat, grayMat, Imgproc.COLOR_RGB2GRAY);//rgbMat to gray grayMat
        Utils.matToBitmap(grayMat, grayBitmap); //convert mat to bitmap
        Log.i(TAG, "procSrc2Gray sucess...");


    }
    public void CannyDetection(){
        Mat rgba = new Mat();
        Utils.bitmapToMat( srcBitmap, rgba);
        edges = new Mat(rgba.size(), CvType.CV_8UC1);
        cannyBitmap = Bitmap.createBitmap(edges.cols(), edges.rows(), Bitmap.Config.ARGB_8888);
        Imgproc.cvtColor(rgba, edges, Imgproc.COLOR_RGB2GRAY, 4);
        Imgproc.Canny(edges, edges, 200, 100);
        Utils.matToBitmap(edges, cannyBitmap);
        Log.i(TAG, "CannyDetection sucess...");
    }

    public void erodeOrDilate(String command, String Xcommand,Bitmap bitmap, int iteration) {
        Mat strElement;
        Mat src=new Mat();
        Utils.bitmapToMat(bitmap, src);
        dst=new Mat(src.size(), CvType.CV_32SC1);
        erodeBitmap= Bitmap.createBitmap(dst.cols(), dst.rows(), Bitmap.Config.ARGB_8888);
        Boolean isErode =command.equals("Erode");
        Boolean  isX=Xcommand.equals("X");
        if(isX) {
            strElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
                    new Size(29, 25), new Point(3, 3));
        }
        else{
            strElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
                    new Size(18, 14), new Point(3, 3));
        }
        if (isErode) {
            Imgproc.erode(src, dst, strElement, new Point(-1, -1), iteration);
        } else {
            Imgproc.dilate(src, dst, strElement, new Point(-1, -1), iteration);
        }
        Utils.matToBitmap(dst, erodeBitmap);
        strElement.release();
        src.release();
        // dst.release();
    }
    public void Blur(Mat blurr_image){
        Imgproc.GaussianBlur(blurr_image, blurr_image, new Size(5, 5), 5);
        //Imgproc.medianBlur(blurr_image, dstImage, 15);
        //Utils.matToBitmap(blurr_image, bitmap);

    }
    public void contourProfile(Mat blurredImage) {
        Imgproc.cvtColor(blurredImage,blurredImage, Imgproc.COLOR_RGB2GRAY, 4);
        Imgproc.Canny(blurredImage, blurredImage, 200, 100);
        Blur(blurredImage);
        List<MatOfPoint> contours=new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(blurredImage,contours,hierarchy,Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);
        double maxArea = -1;
        int maxAreaIdx = 4;
        MatOfPoint temp_contour=contours.get(4);//假设最大的轮廓在index=0处
        MatOfPoint2f approxCurve=new MatOfPoint2f();
        // for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++)
        //{
        temp_contour=contours.get(4);
        double contourarea=Imgproc.contourArea(temp_contour);
        //if (contourarea>maxArea) {
        MatOfPoint2f new_mat = new MatOfPoint2f(temp_contour.toArray());
        int contourSize = (int) temp_contour.total();
        MatOfPoint2f approxCurve_temp = new MatOfPoint2f();
        Imgproc.approxPolyDP(new_mat, approxCurve_temp, contourSize * 0.03, true);


        //if (approxCurve_temp.total() == 4) {
        maxArea = contourarea;
        maxAreaIdx = 4;
        approxCurve=approxCurve_temp;
        //    }
        // }
        //}
        double[] temp_double=approxCurve.get(0,0);
        Point point1=new Point(temp_double[0],temp_double[1]);

        temp_double=approxCurve.get(1,0);
        Point point2=new Point(temp_double[0],temp_double[1]);

        temp_double=approxCurve.get(2,0);
        Point point3=new Point(temp_double[0],temp_double[1]);
        temp_double=approxCurve.get(3,0);

        Point point4=new Point(temp_double[0],temp_double[1]);

        List<Point> source=new ArrayList<>();
        source.add(point1);
        source.add(point2);
        source.add(point3);
        source.add(point4);
        //对4个点进行排序
        Point centerPoint=new Point(0,0);//质心
        for (Point corner:source){
            centerPoint.x+=corner.x;
            centerPoint.y+=corner.y;
        }
        centerPoint.x=centerPoint.x/source.size();
        centerPoint.y=centerPoint.y/source.size();
        Point lefttop=new Point();
        Point righttop=new Point();
        Point leftbottom=new Point();
        Point rightbottom=new Point();
        for (int i=0;i<source.size();i++){
            if (source.get(i).x<centerPoint.x&&source.get(i).y<centerPoint.y){
                lefttop=source.get(i);
            }else if (source.get(i).x>centerPoint.x&&source.get(i).y<centerPoint.y){
                righttop=source.get(i);
            }else if (source.get(i).x<centerPoint.x&& source.get(i).y>centerPoint.y){
                leftbottom=source.get(i);
            }else if (source.get(i).x>centerPoint.x&&source.get(i).y>centerPoint.y){
                rightbottom=source.get(i);
            }
        }
        System.out.println(lefttop.x);
        System.out.println(lefttop.y);
        System.out.println(righttop.x);
        System.out.println(righttop.y);
        System.out.println(leftbottom.x);
        System.out.println(leftbottom.y);
        System.out.println(rightbottom.x);
        System.out.println(rightbottom.y);




        Rect rect = new Rect(32, 24, 320, 40); // 设置矩形ROI的位置
        Mat imgRectROI= srcMat.submat( rect );

        Mat mRgba=new Mat();
        mRgba.create(blurredImage.rows(), blurredImage.cols(), CvType.CV_8UC3);
        Imgproc.drawContours(mRgba,contours, 5, new Scalar(0, 255, 0), 5);
        //绘制检测到的轮廓
        Log.i(TAG, "Buildaaaaa sucess...");
        Blur(mRgba);
        cBitmap= Bitmap.createBitmap(imgRectROI.cols(), imgRectROI.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(imgRectROI, cBitmap);
    }


    private class ProcessClickListener implements OnClickListener{

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if(isFirst)
            {
               /* new Handler().postDelayed(new Runnable(){

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        procSrc2Gray();
                    }

                }, 500);*/
              /*  procSrc2Gray();
                isFirst = false;
            }
            if(flag){
                imgHuaishi.setImageBitmap(grayBitmap);
                //btnProcess.setText("查看原图");
                flag = false;
            }
            else{
                imgHuaishi.setImageBitmap(srcBitmap);
                // btnProcess.setText("灰度化");
                flag = true;
            }
        }

    }
    private class CannyProcessClickListener implements OnClickListener{

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if(isFirst1)
            {
                /*new Handler().postDelayed(new Runnable(){

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        CannyDetection();
                    }

                }, 500);*/
                /*CannyDetection();
                isFirst1 = false;
            }
            if(flag1){
                imgHuaishi.setImageBitmap(cannyBitmap);
                //btnProcess.setText("查看原图");
                flag1 = false;
            }
            else{
                imgHuaishi.setImageBitmap(srcBitmap);
                // btnProcess.setText("灰度化");
                flag1 = true;
            }
        }

    }
   /* private class ErodeProcessClickListener implements OnClickListener{

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if(isFirst2)
            {
                Blur(edges);
                erodeOrDilate("Dilate","X",cannyBitmap,16);
                erodeOrDilate("Erode","X",cannyBitmap,8);
                erodeOrDilate("Dilate","X",cannyBitmap,16);
                erodeOrDilate("Erode","Y",cannyBitmap,2);
                erodeOrDilate("Dilate","Y",cannyBitmap,4);
                //Blur(erodeBitmap);

                // contourProfile(cannyBitmap);
                isFirst2 = false;
            }
            if(flag2){
                imgHuaishi.setImageBitmap(erodeBitmap);
                //btnProcess.setText("查看原图");
                flag2 = false;
            }
            else{
                imgHuaishi.setImageBitmap(cBitmap);
                // btnProcess.setText("灰度化");
                flag2 = true;
            }
        }

    }
    private class ContourProcessClickListener implements OnClickListener{

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if(isFirst3)
            {
                contourProfile(dst);
                isFirst3 = false;
            }
            if(flag3){
                imgHuaishi.setImageBitmap(cBitmap);
                //btnProcess.setText("查看原图");
                flag3 = false;
            }
            else{
                imgHuaishi.setImageBitmap(srcBitmap);
                // btnProcess.setText("灰度化");
                flag3 = true;
            }
        }

    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }*/





//Blur
//Mat blurred = src.clone();
//Imgproc.medianBlur(src, blurred, 9);


//trun to CvType.CV_8U
        /*Mat gray0 = new Mat(blurred.size(), CvType.CV_8U), gray = new Mat();

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        List<Mat> blurredChannel = new ArrayList<Mat>();
        blurredChannel.add(blurred);
        List<Mat> gray0Channel = new ArrayList<Mat>();
        gray0Channel.add(gray0);

        MatOfPoint2f approxCurve;

        double maxArea = 0;
        int maxId = -1;

        for (int c = 0; c < 3; c++) {
            int ch[] = { c, 0 };
            Core.mixChannels(blurredChannel, gray0Channel, new MatOfInt(ch));

            int thresholdLevel = 1;
            for (int t = 0; t < thresholdLevel; t++) {
                if (t == 0) {
                    Imgproc.Canny(gray0, gray, 10, 20, 3, true); // true ?
                    Mat strElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
                            new Size(1, 7), new Point(-1, -1));
                    Imgproc.dilate(gray, gray, strElement, new Point(-1, -1), 2);
                    Imgproc.erode(gray, gray, strElement, new Point(-1, -1), 1);

                    Mat strElement1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
                            new Size(3, 1), new Point(-1, -1));
                    Imgproc.dilate(gray, gray, strElement1, new Point(-1, -1), 2);
                    Imgproc.erode(gray, gray, strElement1, new Point(-1, -1), 1);
                    Imgproc.dilate(gray, gray, strElement1, new Point(-1, -1), 2);
                    // 1
                    // ?
                } else {
                    //阙值分割
                    Imgproc.adaptiveThreshold(gray0, gray, thresholdLevel,
                            Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                            Imgproc.THRESH_BINARY,
                            (src.width() + src.height()) / 200, t);
                }

                Imgproc.findContours(gray, contours, new Mat(),
                        Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

                for (MatOfPoint contour : contours) {
                    MatOfPoint2f temp = new MatOfPoint2f(contour.toArray());

                    double area = Imgproc.contourArea(contour);
                    approxCurve = new MatOfPoint2f();
                    Imgproc.approxPolyDP(temp, approxCurve,
                            Imgproc.arcLength(temp, true) * 0.02, true);

                    if (approxCurve.total() == 4 && area >= maxArea) {

                        double maxCosine = 0;

                        List<Point> curves = approxCurve.toList();
                        for (int j = 2; j < 5; j++) {

                            double cosine = Math.abs(angle(curves.get(j % 4),
                                    curves.get(j - 2), curves.get(j - 1)));
                            maxCosine = Math.max(maxCosine, cosine);
                        }

                        if (maxCosine < 0.3) {
                            maxArea = area;
                            maxId = contours.indexOf(contour);


                        }
                    }
                    //Imgproc.drawContours(gray,contours, 5, new Scalar(0, 255, 0), 5);
                }
            }
        }
        System.out.println(maxId);
        if (maxId >= 0) {
            Rect rect = Imgproc.boundingRect(contours.get(maxId));
            Mat imgRectROI= src.submat( rect );
            Imgproc.rectangle(gray, rect.tl(), rect.br(), new Scalar(255, 0, 0,
                    .8), 4);


            //绘制检测到的轮廓
            int mDetectedWidth = rect.width;
            int mDetectedHeight = rect.height;

            Log.d(TAG, "Rectangle width :"+mDetectedWidth+ " Rectangle height :"+mDetectedHeight);

        }*/

