/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mayliu.opencvplate;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by mayliu on 2018/5/6.
 */

public class ShowActivity extends AppCompatActivity {

    private ImageView mImageView;
    Intent mIntent;
    String filename;
    String dir;
    private Bitmap target;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_layout);

        mIntent=getIntent();
        filename=mIntent.getStringExtra("fileName");
        //dir=mIntent.getStringExtra("dir");


        mImageView = (ImageView) findViewById(R.id.imageView);

        showUI();
    }

    public void showUI(){
        String path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/camtest/"+filename;
        System.out.println("path:"+path);
        target=getDiskBitmap(path);
        mImageView.setImageBitmap(target);

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


    public static Bitmap bytesToBitmap (byte[] imageBytes)
    {
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        return bitmap;
    }
}
