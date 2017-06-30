package at.makubi.maven.plugin.avrohugger.api;

import at.makubi.maven.plugin.avrohugger.internal.AvrohuggerGeneratorClassLoader;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class AvrohuggerGeneratorFactory {

    private static final AvrohuggerGeneratorClassLoader AVROHUGGER_GENERATOR_CLASS_LOADER = new AvrohuggerGeneratorClassLoader(AvrohuggerGeneratorFactory.class.getClassLoader());

    public static AvrohuggerGenerator getAvrohuggerGenerator() {

        try {
            final Class<?> behindProxyClass = AVROHUGGER_GENERATOR_CLASS_LOADER.loadClass("at.makubi.maven.plugin.avrohugger.AvrohuggerGeneratorImpl");
            final Object loggerInstance = behindProxyClass.newInstance();

            final InvocationHandler handler = new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    System.out.println(method.getName() + " " + Arrays.toString(method.getParameterTypes()));
                    System.out.println(loggerInstance.getClass().getCanonicalName());
                    System.out.println(Arrays.toString(loggerInstance.getClass().getDeclaredMethods()));
                    System.out.println(loggerInstance.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes()));
                    System.out.println(loggerInstance.getClass().getClassLoader());
                    for (Class<?> p : method.getParameterTypes()) {
                        System.out.println("proxy method param type: " + p.getClassLoader());
                    }

                    for (Method m : loggerInstance.getClass().getDeclaredMethods()) {
                        System.out.println("impl method name: " + m.getName());
                        for (Class<?> p : m.getParameterTypes()) {
                            System.out.println("impl method param type: " + p.getClassLoader());
                        }
                    }
                    System.out.println(loggerInstance.getClass().getClassLoader());
                    return loggerInstance.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes()).invoke(loggerInstance, args);
                }
            };

            return (AvrohuggerGenerator) Proxy.newProxyInstance(AvrohuggerGeneratorFactory.class.getClassLoader(), new Class[]{ AvrohuggerGenerator.class }, handler);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
