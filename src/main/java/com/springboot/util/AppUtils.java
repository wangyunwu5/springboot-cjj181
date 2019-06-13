package com.springboot.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import sun.misc.BASE64Decoder;

public class AppUtils {

	/** 获取参数列表 */
	public static Map<String, Object> getMap(Object... values) {
		Map<String, Object> map = new HashMap<>();
		for (int i = 0; i < values.length; i += 2)
			map.put((String) values[i], values[i + 1]);
		return map;
	}

	/** 以指定格式获取当前时间格式字符串 */
	public static String getDate(String format) {
		if (format == null)
			format = "yyyy-MM-dd";
		Date date = new Date();
		SimpleDateFormat simple = new SimpleDateFormat(format);
		return simple.format(date);
	}

	/** 创建图片序列码 */
	public static String UUIDCode() {
		UUID uuid = UUID.randomUUID();
		String code = uuid.toString();
		return code.toUpperCase();
	}

	/** 获取session */
	public static HttpSession getHttpSession() {
		RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
		HttpSession session = request.getSession(true);
		return session;
	}

	/** 添加数据至session */
	public static void pushMap(String key, Object value) {
		HttpSession session = getHttpSession();
		session.setMaxInactiveInterval(604800);
		session.setAttribute(key, value);
	}

	/** 从session获取数据 */
	public static Object findMap(String key) {
		HttpSession session = getHttpSession();
		return session.getAttribute(key);
	}

	/** 从session移除数据 */
	public static void removeSession(String key) {
		HttpSession session = getHttpSession();
		session.removeAttribute(key);
	}

	public static void saveImage(String data, String path, String name) {
		try {
			BASE64Decoder decoder = new BASE64Decoder();
			byte[] bytes = decoder.decodeBuffer(data);
			for (int i = 0; i < bytes.length; i++)
				bytes[i] = bytes[i] < 0 ? bytes[i] += 256 : bytes[i];
			OutputStream output = new FileOutputStream(path + name + ".png");
			System.out.println(path + name + ".png");
			output.write(bytes);
			output.flush();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * <p>
	 * Title: getNumber
	 * </p>
	 * <p>
	 * Description:随机生成6个字符串
	 * </p>
	 * 
	 * @return
	 */
	public static String getNumber() {
		String str = "0123456789";
		String code = "";
		for (int i = 0; i < 6; i++) {
			int index = (int) (Math.random() * str.length());
			code += str.charAt(index);
		}
		return code;
	}

	/**
	 * 
	 * @Title: longTimeToDay
	 * @Description: 计算两个时间差
	 * @param startTime  开始时间
	 * @param endTime    结束时间
	 * @param resultType 返回day，hour，min
	 * @return
	 */
	public static long longTimeToDay(Date startTime, Date endTime, String resultType) {
		long nd = 1000 * 24 * 60 * 60;
		long nh = 1000 * 60 * 60;
		long nm = 1000 * 60;

		// 获得两个时间的毫秒时间差异
		long diff = endTime.getTime() - startTime.getTime();

		switch (resultType) {
		case "day":
			long day = diff / nd;
			return day;
		case "hour":
			long hour = diff % nd / nh;
			return hour;
		case "min":
			long min = diff % nd % nh / nm;
			return min;
		default:
			return diff;
		}
	}

	/**
	 * 是否含有英文
	 * 
	 */
	public static boolean isExisLetter(String str) {
		if (str == null)
			return false;
		String regex = ".*[a-zA-z].*";
		return str.matches(regex);
	}

	// 判定是否为正整数
	public static boolean isNumeric(String string) {
		if (string == null)
			return false;
		Pattern pattern = Pattern.compile("^-?\\d+(\\.\\d+)?$");
		return pattern.matcher(string).matches();
	}

	/**
	 * 是否含有数字
	 * 
	 */
	public static boolean isExisNumber(String str) {
		if (str == null)
			return false;
		String regex = ".*[0-9].*";
		return str.matches(regex);
	}

	/**
	 * 只含有英文或者字符
	 */
	public boolean isNumberAndLetter(String str) {
		if (str == null)
			return false;
		return str.matches("[a-zA-Z0-9]+");
	}

}
