//package com.example.mayliu.opencvplate;
//
//import android.app.Activity;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//
//import org.opencv.android.BaseLoaderCallback;
//import org.opencv.android.LoaderCallbackInterface;
//import org.opencv.android.OpenCVLoader;
//import org.opencv.android.Utils;
//import org.opencv.core.Core;
//import org.opencv.core.Mat;
//import org.opencv.core.MatOfPoint;
//import org.opencv.core.MatOfPoint2f;
//import org.opencv.core.Point;
//import org.opencv.core.Rect;
//import org.opencv.core.RotatedRect;
//import org.opencv.core.Scalar;
//import org.opencv.core.Size;
//import org.opencv.imgproc.Imgproc;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//import java.util.Random;
//
//import static java.lang.Math.PI;
//import static java.lang.Math.abs;
//import static java.lang.Math.asin;
//import static java.lang.Math.atan;
//import static java.lang.Math.pow;
//import static org.opencv.core.CvType.CV_8UC3;
//
///**
// * Created by mayliu on 2018/4/3.
// */
//
//public class newMain {
//    package com.example.mayliu.opencvplate;
//
//import org.opencv.android.BaseLoaderCallback;
//import org.opencv.android.LoaderCallbackInterface;
//import org.opencv.android.OpenCVLoader;
//import org.opencv.android.Utils;
//import org.opencv.core.Core;
//import org.opencv.core.CvType;
//import org.opencv.core.Mat;
//import org.opencv.core.MatOfInt;
//import org.opencv.core.MatOfPoint;
//import org.opencv.core.MatOfPoint2f;
//import org.opencv.core.Point;
//import org.opencv.core.Rect;
//import org.opencv.core.RotatedRect;
//import org.opencv.core.Scalar;
//import org.opencv.core.Size;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.imgproc.Imgproc;
//
//import android.app.Activity;
//import android.graphics.Bitmap;
//import android.graphics.Bitmap.Config;
//import android.graphics.BitmapFactory;
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Log;
//import android.view.Menu;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.ImageView;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//import java.util.Random;
//
//import static java.lang.Math.PI;
//import static java.lang.Math.abs;
//import static java.lang.Math.asin;
//import static java.lang.Math.atan;
//import static java.lang.Math.pow;
//import static org.opencv.core.CvType.CV_8UC3;
//
//    public class MainActivity extends Activity {
//
//
//
//
//
//
//        private  Bitmap findRectangle(Bitmap image) {
//            Mat tempor = new Mat();
//            Mat src = new Mat();
//            Utils.bitmapToMat(image, tempor);
//
//            //trun to gray picture
//            List<Mat> hsv_channel = new ArrayList<Mat>();
//            Imgproc.cvtColor(tempor, src, Imgproc.COLOR_BGR2HSV);
//            Core.split(src, hsv_channel);
//            Mat imgValue=hsv_channel.get(2);
//
//            //maximizeContrast
//            Mat imgTopHat=new Mat();
//            Mat imgBlackHat=new Mat();
//            Mat imgGrayscalePlusTopHat=new Mat();
//            Mat imgMaxContrastGrayscale=new Mat();
//            Mat structElement = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT,
//                    new Size(3, 3), new Point(-1, -1));
//            Imgproc.morphologyEx(imgValue, imgTopHat, Imgproc.MORPH_TOPHAT, structElement);
//            Imgproc.morphologyEx(imgValue, imgBlackHat, Imgproc.MORPH_BLACKHAT, structElement);
//            Core.add(imgValue,imgTopHat,imgGrayscalePlusTopHat);
//            Core.subtract(imgGrayscalePlusTopHat,imgBlackHat,imgMaxContrastGrayscale);
//
//
//
//            //GaussianBlur
//            Mat imgBlurred=new Mat();
//            Imgproc.GaussianBlur(imgMaxContrastGrayscale, imgBlurred, new Size(5, 5), 0);
//
//
//            //https://blog.csdn.net/jianguo_cui/article/details/7380784
//            Mat imgThresh=new Mat();
//            Imgproc.adaptiveThreshold(imgBlurred, imgThresh, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 19, 9);
//
//            // find all possible chars in the scene,
//            // this function first finds all contours, then only includes contours that could be chars (without comparison to other chars yet)
//            ArrayList<PossibleChar> vectorOfPossibleCharsInScene = findPossibleCharsInScene(imgThresh);
//            System.out.println("step 2 - vectorOfPossibleCharsInScene.Count = "+vectorOfPossibleCharsInScene.size());
//
//            Mat imgContours = new Mat(tempor.size(), CV_8UC3, new Scalar(0, 0, 0));
//            ArrayList<MatOfPoint> contours= new ArrayList<MatOfPoint>();
//
//            for (PossibleChar possibleChar : vectorOfPossibleCharsInScene) {
//                contours.add(possibleChar.contour);
//            }
//            //画出所有经过第一遍筛选的值
//            Imgproc.drawContours(imgContours, contours, -1, new Scalar(255, 255, 255));
//            //按照group找到更适合的值
//            ArrayList<ArrayList<PossibleChar>> vectorOfVectorsOfMatchingCharsInScene = findVectorOfVectorsOfMatchingChars(vectorOfPossibleCharsInScene);
//
//            System.out.println("step 3 - vectorOfVectorsOfMatchingCharsInScene.size() ="+vectorOfVectorsOfMatchingCharsInScene.size() );
//            Mat secondImgContours=new Mat(tempor.size(), CV_8UC3, new Scalar(0, 0, 0));//BLACK
//            for (ArrayList<PossibleChar> vectorOfMatchingChars : vectorOfVectorsOfMatchingCharsInScene) {
//                // int intRandomBlue = rng.uniform(0, 256);
//                //ArrayList<PossibleChar> vectorOfMatchingChars=vectorOfVectorsOfMatchingCharsInScene.get(0);
//                System.out.println(vectorOfMatchingChars.size());
//                Random random=new java.util.Random();
//                int intRandomBlue=random.nextInt(256);
//                int intRandomGreen = random.nextInt(256);
//                int intRandomRed = random.nextInt(256);
//
//                ArrayList<MatOfPoint> secondContours = new ArrayList<MatOfPoint>();
//
//                for (PossibleChar matchingChar : vectorOfMatchingChars) {
//                    secondContours.add(matchingChar.contour);
//                }
//
//                Imgproc.drawContours(secondImgContours, secondContours, -1, new Scalar((double)intRandomBlue, (double)intRandomGreen, (double)intRandomRed),3);
//
//            }
//
//
//            Mat plate=new Mat();
//            ArrayList<PossiblePlate> vectorOfPossiblePlates=new ArrayList<PossiblePlate>();
//            for (ArrayList<PossibleChar> vectorOfMatchingChars : vectorOfVectorsOfMatchingCharsInScene) {                     // for each group of matching chars
//                PossiblePlate possiblePlate = extractPlate(tempor, vectorOfMatchingChars);        // attempt to extract plate
//                plate=possiblePlate.imgPlate;
//                if (possiblePlate.imgPlate.empty() == false) {                                              // if plate was found
//                    vectorOfPossiblePlates.add(possiblePlate);                                        // add to vector of possible plates
//                }
//                break;
//
//            }
//
//            System.out.println(vectorOfPossiblePlates.size()+" possible plates found");       // 13 with MCLRNF1 image
//
//            for (int i = 0; i < vectorOfPossiblePlates.size(); i++) {
//                Point p2fRectPoints[]= new Point[4];
//
//                vectorOfPossiblePlates.get(i).rrLocationOfPlateInScene.points(p2fRectPoints);
//
//                for (int j = 0; j < 4; j++) {
//                    Imgproc.line(secondImgContours, p2fRectPoints[j], p2fRectPoints[(j + 1) % 4], new Scalar(255,0,0), 2);
//                }
//                //cv::imshow("4a", imgContours);//红色方块图
//
//                System.out.println("possible plate "+i+", click on any image and press a key to continue . . .");
//
//                // cv::imshow("4b", vectorOfPossiblePlates[i].imgPlate);//最终结果
//                //cv::waitKey(0);
//            }
//
//            System.out.println("detection finish");
//
//            int intPlateCounter = 0;				// this is only for showing steps
//            Mat imgContours1;
//            ArrayList<MatOfPoint> contours1= new ArrayList<MatOfPoint>();
//
//            if (vectorOfPossiblePlates.isEmpty()) {               // if vector of possible plates is empty
//                //return(vectorOfPossiblePlates);
//                System.out.println("no plates");
//            }
//            // at this point we can be sure vector of possible plates has at least one plate
//
//            Mat finalresult= new Mat();
//            for (PossiblePlate possiblePlate : vectorOfPossiblePlates) {            // for each possible plate, this is a big for loop that takes up most of the function
//
//                preprocess(possiblePlate.imgPlate, possiblePlate.imgGrayscale, possiblePlate.imgThresh);        // preprocess to get grayscale and threshold images
//
//
//                // upscale size by 60% for better viewing and character recognition
//                Size sz = possiblePlate.imgThresh.size();
//                Imgproc.resize(possiblePlate.imgThresh, possiblePlate.imgThresh, new Size(sz.width * 1.6, sz.height * 1.6));
//                // Imgcodecs.imwrite("test2.jpg", possiblePlate.imgThresh);
//
//                // threshold again to eliminate any gray areas
//                Imgproc.threshold(possiblePlate.imgThresh, possiblePlate.imgThresh, 0.0, 255.0, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
//                //Imgproc.cvtColor(imgThresh, possiblePlate.imgThresh, Imgproc.COLOR_GRAY2BGR);
//                //finalresult=possiblePlate.imgThresh.clone();
//
//                ArrayList<PossibleChar> vectorOfPossibleCharsInPlate = findPossibleCharsInPlate(possiblePlate.imgGrayscale, possiblePlate.imgThresh);
//                imgContours = new Mat(possiblePlate.imgThresh.size(), CV_8UC3, new Scalar(0, 0, 0));
//                contours.clear();
//
//                for (PossibleChar possibleChar : vectorOfPossibleCharsInPlate) {
//                    contours.add(possibleChar.contour);
//                }
//
//                Imgproc.drawContours(imgContours, contours, -1, new Scalar(255, 255, 255));
//                ArrayList<ArrayList<PossibleChar>> vectorOfVectorsOfMatchingCharsInPlate = findVectorOfVectorsOfMatchingChars(vectorOfPossibleCharsInPlate);
//
////#ifdef SHOW_STEPS
//                imgContours = new Mat(possiblePlate.imgThresh.size(), CV_8UC3, new Scalar(0, 0, 0));
//                contours.clear();
//                Mat imgThreshColor=new Mat();
//                Mat imgCropped= new Mat();
//                for (ArrayList<PossibleChar> vectorOfMatchingChars : vectorOfVectorsOfMatchingCharsInPlate) {
//                    Random random = new java.util.Random();
//                    int intRandomBlue = random.nextInt(256);
//                    int intRandomGreen = random.nextInt(256);
//                    int intRandomRed = random.nextInt(256);
//
//                    for (PossibleChar matchingChar : vectorOfMatchingChars) {
//                        contours.add(matchingChar.contour);
//                    }
//
//                    Imgproc.cvtColor(possiblePlate.imgThresh, imgThreshColor, Imgproc.COLOR_GRAY2BGR);
//                    for (PossibleChar currentChar : vectorOfMatchingChars) {
//                        Imgproc.rectangle(imgThreshColor, new Point(currentChar.boundingRect.x,currentChar.boundingRect.y),new Point(currentChar.boundingRect.x + currentChar.boundingRect.width, currentChar.boundingRect.y + currentChar.boundingRect.height), new Scalar(0,255,0));
//                        Rect rect = new Rect(currentChar.boundingRect.x,currentChar.boundingRect.y,currentChar.boundingRect.width,currentChar.boundingRect.height);
//                        imgCropped= imgThreshColor.submat( rect );
//                        Size szt = imgCropped.size();
//                        Imgproc.resize(imgCropped, imgCropped, new Size(32, 40));
//                        break;
//                    }
//                    // Imgproc.drawContours(imgContours, contours, -1, new Scalar((double) intRandomBlue, (double) intRandomGreen, (double) intRandomRed));
//                }
//
//                finalresult=imgCropped.clone();
//                break;
//
//            }
//
//
//
//
//
//            Bitmap bmp;
//            bmp = Bitmap.createBitmap(finalresult.cols(), finalresult.rows(),
//                    Bitmap.Config.ARGB_8888);
//            Utils.matToBitmap(finalresult, bmp);
//            return bmp;
//
//        }
//
//
//        public void preprocess (Mat imgPlate, Mat imgGrayscale, Mat imgThresh){
//            //trun to gray picture
//            List<Mat> hsv_channel = new ArrayList<Mat>();
//            Imgproc.cvtColor(imgPlate, imgGrayscale, Imgproc.COLOR_BGR2HSV);
//            Core.split(imgGrayscale, hsv_channel);
//            Mat imgValue=hsv_channel.get(2);
//
//
//            //maximizeContrast
//            Mat imgTopHat=new Mat();
//            Mat imgBlackHat=new Mat();
//            Mat imgGrayscalePlusTopHat=new Mat();
//            Mat imgMaxContrastGrayscale=new Mat();
//            Mat structElement = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT,
//                    new Size(3, 3), new Point(-1, -1));
//            Imgproc.morphologyEx(imgValue, imgTopHat, Imgproc.MORPH_TOPHAT, structElement);
//            Imgproc.morphologyEx(imgValue, imgBlackHat, Imgproc.MORPH_BLACKHAT, structElement);
//            Core.add(imgValue,imgTopHat,imgGrayscalePlusTopHat);
//            Core.subtract(imgGrayscalePlusTopHat,imgBlackHat,imgMaxContrastGrayscale);
//
//            //GaussianBlur
//            Mat imgBlurred=new Mat();
//            Imgproc.GaussianBlur(imgMaxContrastGrayscale, imgBlurred, new Size(5, 5), 0);
//
//
//            //https://blog.csdn.net/jianguo_cui/article/details/7380784
//            Imgproc.adaptiveThreshold(imgBlurred, imgThresh, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 19, 9);
//        }
//
//
//        public List<PossiblePlate> detectCharsInPlates(List<PossiblePlate> vectorOfPossiblePlates){
//         int intPlateCounter = 0;				// this is only for showing steps
//         Mat imgContours;
//         ArrayList<MatOfPoint> contours= new ArrayList<MatOfPoint>();
//
//         if (vectorOfPossiblePlates.isEmpty()) {               // if vector of possible plates is empty
//             return(vectorOfPossiblePlates);                 // return
//         }
//         // at this point we can be sure vector of possible plates has at least one plate
//
//         for (PossiblePlate possiblePlate : vectorOfPossiblePlates) {            // for each possible plate, this is a big for loop that takes up most of the function
//
//             preprocess(possiblePlate.imgPlate, possiblePlate.imgGrayscale, possiblePlate.imgThresh);        // preprocess to get grayscale and threshold images
//
//
//             // upscale size by 60% for better viewing and character recognition
//             Size sz = possiblePlate.imgThresh.size();
//             Imgproc.resize(possiblePlate.imgThresh, possiblePlate.imgThresh, new Size(sz.width * 1.6, sz.height * 1.6));
//             // Imgcodecs.imwrite("test2.jpg", possiblePlate.imgThresh);
//
//             // threshold again to eliminate any gray areas
//             Imgproc.threshold(possiblePlate.imgThresh, possiblePlate.imgThresh, 0.0, 255.0, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
//
//
//             // find all possible chars in the plate,
//             // this function first finds all contours, then only includes contours that could be chars (without comparison to other chars yet)
//             ArrayList<PossibleChar> vectorOfPossibleCharsInPlate = findPossibleCharsInPlate(possiblePlate.imgGrayscale, possiblePlate.imgThresh);
//
//     //ifdef SHOW_STEPS
//             imgContours = new Mat(possiblePlate.imgThresh.size(), CV_8UC3, new Scalar(0, 0, 0));
//             contours.clear();
//
//             for (PossibleChar possibleChar : vectorOfPossibleCharsInPlate) {
//                 contours.add(possibleChar.contour);
//             }
//
//             Imgproc.drawContours(imgContours, contours, -1, new Scalar(255, 255, 255));
//
//             // cv::imshow("6", imgContours);
//     //#endif	// SHOW_STEPS
//
//             // given a vector of all possible chars, find groups of matching chars within the plate
//             ArrayList<ArrayList<PossibleChar>> vectorOfVectorsOfMatchingCharsInPlate = findVectorOfVectorsOfMatchingChars(vectorOfPossibleCharsInPlate);
//
//     //#ifdef SHOW_STEPS
//             imgContours = new Mat(possiblePlate.imgThresh.size(), CV_8UC3, new Scalar(0, 0, 0));
//             contours.clear();
//
//             for (ArrayList<PossibleChar> vectorOfMatchingChars : vectorOfVectorsOfMatchingCharsInPlate) {
//                 Random random = new java.util.Random();
//                 int intRandomBlue = random.nextInt(256);
//                 int intRandomGreen = random.nextInt(256);
//                 int intRandomRed = random.nextInt(256);
//
//                 for (PossibleChar matchingChar : vectorOfMatchingChars) {
//                     contours.add(matchingChar.contour);
//                 }
//                 Imgproc.drawContours(imgContours, contours, -1, new Scalar((double) intRandomBlue, (double) intRandomGreen, (double) intRandomRed));
//             }
//             // cv::imshow("7", imgContours);
//     //#endif	// SHOW_STEPS
//
//             if (vectorOfVectorsOfMatchingCharsInPlate.size() == 0) {                // if no groups of matching chars were found in the plate
//     //#ifdef SHOW_STEPS
//                 System.out.println("chars found in plate number " + intPlateCounter + " = (none), click on any image and press a key to continue . . .");
//                 intPlateCounter++;
//     //#endif	// SHOW_STEPS
//                 possiblePlate.strChars = "";            // set plate string member variable to empty string
//                 continue;                               // go back to top of for loop
//             }
//
//             for (ArrayList<PossibleChar> vectorOfMatchingChars : vectorOfVectorsOfMatchingCharsInPlate) {                                         // for each vector of matching chars in the current plate
//                 Collections.sort(vectorOfMatchingChars,
//                         new Comparator<PossibleChar>() {
//                             public int compare(PossibleChar o1, PossibleChar o2) {
//                                 if(o1.intCenterX>o2.intCenterX) return 1;
//                                 else return -1;
//                             }
//                         });  // sort the chars left to right
//                 vectorOfMatchingChars = removeInnerOverlappingChars(vectorOfMatchingChars);                                     // and eliminate any overlapping chars
//             }
//
//     // SHOW_STEPS
//             imgContours = new Mat(possiblePlate.imgThresh.size(), CV_8UC3, new Scalar(0, 0, 0));
//
//             for (ArrayList<PossibleChar> vectorOfMatchingChars : vectorOfVectorsOfMatchingCharsInPlate) {
//                 Random random = new java.util.Random();
//                 int intRandomBlue = random.nextInt(256);
//                 int intRandomGreen = random.nextInt(256);
//                 int intRandomRed = random.nextInt(256);
//
//                 contours.clear();
//
//                 for (PossibleChar matchingChar : vectorOfMatchingChars) {
//                     contours.add(matchingChar.contour);
//                 }
//                 Imgproc.drawContours(imgContours, contours, -1, new Scalar((double) intRandomBlue, (double) intRandomGreen, (double) intRandomRed));
//             }
//             //cv::imshow("8", imgContours);
//
//
//             // within each possible plate, suppose the longest vector of potential matching chars is the actual vector of chars
//             int intLenOfLongestVectorOfChars = 0;
//             int intIndexOfLongestVectorOfChars = 0;
//             // loop through all the vectors of matching chars, get the index of the one with the most chars
//             for (int i = 0; i < vectorOfVectorsOfMatchingCharsInPlate.size(); i++) {
//                 if (vectorOfVectorsOfMatchingCharsInPlate.get(i).size() > intLenOfLongestVectorOfChars) {
//                     intLenOfLongestVectorOfChars = vectorOfVectorsOfMatchingCharsInPlate.get(i).size();
//                     intIndexOfLongestVectorOfChars = i;
//                 }
//             }
//             // suppose that the longest vector of matching chars within the plate is the actual vector of chars
//             ArrayList<PossibleChar> longestVectorOfMatchingCharsInPlate = vectorOfVectorsOfMatchingCharsInPlate.get(intIndexOfLongestVectorOfChars);
//
//     // SHOW_STEPS
//             imgContours = new Mat(possiblePlate.imgThresh.size(), CV_8UC3, new Scalar(0, 0, 0));
//
//             contours.clear();
//
//             for (PossibleChar matchingChar : longestVectorOfMatchingCharsInPlate) {
//                 contours.add(matchingChar.contour);
//             }
//             Imgproc.drawContours(imgContours, contours, -1, new Scalar(255, 255, 255));
//
//             //cv::imshow("9", imgContours);
//
//
//         }
//
//         return(vectorOfPossiblePlates);
//     }
//
//
//
//        public  ArrayList<PossibleChar> removeInnerOverlappingChars(ArrayList<PossibleChar> vectorOfMatchingChars) {
//
//            double MIN_DIAG_SIZE_MULTIPLE_AWAY = 0.3;
//            double MAX_DIAG_SIZE_MULTIPLE_AWAY = 5.0;
//            ArrayList <PossibleChar> vectorOfMatchingCharsWithInnerCharRemoved=vectorOfMatchingChars;
//
//             for (PossibleChar currentChar : vectorOfMatchingChars) {
//                 for (PossibleChar otherChar : vectorOfMatchingChars) {
//                     if (currentChar != otherChar) {                         // if current char and other char are not the same char . . .
//                         // if current char and other char have center points at almost the same location . . .
//                         if (distanceBetweenChars(currentChar, otherChar) < (currentChar.dblDiagonalSize * MIN_DIAG_SIZE_MULTIPLE_AWAY)) {
//                             // if we get in here we have found overlapping chars
//                             // next we identify which char is smaller, then if that char was not already removed on a previous pass, remove it
//
//                             // if current char is smaller than other char
//                             if (currentChar.boundingRect.area() < otherChar.boundingRect.area()) {
//                                 // look for char in vector with an iterator
//                                 std::vector<PossibleChar>::iterator currentCharIterator = std::find(vectorOfMatchingCharsWithInnerCharRemoved.begin(), vectorOfMatchingCharsWithInnerCharRemoved.end(), currentChar);
//                                 // if iterator did not get to end, then the char was found in the vector
//                                 if (currentCharIterator != vectorOfMatchingCharsWithInnerCharRemoved.end()) {
//                                     vectorOfMatchingCharsWithInnerCharRemoved.erase(currentCharIterator);       // so remove the char
//                                 }
//                             }
//                             else {        // else if other char is smaller than current char
//                                 // look for char in vector with an iterator
//                                 std::vector<PossibleChar>::iterator otherCharIterator = std::find(vectorOfMatchingCharsWithInnerCharRemoved.begin(), vectorOfMatchingCharsWithInnerCharRemoved.end(), otherChar);
//                                 // if iterator did not get to end, then the char was found in the vector
//                                 if (otherCharIterator != vectorOfMatchingCharsWithInnerCharRemoved.end()) {
//                                     vectorOfMatchingCharsWithInnerCharRemoved.erase(otherCharIterator);         // so remove the char
//                                 }
//                             }
//                         }
//                     }
//                 }
//             }
//
//             return(vectorOfMatchingCharsWithInnerCharRemoved);
//         }*/
//       public ArrayList<PossibleChar> findPossibleCharsInScene(Mat imgThresh){
//            //返回值 返回可能是车牌的 char
//            ArrayList<PossibleChar> vectorOfPossibleChars=new ArrayList<PossibleChar>();
//            //https://docs.opencv.org/2.4/doc/tutorials/core/basic_geometric_drawing/basic_geometric_drawing.html
//            // 创建一块黑色的布
//            Mat imgContours=new Mat(imgThresh.size(), CV_8UC3, new Scalar(0, 0, 0));//BLACK
//            int intCountOfPossibleChars = 0;
//            //克隆
//            Mat imgThreshCopy = imgThresh.clone();
//            ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
//            //find all contours
//            Imgproc.findContours(imgThreshCopy, contours, new Mat(),
//                    Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
//            for (int i = 0;i < contours.size();i++) {// for each contour
//                // System.out.println(contours.size());
//                //勾画所有轮廓
//                Imgproc.drawContours(imgThreshCopy, contours, i, new Scalar(255, 255, 255), 5);//WHITE
//                //https://blog.csdn.net/qq_18343569/article/details/47999257
//                //https://stackoverflow.com/questions/36882329/how-to-draw-rectangle-around-contours-in-opencv-android
//                MatOfPoint2f approxCurve = new MatOfPoint2f();
//                MatOfPoint2f contour2f = new MatOfPoint2f( contours.get(i).toArray() );
//                double approxDistance = Imgproc.arcLength(contour2f, true)*0.02;
//                Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);
//                MatOfPoint points = new MatOfPoint( approxCurve.toArray() );
//                PossibleChar possibleChar= new PossibleChar(points);
//                //Rect rect = Imgproc.boundingRect(points);
//                //System.out.println("area"+rect.area());
//
//                if (checkIfPossibleChar(possibleChar)) {                // if contour is a possible char, note this does not compare to other chars (yet) . . .
//                    intCountOfPossibleChars++;                          // increment count of possible chars
//                    vectorOfPossibleChars.add(possibleChar);      // and add to vector of possible chars
//                }
//            }
//            System.out.println("contours.size() = "+contours.size());
//            System.out.println("step 2 - intCountOfValidPossibleChars = "+intCountOfPossibleChars);
//            return(vectorOfPossibleChars);
//
//        }
//
//        public ArrayList<PossibleChar> findPossibleCharsInPlate(Mat imgGrayscale, Mat imgThresh) {
//            ArrayList<PossibleChar> vectorOfPossibleChars=new ArrayList<>();                            // this will be the return value
//
//            Mat imgThreshCopy;
//
//            ArrayList<MatOfPoint> contours= new ArrayList<>();
//
//            imgThreshCopy = imgThresh.clone();				// make a copy of the thresh image, this in necessary b/c findContours modifies the image
//
//            Imgproc.findContours(imgThreshCopy, contours, new Mat(),Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);        // find all contours in plate
//
//            for (MatOfPoint contour : contours) {                            // for each contour
//                PossibleChar possibleChar= new PossibleChar(contour);
//
//                if (checkIfPossibleChar(possibleChar)) {                // if contour is a possible char, note this does not compare to other chars (yet) . . .
//                    vectorOfPossibleChars.add(possibleChar);      // add to vector of possible chars
//                }
//            }
//
//            return(vectorOfPossibleChars);
//        }
//
//        public ArrayList<ArrayList<PossibleChar>> findVectorOfVectorsOfMatchingChars(ArrayList<PossibleChar> ListofPossibleChars) {
//            //https://www.tangiblesoftwaresolutions.com/articles/java_equivalent_to_cplus_vector.html
//            //返回值
//            ArrayList<ArrayList<PossibleChar>> vectorOfVectorsOfMatchingChars=new ArrayList<ArrayList<PossibleChar>>();
//
//            int MIN_NUMBER_OF_MATCHING_CHARS =3;
//
//            // for each possible char in the one big vector of chars
//            for (PossibleChar possibleChar : ListofPossibleChars) {                  // for each possible char in the one big vector of chars
//
//                // find all chars in the big vector that match the current char
//                ArrayList<PossibleChar> vectorOfMatchingChars = findVectorOfMatchingChars(possibleChar, ListofPossibleChars);
//                // also add the current char to current possible vector of matching chars
//                vectorOfMatchingChars.add(possibleChar);
//
//                // if current possible vector of matching chars is not long enough to constitute a possible plate
//                if (vectorOfMatchingChars.size() < MIN_NUMBER_OF_MATCHING_CHARS) {
//                    continue;                       // jump back to the top of the for loop and try again with next char, note that it's not necessary
//                    // to save the vector in any way since it did not have enough chars to be a possible plate
//                }
//                // so add to our vector of vectors of matching chars
//                vectorOfVectorsOfMatchingChars.add(vectorOfMatchingChars);
//
//                // remove the current vector of matching chars from the big vector so we don't use those same chars twice,
//                // make sure to make a new big vector for this since we don't want to change the original big vector
//
//                ArrayList<PossibleChar> vectorOfPossibleCharsWithCurrentMatchesRemoved=new ArrayList<PossibleChar>();
//
//                for (PossibleChar possChar : ListofPossibleChars) {
//                    boolean adding=true;
//                    for(PossibleChar possCharFind : vectorOfMatchingChars){
//                        if(possCharFind== possChar){
//                            adding=false;
//                            break;
//                        }
//                    }
//                    if(adding){
//                        vectorOfPossibleCharsWithCurrentMatchesRemoved.add(possChar);
//                    }
//                    //if (find(vectorOfMatchingChars.begin(), vectorOfMatchingChars.end(), possChar) == vectorOfMatchingChars.end()) {
//                    //  vectorOfPossibleCharsWithCurrentMatchesRemoved.add(possChar);
//                    //}
//                }
//                // declare new vector of vectors of chars to get result from recursive call
//                ArrayList<ArrayList<PossibleChar>> recursiveVectorOfVectorsOfMatchingChars=new ArrayList<>();
//                // recursive call
//                recursiveVectorOfVectorsOfMatchingChars = findVectorOfVectorsOfMatchingChars(vectorOfPossibleCharsWithCurrentMatchesRemoved);	// recursive call !!
//                for (ArrayList<PossibleChar> recursiveVectorOfMatchingChars : recursiveVectorOfVectorsOfMatchingChars) {
//                    // for each vector of matching chars found by recursive call
//                    vectorOfVectorsOfMatchingChars.add(recursiveVectorOfMatchingChars);
//                    // add to our original vector of vectors of matching chars
//                }
//
//                break;
//
//            }
//            return(vectorOfVectorsOfMatchingChars);
//        }
//
//        public double distanceBetweenChars(PossibleChar firstChar, PossibleChar secondChar) {
//            int intX = abs(firstChar.intCenterX - secondChar.intCenterX);
//            int intY = abs(firstChar.intCenterY - secondChar.intCenterY);
//            return(Math.sqrt(pow(intX, 2) + pow(intY, 2)));
//        }
//
//        public double angleBetweenChars(PossibleChar firstChar, PossibleChar secondChar) {
//            double dblAdj = abs(firstChar.intCenterX - secondChar.intCenterX);
//            double dblOpp = abs(firstChar.intCenterY - secondChar.intCenterY);
//            double dblAngleInRad = atan(dblOpp / dblAdj);
//            double dblAngleInDeg = dblAngleInRad * (180.0 / PI);
//            return(dblAngleInDeg);
//        }
//
//        public ArrayList<PossibleChar> findVectorOfMatchingChars(PossibleChar possibleChar,List<PossibleChar> vectorOfChars){
//            // double MIN_DIAG_SIZE_MULTIPLE_AWAY = 0.3;
//            int counts=0;
//            double MAX_DIAG_SIZE_MULTIPLE_AWAY = 5.0;
//            double MAX_CHANGE_IN_AREA = 0.5;
//            double MAX_CHANGE_IN_WIDTH = 0.8;
//            double MAX_CHANGE_IN_HEIGHT = 0.2;
//            double MAX_ANGLE_BETWEEN_CHARS = 12.0;
//
//            ArrayList<PossibleChar> vectorOfMatchingChars=new ArrayList<>();
//
//            for (PossibleChar possibleMatchingChar : vectorOfChars) {// for each char in big vector
//                if (possibleMatchingChar == possibleChar) {
//                    // then we should not include it in the vector of matches b/c that would end up double including the current char
//                    continue;           // so do not add to vector of matches and jump back to top of for loop
//                }
//                // compute stuff to see if chars are a match
//                //计算两个char间的距离
//                double dblDistanceBetweenChars = distanceBetweenChars(possibleChar, possibleMatchingChar);
//                // System.out.println("dblDistanceBetweenChars"+dblDistanceBetweenChars);
//                //计算两个char间的角度
//                double dblAngleBetweenChars = angleBetweenChars(possibleChar, possibleMatchingChar);
//                // System.out.println("dblAngleBetweenChars"+dblAngleBetweenChars);
//                //计算面积，高，宽间的差距
//                double dblChangeInArea = (double) abs(possibleMatchingChar.boundingRect.area() - possibleChar.boundingRect.area()) / (double) possibleChar.boundingRect.area();
//                // System.out.println("dblChangeInArea"+dblChangeInArea);
//                double dblChangeInWidth = (double) abs(possibleMatchingChar.boundingRect.width - possibleChar.boundingRect.width) / (double) possibleChar.boundingRect.width;
//                // System.out.println("dblChangeInWidth"+dblChangeInWidth);
//                double dblChangeInHeight = (double) abs(possibleMatchingChar.boundingRect.height - possibleChar.boundingRect.height) / (double) possibleChar.boundingRect.height;
//                // System.out.println("dblChangeInHeight"+dblChangeInWidth);
//                if (dblDistanceBetweenChars < (possibleChar.dblDiagonalSize * MAX_DIAG_SIZE_MULTIPLE_AWAY) &&
//                        dblAngleBetweenChars < MAX_ANGLE_BETWEEN_CHARS &&
//                        dblChangeInArea < MAX_CHANGE_IN_AREA &&
//                        dblChangeInWidth < MAX_CHANGE_IN_WIDTH &&
//                        dblChangeInHeight < MAX_CHANGE_IN_HEIGHT) {
//                    vectorOfMatchingChars.add(possibleMatchingChar);
//                    counts++;// if the chars are a match, add the current char to vector of matching chars
//                }
//            }
//            //System.out.println("counts"+vectorOfMatchingChars.size());
//            return(vectorOfMatchingChars);
//            // return result
//        }
//
//
//        public PossiblePlate extractPlate(Mat imgOriginal, ArrayList<PossibleChar> vectorOfMatchingChars) {
//            double PLATE_WIDTH_PADDING_FACTOR = 1.3;
//            double PLATE_HEIGHT_PADDING_FACTOR = 1.5;
//            PossiblePlate possiblePlate=new PossiblePlate();            // this will be the return value
//            //将intCenterX从小到大排列
//            Collections.sort(vectorOfMatchingChars,
//                    new Comparator<PossibleChar>() {
//                        public int compare(PossibleChar o1, PossibleChar o2) {
//                            if(o1.intCenterX>o2.intCenterX) return 1;
//                            else return -1;
//                        }
//                    });
//            // System.out.println(vectorOfMatchingChars.get(0).intCenterX);
//            //System.out.println(vectorOfMatchingChars.get(1).intCenterX);
//            //System.out.println(vectorOfMatchingChars.get(2).intCenterX);
//            //System.out.println(vectorOfMatchingChars.get(vectorOfMatchingChars.size() - 1).intCenterX);
//            // sort chars from left to right based on x position
//            //std::sort(vectorOfMatchingChars.begin(), vectorOfMatchingChars.end(), PossibleChar::sortCharsLeftToRight);
//
//            // calculate the center point of the plate
//            double dblPlateCenterX = (double)(vectorOfMatchingChars.get(0).intCenterX + vectorOfMatchingChars.get(vectorOfMatchingChars.size() - 1).intCenterX) / 2.0;
//            double dblPlateCenterY = (double)(vectorOfMatchingChars.get(0).intCenterY + vectorOfMatchingChars.get(vectorOfMatchingChars.size() - 1).intCenterY) / 2.0;
//            Point p2dPlateCenter=new Point(dblPlateCenterX, dblPlateCenterY);
//
//            // calculate plate width and height
//            int intPlateWidth = (int)((vectorOfMatchingChars.get(vectorOfMatchingChars.size() - 1).boundingRect.x + vectorOfMatchingChars.get(vectorOfMatchingChars.size() - 1).boundingRect.width - vectorOfMatchingChars.get(0).boundingRect.x) * PLATE_WIDTH_PADDING_FACTOR);
//
//            double intTotalOfCharHeights = 0;
//
//            for (PossibleChar matchingChar : vectorOfMatchingChars) {
//                intTotalOfCharHeights = intTotalOfCharHeights + matchingChar.boundingRect.height;
//            }
//
//            double dblAverageCharHeight = (double)intTotalOfCharHeights / vectorOfMatchingChars.size();
//
//            int intPlateHeight = (int)(dblAverageCharHeight * PLATE_HEIGHT_PADDING_FACTOR);
//
//            // calculate correction angle of plate region
//            double dblOpposite = vectorOfMatchingChars.get(vectorOfMatchingChars.size() - 1).intCenterY - vectorOfMatchingChars.get(0).intCenterY;
//            double dblHypotenuse = distanceBetweenChars(vectorOfMatchingChars.get(0), vectorOfMatchingChars.get(vectorOfMatchingChars.size() - 1));
//            double dblCorrectionAngleInRad = asin(dblOpposite / dblHypotenuse);
//            double dblCorrectionAngleInDeg = dblCorrectionAngleInRad * (180.0 / PI);
//
//            // assign rotated rect member variable of possible plate
//            possiblePlate.rrLocationOfPlateInScene = new RotatedRect(p2dPlateCenter, new Size((float)intPlateWidth, (float)intPlateHeight), (float)dblCorrectionAngleInDeg);
//
//            // Mat rotationMatrix;             // final steps are to perform the actual rotation
//            Mat imgRotated=new Mat(imgOriginal.rows(), imgOriginal.cols(), imgOriginal.type());
//            //Mat imgCropped=new Mat(imgRotated.rows(), imgRotated.cols(), imgRotated.type());
//
//            // get the rotation matrix for our calculated correction angle
//            Mat rotationMatrix = Imgproc.getRotationMatrix2D(p2dPlateCenter, dblCorrectionAngleInDeg, 1.0);
//
//            // rotate the entire image
//            Imgproc.warpAffine(imgOriginal, imgRotated, rotationMatrix, imgRotated.size());
//
//            // crop out the actual plate portion of the rotated image
//            //从原图像中提取提取一个感兴趣的矩形区域图像
//            // Imgproc.getRectSubPix(imgRotated, possiblePlate.rrLocationOfPlateInScene.size, possiblePlate.rrLocationOfPlateInScene.center, imgCropped,-1);
//            System.out.println("x"+(possiblePlate.rrLocationOfPlateInScene.center.x-possiblePlate.rrLocationOfPlateInScene.size.width/2));
//            System.out.println("y"+(possiblePlate.rrLocationOfPlateInScene.center.y-possiblePlate.rrLocationOfPlateInScene.size.height/2));
//            System.out.println("width"+possiblePlate.rrLocationOfPlateInScene.size.width);
//            System.out.println("height"+possiblePlate.rrLocationOfPlateInScene.size.height);
//            Rect rect = new Rect((int)(possiblePlate.rrLocationOfPlateInScene.center.x-possiblePlate.rrLocationOfPlateInScene.size.width/2), (int)(possiblePlate.rrLocationOfPlateInScene.center.y-possiblePlate.rrLocationOfPlateInScene.size.height/2),(int)possiblePlate.rrLocationOfPlateInScene.size.width ,(int)possiblePlate.rrLocationOfPlateInScene.size.height); // 设置矩形ROI的位置
//            Mat imgCropped= imgRotated.submat( rect );
//            possiblePlate.imgPlate = imgCropped;            // copy the cropped plate image into the applicable member variable of the possible plate
//
//            return(possiblePlate);
//        }
//
//        public boolean checkIfPossibleChar(PossibleChar possibleChar){
//            // this function is a 'first pass' that does a rough check on a contour to see if it could be a char,
//            // note that we are not (yet) comparing the char to other chars to look for a group
//            int MIN_PIXEL_AREA = 120;
//            int MIN_PIXEL_HEIGHT = 10;
//            double MAX_ASPECT_RATIO = 0.6;
//            int MIN_PIXEL_WIDTH = 3;
//            double MIN_ASPECT_RATIO = 0.33;
//            //System.out.println(possibleChar.boundingRect.area()+"+"+possibleChar.boundingRect.width);
//            if (possibleChar.boundingRect.area() > MIN_PIXEL_AREA &&
//                    possibleChar.boundingRect.width > MIN_PIXEL_WIDTH && possibleChar.boundingRect.height > MIN_PIXEL_HEIGHT &&
//                    MIN_ASPECT_RATIO < possibleChar.dblAspectRatio && possibleChar.dblAspectRatio < MAX_ASPECT_RATIO) {
//                return(true);
//            } else {
//                return(false);
//            }
//        }
//        private class ProcessClickListener implements View.OnClickListener {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                new Thread() {
//                    public void run() {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Preprocess pre=new Preprocess(srcBitmap);
//                                pre.TurntoGray();
//                                Mat grayImg= pre.getImgGray();
//                                Bitmap bmp;
//                                bmp = Bitmap.createBitmap(grayImg.cols(), grayImg.rows(),
//                                        Bitmap.Config.ARGB_8888);
//                                Utils.matToBitmap(grayImg, bmp);
//                                imgHuaishi.setImageBitmap(bmp);
//                            }
//                        });
//                    }
//                }.start();
//
//
//            }
//
//        }
//
//
//        @Override
//        protected void onResume() {
//            // TODO Auto-generated method stub
//            super.onResume();
//            if (!OpenCVLoader.initDebug()) {
//                Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
//                OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
//            } else {
//                Log.d(TAG, "OpenCV library found inside package. Using it!");
//                mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
//            }
//        }
//
//
//    }*/
//
//
//}
