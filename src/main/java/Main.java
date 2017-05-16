
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class Main {
	/**
	 * Create the array
	 */
	public static final BiConsumer<Integer, BiConsumer<String[], String[]>> GET_ARRAY_ROWS = new BiConsumer<Integer, BiConsumer<String[], String[]>>() {

		private final Supplier<String> randomStringSupplier = new Supplier<String>() {
			private final Random RANDOM = new Random();
			private final char[] CHARACTERS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'A',
					'b', 'B', 'c', 'C', 'd', 'D', 'e', 'E', 'f', 'F', 'g', 'G', 'h', 'H', 'i', 'I', 'j', 'J', 'k', 'K',
					'l', 'L', 'm', 'M', 'n', 'N', 'o', 'O', 'p', 'P', 'q', 'Q', 'r', 'R', 's', 'S', 't', 'T', 'u', 'U',
					'v', 'V', 'x', 'X', 'y', 'Y', 'z', 'Z', '@', '?', '+', '-', '?', ',', '.', ':', ';', '"', '\'',
					'\\', '/' };

			@Override
			public String get() {
				char[] rv = new char[100];
				for (int i = 0; i < rv.length; i++) {
					rv[i] = CHARACTERS[RANDOM.nextInt(CHARACTERS.length)];
				}
				return new String(rv);
			}

		};

		@Override
		public void accept(Integer maxSize, BiConsumer<String[], String[]> u) {
			// I want some of the items to match
			int matchSize = maxSize / 1000;
			if (matchSize < 100) {
				matchSize = 100;
			}
			Set<String> rv1 = new HashSet<>();
			Set<String> rv2 = new HashSet<>();
			while (rv1.size() < maxSize) {
				String _val = randomStringSupplier.get();
				if (!rv1.contains(_val)) {
					rv1.add(_val);
					if (rv2.size() < matchSize) {
						rv2.add(_val);
					}
				}
			}
			while (rv2.size() < maxSize) {
				String _val = randomStringSupplier.get();
				if (!rv1.contains(_val) && !rv2.contains(_val)) {
					rv2.add(_val);
				}
			}
			u.accept(rv1.toArray(new String[rv1.size()]), rv2.toArray(new String[rv2.size()]));
		}
	};

	public static void main(String... args) throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		List<String> names = new ArrayList<>();
		List<BiFunction<List<String>, List<String>, List<String>>> methods = new ArrayList<>();
		Arrays.asList(args).stream().forEach(argument -> {
			try {
				Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(argument);
				try {
					Field field = clazz.getField("DESCRIPTION");
					names.add(field.get(null).toString());
				} catch (NoSuchFieldException | IllegalAccessException | NullPointerException e) {
					names.add(clazz.getName());
				}
				Method m = clazz.getMethod("findMatchingItems", List.class, List.class);
				methods.add(new BiFunction<List<String>, List<String>, List<String>>() {
					@SuppressWarnings("unchecked")
					@Override
					public List<String> apply(List<String> t, List<String> u) {
						try {
							return (List<String>) m.invoke(null, t, u);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							throw new RuntimeException(e);
						}
					}
				});
			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
				throw new RuntimeException(e);
			}
		});
		if (methods.size() == 0) {
			throw new IllegalArgumentException("Arguments are required. With full path to class to run. For instance solution.FullCpuThreadsMethod");
		}
		test(names, methods);
	}

	public static final DecimalFormat NUIMBER_FORMAT = new DecimalFormat("00.00");

	public static void test(List<String> labels, List<BiFunction<List<String>, List<String>, List<String>>> calls) {
		int iterations = 20;

		double[] mathAverages = new double[calls.size()];
		Double[][] values = new Double[calls.size()][];

		IntStream.range(0, calls.size()).forEach(i -> {
			mathAverages[i] = 0d;
			values[i] = new Double[iterations - 1];
		});

		IntStream.range(0, iterations).forEach(iteration -> {
			System.gc();
			System.out.print(iteration + ")");
			GET_ARRAY_ROWS.accept(1000000, (a1, a2) -> {
				doSearch(a1, a2, (index, value) -> {
					System.out.print('\t');
					System.out.print(NUIMBER_FORMAT.format(value));
					if (iteration > 0) {
						mathAverages[index] += value;
						values[index][iteration - 1] = value;
					}
				}, calls);
				System.out.println();
				if (iteration > 0) {
					for (double d : mathAverages) {
						System.out.print('\t');
						System.out.print(NUIMBER_FORMAT.format(d / (iteration)));
					}
					System.out.println();
				}
			});
		});
		double[] maxes = new double[calls.size()];
		double[] mins = new double[calls.size()];
		double[] averages = new double[calls.size()];
		for (int i = 0; i < values.length; i++) {
			Arrays.sort(values[i]);
			maxes[i] = values[i][values[i].length - 1];
			mins[i] = values[i][0];
			List<Double> dls = new ArrayList<>(Arrays.asList(values[i]));
			while (dls.size() > 2) {
				dls.remove(0);
				if (dls.size() > 1) {
					dls.remove(dls.size() - 1);
				}
			}
			averages[i] = dls.remove(0);
			if (dls.size() > 0) {
				averages[i] = (averages[i] + dls.remove(0)) / 2;
			}
		}
		System.out.println();
		for (int i = 0; i < mathAverages.length; i++) {
			System.out.println(labels.get(i));
			System.out.println(
					"\t" + "\tmax: " + NUIMBER_FORMAT.format(maxes[i]) + "\tmin:" + NUIMBER_FORMAT.format(mins[i])
							+ "\tavg: " + NUIMBER_FORMAT.format(mathAverages[i] / iterations) + "\tmedian:"
							+ NUIMBER_FORMAT.format(averages[i]));
		}
	}

	private static void doSearch(String[] a1, String[] a2, BiConsumer<Integer, Double> onDone,
			List<BiFunction<List<String>, List<String>, List<String>>> calls) {
		System.gc();
		int[] resultSizes = new int[calls.size()];
		IntStream.range(0, calls.size()).forEach(i -> {
			doMeasureVariations(a1, a2, calls.get(i), (t, resultSize) -> {
				resultSizes[i] = resultSize;
				onDone.accept(i, t);
			});
		});
		for (int i = 1; i < resultSizes.length; i++) {
			if (resultSizes[i - 1] != resultSizes[i]) {
				throw new RuntimeException("The results are not equal! " + (i + 1) + ": " + resultSizes[i - 1] + "!="
						+ i + ": " + resultSizes[i]);
			}
		}
	}

	private static void doMeasureVariations(String[] a1, String[] a2,
			BiFunction<List<String>, List<String>, List<String>> func, BiConsumer<Double, Integer> onDone) {
		System.gc();
		AtomicLong rvL = new AtomicLong(0L);
		AtomicInteger rvI = new AtomicInteger(0);
		Integer[] results = new Integer[2];
		BiConsumer<Long, Integer> accept = new BiConsumer<Long, Integer>() {
			@Override
			public void accept(Long t, Integer u) {
				if (results[0] == null) {
					results[0] = u;
				} else {
					results[1] = u;
					if (results[1].intValue() != results[0].intValue()) {
						throw new RuntimeException("The results are not equal! " + results[1] + "!=" + results[0]);
					}
				}
				rvL.addAndGet(t);
				rvI.addAndGet(u);
			}
		};
		doRunTest(a1, a2, func, accept);
		System.gc();
		doRunTest(a2, a1, func, accept);
		onDone.accept(rvL.doubleValue() / 2d, results[0]);
	}

	private static void doRunTest(String[] a1, String[] a2, BiFunction<List<String>, List<String>, List<String>> func,
			BiConsumer<Long, Integer> onDone) {
		System.gc();
		// String[][] arrays = CreateStringArray.getArrays();
		doMeasureLists(Arrays.asList(a1), Arrays.asList(a2), func, onDone);
	}

	private static void doMeasureLists(List<String> a1, List<String> a2,
			BiFunction<List<String>, List<String>, List<String>> func, BiConsumer<Long, Integer> onDone) {
		long current = System.currentTimeMillis();
		List<String> result = func.apply(a1, a2);
		long done = System.currentTimeMillis() - current;
		onDone.accept(done, result.size());
	}

}
