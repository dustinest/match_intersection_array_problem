package solution;

import java.util.List;
import java.util.stream.Collectors;

public class SimpleStream {
	public static List<String> findMatchingItems(List<String> a1, List<String> a2) {
		return a1.stream().filter(t -> a2.contains(t)).collect(Collectors.toList());
	}
}
