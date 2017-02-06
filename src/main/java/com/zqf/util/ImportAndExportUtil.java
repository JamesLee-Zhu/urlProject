package com.zqf.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.zqf.common.Constants;

public class ImportAndExportUtil {
	public static final String TMP_IMAGE_PATH = "/imgUpload/tmp/";
	public static final String REAL_IMAGE_PATH = "/imgUpload/real";
	public static final String TMP_PATH_SEPARATOR = "/tmp";

	/**
	 * @author ZhuQiFeng
	 * @addDate 2016年3月3日上午10:28:41
	 * @description 上传图片到临时目录
	 * @param TODO
	 * @return TODO
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	public static String uploadImageToTmp(HttpServletRequest request) throws Exception {
		ServletContext servletContext = request.getSession().getServletContext();
		// 解析器解析request的上下文
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(servletContext);
		String tmpPath = "";
		// 先判断request中是否包涵multipart类型的数据，
		if (multipartResolver.isMultipart(request)) {
			// 再将request中的数据转化成multipart类型的数据
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			Iterator<String> iter = multiRequest.getFileNames();
			while (iter.hasNext()) {
				MultipartFile file = multiRequest.getFile(iter.next());
				if (file != null) {
					String fileName = file.getOriginalFilename();
					String extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
					// 重命名图片名字
					SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmsss");
					String newFileName = df.format(new Date()) + "_" + new Random().nextInt(1000);
					fileName = newFileName + generateWord() + extension;
					String formatDate = DateUtil.getFormatDate(new Date(), "yyyyMMdd");
					String rootPath = servletContext.getRealPath("/");
					tmpPath = TMP_IMAGE_PATH + formatDate + "/" + fileName;
					String savePath = rootPath + tmpPath;
					File tempFile = new File(savePath);
					if (!tempFile.exists()) {
						tempFile.mkdirs();
					}
					file.transferTo(tempFile);
				}
			}
		}
		return tmpPath;
	}

	/**
	 * @author ZhuQiFeng
	 * @addDate 2016年3月3日上午9:44:24
	 * @description 图片从临时目录拷贝到真实目录
	 * @param TODO
	 * @return TODO
	 */
	public static String copyTmpImageToReal(HttpServletRequest request, String imgPath) {
		String finalPath = "";
		if (StringUtil.isNotEmpty(imgPath)) {
			StringTokenizer tokenizer = new StringTokenizer(imgPath, ",");
			while (tokenizer.hasMoreElements()) {
				String currPath = tokenizer.nextToken();
				String dbPath = "";
				if (currPath.contains(TMP_PATH_SEPARATOR)) {
					// 临时文件路径
					String projectPath = request.getSession().getServletContext().getRealPath("/");
					File inputFile = new File(projectPath + currPath);
					String inPath = inputFile.getAbsolutePath();
					String realPath = currPath.split(TMP_PATH_SEPARATOR)[1];
					// 路径分隔符转换
					projectPath = projectPath.replaceAll("\\\\", "/");
					String dataRoot = projectPath.split("/")[0];
					dbPath = REAL_IMAGE_PATH + realPath;
					// 要拷贝的文件路径
					File outputFile = new File(dataRoot + dbPath);
					String ab_outPath = outputFile.getAbsolutePath();
					String substring = "";
					if (ab_outPath.lastIndexOf("/") > 0) {
						substring = ab_outPath.substring(0, ab_outPath.lastIndexOf("/"));
					} else if (ab_outPath.lastIndexOf("\\") > 0) {
						substring = ab_outPath.substring(0, ab_outPath.lastIndexOf("\\"));
					}
					File createFile = new File(substring);
					if (!createFile.exists()) {
						createFile.mkdirs();
					}
					try {
						FileUtils.copyFile(inPath, ab_outPath);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
						finalPath = "";
					}
				} else {
					dbPath = currPath;
				}
				if (StringUtil.isEmpty(finalPath)) {
					finalPath = dbPath;
				} else {
					finalPath += "," + dbPath;
				}
			}
		}
		return finalPath;
	}

	/**
	 * @author ZhuQiFeng
	 * @addDate 2016年9月24日下午2:50:52
	 * @description 临时目录图片通过ftp上传到图片服务器
	 * @param imgPath
	 * @return TODO
	 */
	public static String copyTmpImageToFTP(HttpServletRequest request, String imgPath) {
		String finalPath = "";
		if (StringUtil.isNotEmpty(imgPath)) {
			StringTokenizer tokenizer = new StringTokenizer(imgPath, Constants.SEPARATOR_COMMA);
			while (tokenizer.hasMoreElements()) {
				String currPath = tokenizer.nextToken();
				String dbPath = "";
				if (currPath.contains(TMP_PATH_SEPARATOR)) {
					String ftp_server_ip = PropertiesUtil.getConfig("ftp_server_ip");
					Integer ftp_server_port = (PropertiesUtil.getConfig("ftp_server_port") == null || "".equals(PropertiesUtil.getConfig("ftp_server_port"))) ? 21 : Integer.valueOf(PropertiesUtil
							.getConfig("ftp_server_port"));
					String ftp_server_username = PropertiesUtil.getConfig("ftp_server_username");
					String ftp_server_password = PropertiesUtil.getConfig("ftp_server_password");
					String nginx_image_server_url = PropertiesUtil.getConfig("nginx_image_server_url");
					// 临时文件路径
					String projectPath = request.getSession().getServletContext().getRealPath("/");
					File inputFile = new File(projectPath + currPath);
					String inPath = inputFile.getAbsolutePath();
					String realPath = currPath.split(TMP_PATH_SEPARATOR)[1];
					dbPath = nginx_image_server_url + realPath;
					String folderName = realPath.substring(0, realPath.lastIndexOf("/"));
					String imgName = currPath.substring((currPath.lastIndexOf("/") + 1), currPath.length());
					try {
						InputStream input = new FileInputStream(inPath);
						FTPUtil.uploadFile(ftp_server_ip, ftp_server_port, ftp_server_username, ftp_server_password, folderName, imgName, input);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
						finalPath = "";
					}
				} else {
					dbPath = currPath;
				}
				if (StringUtil.isEmpty(finalPath)) {
					finalPath = dbPath;
				} else {
					finalPath += Constants.SEPARATOR_COMMA + dbPath;
				}
			}
		}
		return finalPath;
	}

	private static String generateWord() {
		String[] beforeShuffle = new String[] { "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
				"W", "X", "Y", "Z" };
		List<String> list = Arrays.asList(beforeShuffle);
		Collections.shuffle(list);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i));
		}
		String afterShuffle = sb.toString();
		String result = afterShuffle.substring(5, 9);
		return result;
	}
}
