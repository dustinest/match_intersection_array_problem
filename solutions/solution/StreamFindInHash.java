package solution;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class StreamFindInHash {
	public static List<String> findMatchingItems(List<String> v1, List<String> v2) {
		HashSet<String> v3 = new HashSet<>(v2);
		return v1.stream().filter(t -> v3.contains(t)).collect(Collectors.toList());
	}
}
