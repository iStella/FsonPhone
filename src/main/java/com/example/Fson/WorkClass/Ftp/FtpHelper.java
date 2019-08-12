package com.example.Fson.WorkClass.Ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import android.util.Log;

/**
 * Created by PC on 2018/4/30.
 */
/**
 * FTP上传文件类
 * @author lookingfor
 *
 */
public class FtpHelper {
    /**
     * 服务器名.
     */
    private String hostName;

    /**
     * 用户名.
     */
    private String userName;

    /**
     * 密码.
     */
    private String password;

    /**
     * FTP连接.
     */
    private FTPClient ftpClient;

    /**
     * 新建文件夹名
     */
    private String dirName;

    /**
     * TAG
     */
    private String TAG = "MyListener";

    /**
     * 构造函数.
     *
     * @param host hostName 服务器名
     * @param user userName 用户名
     * @param pass password 密码
     */
    public FtpHelper(String host, String user, String pass) {
        this.hostName = host;
        this.userName = user;
        this.password = pass;
        this.ftpClient = new FTPClient();
    }

    /**
     * 打开FTP服务.
     *
     */
    public boolean openConnect() {
        boolean flag = true;
        // 编码格式为UTF-8
        ftpClient.setControlEncoding("UTF-8");
        int reply; // 服务器响应值
        try {
            // 连接至服务器
            ftpClient.setDefaultPort(21);//默认端口21
            ftpClient.setDefaultTimeout(30000);
            ftpClient.connect(hostName);
            // 获取响应值
            reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                // 断开连接
                ftpClient.disconnect();
                Log.i(TAG, "connect failed " + reply);
                return flag = false;
            }
            // 登录到服务器
            ftpClient.login(userName, password);
            // 获取响应值
            reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                // 断开连接
                ftpClient.disconnect();
                Log.i(TAG, "login failed " + reply);
                return flag = false;
            } else {

                // 使用被动模式设为默认
                ftpClient.enterLocalPassiveMode();
                // 以二进制文件发送
                ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
                Log.i(TAG, "login");
                return flag;
            }
        } catch (Exception e) {
            // TODO: handle exception
            Log.i(TAG, "connect failed message: " + e);
            return flag = false;
        }

    }

    /**
     * 关闭FTP服务.
     *
     */
    public void closeConnect()  {
        if (ftpClient != null) {
            try {
                ftpClient.logout();
                // 断开连接
                ftpClient.disconnect();
                Log.i(TAG, "logout");
            } catch (Exception e) {
                // TODO: handle exception
                Log.i(TAG, "logout failed message : " + e);
            }
        }
    }

    /**
     * 在FTP上创建文件夹
     * @param dirname
     * @return成功返回true；
     */
    public boolean createDirectory(String dirname) {
        boolean flag = true;
        this.dirName = dirname;
        try {
            ftpClient.makeDirectory(dirName);
            return flag;
        } catch (Exception e) {
            Log.i(TAG, "failed message : "+e);
            closeConnect();
            return flag = false;
        }
    }

    /**
     * 更改ftp工作目录
     * @param dirname
     * @return
     */
    public boolean changeDir(String dirname){
        this.dirName = dirname;
        try {
            ftpClient.changeWorkingDirectory(dirName);
            return true;
        } catch (Exception e) {
            // TODO: handle exception

            Log.i(TAG, "changedir failed message: " + e);
            closeConnect();
            return false;
        }
    }
    /**
     * 上传文件.
     *
     * @param localFile 本地文件
     * @return true上传成功, false上传失败
     */
    public boolean uploadingSingle(File localFile) {
        boolean flag = true;
        try {
            // 创建输入流
            InputStream inputStream = new FileInputStream(localFile);
            // 上传单个文件
            flag = ftpClient.storeFile(localFile.getName(), inputStream);
            // 关闭文件流
            inputStream.close();
            Log.i(TAG, "upload success");
            return flag;
        } catch (Exception e) {
            // TODO: handle exception
            Log.i(TAG, "upload failed message: " + e);
            return flag = false;
        }

    }
}