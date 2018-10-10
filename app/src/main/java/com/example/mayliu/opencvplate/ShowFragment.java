package com.example.mayliu.opencvplate;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShowFragment extends Fragment implements View.OnClickListener {


    public ShowFragment() {
        // Required empty public constructor
    }

    Button btnProcess;
    Button button1;
    ImageView imgHuaishi;
    EditText editText;
    Bitmap srcBitmap;
    private static final String TAG = "MainActivity";
    public float[] pixels;
    String text;
    String result="";

    private showResult myCallback;
    Handler handler;
    private static final int PIXEL_WIDTH = 32;
    private static final int PIXEL_Height = 40;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // lyaout
        View view = inflater.inflate(R.layout.fragment_show, container, false);
        btnProcess = (Button)view.findViewById(R.id.button);
        button1 = (Button)view.findViewById(R.id.button1);
        imgHuaishi = (ImageView)view.findViewById(R.id.imageView2);
        editText=(EditText)view.findViewById(R.id.editText);

       //传值
        String str="";
        Bundle bundle = this.getArguments();
        if (bundle != null)
        {
            str = bundle.getString("key");
        }


        try {
            srcBitmap = getDiskBitmap(str);
        }catch (OutOfMemoryError e) {
            System.out.println("内存泄漏a");
            if(srcBitmap != null && !srcBitmap.isRecycled()){
                // 回收并且置为null
                srcBitmap.recycle();
                srcBitmap = null;
            }
            System.gc();
            srcBitmap = rotateBitmapByDegree(getDiskBitmap(str),getBitmapDegree(str));
        }

        imgHuaishi.setImageBitmap(rotateBitmapByDegree(getSmallBitmap(str),getBitmapDegree(str)));
        handler=new Handler();



        btnProcess.setOnClickListener(new View.OnClickListener(){
            @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            new Thread(new Runnable() {
                public void run(){
                    Mat src=new Mat();
                    Preprocess pre=new Preprocess(srcBitmap);
                    Utils.bitmapToMat(srcBitmap, src);
                            pre.TurntoGray();
                            //savePicture(turnToBitmap(pre.getImgGray()));
                            pre.maximizeContrast();
                            pre.GaussianBlur();
                            pre.adaptiveThreshold();
                           // savePicture(turnToBitmap(pre.getImgThresh()));

                            //savePicture(turnToBitmap(pre.Manage()));
                            FindPlate fp3=new FindPlate(pre.Manage());

                            Mat first3=fp3.findAllPossibleChars();
                            //savePicture(turnToBitmap(first3));

                            Mat group3=fp3.findByGroup();
                            savePicture(turnToBitmap(group3));

                            GetPlate gp2=new GetPlate(fp3.getVectorOfVectorsOfMatchingCharsInScene(),src,group3);
                            gp2.getPossiblePlate();
                           //savePicture(turnToBitmap(gp2.vectorOfPossiblePlates.get(1).imgPlate));
                            //savePicture(turnToBitmap(gp2.secondImgContours));
                            Segmentation sg=new Segmentation();
                            sg.detectCharsInPlates(gp2.vectorOfPossiblePlates);
                            //savePicture(turnToBitmap(sg.findPossible));
                            //savePicture(turnToBitmap(sg.matching));
                             // savePicture(turnToBitmap(sg.imgThresh));
                            System.out.println(sg.imgCropped.size());
                            //savePicture(turnToBitmap(sg.imgThreshColor));
                            Bitmap newbm;
                            for(int k=0;k<sg.imgCropped.size();k++){
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
                                newbm = Bitmap.createBitmap(unnewbm1, 0, 0, orgWidth, orgHeight, matrix,
                                        true);
                                pixels = new float[newWidth * newHeight];
                                int pixColor;

                                for (int i = 0; i < newbm.getHeight(); i++) {
                                    for (int j = 0; j < newbm.getWidth(); j++) {
                                        //获取对应点的像素

                                        pixColor = newbm.getPixel(j, i);

                                        if (pixColor < -7829368) {
                                            pixels[newWidth * i + j] = 1;
                                        } else {
                                            pixels[newWidth * i + j] = 0;
                                        }
                                    }
                                }
                                Garbage(newbm);
                                Garbage(unnewbm1);
                                Garbage(srcBitmap);
                                System.gc();

                                if(k==0) loadModel(3);
                                else if(k==1) loadModel(2);
                                else loadModel(1);
                            }

                            System.out.println(result);
                            myCallback.showResultString(result);

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    editText.setText(result);
                            }});

                }
            }).start();

        }
        });

        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new Thread(new Runnable() {
                    public void run(){
                        Mat src=new Mat();
                        Preprocess pre=new Preprocess(srcBitmap);
                        Utils.bitmapToMat(srcBitmap, src);
                        pre.TurntoGray();
                        savePicture(turnToBitmap(pre.getImgGray()));
                        pre.maximizeContrast();
                        pre.GaussianBlur();
                        pre.adaptiveThreshold();

                        //savePicture(turnToBitmap(pre.getImgThresh()));

                        //savePicture(turnToBitmap(pre.Manage()));
                        FindPlate fp3=new FindPlate(pre.getImgThresh());
                        Mat first3=fp3.findAllPossibleChars();
                        //savePicture(turnToBitmap(first3));

                        Mat group3=fp3.findByGroup();
                        savePicture(turnToBitmap(group3));

                        GetPlate gp2=new GetPlate(fp3.getVectorOfVectorsOfMatchingCharsInScene(),src,group3);
                        gp2.getPossiblePlate();
                        //savePicture(turnToBitmap(gp2.vectorOfPossiblePlates.get(0).imgPlate));
                        //savePicture(turnToBitmap(gp2.secondImgContours));
                        Segmentation1 sg=new Segmentation1();
                        sg.detectCharsInPlates(gp2.vectorOfPossiblePlates);
                        //savePicture(turnToBitmap(sg.findPossible));
                        //savePicture(turnToBitmap(sg.matching));
                        System.out.println(sg.imgCropped.size());
                        savePicture(turnToBitmap(sg.imgThreshColor));
                        Bitmap newbm;
                        for(int k=0;k<sg.imgCropped.size();k++){
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
                            newbm = Bitmap.createBitmap(unnewbm1, 0, 0, orgWidth, orgHeight, matrix,
                                    true);
                            pixels = new float[newWidth * newHeight];
                            int pixColor;

                            for (int i = 0; i < newbm.getHeight(); i++) {
                                for (int j = 0; j < newbm.getWidth(); j++) {
                                    //获取对应点的像素

                                    pixColor = newbm.getPixel(j, i);

                                    if (pixColor < -7829368) {
                                        pixels[newWidth * i + j] = 1;
                                    } else {
                                        pixels[newWidth * i + j] = 0;
                                    }
                                }
                            }
                            Garbage(newbm);
                            Garbage(unnewbm1);
                            Garbage(srcBitmap);
                            System.gc();

                            if(k==0) loadModel(3);
                            else if(k==1) loadModel(2);
                            else loadModel(1);
                        }

                        System.out.println(result);
                        myCallback.showResultString(result);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                editText.setText(result);
                            }});

                    }
                }).start();

            }
        });
        return view;
    }


    public void Garbage(Bitmap bitmap){
        if(bitmap != null && !bitmap.isRecycled()){
            // 回收并且置为null
            bitmap.recycle();
            bitmap = null;
        }

    }

    //旋转图片
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }
    //旋转角度
    private int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }



    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        getActivity().sendBroadcast(mediaScanIntent);
    }

    public Bitmap getSmallBitmap(String path){
        //new 出来一个bitmap的参数
        BitmapFactory.Options options=new BitmapFactory.Options();
        //设置为true，不会生成bitmao对象，只是读取尺寸和类型信息
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(path, options);
        //得到这个比例   并赋予option里面的inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 450, 800);
        //设置为false，即将要生成bitmap对象啦
        options.inJustDecodeBounds = false;
        //有了这个option，我们可以生成bitmap对象了

        Bitmap bitmap=BitmapFactory.decodeFile(path, options);

        return bitmap;

    }
    public int calculateInSampleSize(BitmapFactory.Options options,int reqHeight,int reqWidth){
        //得到原始图片宽高
        int height=options.outHeight;
        int width=options.outWidth;
        //默认设置为1，即不缩放
        int inSampleSize=1;
        //如果图片原始的高大于我们期望的高，或者图片的原始宽大于我们期望的宽，换句话意思就是，我们想让它变小一点
        if (height > reqHeight || width > reqWidth) {
            //原始的高除以期望的高，得到一个比例
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            //原始的宽除以期望的宽，得到一个比例
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            //取上面两个比例中小的一个，返回这个比例
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;

    }


    private Bitmap getDiskBitmap(String pathString)
    {
        Bitmap bitmap = null;
        try
        {
            File file = new File(pathString);
            if(file.exists())
            {
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

   // int i=0;

    private void loadModel(int type) {
        List<Classifier> mClassifiers = new ArrayList<>();

        try {
            switch (type) {
                case 1:
                    mClassifiers.add(TensorFlowClassifier.create(getActivity().getAssets(), "TensorFlowDigits",
                            "optimized_digits_train.pb", "labels1.txt", PIXEL_WIDTH, PIXEL_Height,
                            "input", "out", true, 1));
                    break;
                case 2:
                    mClassifiers.add(TensorFlowClassifier.create(getActivity().getAssets(), "TensorFlowLetter",
                            "frozen_letter_train.pb", "labels.txt", PIXEL_WIDTH, PIXEL_Height,
                            "input", "out", true, 2));
                    break;
                case 3:

                        mClassifiers.add(TensorFlowClassifier.create(getActivity().getAssets(), "TensorFlowProvince",
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
        System.out.println(text);



    }

    public void savePicture(Bitmap bitmap){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            FileOutputStream outStream = null;
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/camtest");
            dir.mkdirs();

            String fileName = String.format("%d.jpg", System.currentTimeMillis());
            File outFile = new File(dir, fileName);

            try {
                outStream = new FileOutputStream(outFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
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

    @Override
    public void onClick(View view) {

    }


//    @Override
//    public void onResume() {
//        // TODO Auto-generated method stub
//        super.onResume();
//        if (!OpenCVLoader.initDebug()) {
//            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
//            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this.getActivity(), mLoaderCallback);
//        } else {
//            Log.d(TAG, "OpenCV library found inside package. Using it!");
//            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
//        }
//    }

    public void getPath(String filename)
    {
        System.out.println("here"+filename);
        //path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/camtest/"+filename;
        //srcBitmap=getDiskBitmap(path);


        //srcBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.school);
    }
    public interface showResult {
        public void showResultString(String name);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity != null) {
            myCallback = (ShowFragment.showResult) activity;
        }
    }



}
