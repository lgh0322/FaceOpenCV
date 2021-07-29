package com.bittle.myapplication;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;


import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgcodecs.Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;

/**
 * 人脸匹配
 * Created by Luke on 2017/8/22.
 */

public class FaceMatcher {

    private static final String TAG = "FaceMatcher";
    private static int counter;
    public final int UNFINISHED = -2;
    public final int NO_MATCHER = -1;
    private final int MAX_COUNTER = 45;
    private final double MY_SIMILARITY = 0.8;
    private List<String> mPathList;



    public static double histogramMatch(Bitmap bitmap1,Bitmap bitmap2) {

            Mat testMat = new Mat();
            Utils.bitmapToMat(bitmap1, testMat);
            // 转灰度矩阵
            Imgproc.cvtColor(testMat, testMat, Imgproc.COLOR_RGB2GRAY);
            // 把矩阵的类型转换为Cv_32F，因为在c++代码中会判断类型
            testMat.convertTo(testMat, CvType.CV_32F);
                Mat mat2 = new Mat();
               Utils.bitmapToMat(bitmap2,mat2);
//                Imgproc.resize(mat2, mat2, new Size(320, 320));
                Imgproc.cvtColor(mat2, mat2, Imgproc.COLOR_RGB2GRAY);
                mat2.convertTo(mat2, CvType.CV_32F);
                // 直方图比较
                double similarity = Imgproc.compareHist(mat2, testMat,
                        Imgproc.CV_COMP_CORREL);
                Log.e(TAG, "histogramMatch: " + similarity);


            return similarity;

    }


}