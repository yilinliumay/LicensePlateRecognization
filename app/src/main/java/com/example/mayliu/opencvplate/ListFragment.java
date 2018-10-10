package com.example.mayliu.opencvplate;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment {
    ListView listView;
    PictureAdapter adapter;
    public static ArrayList<String> result= new ArrayList<>();
    public static ArrayList<Bitmap> bitmaps=new ArrayList<>();
    public static int start=0;

    Handler handler;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            handler.post(new Runnable() {

                @Override
                public void run() {
                    System.out.println("b"+bitmaps.size()+"re"+result.size());
                    if(start!=bitmaps.size()) {
                        for (int i = 0; i < bitmaps.size(); i++) {
                            PictureProvider pictureDataProvider = new PictureProvider(bitmaps.get(i), result.get(i));
                            adapter.add(pictureDataProvider);
                        }
                    }
                    start=bitmaps.size();

                    Log.i("Main", Thread.currentThread().getName() + "---post--run");
                }
            });
            //Write down your refresh code here, it will call every time user come to this fragment.
            //If you are using listview with custom adapter, just call notifyDataSetChanged().
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new PictureAdapter(getActivity().getApplicationContext(), R.layout.row_layout);
        listView.setAdapter(adapter);
        handler=new Handler();
        return view;
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

    public Bitmap getSmallBitmap(String path){
        //new 出来一个bitmap的参数
        BitmapFactory.Options options=new BitmapFactory.Options();
        //设置为true，不会生成bitmao对象，只是读取尺寸和类型信息
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(path, options);
        //得到这个比例   并赋予option里面的inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 320, 400);
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


    public void GetBitmap(String path)
    {
//        String filename= Environment.getExternalStorageDirectory().getAbsolutePath()+"/camtest/"+path;
//        bitmaps.add(getSmallBitmap(filename));
//        System.out.println("bitmap大小"+bitmaps.size());
    }
    public void GetResult(String sContent){
        result.add(sContent);
        System.out.println("size大小"+result.size());
        String str="";
        Bundle bundle = this.getArguments();
        if (bundle != null)
        {
            str = bundle.getString("key");
        }
        bitmaps.add(getSmallBitmap(str));
    }





}
