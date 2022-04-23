package org.tonibauti.jpa.generator.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.function.Function;


public class Files
{
    public static final Charset CHARSET  = StandardCharsets.UTF_8;
    private static final int BUFFER_SIZE = 8*1024;


    private Files() {}


    public static File getFile(String fileName) throws FileNotFoundException
    {
        File file = null;

        if (fileName != null && !fileName.trim().isEmpty())
        {
            file = new File( fileName );

            if (!file.exists() || !file.isFile())
                throw new FileNotFoundException( fileName );
        }

        return file;
    }


    public static StringBuilder readLines(InputStream inputStream, Function<String,String> processLine) throws IOException
    {
        StringBuilder result = new StringBuilder();

        if (inputStream == null)
            return result;

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, CHARSET)))
        {
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                if (processLine != null)
                    line = processLine.apply( line );

                result.append( line ).append( System.lineSeparator() );
            }

            return result;
        }
    }


    public static StringBuilder readStream(InputStream inputStream) throws IOException
    {
        StringBuilder result = new StringBuilder();

        if (inputStream == null)
            return result;

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream))
        {
            byte[] buffer = new byte[ BUFFER_SIZE ];

            int len;
            while ((len = bufferedInputStream.read(buffer)) > 0)
            {
                result.append( new String(buffer, 0, len, CHARSET) );
            }

            return result;
        }
    }


    public static void writeStream(InputStream inputStream, OutputStream outputStream) throws IOException
    {
        if (inputStream == null || outputStream == null)
            return;

        try( BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream) )
        {
            byte[] buffer = new byte[ BUFFER_SIZE ];

            int len;
            while ((len = bufferedInputStream.read(buffer)) > 0)
            {
                bufferedOutputStream.write(buffer, 0, len);
            }

            bufferedOutputStream.flush();
        }
    }


    public static StringBuilder readFile(String fileName) throws IOException
    {
        return readStream( new FileInputStream(fileName) );
    }


    public static StringBuilder readFile(File file) throws IOException
    {
        return readStream( new FileInputStream(file) );
    }


    public static Properties readProperties(InputStream inputStream) throws IOException
    {
        try (InputStreamReader reader = new InputStreamReader(inputStream, CHARSET))
        {
            Properties props = new Properties();
            props.load( reader );
            return props;
        }
    }


    public static void writeFile(String content, File file) throws IOException
    {
        content = ((content != null) ? content : "");
        InputStream inputStream = new ByteArrayInputStream( content.getBytes(CHARSET) );
        OutputStream outputStream = new FileOutputStream(file);
        writeStream(inputStream, outputStream);
    }


    public static File writeFile(String content, String fileName) throws IOException
    {
        File file = new File(fileName);
        writeFile(content, new File(fileName));
        return file;
    }

}

