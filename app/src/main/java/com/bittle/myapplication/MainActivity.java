package com.bittle.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import static org.opencv.core.CvType.CV_8UC;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    private static final int MY_PERMISSIONS_REQUEST_CALL_CAMERA = 2;
    /* 相机请求码 */
    private static final int REQUEST_CAMERA = 0;
    private CameraBridgeViewBase openCvCameraView;

    private static final String TAG = "OpencvActivity";
    private CascadeClassifier cascadeClassifier = null; //级联分类器
    private Mat mRgba; //图像容器
    private Mat mGray;
    private int absoluteFaceSize = 0;
    private Handler handler;
    private ImageView img1,img2;
    private Button btn_face1,btn_face2,btn_compare;
    String face1,face2;
    Bitmap bitmap1,bitmap2;
    private TextView textView;
    private FaceMatcher matcher;
    private Mat mat_face,mat_f;
    static {
        System.loadLibrary("opencv_java3");
    }
    private void initializeOpenCVDependencies() {
        try {
            InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface_improved); //OpenCV的人脸模型文件： lbpcascade_frontalface_improved
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface_improved.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            // 加载cascadeClassifier
            cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
        } catch (Exception e) {
            Log.e(TAG, "Error loading cascade", e);
        }
        // 显示
        openCvCameraView.enableView();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mat_face = new Mat(200,200,CV_8UC(15));

        btn_face1 = findViewById(R.id.bn_get_face1);
        btn_face2 = findViewById(R.id.bn_get_face2);
        btn_compare = findViewById(R.id.btn_compare);
        textView = findViewById(R.id.text_view);
        img1 = findViewById(R.id.face1);
        img2 =findViewById(R.id.face2);
        handler = new Handler();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,},
                        REQUEST_CAMERA);
            }else {

            }
        }
        openCvCameraView = (CameraBridgeViewBase) findViewById(R.id.javaCameraView);
        openCvCameraView.setCameraIndex(0); //摄像头索引        -1/0：后置双摄     1：前置
        openCvCameraView.enableFpsMeter(); //显示FPS
        openCvCameraView.setCvCameraViewListener(this);
        btn_face1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               face1= saveMatData(mRgba,"face1.jpg");
               bitmap1 = ImageTools.getImageFromPath(face1);
               img1.setImageBitmap(bitmap1);

            }
        });
        btn_face2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              face2=  saveMatData(mRgba,"face2.jpg");
                bitmap2 = ImageTools.getImageFromPath(face2);
                img2.setImageBitmap(bitmap2);
            }
        });
        btn_compare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                bitmap1=ImageTools.zoomBitmap(bitmap1,300,300);
//                bitmap2 = ImageTools.zoomBitmap(bitmap2,300,300);
                Log.d("人脸",bitmap1.getWidth()+"--"+bitmap2.getWidth()+"|"+bitmap1.getHeight()+"--"+bitmap2.getHeight());
             double d=   FaceMatcher.histogramMatch(bitmap1,bitmap2);

             String str = "对比度：";
             textView.setText(str+d);
              Log.d("人脸对比系数",d+"");
            }
        });
    }
    /**
     * 特征对比
     * 对比的两张图片必须是灰度图
     *
     * @param file1 人脸特征
     * @param file2 人脸特征
     * @return 相似度
     */
    public double CmpPic(String file1, String file2) {


        return 0;
    }
    public String saveMatData(Mat mat,String path) {
        File fileDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "mybook");
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        String name = path;
        File tempFile = new File(fileDir.getAbsoluteFile() + File.separator, name);
        Mat dst = new Mat(mat.rows(), mat.cols(), CvType.CV_8UC4);    //新建目标输出图像
        Imgproc.cvtColor(mat, dst, Imgproc.COLOR_RGB2BGR);
        Imgcodecs.imwrite(tempFile.getAbsolutePath(), dst);
        Log.e("存储", "FielSaveMatData" + tempFile.getPath());
        return tempFile.getPath();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
//                showToast("权限已拒绝");
            }
        }else if (requestCode == MY_PERMISSIONS_REQUEST_CALL_CAMERA){
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    //判断是否勾选禁止后不再询问
                    boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissions[i]);
                    if (showRequestPermission) {
//                        showToast("权限未申请");
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "OpenCV init error");
        }
        initializeOpenCVDependencies();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat();
        mGray = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
    }
    private static final String FACE1 = "face1";
    private static final String FACE2 = "face2";
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba(); //RGBA
        mGray = inputFrame.gray(); //单通道灰度图

        if (absoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * 0.2f) > 0) {
                absoluteFaceSize = Math.round(height * 0.2f);
            }
        }

        //解决  前置摄像头旋转显示问题
//        Core.flip(mRgba, mRgba, 0); //旋转
//        Core.flip(mGray, mGray, 0);



        //检测并显示
        MatOfRect faces = new MatOfRect();
        if (cascadeClassifier != null) {
            cascadeClassifier.detectMultiScale(mGray, faces, 1.1, 2, 2, new Size(absoluteFaceSize, absoluteFaceSize), new Size());
        }
        Rect[] facesArray = faces.toArray();
        if (facesArray.length > 0){
            for (int i = 0; i < facesArray.length; i++) {    //用框标记
                Rect rect = new Rect();
                if(facesArray[i].width<400){
                   rect.x=(facesArray[i].x-(400-facesArray[i].width)/2)>0?(facesArray[i].x-(400-facesArray[i].width)/2):0;
                  rect.width=400;
                }
                if(facesArray[i].height<400){
                    rect.y=(facesArray[i].y-(400-facesArray[i].height)/2)>0?(facesArray[i].y-(400-facesArray[i].height)/2):0;
                   rect.height=400;
                }
//                Imgproc.rectangle(mRgba, rect.tl(), rect.br(), new Scalar(0, 255, 0, 255), 3);
                Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0, 255), 3);
                Log.d("face",facesArray[i].toString());
//                mat_f = new Mat(mRgba,rect);
            }
        }

        return mRgba;
    }
}
