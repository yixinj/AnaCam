package io.github.yixinj.anacam;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;


public class Result extends AppCompatActivity {

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    displayImage();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
//        displayImage();
    }

    private void displayImage() {
        Intent intent = getIntent();
        String path = intent.getStringExtra("path");
        int numContours = intent.getIntExtra("numContours", 3);

        Mat imgOutlined = AnacamUtilities.processImage(path, 3);

//        Mat mat = new Mat();
//
        // find the imageview and draw it!
//        File imgFile = new  File(path);
        ImageView imageView = (ImageView) findViewById(R.id.analysis_image_preview);
//        if(imgFile.exists()){
//            Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//            imageView.setImageBitmap(bmp);
//            Mat mat = new Mat();
//            Bitmap bmp32 = bmp.copy(Bitmap.Config.ARGB_8888, true);
//            Utils.bitmapToMat(bmp32, mat);
//        }

        // convert to bitmap:
        Bitmap bm = Bitmap.createBitmap(imgOutlined.cols(), imgOutlined.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(imgOutlined, bm);

        // find the imageview and draw it!
        imageView.setImageBitmap(bm);

    }
}
