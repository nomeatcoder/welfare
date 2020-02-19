package cn.nomeatcoder.utils;

import cn.nomeatcoder.common.Const;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class FTPUtils {

	private static final Logger logger = LoggerFactory.getLogger(FTPUtils.class);

	private static String ftpIp = Const.FTP_SERVER_IP;
	private static String ftpUser = Const.FTP_USERNAME;
	private static String ftpPass = Const.FTP_PASSWORD;

	private String ip;
	private int port;
	private String user;
	private String pwd;
	private FTPClient ftpClient;

	public FTPUtils(String ip, int port, String user, String pwd) {
		this.ip = ip;
		this.port = port;
		this.user = user;
		this.pwd = pwd;
	}

	public static boolean uploadFile(List<File> fileList) throws IOException {
		FTPUtils ftpUtil = new FTPUtils(ftpIp, 21, ftpUser, ftpPass);
		logger.info("开始连接ftp服务器");
		boolean result = ftpUtil.uploadFile("image", fileList);
		logger.info("开始连接ftp服务器,结束上传,上传结果:{}", result);
		return result;
	}


	private boolean uploadFile(String remotePath, List<File> fileList) throws IOException {
		boolean uploaded = true;
		FileInputStream fis = null;
		//连接FTP服务器
		if (connectServer(this.ip, this.port, this.user, this.pwd)) {
			try {
				ftpClient.changeWorkingDirectory(remotePath);
				ftpClient.setBufferSize(1024);
				ftpClient.setControlEncoding("UTF-8");
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
				ftpClient.enterLocalPassiveMode();
				for (File fileItem : fileList) {
					fis = new FileInputStream(fileItem);
					ftpClient.storeFile(fileItem.getName(), fis);
				}

			} catch (IOException e) {
				logger.error("上传文件异常", e);
				uploaded = false;
				e.printStackTrace();
			} finally {
				fis.close();
				ftpClient.disconnect();
			}
		}
		return uploaded;
	}


	private boolean connectServer(String ip, int port, String user, String pwd) {

		boolean isSuccess = false;
		ftpClient = new FTPClient();
		try {
			ftpClient.connect(ip);
			isSuccess = ftpClient.login(user, pwd);
		} catch (IOException e) {
			logger.error("连接FTP服务器异常", e);
		}
		return isSuccess;
	}

}
