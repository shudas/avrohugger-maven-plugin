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
                    System.out.println(loggerInstance.getClass().getClassLoader());
                    System.out.println("========");
                    System.out.println(method.getParameterTypes()[0].getCanonicalName());
                    System.out.println(args[0].getClass().getCanonicalName());
                    System.out.println(method.getParameterTypes()[0] == args[0].getClass());
                    System.out.println("========");

                    System.out.println(method.getName() + " " + Arrays.toString(method.getParameterTypes()));
                    System.out.println(loggerInstance.getClass().getCanonicalName());
                    System.out.println(Arrays.toString(loggerInstance.getClass().getDeclaredMethods()));

                    System.out.println("!!!!!!!!!!!!!!!");
                    System.out.println(loggerInstance.getClass().getDeclaredMethods()[0].getName());
                    for (Class<?> paramType : loggerInstance.getClass().getDeclaredMethods()[0].getParameterTypes()) {
                        System.out.println(paramType.getCanonicalName());
                    }

                    System.out.println("000000000000000000");
                    System.out.println(loggerInstance.getClass().getDeclaredMethods()[0].getParameterTypes()[2].getCanonicalName());
                    System.out.println(loggerInstance.getClass().getDeclaredMethods()[0].getParameterTypes()[2].getClassLoader());
                    System.out.println(args[2].getClass().getCanonicalName());
                    System.out.println(args[2].getClass().getClassLoader());

                    /*
                    org.apache.maven.plugin.logging.Log
                    at.makubi.maven.plugin.avrohugger.internal.AvrohuggerGeneratorClassLoader@3c854752
                    org.apache.maven.monitor.logging.DefaultLog
                    ClassRealm[plexus.core, parent: null]

                    loggerInstance classes loaded by our class loader
                    args classes loaded by maven classloader
                    */

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



                    return loggerInstance.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes()).invoke(loggerInstance, args);
                }
            };

            return (AvrohuggerGenerator) Proxy.newProxyInstance(AvrohuggerGeneratorFactory.class.getClassLoader(), new Class[]{ AvrohuggerGenerator.class }, handler);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
