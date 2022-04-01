package org.tonibauti.jpa.generator.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;


public class Files
{
    private static final int BUFFER_SIZE = 8*1024;


    private Files() {}


    public static StringBuilder readStream(InputStream inputStream) throws IOException
    {
        StringBuilder result = new StringBuilder();

        if (inputStream == null)
            return result;

        try (BufferedInputStream bufferedInput = new BufferedInputStream(inputStream))
        {
            byte[] buffer = new byte[ BUFFER_SIZE ];

            int len;
            while ((len = bufferedInput.read(buffer)) > 0)
                result.append( new String(buffer, 0, len) );

            return result;
        }
    }


    public static boolean writeStream(InputStream inputStream, OutputStream outputStream) throws IOException
    {
        if (inputStream == null || outputStream == null)
            return false;

        try( BufferedInputStream bufferedInput = new BufferedInputStream(inputStream);
             BufferedOutputStream bufferedOutput = new BufferedOutputStream(outputStream) )
        {
            byte[] buffer = new byte[ BUFFER_SIZE ];

            int len;
            while ((len = bufferedInput.read(buffer)) > 0)
                bufferedOutput.write(buffer, 0, len);

            bufferedOutput.flush();

            return true;
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


    public static void writeFile(String content, File file) throws IOException
    {
        content = ((content != null) ? content : "");
        InputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
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

