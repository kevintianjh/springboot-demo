package kevintian.springbootdemo;

public class StaticVarDemo {
    private static ThreadLocal<ClassA> classA = new ThreadLocal<>();

    public static ClassA getInstance() {
        if(StaticVarDemo.classA.get() == null) {
            StaticVarDemo.classA.set(new ClassA());
        }

        return StaticVarDemo.classA.get();
    }
}
