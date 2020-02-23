package cn.nomeatcoder.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class GsonUtils {

	private final static Gson gson;

	static {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gson = gsonBuilder.create();
	}


	public static <T> String fromObj2Gson(T obj, Class<T> clazz) {
		if (null == obj) {
			return null;
		}
		return gson.toJson(obj, clazz);
	}

	public static <T> T fromGson2Obj(String json, Class<T> clazz) {
		if (StringUtils.isBlank(json)) {
			return null;
		}

		if (clazz.getSimpleName().equals(String.class.getSimpleName())) {
			return (T) String.valueOf(json);
		}
		return gson.fromJson(json, clazz);
	}

	public static <T> String fromObj2Gson(T obj, Type t) {
		if (null == obj) {
			return null;
		}
		return gson.toJson(obj, t);
	}

	public static <T> List<T> fromJsonToList(String json, Class<T[]> type) {
		try {
			T[] list = gson.fromJson(json, type);
			if (list == null) {
				return null;
			}
			return Arrays.asList(list);
		} catch (Exception e) {
			log.error("Jsons.fromJsonToList ex, json=" + json + ", type=" + type, e);
		}
		return null;
	}


	public static <T> T fromGson2Obj(String json, Type type) {
		if (StringUtils.isBlank(json)) {
			return null;
		}
		return gson.<T>fromJson(json, type);
	}


	public static <T> T fromGson2Obj(String json, TypeToken<T> typeToken) {
		if (StringUtils.isBlank(json)) {
			return null;
		}
		if (typeToken == null) {
			throw new NullArgumentException("typeToken");
		}
		return gson.fromJson(json, typeToken.getType());
	}

	public static Map<String, Object> getJsonMap(String jsonStr) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(jsonStr)) {
			Type typeOfT = new TypeToken<Map<String, Object>>() {
			}.getType();
			paramMap = gson.fromJson(jsonStr, typeOfT);
		}
		return paramMap;
	}


	public static String toJson(Object object) {
		if (object instanceof String) {
			return (String) object;
		}
		return gson.toJson(object);
	}

}

