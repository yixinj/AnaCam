package io.github.yixinj.anacam;

import android.util.Log;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AnacamUtilities {

    public static Mat processImage(String path, int numContours, int threshMin) {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat img = Imgcodecs.imread(path);
        Mat imgGray = new Mat();
        Imgproc.cvtColor(img, imgGray, Imgproc.COLOR_BGR2GRAY);
        Mat imgHsv = new Mat();
        Imgproc.cvtColor(img, imgHsv, Imgproc.COLOR_BGR2HSV);

        Mat thresh = getThreshold(imgGray, threshMin);
//        Imgcodecs.imwrite("img/thresh.jpg", thresh);
        List<MatOfPoint> contours = getContours(thresh, numContours);

        // Saves masks
        List<Mat> masks = generateMasks(img, contours);
//        for (int i = 0; i < masks.size(); i++) {
//            Imgcodecs.imwrite("img/contour" + i + ".jpg", masks.get(i));
//        }

        // Get mean hue
        List<Double> hueValues = getMeanHue(imgHsv, masks);

        // Outlines and labels for final image
        Mat imgOutlined = img.clone();
        for (int i = 0; i < numContours; i++) {
            Imgproc.drawContours(imgOutlined, contours, i, new Scalar(0, 255, 255), 3);

            Moments m = Imgproc.moments(contours.get(i));
            Point p = new Point(Math.round(m.m10 / m.m00), Math.round(m.m01 / m.m00));
            String t = Double.toString(hueValues.get(i)*2).substring(0, 5);
            Imgproc.putText(imgOutlined, t, p, 0, 1, new Scalar(0, 0, 255), 3);
        }
//        Imgcodecs.imwrite("img/outlined.jpg", imgOutlined);

        System.out.println(hueValues);

        Mat imgFinal = new Mat();
        Imgproc.cvtColor(imgOutlined, imgFinal, Imgproc.COLOR_BGR2RGB);

        return imgFinal;
    }


    /**
     * Get threshold of img
     *
     * @param img
     * @param threshMin
     * @return
     */
    public static Mat getThreshold(Mat img, int threshMin) {
        Mat thresh = new Mat();
        Imgproc.threshold(img, thresh, threshMin, 255, Imgproc.THRESH_BINARY);
        return thresh;
    }

    /**
     * Get the top n contours from thresh
     *
     * @param thresh
     * @param numContours
     * @return
     */
    public static List<MatOfPoint> getContours(Mat thresh, int numContours) {
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(thresh, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        Collections.sort(contours, new Comparator<MatOfPoint>() {
            public int compare(MatOfPoint c1, MatOfPoint c2) {
                double a1 = Imgproc.contourArea(c1);
                double a2 = Imgproc.contourArea(c2);

                if (a1 < a2) return -1;
                if (a1 > a2) return 1;
                return 0;
            }
        });
        Collections.reverse(contours);
        return contours.subList(0, numContours);
    }

    /**
     * Gets n number of masks and fills them in according to contours
     *
     * @param img
     * @param contours
     * @return
     */
    public static List<Mat> generateMasks(Mat img, List<MatOfPoint> contours) {
        List<Mat> masks = new ArrayList<>();
        int n = contours.size();
        for (int i = 0; i < n; i++) {
            Mat newMask = new Mat(img.rows(), img.cols(), CvType.CV_8U, Scalar.all(0));
            Imgproc.drawContours(newMask, contours.subList(i, i + 1), -1, new Scalar(255, 255, 255), -1);

            Moments moments = Imgproc.moments(contours.get(i));
            double cx = moments.m10 / moments.m00;
            double cy = moments.m01 / moments.m00;
            Point point = new Point(cx, cy);
            Imgproc.putText(newMask, "Area " + i + 1, point, 1, 1.0, new Scalar(255));

            masks.add(newMask);
        }

        return masks;
    }

    /**
     * Find mean HUE from each contour (using masks) on the image specified
     *
     * @param img   source image to find mean of
     * @param masks
     * @return
     */
    public static List<Double> getMeanHue(Mat img, List<Mat> masks) {
        List<Double> hueValues = new ArrayList<>();

        for (Mat mask : masks) {
            Scalar meanColour = Core.mean(img, mask);
            hueValues.add(meanColour.val[0]);
        }

        return hueValues;
    }
}

