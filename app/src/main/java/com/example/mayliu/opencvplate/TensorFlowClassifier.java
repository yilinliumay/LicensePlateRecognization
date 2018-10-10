package com.example.mayliu.opencvplate; /**
 * Created by mayliu on 2018/3/27.
 */

//Provides access to an application's raw asset files;

import android.content.res.AssetManager;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class TensorFlowClassifier implements Classifier {
    //must be a classification percetnage greater than this
    private static final float THRESHOLD = 0.1f;

    private TensorFlowInferenceInterface tfHelper;

    private String name;
    private String inputName;
    private String outputName;
    private int inputWidth;
    private int inputHeight;
    private boolean feedKeepProb;

    private List<String> labels;
    private float[] output;
    private String[] outputNames;

    //given a saved drawn model, lets read all the classification labels that are
    //stored and write them to our in memory labels list
    private static List<String> readLabels(AssetManager am, String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(am.open(fileName)));

        String line;
        List<String> labels = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            labels.add(line);
        }

        br.close();
        return labels;
    }

    //given a model, its label file, and its metadata
    //fill out a classifier object with all the necessary
    //metadata including output prediction
    public static TensorFlowClassifier create(AssetManager assetManager, String name,
                                              String modelPath, String labelFile, int inputWidth,int inputHeight, String inputName, String outputName,
                                              boolean feedKeepProb,int type) throws IOException {
        //intialize a classifier
       // System.out.println("here");
        TensorFlowClassifier c = new TensorFlowClassifier();

        //store its name, input and output labels
        c.name = name;

        c.inputName = inputName;
        c.outputName = outputName;

        //read labels for label file
        c.labels = readLabels(assetManager, labelFile);

        //set its model path and where the raw asset files are
        c.tfHelper = new TensorFlowInferenceInterface(assetManager, modelPath);
        int numClasses=0;
        if(type==1) {
            //numClasses = 26;
            numClasses = 34;
        }
        else if(type==2){
             numClasses=26;
        }
        else if(type==3){
            numClasses=6;
        }

        //how big is the input?
        c.inputWidth = inputWidth;
        c.inputHeight=inputHeight;

        // Pre-allocate buffer.
        c.outputNames = new String[] { outputName };

        c.outputName = outputName;
        c.output = new float[numClasses];

        c.feedKeepProb = feedKeepProb;

        return c;
    }

    @Override
    public String name() {
        return name;
    }

    // public Classification recognize(float[] pixels, int type) {
    @Override
    public Classification recognize(float[] pixels) {


        //give it the input name, raw pixels from the drawing,
        //input size
        //输入标识符，图片的像素矩阵，矩阵的大小
        tfHelper.feed(inputName, pixels, 1, inputWidth, inputHeight, 1);
        //输入keep_prob，防止过拟合
        if (feedKeepProb) {
            tfHelper.feed("keep_prob", new float[] { 1 });
        }
        //进行计算，得到可能的输出
        tfHelper.run(outputNames);
        //得到输出，输出每个分类的可能性
        tfHelper.fetch(outputName, output);

        // Find the best classification
        //for each output prediction
        //if its above the threshold for accuracy we predefined
        //write it out to the view
        Classification ans = new Classification();
        for (int i = 0; i < output.length; ++i) {
            System.out.println(output[i]);
            System.out.println(labels.get(i));
            if (output[i] > THRESHOLD && output[i] > ans.getConf()) {
                ans.update(output[i], labels.get(i));
            }
        }

        return ans;
    }
}
