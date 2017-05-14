package solution;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class IterateAndFindInHash {
	public static List<String> findMatchingItems(List<String> v1, List<String> v2) {
		HashSet<String> v3 = new HashSet<>(v2);
		List<String> rv = new ArrayList<>();
		for (String s : v1) {
			if (v3.remove(s)) {
				rv.add(s);
			}
		}
		return rv;
	}
}
