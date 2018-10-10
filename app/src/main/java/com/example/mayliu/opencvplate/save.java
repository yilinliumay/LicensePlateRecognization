package com.example.mayliu.opencvplate;

import android.content.Context;
import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * Created by mayliu on 2018/4/4.
 */

public class save {

    ArrayList<Mat> segment=new ArrayList<>();

    save(ArrayList<Mat> segment){
        this.segment=segment;
    }

    public void create(){
         int i=0;
        for(Mat seg :segment) {
            Bitmap bmp = Bitmap.createBitmap(seg.cols(), seg.rows(),
                    Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(seg, bmp);

            String name="aaaaa.png";
            savePNG_After(bmp,name);

        }
        String FILENAME = "hello_file";
        String string = "hello world!";

       // FileOutputStream fos = MainActivity.getContext().openFileOutput(FILENAME, Context.MODE_PRIVATE);
        //fos.write(string.getBytes());
        //fos.close();
    }


    public static void savePNG_After(Bitmap bitmap, String name) {
        File f = new File(name);
        try {
           // if (!f.getParentFile().exists())
             //   f.getParentFile().mkdirs();
            if (!f.exists()){ System.out.println("Yes");
                f.createNewFile();}

            FileOutputStream out = new FileOutputStream(f);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
