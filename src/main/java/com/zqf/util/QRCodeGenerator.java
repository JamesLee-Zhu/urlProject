package com.zqf.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swetake.util.Qrcode;

public class QRCodeGenerator {

	private final static Logger logger = LoggerFactory.getLogger(QRCodeGenerator.class);

	public static String buildBarCodeTwo(File filePath, String fileName, String content, String imgType, boolean useLogoImg, String logoPath, int version) throws Exception {
		if (!filePath.exists()) {
			try {
				filePath.mkdirs();
			} catch (Exception e) {
				logger.error("二维码目录：" + "[->]" + filePath.getName() + ",创建失败", e);
				fileName = null;
			}
		}
		BufferedImage bufImg = qRCodeCommon(content, imgType, version, useLogoImg, logoPath);
		int width = bufImg.getWidth();
		int height = bufImg.getHeight();
		fileName = fileName + "_" + String.valueOf(width) + "X" + String.valueOf(height) + ".jpg";
		File imgFile = new File(filePath.getPath() + File.separator + fileName);
		ImageIO.write(bufImg, imgType, imgFile);
		return fileName;
	}

	/**
	 * 生成二维码(QRCode)图片的公共方法
	 * 
	 * @param content
	 *            存储内容
	 * @param imgType
	 *            图片类型
	 * @param size
	 *            二维码尺寸
	 * @return
	 */
	private static BufferedImage qRCodeCommon(String content, String imgType, int version, boolean useLogoImg, String logoPath) throws Exception {
		BufferedImage bufImg = null;
		Qrcode qrcodeHandler = new Qrcode();
		// 设置二维码排错率，可选L(7%)、M(15%)、Q(25%)、H(30%)，排错率越高可存储的信息越少，但对二维码清晰度的要求越小
		qrcodeHandler.setQrcodeErrorCorrect('L');
		qrcodeHandler.setQrcodeEncodeMode('B');
		// 设置设置二维码尺寸，取值范围1-40，值越大尺寸越大，可存储的信息越大
		qrcodeHandler.setQrcodeVersion(version);
		// 获得内容的字节数组，设置编码格式
		byte[] contentBytes = content.getBytes("utf-8");
		// 图片尺寸
		int imgSize = 67 + 12 * (version - 1);
		bufImg = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_RGB);
		Graphics2D gs = bufImg.createGraphics();
		// 设置背景颜色
		gs.setBackground(Color.WHITE);
		gs.clearRect(0, 0, imgSize, imgSize);
		// 设定图像颜色> BLACK
		gs.setColor(Color.BLACK);
		// 设置偏移量，不设置可能导致解析出错
		int pixoff = 2;
		// 输出内容> 二维码
		boolean[][] codeOut = qrcodeHandler.calQrcode(contentBytes);
		for (int i = 0; i < codeOut.length; i++) {
			for (int j = 0; j < codeOut.length; j++) {
				if (codeOut[j][i]) {
					gs.fillRect(j * 3 + pixoff, i * 3 + pixoff, 3, 3);
				}
			}
		}
		// 是否启用logo
		if (useLogoImg) {
			int width_4 = imgSize / 4;
			int width_8 = width_4 / 2;
			int height_4 = imgSize / 4;
			int height_8 = height_4 / 2;
			Image img = ImageIO.read(new File(logoPath));
			gs.drawImage(img, width_4 + width_8, height_4 + height_8, width_4, height_4, null);
		}
		gs.dispose();
		bufImg.flush();
		return bufImg;
	}

	public static void main(String[] args) throws Exception {
		// int imgSize = 5;
		// String qRcode =
		// String.valueOf(Calendar.getInstance().getTimeInMillis());
		// File file = new File("d:/qrcode");
		// QRCodeGenerator.buildBarCodeTwo(file, qRcode,
		// "http://zhu.hunme.net:9080/fintechBank-Act/index.html?dev_code=1234567890123",
		// "jpg", true, "d:/1.jpg", imgSize);
		File normalFile = new File("d:/qrcode/a.jpg");
		File resizeFile = new File("d:/qrcode/b.jpg");
		ImageUtil.resize(normalFile.getPath(), resizeFile.getPath(), 50, 100);
	}

}
