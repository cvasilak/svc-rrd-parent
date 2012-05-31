package gr.forthnet.nms.svc.rrd.core.util;


import java.util.Set;

public class StringUtils {
	
	public static String tokenizeList(Set<String> list) {
		if (list == null)
			return null;

		StringBuilder builder = new StringBuilder();

		int size = list.size();
		int i = 1;
		for (String s: list) {
			builder.append(s);
			i++;
			if (i <= size) {
				builder.append(',');
			}
		}

		return builder.toString();
	}
}
