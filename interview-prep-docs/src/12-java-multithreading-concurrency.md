# 12. Java Multithreading & Concurrency

## Core Concepts
- **Thread**: Smallest unit of execution. Created by extending `Thread` or implementing `Runnable`/`Callable`.
- **Process vs Thread**: A process is an independent program with its own memory space; a thread is a lightweight sub-process sharing memory with other threads in the same process.
- **Synchronization**: Ensures only one thread accesses a resource at a time. Use `synchronized` keyword or locks.
- **Thread Safety**: Code behaves correctly when accessed by multiple threads.
- **Volatile**: Ensures visibility of changes to variables across threads.
- **Atomic Operations**: Performed as a single unit (e.g., `AtomicInteger`).
- **Executors**: Framework for managing thread pools (`ExecutorService`, `ScheduledExecutorService`).
- **Future/CompletableFuture**: Handle async computation results.
- **Thread Lifecycle**: NEW → RUNNABLE → RUNNING → BLOCKED/WAITING → TERMINATED.
- **Daemon Threads**: Background threads that do not prevent JVM shutdown.

## Creating Threads
```java
// Using Runnable
Thread t = new Thread(() -> System.out.println("Hello from thread!"));
t.start();

// Using ExecutorService
ExecutorService executor = Executors.newFixedThreadPool(2);
executor.submit(() -> doWork());
executor.shutdown();
```

## Synchronization & Locks
- Use `synchronized` blocks/methods to prevent race conditions.
- Use `ReentrantLock` for advanced locking features (tryLock, fairness, interruptibility).
- **Deadlock**: Occurs when two or more threads wait forever for locks held by each other.
- **Livelock**: Threads keep changing state in response to each other but cannot proceed.
- **Starvation**: A thread never gets CPU time or resources.

```java
synchronized(this) {
    // critical section
}

// Using ReentrantLock
Lock lock = new ReentrantLock();
lock.lock();
try {
    // critical section
} finally {
    lock.unlock();
}
```

## Thread Communication
- **wait() / notify() / notifyAll()**: Used for inter-thread communication (producer-consumer, etc.).
- Always call `wait()`/`notify()` inside a synchronized block.

```java
synchronized(sharedObject) {
    while (!condition) sharedObject.wait();
    // do work
    sharedObject.notifyAll();
}
```

## Thread Safety & Collections
- Use thread-safe collections: `ConcurrentHashMap`, `CopyOnWriteArrayList`.
- Avoid sharing mutable state between threads.
- Use `Collections.synchronizedList()` for legacy code, but prefer concurrent collections.

## Executors & Thread Pools
- Use `Executors` to manage thread pools.
- Types: `newFixedThreadPool`, `newCachedThreadPool`, `newSingleThreadExecutor`, `newScheduledThreadPool`.
- **ThreadPoolExecutor**: Advanced configuration (core pool size, max pool size, queue, rejection policy).

## Future & CompletableFuture
- `Future` represents the result of an async computation. You can check if the task is done, cancel it, or get the result (blocking if not ready).
- **Limitations of Future:** Cannot be manually completed, no chaining, and only blocking get().
- **CompletableFuture** is an advanced API for asynchronous programming, supporting non-blocking, event-driven, and functional-style callbacks.
- **Key Features of CompletableFuture:**
    - Non-blocking async computation (`supplyAsync`, `runAsync`).
    - Chaining with `thenApply`, `thenAccept`, `thenCompose`, `thenCombine`.
    - Exception handling with `exceptionally`, `handle`, `whenComplete`.
    - Combining multiple futures (allOf, anyOf).
    - Manual completion (`complete`, `completeExceptionally`).
- **Common Methods:**
    - `supplyAsync(Supplier<T>)`: Runs a Supplier asynchronously.
    - `thenApply(Function<T, R>)`: Transforms the result.
    - `thenAccept(Consumer<T>)`: Consumes the result.
    - `thenCompose(Function<T, CompletionStage<R>>)` : Flattens nested futures.
    - `thenCombine(CompletionStage<U>, BiFunction<T, U, R>)`: Combines two futures.
    - `exceptionally(Function<Throwable, T>)`: Handles exceptions.
    - `allOf(CompletableFuture<?>...)`: Waits for all futures to complete.
    - `anyOf(CompletableFuture<?>...)`: Completes when any future completes.

**Example: Basic Usage**
```java
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hello");
future.thenApply(s -> s + " World").thenAccept(System.out::println);
```

**Example: Combining Futures**
```java
CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(() -> 10);
CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(() -> 20);
CompletableFuture<Integer> combined = f1.thenCombine(f2, Integer::sum);
System.out.println(combined.get()); // 30
```

**Example: Exception Handling**
```java
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    if (true) throw new RuntimeException("Error!");
    return "Result";
});
future.exceptionally(ex -> "Recovered: " + ex.getMessage())
      .thenAccept(System.out::println);
```

**Example: Waiting for Multiple Futures**
```java
CompletableFuture<Void> all = CompletableFuture.allOf(f1, f2);
all.thenRun(() -> System.out.println("All done!"));
```

- **Best Practices:**
    - Avoid blocking calls (`get()`) in async code; prefer chaining.
    - Use thread pools wisely to avoid resource exhaustion.
    - Handle exceptions in async pipelines.
    - Use `CompletableFuture` for complex async workflows, parallelism, and non-blocking I/O.

## Fork/Join Framework
- Used for parallelism and divide-and-conquer tasks.
- `ForkJoinPool` and `RecursiveTask`/`RecursiveAction`.

```java
class SumTask extends RecursiveTask<Integer> {
    // ... split task recursively ...
}
ForkJoinPool pool = new ForkJoinPool();
int result = pool.invoke(new SumTask(...));
```

## Concurrency Utilities
- **CountDownLatch**: Waits for other threads to complete.
- **CyclicBarrier**: All threads wait until a barrier is reached.
- **Semaphore**: Controls access to a resource with limited permits.
- **Exchanger**: Two threads exchange data.

```java
CountDownLatch latch = new CountDownLatch(3);
// In each thread: latch.countDown();
latch.await(); // main thread waits
```

## Immutability
- Immutable objects are inherently thread-safe (e.g., `String`, custom classes with final fields).
- Prefer immutability for shared data.

## Daemon vs User Threads
- **User Thread**: Keeps JVM alive until finished.
- **Daemon Thread**: JVM exits when only daemon threads remain (e.g., garbage collector).

## Best Practices
- Minimize shared mutable state.
- Use higher-level concurrency utilities.
- Avoid deadlocks by acquiring locks in a consistent order.
- Prefer immutability and stateless design.
- Use thread pools instead of manual thread management.
- Always release locks in a finally block.

## Common Interview Questions
- What is the difference between `synchronized` and `Lock`?
- How does `volatile` work?
- What is a deadlock and how do you prevent it?
- How do you create a thread pool in Java?
- What is the difference between `wait()` and `sleep()`?
- How do you use `CompletableFuture` for async programming?
- What is the Fork/Join framework?
- Explain CountDownLatch vs CyclicBarrier.
- What is the difference between a user thread and a daemon thread?
- How do you ensure thread safety in Java?

---

