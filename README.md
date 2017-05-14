# The problem

The problem was to find the fastest way to find intersaction in two arrays.

## The initial solution

[The first solution](solutions/solution/SimpleStream.java)  was quite obvious:

```java
public static List<String> findMatchingItems(List<String> a1, List<String> a2) {
	return a1.stream().filter(t -> a2.contains(t)).collect(Collectors.toList());
}
```

The problem is that it is not fast enough. Because for each item in a1 programs has to iterate each item in the second array.

## More like this

[The second solution](src\main\solutions\solution\StreamFindInHash.java) was still the stream, but I created HashSet out of the second array. As I was testing further I found streams a bit slower than generic iteration. So, [the third solution](src\main\solutions\solution\IterateAndFindInHash.java) I came up was:

```java
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
```

## Multicore solutions

Absurd as it is, but also I thought It would be cool to try to use parallel processing. I tried different versions, and the one which won the benchmark was [using all the CPU cores](src\main\solutions\solution\FullCpuThreadsMethod.java)

# I created a main test program

This is actually fast enough for most cases. But I wanted to push further. So, I came up with the test case with 1 000 000 strings in each array. Each item is 100 characters long. See the code at [Main class](src/main/java/Main.java)

The program runs 20 times, each time running all the solutions and for each run it creates a new array. In the end it calculates average and median run time, also prints minimum and maximum run. The reason for this is to see the peaks if they happen. Also initial run is ignored because of the compiling issues.

And the solutions I did were not fast enough. I knew there is more I can do.


## How to run this program.

Build it. And run:

```bash
java -jar arrayproblem.jar solution.StreamFindInHash solution.IterateAndFindInHash solution.FullCpuThreadsMethod
```

Or your own class names. In your class there must have static function with thwo arrays as argument and must return a list of matching arrays.

```
public static List<String> findMatchingItems(List<String> v1, List<String> v2)
```

## The test notes:

- Test runs 20 times;
- Initial run time is printed but ignored;
- Test class must have static method `public static List<String> findMatchingItems(List<String> v1, List<String> v2)`;
- Test class may have a public static variable `public static final String DESCRIPTION = "This is my test";` otherwise class name is used at statistics;
- Test only calculates the time of the function call, so remember, your function must block current thread until it is done;
- After each run, except the first time, the second row shows current average running time;
- At the end of 20 iterations maximum, minimum, average and median time of the running time of all methods is listed.
