package com.example.Fson.WorkClass.Ftp;

import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

/**
 * Created by PC on 2018/4/30.
 */
public class FTPClientFunctions {

    private static final String TAG = "FTPClientFunctions";

    private FTPClient ftpClient = new FTPClient(); // FTP客户端

    /**
     * 连接到FTP服务器
     *
     * @param host     ftp服务器域名
     * @param username 访问用户名
     * @param password 访问密码
     * @param port     端口
     * @return 是否连接成功
     */
    public boolean ftpConnect(String host, String username, String password, int port) {
        try {
            ftpClient = new FTPClient();
            ftpClient.setDataTimeout(1000 * 60);
            Log.e(TAG, "connecting to the ftp server " + host + " ：" + port);
            ftpClient.connect(host, 888);
            // 根据返回的状态码，判断链接是否建立成功
            if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                Log.e(TAG, "login to the ftp server");
                boolean status = ftpClient.login(username, password);
                /*
                 * 设置文件传输模式
                 * 避免一些可能会出现的问题，在这里必须要设定文件的传输格式。
                 * 在这里我们使用BINARY_FILE_TYPE来传输文本、图像和压缩文件。
                 */
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                return status;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Error: could not connect to host " + host);
        }
        return false;
    }
    /**
     * 断开ftp服务器连接
     *
     * @return 断开结果
     */
    public boolean ftpDisconnect() {
        // 判断空指针
        if (ftpClient == null) {
            return true;
        }
        // 断开ftp服务器连接
        try {
            ftpClient.logout();
            ftpClient.disconnect();
            return true;
        } catch (Exception e) {
            Log.d(TAG, "Error occurred while disconnecting from ftp server.");
        }
        return false;
    }

    /**
     * ftp 文件上传
     *
     * @param srcFilePath  源文件目录
     * //@param desFileName  文件名称
     * //@param desDirectory 目标文件
     * @return 文件上传结果
     */
    public boolean ftpUpload(String srcFilePath) {

        boolean status = false;
        srcFilePath = srcFilePath.substring(1, srcFilePath.length());
        String[] path = srcFilePath.split("/");
        ArrayList<String> pathArray = new ArrayList<>();
        for (int i = 0; i < path.length; i ++){
            if (i != 0 && i != 1 && i != 2 && i != 3 && i != path.length - 1){
                Log.e("path----", path[i]);
//                if (null != path[i] && !path[i].equals("")){
//                    pathArray.add(path[i]);
//                }
                pathArray.add(path[i]);
            }

//            pathArray.add(path[i]);
        }
        try {
            for(int i = 0; i < pathArray.size(); i++) {
                ftpClient.makeDirectory(new String(pathArray.get(i).getBytes("GBK"), "iso-8859-1"));
                ftpClient.changeWorkingDirectory(new String(pathArray.get(i).getBytes("GBK"), "iso-8859-1"));
            }
            FileInputStream srcFileStream = new FileInputStream(srcFilePath);
            status = ftpClient.storeFile(new File(srcFilePath).getName(), srcFileStream);
            srcFileStream.close();
            return status;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "upload failed: " + e.getLocalizedMessage());
        }
        return status;
    }

    /**
     * ftp 更改目录
     *
     * @param path 更改的路径
     * @return 更改是否成功
     */
    public boolean ftpChangeDir(String path) {
        boolean status = false;
        try {
            status = ftpClient.changeWorkingDirectory(path);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "change directory failed: " + e.getLocalizedMessage());
        }
        return status;
    }

}