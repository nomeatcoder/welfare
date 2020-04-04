package cn.nomeatcoder.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Component
@Data
@Slf4j
public class FTPUtils {
	
	@Value("${ftp.server.ip}")
	private String ip;
	@Value("${ftp.password}")
	private String pwd;
	@Value("${ftp.user}")
	private String user;
	private FTPClient ftpClient;

	public boolean uploadFile(List<File> fileList) throws IOException {

		log.info("开始连接ftp服务器");
		boolean result = uploadFile("image", fileList);
		log.info("开始连接ftp服务器,结束上传,上传结果:{}", result);
		return result;
	}

	private boolean uploadFile(String remotePath, List<File> fileList) throws IOException {
		boolean uploaded = true;
		FileInputStream fis = null;
		//连接FTP服务器
		if (connectServer(this.ip, this.user, this.pwd)) {
			try {
				ftpClient.changeWorkingDirectory(remotePath);
				ftpClient.setBufferSize(1024);
				ftpClient.setControlEncoding("UTF-8");
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
				ftpClient.enterLocalActiveMode();
				for (File fileItem : fileList) {
					fis = new FileInputStream(fileItem);
					ftpClient.storeFile(fileItem.getName(), fis);
				}

			} catch (IOException e) {
				log.error("上传文件异常", e);
				uploaded = false;
				e.printStackTrace();
			} finally {
				fis.close();
				ftpClient.disconnect();
			}
		}
		return uploaded;
	}


	private boolean connectServer(String ip, String user, String pwd) {

		boolean isSuccess = false;
		ftpClient = new FTPClient();
		try {
			ftpClient.connect(ip);
			isSuccess = ftpClient.login(user, pwd);
		} catch (IOException e) {
			log.error("连接FTP服务器异常", e);
		}
		return isSuccess;
	}

}
