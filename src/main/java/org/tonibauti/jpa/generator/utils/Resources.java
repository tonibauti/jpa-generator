package org.tonibauti.jpa.generator.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;


public class Resources
{
    public static final String JAVA_VERSION = System.getProperty("java.version");


    private Resources() {}


    /*
    public static void addToClassPath(String fileName) throws Exception
    {
        File f = new File(fileName);
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        Class<URLClassLoader> urlClass = URLClassLoader.class;
        Method method = urlClass.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(classLoader, f.toURL());
    }
    */

    public static void addToClassPath(String fileName) throws Exception
    {
        // run with vm options: --add-opens=java.base/jdk.internal.loader=ALL-UNNAMED
        // manifest --> Add-Opens: java.base/jdk.internal.loader

        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        Method method = classLoader.getClass().getDeclaredMethod("appendToClassPathForInstrumentation", String.class);
        method.setAccessible(true);
        method.invoke(classLoader, fileName);
    }


    private static ClassLoader getClassLoader() 
    {
        return Thread.currentThread().getContextClassLoader();
    }


    public static InputStream getResourceAsStream(String resource) throws IOException
    {
        return getResourceAsStream(getClassLoader(), resource);
    }


    public static InputStream getResourceAsStream(ClassLoader loader, String resource) throws IOException 
    {
        InputStream inputStream = null;
        
        if (loader != null)
            inputStream = loader.getResourceAsStream(resource);
        
        if (inputStream == null)
            inputStream = ClassLoader.getSystemResourceAsStream(resource);
        
        if (inputStream == null)
            throw new IOException("Resource not found: '" + resource + "'");
        
        return inputStream;
    }


    public static Properties getResourceAsProperties(String resource) throws IOException 
    {
        try (InputStream inputStream = getResourceAsStream(resource))
        {
            Properties props = new Properties();
            props.load( inputStream );
            return props;    
        }
    }
    
}

