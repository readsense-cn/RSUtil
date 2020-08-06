package cn.readsense.module.net;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.UUID;


public class UCNHttp {

    private static final String PREFIX = "--";                            //前缀
    private static final String BOUNDARY = UUID.randomUUID().toString();  //边界标识 随机生成
    private static final String CONTENT_TYPE = "multipart/form-data";     //内容类型
    private static final String LINE_END = "\r\n";                        //换行

    private static HttpURLConnection setUpHttpURLConnection(String url, String method) throws IOException {
        final HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod(method);
        if (method.equals("POST")) {
            conn.setUseCaches(false);//Post 请求不能使用缓存
            conn.setDoOutput(true);// 设置此方法,允许向服务器输出内容
        }
        conn.setReadTimeout(5000);// 设置读取超时为5秒
        conn.setConnectTimeout(5000);// 设置连接网络超时为10秒
        return conn;
    }


    //从流中读取数据
    private static String read(HttpURLConnection conn) {
        int responseCode = -1;// 调用此方法就不必再使用conn.connect()方法
        try {
            responseCode = conn.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log("code: " + responseCode);
        if (responseCode != -1) {
            InputStream inputStream = null;
            try {
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    inputStream = conn.getInputStream();
                } else {
                    inputStream = conn.getErrorStream();
                }
                return StreamUtil.read(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                StreamUtil.closeStreamPipe(inputStream);
            }
        } else {
            return "未打开连接";
        }

        return null;

    }

    public static String get(String url) {
        HttpURLConnection conn = null;
        try {
            conn = setUpHttpURLConnection(url, "GET");
            return read(conn);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null)
                conn.disconnect();
        }

        return null;
    }

    public static String post(String url, Map<String, String> map) {
        HttpURLConnection conn = null;
        try {
            conn = setUpHttpURLConnection(url, "POST");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

            OutputStream os = conn.getOutputStream();// 获得一个输出流,向服务器写数据

            DataOutputStream dos = new DataOutputStream(os);
            if (map != null && map.size() > 0) {
                dos.write(getStrParams(map).toString().getBytes());
                dos.flush();
            }
            //请求结束标志
            dos.writeBytes(PREFIX + BOUNDARY + PREFIX + LINE_END);
            dos.flush();
            dos.close();
            os.close();

            return read(conn);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) conn.disconnect();
        }

        return null;
    }


    private static void log(String s) {
        System.out.println(s);
    }


    private static StringBuilder getStrParams(Map<String, String> strParams) {
        StringBuilder strSb = new StringBuilder();
        for (Map.Entry<String, String> entry : strParams.entrySet()) {
            strSb.append(PREFIX)
                    .append(BOUNDARY)
                    .append(LINE_END)
                    .append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINE_END)
                    .append("Content-Type: text/plain; charset=" + "utf-8" + LINE_END)
                    .append("Content-Transfer-Encoding: 8bit" + LINE_END)
                    .append(LINE_END)// 参数头设置完以后需要两个换行，然后才是参数内容
                    .append(entry.getValue())
                    .append(LINE_END);
        }
        return strSb;
    }

}
