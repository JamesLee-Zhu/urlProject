package com.zqf.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 * 
 */
public class FileUtils {
	/**
	 * @param response
	 * @param filePath
	 *            //文件完整路径(包括文件名和扩展名)
	 * @param fileName
	 *            //下载后看到的文件名
	 * @return 文件名
	 */
	public static void fileDownload(final HttpServletResponse response, String filePath, String fileName) throws Exception {
		byte[] data = FileUtils.toByteArray2(filePath);
		fileName = URLEncoder.encode(fileName, "UTF-8");
		response.reset();
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		response.addHeader("Content-Length", "" + data.length);
		response.setContentType("application/octet-stream;charset=UTF-8");
		OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
		outputStream.write(data);
		outputStream.flush();
		outputStream.close();
		response.flushBuffer();

	}

	/**
	 * 多文件上传
	 * 
	 * @param request
	 * @param path
	 *            存储路径
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public static void upload(HttpServletRequest request, String path, boolean issuo) throws IllegalStateException, IOException {
		FileUtils.createDir(path);
		// 创建一个通用的多部分解析器
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
		// 判断 request 是否有文件上传,即多部分请求
		if (multipartResolver.isMultipart(request)) {
			// 转换成多部分request
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			// 取得request中的所有文件名
			Iterator<String> iter = multiRequest.getFileNames();
			int size = multiRequest.getFileMap().size();
			while (iter.hasNext()) {
				// 取得上传文件
				MultipartFile file = multiRequest.getFile(iter.next());
				if (file != null) {
					// 取得当前上传文件的文件名称
					String myFileName = file.getOriginalFilename();
					// 如果名称不为“”,说明该文件存在，否则说明该文件不存在
					if (myFileName.trim() != "") {
						// 重命名上传后的文件名
						Long time = System.currentTimeMillis();
						String fileName = time + file.getOriginalFilename();
						// 定义上传路径
						String imgpath = path + File.separator + fileName;
						File localFile = new File(imgpath);
						file.transferTo(localFile);
						// 原图已上传完毕
						if (issuo) {
							String imgpath2 = path + File.separator + "s" + File.separator + fileName;
							File sfile = new File(imgpath2);
							String wh = ImageUtil.resize(imgpath, imgpath2, 180, 1F);
							int w = Integer.parseInt(wh.split(",")[0]);
							int h = Integer.parseInt(wh.split(",")[1]);
							int x = 0;
							int y = 0;
							if (size > 1) {
								if (w > h) {
									x = (w - h) / 2;
								} else if (h > w) {
									y = (h - w) / 2;
								}
								w = 180;
								h = 180;
							} else {
								if (w > h) {
									w = w * 180 / h;
									h = 180;
								} else {
									h = h * 180 / w;
									w = 180;
								}
								x = 0;
								y = 0;
							}
							ImageUtil.cut(sfile, sfile, x, y, w, h);
						}
					}
				}
			}
		}
	}

	/**
	 * @param inputFileName
	 *            你要压缩的文件夹(整个完整路径)
	 * @param zipFileName
	 *            压缩后的文件(整个完整路径)
	 */
	public static void zip(String inputFileName, String zipFileName) throws Exception {
		zip(zipFileName, new File(inputFileName));
	}

	private static void zip(String zipFileName, File inputFile) throws Exception {
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
		zip(out, inputFile, "");
		out.flush();
		out.close();
	}

	private static void zip(ZipOutputStream out, File f, String base) throws Exception {
		if (f.isDirectory()) {
			File[] fl = f.listFiles();
			out.putNextEntry(new ZipEntry(base + "/"));
			base = base.length() == 0 ? "" : base + "/";
			for (int i = 0; i < fl.length; i++) {
				zip(out, fl[i], base + fl[i].getName());
			}
		} else {
			out.putNextEntry(new ZipEntry(base));
			FileInputStream in = new FileInputStream(f);
			int b;
			while ((b = in.read()) != -1) {
				out.write(b);
			}
			in.close();
		}
	}

	@SuppressWarnings("rawtypes")
	public static void unZipFiles(File zipFile, String descDir) throws IOException {
		File pathFile = new File(descDir);
		if (!pathFile.exists()) {
			pathFile.mkdirs();
		}
		ZipFile zip = new ZipFile(zipFile);
		for (Enumeration entries = zip.entries(); entries.hasMoreElements();) {
			ZipEntry entry = (ZipEntry) entries.nextElement();
			String zipEntryName = entry.getName();
			InputStream in = zip.getInputStream(entry);
			String outPath = (descDir + zipEntryName).replaceAll("\\*", "/");
			;
			// 判断路径是否存在,不存在则创建文件路径
			File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
			if (!file.exists()) {
				file.mkdirs();
			}
			// 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
			if (new File(outPath).isDirectory()) {
				continue;
			}
			// 输出文件路径信息
			OutputStream out = new FileOutputStream(outPath);
			byte[] buf1 = new byte[1024];
			int len;
			while ((len = in.read(buf1)) > 0) {
				out.write(buf1, 0, len);
			}
			in.close();
			out.close();
		}
	}

	public static void main(String[] args) {
		String path = "C:\\Users\\27749\\Documents\\Tencent Files\\277490115\\QTalk";
	}

	/**
	 * 创建目录
	 * 
	 * @param destDirName
	 *            目标目录名
	 * @return 目录创建成功返回true，否则返回false
	 */
	public static boolean createDir(String destDirName) {
		File dir = new File(destDirName);
		if (dir.exists()) {
			return false;
		}
		if (!destDirName.endsWith(File.separator)) {
			destDirName = destDirName + File.separator;
		}
		// 创建单个目录
		if (dir.mkdirs()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param filePathAndName
	 *            String 文件路径及名称 如c:/fqf.txt（删除文件）或路径（目录下所有文件及子目录下所有文件）
	 */
	public static void delFile(String filePathAndName) {
		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			java.io.File myDelFile = new java.io.File(filePath);
			if (myDelFile.isDirectory()) {
				deleteDir(myDelFile);
			} else {
				myDelFile.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 删除文件夹及旗下所有子文件
	 * 
	 * @param dir
	 * @return
	 */
	private static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// 目录此时为空，可以删除
		return dir.delete();
	}

	/**
	 * 读取到字节数组0
	 * 
	 * @param filePath
	 *            //路径
	 * @throws IOException
	 */
	public static byte[] getContent(String filePath) throws IOException {
		File file = new File(filePath);
		long fileSize = file.length();
		if (fileSize > Integer.MAX_VALUE) {
			return null;
		}
		FileInputStream fi = new FileInputStream(file);
		byte[] buffer = new byte[(int) fileSize];
		int offset = 0;
		int numRead = 0;
		while (offset < buffer.length && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
			offset += numRead;
		}
		// 确保所有数据均被读取
		if (offset != buffer.length) {
			throw new IOException("Could not completely read file " + file.getName());
		}
		fi.close();
		return buffer;
	}

	/**
	 * 读取到字节数组1
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static byte[] toByteArray(String filePath) throws IOException {

		File f = new File(filePath);
		if (!f.exists()) {
			throw new FileNotFoundException(filePath);
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(f));
			int buf_size = 1024;
			byte[] buffer = new byte[buf_size];
			int len = 0;
			while (-1 != (len = in.read(buffer, 0, buf_size))) {
				bos.write(buffer, 0, len);
			}
			return bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			bos.close();
		}
	}

	/**
	 * 读取到字节数组2
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static byte[] toByteArray2(String filePath) throws IOException {

		File f = new File(filePath);
		if (!f.exists()) {
			throw new FileNotFoundException(filePath);
		}

		FileChannel channel = null;
		FileInputStream fs = null;
		try {
			fs = new FileInputStream(f);
			channel = fs.getChannel();
			ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());
			while ((channel.read(byteBuffer)) > 0) {
			}
			return byteBuffer.array();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				channel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Mapped File way MappedByteBuffer 可以在处理大文件时，提升性能
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static byte[] toByteArray3(String filePath) throws IOException {

		FileChannel fc = null;
		RandomAccessFile rf = null;
		try {
			rf = new RandomAccessFile(filePath, "r");
			fc = rf.getChannel();
			MappedByteBuffer byteBuffer = fc.map(MapMode.READ_ONLY, 0, fc.size()).load();
			byte[] result = new byte[(int) fc.size()];
			if (byteBuffer.remaining() > 0) {
				byteBuffer.get(result, 0, byteBuffer.remaining());
			}
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rf.close();
				fc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String toByteArray4(String filePath) throws IOException {
		Scanner in = new Scanner(new File(filePath), "UTF-8");
		StringBuffer sb = new StringBuffer();
		// in.hasNextLine()//用于判断流中是否还有
		// 可读取数据，天生是给while语句设计的
		while (in.hasNextLine()) {
			sb.append(in.nextLine());
		}
		in.close();
		return sb.toString();
	}

	/**
	 * 获取指定目录下的所有文件名
	 * 
	 * @param path
	 * @param imgs
	 */
	public static List<String> getFile(String path, List<String> imgs) {
		File file = new File(path);
		File[] array = file.listFiles();
		if (array != null) {
			for (int i = 0; i < array.length; i++) {
				if (array[i].isFile()) {
					imgs.add(path + "/" + array[i].getName());
				} else if (array[i].isDirectory()) {
					getFile(array[i].getPath(), imgs);
				}
			}
		}
		return imgs;
	}

	public static boolean createFile(String filename) throws IOException {
		File file = new File(filename);
		boolean b = false;
		if (!file.exists()) {
			File parent = file.getParentFile();
			if (!parent.exists()) {
				parent.mkdirs();
			}
			b = file.createNewFile();
		}
		return b;
	}

	public static boolean deleteFile(String filename) {
		return new File(filename).delete();
	}

	public static void deleteDirectory(String filepath) throws IOException {
		File f = new File(filepath);
		if (f.exists() && f.isDirectory()) {
			if (f.listFiles().length == 0) {
				f.delete();
			} else {
				File[] delFile = f.listFiles();
				for (int index = 0; index < f.listFiles().length; index++) {
					if (delFile[index].isDirectory()) {
						deleteDirectory(delFile[index].getAbsolutePath());
					}
					delFile[index].delete();
				}
			}
			deleteDirectory(filepath);
		}
	}

	public static boolean writeFile(String file, String txt, boolean appent) throws IOException {
		File f = new File(file);
		if (!f.exists()) {
			createFile(file);
		}
		FileWriter writer = new FileWriter(f, appent);
		writer.write(txt);
		writer.close();
		return true;
	}

	public static boolean writeFile(String file, String txt, String encoding) throws IOException {
		File f = new File(file);
		if (!f.exists()) {
			createFile(file);
		}
		FileOutputStream fos = new FileOutputStream(file, true);
		OutputStreamWriter os = new OutputStreamWriter(fos, encoding == null ? "UTF-8" : encoding);
		os.write(txt);
		os.close();
		fos.close();
		return true;
	}

	@Deprecated
	public static void writeObjectToFile(Object obj, String filename) throws IOException {
		FileUtils.deleteFile(filename);
		final FileOutputStream fos = new FileOutputStream(filename);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(obj);
		fos.close();
		oos.close();
	}

	@Deprecated
	public static Object readObjectFromFile(String filename) throws IOException, ClassNotFoundException {
		final FileInputStream fis = new FileInputStream(filename);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Object obj = ois.readObject();
		fis.close();
		ois.close();
		return obj;
	}

	/**
	 * 获取文件名
	 * 
	 * @param path
	 * @return
	 */
	public static String getFileName(String path) {
		int len = path.lastIndexOf(File.separator);
		return path.substring(len + 1);
	}

	/**
	 * 获取文件名　前缀
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFilePrefix(String fileName) {
		int len = fileName.lastIndexOf(".");
		return fileName.substring(0, len);
	}

	/**
	 * 获取文件名　后缀(文件类型）
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileSuffix(String fileName) {
		int len = fileName.lastIndexOf(".");
		return fileName.substring(len + 1);
	}

	/**
	 * 文件复制 方法摘要：这里一句话描述方法的用途
	 * 
	 * @param
	 * @return void
	 */
	public static void copyFile(String inputFile, String outputFile) throws FileNotFoundException {
		File sFile = new File(inputFile);
		File tFile = new File(outputFile);
		FileInputStream fis = new FileInputStream(sFile);
		FileOutputStream fos = new FileOutputStream(tFile);
		int temp = 0;
		byte[] buf = new byte[10240];
		try {
			while ((temp = fis.read(buf)) != -1) {
				fos.write(buf, 0, temp);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
