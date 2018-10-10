package com.example.mayliu.opencvplate;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;


public class MainActivity extends Activity {


    private ImageView mImageView;
    Intent mIntent;
    String filename;
    String dir;
    private Bitmap target;
    Button btnProcess;
    ImageView imgHuaishi;
    Bitmap srcBitmap;
    private static final String TAG = "MainActivity";
    int steps=0;
    FileOutputStream out;
    Bitmap unnewbm;
    //private List<Classifier> mClassifiers = new ArrayList<>();
    public float[] pixels;
    String text;
    String result="";

    Mat result1;
    Mat result2;

    int fake=0;



    private static final int PIXEL_WIDTH = 32;
     private static final int PIXEL_Height = 40;

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
        btnProcess = (Button)findViewById(R.id.btn_gray_process);
        imgHuaishi = (ImageView)findViewById(R.id.img_huaishi);
        this.setTitle("Detection");

//      // 要恢复的
//        mIntent=getIntent();
//        filename=mIntent.getStringExtra("fileName");
//        String path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/camtest/"+filename;
//        System.out.println("path:"+path);
//        srcBitmap=getDiskBitmap(path);

       srcBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.school);

        imgHuaishi.setImageBitmap(srcBitmap);

        btnProcess.setOnClickListener(new MainActivity.ProcessClickListener());
    }

    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
    }

    private Bitmap getDiskBitmap(String pathString)
    {
        Bitmap bitmap = null;
        try
        {
            File file = new File(pathString);
            if(file.exists())
            {
                System.out.println("here");
                bitmap = BitmapFactory.decodeFile(pathString);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }


        return bitmap;
    }





    public Bitmap turnToBitmap(Mat example){
        Bitmap bmp = Bitmap.createBitmap(example.cols(), example.rows(),
                Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(example, bmp);
        return bmp;
    }

    private void loadModel(int type) {
        List<Classifier> mClassifiers = new ArrayList<>();

           try {
               switch (type) {
                   case 1:
                        mClassifiers.add(TensorFlowClassifier.create(getAssets(), "TensorFlowDigits",
                           "optimized_digits_train.pb", "labels1.txt", PIXEL_WIDTH, PIXEL_Height,
                           "input", "out", true, 1));
                        break;
                   case 2:
                   mClassifiers.add(TensorFlowClassifier.create(getAssets(), "TensorFlowLetter",
                           "frozen_letter_train.pb", "labels.txt", PIXEL_WIDTH, PIXEL_Height,
                           "input", "out", true, 2));
                       break;
                   case 3:
                       mClassifiers.add(TensorFlowClassifier.create(getAssets(), "TensorFlowProvince",
                               "frozen_province_train.pb", "labelp.txt", PIXEL_WIDTH, PIXEL_Height,
                               "input", "out", true, 3));
                       break;

               }
           } catch (final Exception e) {
                              //if they aren't found, throw an error!
                throw new RuntimeException("Error initializing classifiers!", e);
           }

           Classification[] classification=new Classification[2];
           int index=0;
           for (Classifier classifier : mClassifiers) {
                       //perform classification on the image
               final Classification res = classifier.recognize(pixels);
              classification[index++]=res;
                       //if it can't classify, output a question mark
                if (res.getLabel() == null) {
                     text += classifier.name() + ": ?\n";
                } else {
                           //else output its name
                    text += String.format("%s: %s, %f\n", classifier.name(), res.getLabel(),res.getConf());

                   result+=String.format("%s",res.getLabel());
                }
           }
//           if(classification[0].getConf()<classification[1].getConf()){
//               result+=String.format("%s",classification[1].getLabel());
//           }
//           else{
//               result+=String.format("%s",classification[0].getLabel());
//           }
//        if(fake<4){
//               result+=String.format("%s",classification[0].getLabel());
//               fake++;
//           }
//        else{
//          result+=String.format("%s",classification[1].getLabel());
//          fake++;
//        }
           System.out.println(text);



    }

    public void savePicture(){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            FileOutputStream outStream = null;
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/camtest");
            dir.mkdirs();

            String fileName = String.format("%d.jpg", System.currentTimeMillis());
            File outFile = new File(dir, fileName);

            try {
                outStream = new FileOutputStream(outFile);
                unnewbm.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
                System.out.println("_________保存到____sd______指定目录文件夹下____________________");
                refreshGallery(outFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                outStream.flush();
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Toast.makeText(MainActivity.this, "保存已经至" + Environment.getExternalStorageDirectory() + "/CoolImage/" + "目录文件夹下", Toast.LENGTH_SHORT).show();
            // 把文件插入到系统图库
        }

    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //创建文件夹
                    System.out.println("here");
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        String sdCardDir = Environment.getExternalStorageDirectory() + "/CoolImage/";
                        File file = new File(sdCardDir);
                        if (!file.exists()) {
                            Log.d("jim", "path1 create:" + file.mkdirs());
                        }
                        file = new File(sdCardDir, System.currentTimeMillis() + ".jpg");// 在SDcard的目录下创建图片文,以当前时间为其命名

                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            System.out.println("在保存图片时出错：" + e.toString());
                        }

                        try {
                            out = new FileOutputStream(file);
                            unnewbm.compress(Bitmap.CompressFormat.JPEG, 90, out);
                            System.out.println("_________保存到____sd______指定目录文件夹下____________________");
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        try {
                            out.flush();
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(MainActivity.this, "保存已经至" + Environment.getExternalStorageDirectory() + "/CoolImage/" + "目录文件夹下", Toast.LENGTH_SHORT).show();
                        // 把文件插入到系统图库
                    }
                    break;
                }
        }
    }

    private class ProcessClickListener implements OnClickListener{

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            new Thread(new Runnable() {

                public void run(){
                    Mat src=new Mat();
                    Preprocess pre=new Preprocess(srcBitmap);
                    Utils.bitmapToMat(srcBitmap, src);

                    switch (steps){

                        case 150:
                            pre.TurntoGray();
                            pre.maximizeContrast();
                            pre.GaussianBlur();
                            pre.adaptiveThreshold();
                           result1=pre.getImgThresh();
                           // imgHuaishi.setImageBitmap(turnToBitmap(result1));
                            //imgHuaishi.setImageBitmap(turnToBitmap(pre.getImgGray()));
                           // unnewbm=turnToBitmap(result1);
                           // ActivityCompat.requestPermissions(MainActivity.this, new String[]{android .Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            steps++;
                            break;
                        case 102:
//                            pre.TurntoGray();
//                            pre.maximizeContrast();
//                            pre.GaussianBlur();
//                            pre.adaptiveThreshold();
                            FindPlate fp=new FindPlate(result1);
                            Mat first=fp.findAllPossibleChars();    //删选些细碎的
                            Mat group=fp.findByGroup();
                            result2=group;
                            //imgHuaishi.setImageBitmap(turnToBitmap(first));
                            unnewbm=turnToBitmap(group);
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android .Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            steps++;
                            break;
                        case 20:
                            pre.TurntoGray();
                            pre.maximizeContrast();
                            pre.GaussianBlur();
                            pre.adaptiveThreshold();
                            FindPlate fp1=new FindPlate(pre.getImgThresh());
                            Mat first1=fp1.findAllPossibleChars();
                            Mat group1=fp1.findByGroup();
                            System.out.println("done");
                            //imgHuaishi.setImageBitmap(turnToBitmap(pre.getImgThresh()));
                            GetPlate gp=new GetPlate(fp1.getVectorOfVectorsOfMatchingCharsInScene(),src,group1);
                            gp.getPossiblePlate();
                            unnewbm=turnToBitmap(gp.secondImgContours);
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android .Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            //imgHuaishi.setImageBitmap(turnToBitmap(gp.secondImgContours));
                            steps++;
                            break;
                        case 02:
                            pre.TurntoGray();
                            pre.maximizeContrast();
                            pre.GaussianBlur();
                            pre.adaptiveThreshold();
                            FindPlate fp2=new FindPlate(pre.getImgThresh());
                            Mat first2=fp2.findAllPossibleChars();
                            Mat group2=fp2.findByGroup();
                            System.out.println("done");
                            GetPlate gp1=new GetPlate(fp2.getVectorOfVectorsOfMatchingCharsInScene(),src,group2);
                            gp1.getPossiblePlate();
                            unnewbm=turnToBitmap(gp1.vectorOfPossiblePlates.get(2).imgPlate) ; //找到长度最长的那个
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android .Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            //imgHuaishi.setImageBitmap(turnToBitmap(gp1.vectorOfPossiblePlates.get(0).imgPlate));
                            steps++;
                            break;
                        case 0:
                            pre.TurntoGray();
                            pre.maximizeContrast();
                            pre.GaussianBlur();
                            pre.adaptiveThreshold();
                            FindPlate fp3=new FindPlate(pre.getImgThresh());
                            Mat first3=fp3.findAllPossibleChars();
                            Mat group3=fp3.findByGroup();
                            GetPlate gp2=new GetPlate(fp3.getVectorOfVectorsOfMatchingCharsInScene(),src,group3);
                            gp2.getPossiblePlate();
                            Segmentation sg=new Segmentation();
                            sg.detectCharsInPlates(gp2.vectorOfPossiblePlates);
                            System.out.println(sg.imgCropped.size());
                            unnewbm=turnToBitmap(sg.imgThreshColor);
                            //
                            savePicture();
                          // ActivityCompat.requestPermissions(MainActivity.this, new String[]{android .Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);




//
                          for(int k=0;k<sg.imgCropped.size();k++){
                           //    for(int k=5;k<6;k++) {

                               System.out.println("k"+k);
                                Bitmap unnewbm1 = turnToBitmap(sg.imgCropped.get(k));

                                int orgWidth = unnewbm1.getWidth();
                                int orgHeight = unnewbm1.getHeight();
                                int newWidth = 32;
                                int newHeight = 40;
                                float scaleWidth = ((float) newWidth) / orgWidth;
                                float scaleHeight = ((float) newHeight) / orgHeight;
                                Matrix matrix = new Matrix();
                                matrix.postScale(scaleWidth, scaleHeight);
                                Bitmap newbm = Bitmap.createBitmap(unnewbm1, 0, 0, orgWidth, orgHeight, matrix,
                                        true);
                                pixels = new float[newWidth * newHeight];
                                int pixColor;
                                Bitmap newbm1 = Bitmap.createBitmap(unnewbm1, 0, 0, orgWidth, orgHeight, matrix,
                                        true);
                                for (int i = 0; i < newbm.getHeight(); i++) {
                                    for (int j = 0; j < newbm.getWidth(); j++) {
                                        //获取对应点的像素

                                        pixColor = newbm.getPixel(j, i);

                                       // System.out.println(pixColor);
                                        if (pixColor < -7829368) {
                                            pixels[newWidth * i + j] = 1;
                                            newbm1.setPixel(j, i, BLACK);
                                        } else {
                                            pixels[newWidth * i + j] = 0;
                                            newbm1.setPixel(j, i, WHITE);
                                        }
                                    }
                                }
                              //  unnewbm=newbm1;
                              // ActivityCompat.requestPermissions(MainActivity.this, new String[]{android .Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                              if(k==0) loadModel(3);
                              else if(k==1) loadModel(2);
                              else loadModel(1);
                            }

                            System.out.println(result);


                  }

                }
            }).start();

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
    }


}
