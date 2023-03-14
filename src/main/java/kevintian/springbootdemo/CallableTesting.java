package kevintian.springbootdemo;

import java.util.concurrent.*;

public class CallableTesting {

    public static Callable<String> test1() {
        return () -> {
            System.out.println("Executing some task...");
            return "hello world!";
        };
    }

    public static void main(String[] args) throws Exception {
        Callable<String> callable = test1();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> ret = executorService.submit(callable);

        String val = ret.get();
        System.out.println(val);
    }
}
