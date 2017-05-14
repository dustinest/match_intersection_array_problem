package solution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DoubleCpuThreadsMethod {
	public static final String DESCRIPTION = "Double CPUs and HashSet";
	public static final int THREADS = Runtime.getRuntime().availableProcessors() * 2;
	public static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(THREADS);

	public static List<String> findMatchingItems(List<String> a1, List<String> a2) {
		Collection<String> hash = new HashSet<>(a2);

		int len = 1000;
		int start = 0;
		List<Callable<List<String>>> tasks = new ArrayList<>();

		while(start < a1.size()) {
			final int _end = start + len > a1.size() ? a1.size() : start + len;
			final int _start = start;
			tasks.add(() -> {
				List<String> rv = new ArrayList<>();
				try {
					for (int index = _start; index < _end; index++) {
						String _val = a1.get(index);
						if (hash.contains(_val)) {
							rv.add(_val);
						}
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
				return rv;
			});

			start = _end;
		}

		try {
			List<Future<List<String>>> futures = EXECUTOR_SERVICE.invokeAll(tasks);
			List<String> rv = new ArrayList<>();
			for (Future<List<String>> futu : futures) {
				rv.addAll(futu.get());
			}
			return rv;
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return null;
		}
	}
}
