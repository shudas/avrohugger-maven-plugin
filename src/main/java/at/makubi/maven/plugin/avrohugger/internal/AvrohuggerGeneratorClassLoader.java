package at.makubi.maven.plugin.avrohugger.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class AvrohuggerGeneratorClassLoader extends ClassLoader {

    static {
//        ClassLoader.registerAsParallelCapable();
    }

    private static final String apiPackageName = "at.makubi.maven.plugin.avrohugger";
    private static final String thirdPartyLibsFolderName = "at.makubi.maven.plugin.avrohugger.thirdpartylibs";

    private final List<String> packagedPackageNames = Arrays.asList("scala");

    private final ClassLoader resourceClassLoader;

    public AvrohuggerGeneratorClassLoader(ClassLoader parentClassLoader) {
        super(null);
        this.resourceClassLoader = parentClassLoader;
        assert parentClassLoader != null;
        assert getParent() == null; // Essential, don't use it by default in loadClass, only used for looking up resources
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {

        final String hiddenClassResourceName = getClassResourceName(name);
        System.out.println("[LoggingCLassLoader] name is " + name);

        try {

            if(hiddenClassResourceName.startsWith(thirdPartyLibsFolderName)) {
                System.out.println("[LoggingClassLoader] trying to load " + hiddenClassResourceName + " as resource");
                return getClass(name, hiddenClassResourceName);
            } else {
                return resourceClassLoader.loadClass(name);
            }
        } catch(IOException e){
            throw new ClassNotFoundException("Loading class " + name + "(" + hiddenClassResourceName + ") was not successful", e);
        }
    }

    private String getClassResourceName(String className) {
        String prefixFolder = className.startsWith("scala" + ".")
                ? thirdPartyLibsFolderName + "/"
                : "";

        return prefixFolder + className.replace(".", "/") + ".class";
    }

    private Class<?> getClass(String className, String resource) throws IOException, ClassNotFoundException {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        final InputStream is = resourceClassLoader.getResourceAsStream(resource);
        if (is != null) {
            try {
                int nRead;
                byte[] data = new byte[1024];

                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                System.out.println("[LoggingClassLoader] Loaded bytes of " + className + " successfully - defining class.");
                return this.defineClass(className, buffer.toByteArray(), 0, buffer.size());
            } finally {
                is.close();
            }
        } else {
            throw new ClassNotFoundException("Loading class " + className + "(" + resource + ") was unsuccessful");
        }
    }

}

