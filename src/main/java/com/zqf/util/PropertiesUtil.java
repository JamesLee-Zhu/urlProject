package com.zqf.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class PropertiesUtil {

	public static FileInputStream file;
	public static Properties pros = null;
	public static String url = "";

	private static final String CONFIG_FILE = "config.properties";

	private static Logger logger = Logger.getLogger(PropertiesUtil.class);

	public static String getConfig(String key) {
		Properties props = new Properties();
		try {
			props.load(PropertiesUtil.class.getClassLoader().getResourceAsStream(CONFIG_FILE));
			return props.getProperty(key);
		} catch (IOException e) {
			logger.info(Arrays.toString(e.getStackTrace()));
		}
		return null;
	}

	public static Map<String, String> getConfigs(String... keys) {
		Properties props = new Properties();
		try {
			props.load(PropertiesUtil.class.getClassLoader().getResourceAsStream(CONFIG_FILE));
			Map<String, String> map = new HashMap<String, String>();
			for (int i = 0; i < keys.length; i++) {
				map.put(keys[i], props.getProperty(keys[i]));
			}
			return map;
		} catch (IOException e) {
			logger.info(Arrays.toString(e.getStackTrace()));
		}
		return null;
	}

	public static Map<String, String> getPropertiesValues(HttpServletRequest request, String url) {
		try {
			if (file == null || PropertiesUtil.url != url) {
				PropertiesUtil.url = url;
				String filePath = request.getSession().getServletContext().getRealPath(url);
				file = new FileInputStream(filePath);
				pros = new Properties();
				pros.load(new BufferedInputStream(file));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		@SuppressWarnings("unchecked")
		Map<String, String> map = new HashMap<String, String>((Map) pros);
		return map;
	}

}
