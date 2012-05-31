package gr.forthnet.nms.svcrrd.service.tests;

import org.codehaus.jackson.map.ObjectMapper;

public class JsonUtil {

	private volatile static JsonUtil instance;

	private volatile static ObjectMapper mapper;

	private JsonUtil() {
		mapper = new ObjectMapper();
	}

	public static JsonUtil getInstance() {
		if (instance == null) {
			synchronized (JsonUtil.class) {
				if (instance == null) {  // double-checked-locking
					instance = new JsonUtil();
				}
			}
		}

		return instance;
	}

	public <T> T fromJSON(String json, Class<T> valueType) {
		T obj;

		try {
			obj = mapper.readValue(json, valueType);
		} catch (Exception e) {
			return null;
		}

		return obj;
	}

	public String toJSON(Object obj) {
		String json;
		try {
			json = mapper.defaultPrettyPrintingWriter().writeValueAsString(obj);
		} catch (Exception e ) {
			return null;
		}
		return json;
	}
}
