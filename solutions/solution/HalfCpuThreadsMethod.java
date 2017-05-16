package solution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HalfCpuThreadsMethod {
	public static final String DESCRIPTION = "All CPUs and HashSet 50";
	public static final int THREADS = Runtime.getRuntime().availableProcessors() / 2;
	public static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(THREADS);
	public static final int PROCESS_AMOUNT = 1000;

	public static List<String> findMatchingItems(List<String> a1, List<String> a2) {
		Collection<String> hash = new HashSet<>(a2);

		int threads = a1.size() / PROCESS_AMOUNT < THREADS ? a1.size() / THREADS : PROCESS_AMOUNT;

		List<Future<Boolean>> tasks = new ArrayList<>();

		List<String> rv = new ArrayList<>();
		for (int i = 0; i < a1.size(); i+=threads) {
			final int _start = i;
			final int _end = _start + threads > a1.size() ? a1.size() : _start + threads;
			tasks.add(EXECUTOR_SERVICE.submit(() -> {
				try {
					List<String> _rv = new ArrayList<>();
					for (int index = _start; index < _end; index++) {
						String _val = a1.get(index);
						if (hash.contains(_val)) {
							_rv.add(_val);
						}
					}
					synchronized (rv) {
						rv.addAll(_rv);
					}
				} catch (Throwable t) {
					t.printStackTrace();
					return false;
				}
				return true;
			}));
		}
		try {
			for (Future<Boolean> f : tasks) {
				f.get();
			}
			return rv;
		} catch (ExecutionException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static void shutDown() {
		EXECUTOR_SERVICE.shutdownNow();
	}
}
