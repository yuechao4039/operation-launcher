package com.hualala;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Map;

/**
 * @author yuechao 2018/6/5
 */
public class Test {

    /**
     * 解析响应行
     * @param responseLine
     */
    protected static void processResponseLine(String responseLine) {
        String[] datas = responseLine.split("\\s"); // 空格拆分
        System.out.println("responseLine protocol: " + datas[0]);
        System.out.println("responseLine code: " + datas[1]);
        if(datas.length == 3) {
            System.out.println("responseLine msg: " + datas[2]);
        }
    }

    /**
     * 解析响应头，Content-Length: 67等
     * @param headerMap
     * @param responseLine
     */
    protected static void processResponseHeader(Map<String, Object> headerMap, String responseLine) {
        String[] datas = responseLine.split(": "); // 冒号空格拆分
        headerMap.put(datas[0], datas[1]);
        System.out.println(datas[0] + ": " + datas[1]);
    }


    // 回车换行符
    protected static final String CRLF = "\r\n";

    // 默认的编码
    protected static final String DEFAULT_CHARSET = "UTF-8";

//    GET /v1/users/1 HTTP/1.1[CRLF]               ——>  请求行[Request-Line]
//    Accept-Encoding: gzip, deflate, br[CRLF]     ——>  请求头[Request-Header]
//    Content-Length: 67[CRLF]                     ——>  请求头[Request-Header]
//    Content-Type: application/json[CRLF]         ——>  请求头[Request-Header]
//    Host: 127.0.0.1[CRLF]                        ——>  请求头[Request-Header]
//    Cache-Control: max-age=0[CRLF]               ——>  请求头[Request-Header]
//    Connection: keep-alive[CRLF]                 ——>  请求头[Request-Header]
//            [CRLF]                                       ——>  请求头与请求body分隔符回车换行符CRLF：/r/n
//    {"age":25,"id":1,"password":"123456","status":1,"username":"lipan"}

    public static  void main(String argsp[]) throws IOException {
        SocketAddress addr = new InetSocketAddress("172.16.32.208", 8080);

        Socket socket = new Socket();
        socket.connect(addr);

        InputStream is = socket.getInputStream();

        StringBuilder sb = new StringBuilder();
        sb.append("POST /api/login.htmls HTTP/1.1\r\n");
        sb.append("Accept-Encoding: gzip, deflate\r\n");
        sb.append("Content-Type: application/json\r\n");
        sb.append("Host: 172.16.32.208:8080");
        sb.append("Connection: keep-alive\r\n");
        sb.append("\r\n");
        sb.append("" +
                "{" +
                "  \"header\":{" +
                "    \"trcid\":\"d1338c70-19ac-40e1-a459-681c6f90e733\"," +
                "    \"debug\":true" +
                "  }," +
                "  \"data\":{" +
                "    \"userID\":\"1001\"," +
                "    \"userPassword\":\"2002\"," +
                "    \"userType\":\"admin\"," +
                "    \"tenantID\":\"1003\"" +
                "  }\n" +
                "}");

        OutputStream out = socket.getOutputStream();
        out.write(sb.toString().getBytes());
        out.flush();

        byte[] buf = new byte[2048];
        int length;
        while (-1 != (length = is.read(buf, 0, buf.length))) {
            System.out.println(new String(buf, 0, length));
        }


    }
}
