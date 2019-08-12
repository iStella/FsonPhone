package com.example.Fson.WorkClass.Ftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import android.os.Environment;
import android.util.Log;

import com.example.Fson.ToolsClass.iflytek.FucUtil;

/**
 * Created by PC on 2018/4/30.
 */

public class FTPManager {
    FTPClient ftpClient = null;

    public FTPManager() {
        ftpClient = new FTPClient();
    }

    // 连接到ftp服务器
    public synchronized boolean connect() throws Exception {
        if (ftpClient.isConnected()) {      //判断是否已登陆
            ftpClient.disconnect();
            Log.e("FTP", "已登录");
        }else {
            Log.e("FTP", "未登录");
        }
        ftpClient.setDataTimeout(20000);    //设置连接超时时间
        ftpClient.setControlEncoding("utf-8");

        String uploadIp = "192.168.1.101";
        String readStorageTxt = FucUtil.readStorageTxt(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "FSON/config.txt");
        if (readStorageTxt.length() != 0) {
            ArrayList<String> mLocalConfigs = new ArrayList<>();
            String[] Keys = readStorageTxt.split("\\n|\\r");
            for (String keys : Keys) {
                if (keys.length() != 0) {
                    mLocalConfigs.add(keys);
                }
            }
            for (int i = 0; i < mLocalConfigs.size(); i ++){
                uploadIp = mLocalConfigs.get(0).substring(3, mLocalConfigs.get(0).length());
            }
        }

        ftpClient.connect(uploadIp);    /* 端口 */
        ftpClient.login("root", "123456");
        if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
            Log.e("FTP", "ftp连接成功");
            return true;
        }else {
            Log.e("FTP", "ftp连接失败");
            return false;
        }
    }

    // 创建文件夹
    public boolean createDirectory(String path) throws Exception {
        boolean bool = false;
        String directory = path.substring(0, path.lastIndexOf("/") + 1);
        int start = 0;
        int end = 0;
        if (directory.startsWith("/")) {
            start = 1;
        }
        end = directory.indexOf("/", start);
        while (true) {
            String subDirectory = directory.substring(start, end);
            if (!ftpClient.changeWorkingDirectory(subDirectory)) {
                ftpClient.makeDirectory(subDirectory);
                ftpClient.changeWorkingDirectory(subDirectory);
                bool = true;
            }
            start = end + 1;
            end = directory.indexOf("/", start);
            if (end == -1) {
                break;
            }
        }
        return bool;
    }

    // 实现上传文件的功能
    public synchronized boolean uploadFile(String localPath, String serverPath) throws Exception {
        // 上传文件之前，先判断本地文件是否存在
        File localFile = new File(localPath);
        if (!localFile.exists()) {
            Log.e("FTP", "本地文件不存在");
            return false;
        }
        Log.e("FTP", "本地文件存在，名称为：" + localFile.getName());
        createDirectory(serverPath); // 如果文件夹不存在，创建文件夹
        Log.e("FTP", "服务器文件存放路径：" + serverPath + localFile.getName());
        String fileName = localFile.getName();
        // 如果本地文件存在，服务器文件也在，上传文件，这个方法中也包括了断点上传
        long localSize = localFile.length(); // 本地文件的长度
        FTPFile[] files = ftpClient.listFiles(fileName);
        if (files.length == 0) {
            Log.e("FTP", "服务器文件不存在");
        } else {
            // 服务器文件的长度
            Log.e("FTP", String.valueOf(files[0].getSize()));
        }
        if (ftpClient.deleteFile(fileName)) {
            Log.e("FTP", "服务器文件存在,删除文件,开始重新上传");
        }else {
            return false;
        }
        RandomAccessFile raf = new RandomAccessFile(localFile, "r");
        // 进度
        long step = localSize / 100;
        long process = 0;
        long currentSize = 0;
        // 好了，正式开始上传文件
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.setRestartOffset(0);
        raf.seek(0);
        OutputStream output = ftpClient.appendFileStream(fileName);
        byte[] b = new byte[1024 * 5];
        int length = 0;
        while ((length = raf.read(b)) != -1) {
            output.write(b, 0, length);
            currentSize = currentSize + length;
            if (currentSize / step != process) {
                process = currentSize / step;
                if (process % 2 == 0) {
                    Log.e("FTP", "上传进度：" + process);
                }
            }
        }
        output.flush();
        output.close();
        raf.close();
        if (ftpClient.completePendingCommand()) {
            Log.e("FTP", "文件上传成功");
            return true;
        } else {
            Log.e("FTP", "文件上传失败");
            return false;
        }
    }

    // 实现下载文件功能，可实现断点下载
    public synchronized boolean downloadFile(String localPath, String serverPath)
            throws Exception {
        // 先判断服务器文件是否存在
        FTPFile[] files = ftpClient.listFiles(serverPath);
        if (files.length == 0) {
            System.out.println("服务器文件不存在");
            return false;
        }
        System.out.println("远程文件存在,名字为：" + files[0].getName());
        localPath = localPath + files[0].getName();
        // 接着判断下载的文件是否能断点下载
        long serverSize = files[0].getSize(); // 获取远程文件的长度
        File localFile = new File(localPath);
        long localSize = 0;
        if (localFile.exists()) {
            localSize = localFile.length(); // 如果本地文件存在，获取本地文件的长度
            if (localSize >= serverSize) {
                System.out.println("文件已经下载完了");
                File file = new File(localPath);
                file.delete();
                System.out.println("本地文件存在，删除成功，开始重新下载");
                return false;
            }
        }
        // 进度
        long step = serverSize / 100;
        long process = 0;
        long currentSize = 0;
        // 开始准备下载文件
        ftpClient.enterLocalActiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        OutputStream out = new FileOutputStream(localFile, true);
        ftpClient.setRestartOffset(localSize);
        InputStream input = ftpClient.retrieveFileStream(serverPath);
        byte[] b = new byte[1024];
        int length = 0;
        while ((length = input.read(b)) != -1) {
            out.write(b, 0, length);
            currentSize = currentSize + length;
            if (currentSize / step != process) {
                process = currentSize / step;
                if (process % 10 == 0) {
                    System.out.println("下载进度：" + process);
                }
            }
        }
        out.flush();
        out.close();
        input.close();
        // 此方法是来确保流处理完毕，如果没有此方法，可能会造成现程序死掉
        if (ftpClient.completePendingCommand()) {
            System.out.println("文件下载成功");
            return true;
        } else {
            System.out.println("文件下载失败");
            return false;
        }
    }

    // 如果ftp上传打开，就关闭掉
    public void closeFTP() throws Exception {
        if (ftpClient.isConnected()) {
            ftpClient.disconnect();
        }
    }
}