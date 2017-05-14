# The problem

The problem was to find the fastest way to find intersection of two arrays.

## The initial solution

[The first solution](solutions/solution/SimpleStream.java)  was quite obvious:

```java
public static List<String> findMatchingItems(List<String> a1, List<String> a2) {
    return a1.stream().filter(t -> a2.contains(t)).collect(Collectors.toList());
}
```

The problem is that it is not fast enough. Because for each item in a1 programs has to iterate each item in the second array.

## More like this

[The second solution](solutions\solution\StreamFindInHash.java) was still the stream, but I created HashSet out of the second array. As I was testing further I found streams a bit slower than generic iteration. So, [the third solution](solutions\solution\IterateAndFindInHash.java) I came up was:

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

Absurd as it is, but also I thought It would be cool to try to use parallel processing. I tried different versions, and the one which won the benchmark was [using all the CPU cores](solutions\solution\FullCpuThreadsMethod.java). If the arrays provided for each thread were too small or too large the program got slower. So, I used size as 1000 for each core. This means that the method creates 1000 threads with 1000 runnables.

# I created a main test program

To run and compare the use cases I came up with the test case with 1 000 000 strings in each array. Each item is 100 characters long. See the code at [Main class](src/main/java/Main.java)

The program runs 20 times, each time running all the solutions and for each run it creates a new array. In the end it calculates average and median run time, also prints minimum and maximum run. The reason for this is to see the peaks if they happen. Also initial run is ignored because of the compiling issues.

And the solutions I did were not fast enough. I knew there is more I can do.


## How to run this program.

Just run arrayproblem.jar with list of test classes as arguments. If you want to run all my test cases:

```bash
java -jar arrayproblem.jar solution.SimpleStream solution.StreamFindInHash solution.IterateAndFindInHash solution.FullCpuThreadsMethod solution.HalfCpuThreadsMethod solution.DoubleCpuThreadsMethod
```

**NB** The first tests might take minutes to complete! So you might want to skip them. Or at least the simplestream:

```bash
java -jar arrayproblem.jar solution.IterateAndFindInHash solution.FullCpuThreadsMethod solution.HalfCpuThreadsMethod solution.DoubleCpuThreadsMethod
```


Or your own class names. In your class there **must be** static function with **two arrays as arguments** and it **must return** a list of matching arrays.

```
public static List<String> findMatchingItems(List<String> v1, List<String> v2)
```

The [HalfCpuThreadsMethod](solutions\solution\HalfCpuThreadsMethod.java) uses only half of CPUs available and [DoubleCpuThreadsMethod](solutions\solution\DoubleCpuThreadsMethod.java) uses twice of the amount. It is just to showcase the difference between CPUs used.

## The test notes:

- Test runs 20 times;
- Initial run time is printed but ignored;
- Test class **must have** static method `public static List<String> findMatchingItems(List<String> v1, List<String> v2)`;
- Test class **may have** a public static variable `public static final String DESCRIPTION = "This is my test";` otherwise class name is used at statistics;
- Test only calculates the time of the function call, so remember, your function must block current thread until it is done;
- After each run, except the first time, the second row shows current average running time;
- At the end of 20 iterations maximum, minimum, average and median time of the running time of all methods is listed;
- In Threaded tests (tests using CPUs) described below you have to exit the program manually after it is complete (ctrl + C) as I do not shut down the executor prgrammatically.

