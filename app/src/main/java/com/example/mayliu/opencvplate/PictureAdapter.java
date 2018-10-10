package com.example.mayliu.opencvplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2016/12/17.
 */

public class PictureAdapter extends ArrayAdapter
{
    List list=new ArrayList();

    public PictureAdapter(Context context, int resource)
    {
        super(context, resource);
    }
    static class DataHandler
    {
        ImageView picture;
        TextView photo_time;
    }

    @Override
    public void add(Object object) {
        super.add(object);
        list.add(object);
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        DataHandler handler;
        row=convertView;
        if(convertView==null)
        {
            LayoutInflater inflater=(LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=inflater.inflate(R.layout.row_layout,parent,false);
            handler=new DataHandler();
            handler.picture=(ImageView) row.findViewById(R.id.picture);
            handler.photo_time=(TextView) row.findViewById(R.id.photo_time);
            row.setTag(handler);

        }
       else
        {
            handler=(DataHandler) row.getTag();

        }
        PictureProvider pictureProvider;
        pictureProvider=(PictureProvider) this.getItem(position);
        handler.picture.setImageBitmap(pictureProvider.getPicture_resource());
        handler.photo_time.setText(pictureProvider.getPhoto_time());


        return row;
    }

}

