/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.example.Fson.ToolsClass;

import android.annotation.TargetApi;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Camera related utilities.
 */
public class CameraHelper {

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int MEDIA_TYPE_HTML = 3;
    public static final int MEDIA_TYPE_DATA = 4;
    public static final int MEDIA_TYPE_DATABASE = 5;

    /**
     * Iterate over supported camera video sizes to see which one best fits the
     * dimensions of the given view while maintaining the aspect ratio. If none can,
     * be lenient with the aspect ratio.
     *
     * @param supportedVideoSizes Supported camera video sizes.
     * @param previewSizes        Supported camera preview sizes.
     * @param w                   The width of the view.
     * @param h                   The height of the view.
     * @return Best match camera video size to fit in the view.
     */
    public static Camera.Size getOptimalVideoSize(List<Camera.Size> supportedVideoSizes, List<Camera.Size> previewSizes, int w, int h) {
        // Use w_first_activity very small tolerance because we want an exact match.
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        // Supported video sizes list might be null, it means that we are allowed to use the preview
        // sizes
        List<Camera.Size> videoSizes;
        if (supportedVideoSizes != null) {
            videoSizes = supportedVideoSizes;
        } else {
            videoSizes = previewSizes;
        }
        Camera.Size optimalSize = null;

        // Start with max value and refine as we iterate over available video sizes. This is the
        // minimum difference between view and camera height.
        double minDiff = Double.MAX_VALUE;
        // Target view height
        int targetHeight = h;
        // Try to find w_first_activity video size that matches aspect ratio and the target view size.
        // Iterate over all available sizes and pick the largest size that can fit in the view and
        // still maintain the aspect ratio.
        for (Camera.Size size : videoSizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff && previewSizes.contains(size)) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        // Cannot find video size that matches the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : videoSizes) {
                if (Math.abs(size.height - targetHeight) < minDiff && previewSizes.contains(size)) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public static Camera.Size getOptimalPictureSize(List<Camera.Size> supportedPicSizes, int w, int h) {
        // Use w_first_activity very small tolerance because we want an exact match.
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        // Supported video sizes list might be null, it means that we are allowed to use the preview
        // sizes
        Camera.Size optimalSize = null;
        // Start with max value and refine as we iterate over available video sizes. This is the
        // minimum difference between view and camera height.
        double minDiff = Double.MAX_VALUE;
        // Target view height
        int targetHeight = h;
        // Try to find w_first_activity video size that matches aspect ratio and the target view size.
        // Iterate over all available sizes and pick the largest size that can fit in the view and
        // still maintain the aspect ratio.
        for (Camera.Size size : supportedPicSizes) {
            if (size.width == w || h == size.height) {
                optimalSize = size;
                break;
            }
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        return optimalSize;
    }

    /**
     * @return the default camera on the device. Return null if there is no camera on the device.
     */
    public static Camera getDefaultCameraInstance(int id) {
        return Camera.open(id);
    }

    /**
     * @return the default rear/back facing camera on the device. Returns null if camera is not
     * available.
     */
    public static Camera getDefaultBackFacingCameraInstance() {
        return getDefaultCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    /**
     * @return the default front facing camera on the device. Returns null if camera is not
     * available.
     */
    public static Camera getDefaultFrontFacingCameraInstance() {
        return getDefaultCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    /**
     * @param position Physical position of the camera i.e Camera.CameraInfo.CAMERA_FACING_FRONT
     *                 or Camera.CameraInfo.CAMERA_FACING_BACK.
     * @return the default camera on the device. Returns null if camera is not available.
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private static Camera getDefaultCamera(int position) {
        // Find the total number of cameras available
        try {
            int mNumberOfCameras = Camera.getNumberOfCameras();

            // Find the ID of the back-facing ("default") camera
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            for (int i = 0; i < mNumberOfCameras; i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == position) {
                    return Camera.open(i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creates w_first_activity media file in the {@code Environment.DIRECTORY_PICTURES} directory. The directory
     * is persistent and available to other applications like gallery.
     *
     * @param type Media type. Can be video or image.
     * @return A file object pointing to the newly created file.
     */
    public static File getOutputMediaFile(int type, String item_num, String title, String TITLE, String step, String work_id) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return null;
        }
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "FSON/" + title + "/" + work_id + "/" + TITLE + "/" + step);
        //Log.e("mediaStorageDirPath", Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "FSON/" + title + "/");
        //File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
        //Environment.DIRECTORY_PICTURES), "CameraSample");
        //This location works best if you want the created images to be shared
        //between applications and persist after your app has been uninstalled.
        //Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("CameraSample", "failed to create directory");
                return null;
            }
        }
        // Create w_first_activity media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "FSON/" + title + "/" + work_id + "/" + TITLE + "/" + step);
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + item_num + "." + step + "_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "FSON/" + title + "/" + work_id + "/" + TITLE + "/" + step);
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + item_num + "." + step + "_" + timeStamp + ".mp4");
        } else if (type == MEDIA_TYPE_HTML) {
            mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "FSON/" + title + "/" + work_id + "/" + TITLE + "/" + step);
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "HTML_" + timeStamp + ".html");
        } else {
            return null;
        }
        return mediaFile;
    }

    public static File getOutputMediaFile(int type, String title, String work_id) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return null;
        }
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "FSON/" + title + "/" + work_id);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("CameraSample", "failed to create directory");
                return null;
            }
        }
        File mediaData = new File(mediaStorageDir.getPath() + File.separator + "test.txt");
        if (!mediaData.exists()) {
            try {
                mediaData.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Create w_first_activity media file name
        File mediaFile;
        if (type == MEDIA_TYPE_DATA) {
            mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "FSON/" + title + "/" + work_id);
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "test.txt");
        } else {
            return null;
        }
        return mediaFile;
    }

    public static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return null;
        }
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "FSON");


        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("CameraSample", "failed to create directory");
                return null;
            }
        }
        File mediaDatabase = new File(mediaStorageDir.getPath() + File.separator + "workflow.txt");
        if (!mediaDatabase.exists()) {
            try {
                mediaDatabase.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Create w_first_activity media file name
        File mediaFile;
        if (type == MEDIA_TYPE_DATABASE) {
            mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "FSON");
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "workflow.txt");
        } else {
            return null;
        }
        return mediaFile;
    }

    public static boolean writenotwilf(JSONObject jsonObject) {
        if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return false;
        }
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "FSON");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("CameraSample", "failed to create directory");
                return false;
            }
        }
        File mediaDatabase = new File(mediaStorageDir.getPath() + File.separator + "isupload.txt");
        if (!mediaDatabase.exists()) {
            try {
                mediaDatabase.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(mediaDatabase));
            String line = "";
            BufferedWriter bufferedWriter = null;
            StringBuffer stringBuffer = new StringBuffer();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            if (stringBuffer.length() > 0) {
                bufferedWriter = new BufferedWriter(new FileWriter(mediaDatabase));
                bufferedWriter.write(stringBuffer + "&&" + jsonObject.toString());
                bufferedWriter.flush();
                bufferedWriter.close();
                bufferedReader.close();
                return true;
            } else {
                bufferedWriter = new BufferedWriter(new FileWriter(mediaDatabase));
                bufferedWriter.write(jsonObject.toString());
                bufferedWriter.flush();
                bufferedWriter.close();
                bufferedReader.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static File getOutputMediaFile_(int type, String work_id) {
        if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return null;
        }
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "FSON");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("CameraSample", "failed to create directory");
                return null;
            }
        }

        if (type == CameraHelper.MEDIA_TYPE_DATA) {
            File mediaDatabase =new File(mediaStorageDir.getPath() + File.separator + work_id + ".txt");
            if (!mediaDatabase.exists()) {
                try {
                    mediaDatabase.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return mediaDatabase;
        }


        return null;
    }


    public static boolean writerecord(JSONObject jsonObject) {
        if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return false;
        }
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "FSON");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("CameraSample", "failed to create directory");
                return false;
            }
        }
        File mediaDatabase = new File(mediaStorageDir.getPath() + File.separator + "isuprecord.txt");
        if (!mediaDatabase.exists()) {
            try {
                mediaDatabase.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(mediaDatabase));
            String line = "";
            BufferedWriter bufferedWriter = null;
            StringBuffer stringBuffer = new StringBuffer();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            if (stringBuffer.length() > 0) {
                bufferedWriter = new BufferedWriter(new FileWriter(mediaDatabase));
                bufferedWriter.write(stringBuffer + "&&" + jsonObject.toString());
                bufferedWriter.flush();
                bufferedWriter.close();
                bufferedReader.close();
                return true;
            } else {
                bufferedWriter = new BufferedWriter(new FileWriter(mediaDatabase));
                bufferedWriter.write(jsonObject.toString());
                bufferedWriter.flush();
                bufferedWriter.close();
                bufferedReader.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean writeimges(JSONObject jsonObject) {
        if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return false;
        }
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "FSON");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("CameraSample", "failed to create directory");
                return false;
            }
        }
        File mediaDatabase = new File(mediaStorageDir.getPath() + File.separator + "uploadimges.txt");
        if (!mediaDatabase.exists()) {
            try {
                mediaDatabase.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
//            这段代码可以实现txt文件的追加
//            FileInputStream fis = new FileInputStream(mediaDatabase);
//            byte[] buff = new byte[1024];
//            StringBuilder stringBuilder = new StringBuilder();
//            int len = 0;
//            while ((len = fis.read(buff)) > 0) {
//                stringBuilder.append(new String(buff, 0, len));
//            }
//            fis.close();
//            FileOutputStream fos = new FileOutputStream(mediaDatabase);
//            fos.write((stringBuilder+"&&" + jsonObject.toString()).getBytes());
//            fos.write("\r\n".getBytes());//写入换行

            BufferedReader bufferedReader = new BufferedReader(new FileReader(mediaDatabase));
            String line = "";
            BufferedWriter bufferedWriter = null;
            StringBuffer stringBuffer = new StringBuffer();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            if (stringBuffer.length() > 0) {
                bufferedWriter = new BufferedWriter(new FileWriter(mediaDatabase));
                FileOutputStream fos = null;
                bufferedWriter.write(stringBuffer + "&&" + jsonObject.toString());
                bufferedWriter.flush();
                bufferedWriter.close();
                bufferedReader.close();
                return true;
            } else {
                bufferedWriter = new BufferedWriter(new FileWriter(mediaDatabase));
                bufferedWriter.write(jsonObject.toString());
                bufferedWriter.flush();
                bufferedWriter.close();
                bufferedReader.close();
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static File getOutputbackgroundFile() {
        if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return null;
        }


        File mediaStorageDir = new File(Environment.getDataDirectory().getAbsolutePath() + File.separator + "FSON/");
        if (!mediaStorageDir.exists()) {
            try {
                mediaStorageDir.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mediaStorageDir;
    }
}
