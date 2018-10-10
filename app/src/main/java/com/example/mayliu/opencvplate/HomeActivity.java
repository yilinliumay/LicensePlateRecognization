package com.example.mayliu.opencvplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

public class HomeActivity extends AppCompatActivity implements ShowFragment.showResult {
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    CameraFragment cameraFragment;
    ShowFragment showFragment;
    ListFragment listFragment;
    CamFragment camFragment;
    String path="";
    Bundle bundle;
    Intent mIntent;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tabLayout=(TabLayout)findViewById(R.id.tabLayout);
        viewPager=(ViewPager)findViewById(R.id.viewPager);
        viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());

        //cameraFragment=new CameraFragment();
        //viewPagerAdapter.addFragments(cameraFragment,"拍照");

        showFragment=new ShowFragment();

        mIntent=getIntent();
        String filename=mIntent.getStringExtra("fileName");
        System.out.println("hilename"+filename);
        bundle = new Bundle();
        bundle.putString("key", filename);
        showFragment.setArguments(bundle);


        viewPagerAdapter.addFragments(showFragment,"识别");

        listFragment=new ListFragment();
        listFragment.setArguments(bundle);

        viewPagerAdapter.addFragments(listFragment,"展示");

        camFragment=new CamFragment();
        viewPagerAdapter.addFragments(camFragment,"拍照");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        //listFragment.GetBitmap(path);


        //path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/camtest/"+filename;
       // System.out.println("path:"+path);





    }
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
public void onResume() {
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

    @Override
    public void showResultString(String name) {
        listFragment.GetResult(name);
    }
}
