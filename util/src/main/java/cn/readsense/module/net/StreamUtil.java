package cn.readsense.module.net;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.channels.Channel;
import java.nio.charset.StandardCharsets;

public class StreamUtil {

    //写入本地文件
    public static void write(OutputStream outputStream, File file) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            StreamUtil.closeStreamPipe(fileInputStream);
        }
    }

    //写入字符串
    public static void write(OutputStream outputStream, String msg) {
        OutputStreamWriter outputStreamWriter = null;
        try {
            outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            outputStreamWriter.write(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //读取文件
    public static void read(InputStream inputStream, File file) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);

            byte[] buffer = new byte[2048];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStreamPipe(fileOutputStream);
        }
    }

    //读取字符串
    public static String read(InputStream inputStream) {
        ByteArrayOutputStream outStream = null;
        try {
            outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            return outStream.toString("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String read0(InputStream inputStream) {
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            // 将输入字节流对象包装成输入字符流对象，并将字符编码为UTF-8格式
            inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            // 创建一个输入缓冲区对象，将要输入的字符流对象传入
            bufferedReader = new BufferedReader(inputStreamReader);
            // 使用循环逐行读取缓冲区的数据，每次循环读入一行字符串数据赋值给line字符串变量，直到读取的行为空时标识内容读取结束循环
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                // 将缓冲区读取到的数据追加到可变字符对象中
                sb.append(line);
                sb.append("\r\n");

            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStreamPipe(inputStreamReader);
            closeStreamPipe(bufferedReader);
        }
        return null;
    }

    public static void closeStreamPipe(Object stream) {
        if (stream != null) {
            try {
                if (stream instanceof OutputStream)
                    ((OutputStream) stream).close();
                else if (stream instanceof InputStream)
                    ((InputStream) stream).close();
                else if (stream instanceof Writer)
                    ((Writer) stream).close();
                else if (stream instanceof Reader)
                    ((Reader) stream).close();
                else if (stream instanceof Channel)
                    ((Channel) stream).close();
                else throw new RuntimeException("cant close target stram");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
