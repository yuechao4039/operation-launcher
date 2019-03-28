package com.hualala;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yuechao 2018/7/13
 */
public class SummaryTest extends Test {

    // 往来单位分类查询
    public  void summaryquery()  throws Exception{
        ByteArrayOutputStream data = new ByteArrayOutputStream();

        // body json数据
        byte[] requestBody = "{\"header\":{\"trcid\":\"d1338c70-19ac-40e1-a459-681c6f90e733\",\"debug\":true},\"pageInfo\": {\"num\":2,\"size\":10}}".getBytes(DEFAULT_CHARSET);

        // 写入请求行（协议及版本号,必须是大写的HTTP）
        String requestLine = "POST /api/gl/summaryCategory/query.htmls HTTP/1.1" + CRLF;
        data.write(requestLine.getBytes(DEFAULT_CHARSET));

        // 写入请求头, 冒号:后面含空格，例如[Connection: keep-alive]
        StringBuilder requestHeaders = new StringBuilder();
        requestHeaders.append("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp," +
                "image/apng,*/*;q=0.8").append(CRLF)
                .append("Accept-Encoding: gzip, deflate, br").append(CRLF)
                .append("Accept-Language: zh-CN,zh;q=0.8").append(CRLF)
                .append("Connection: keep-alive").append(CRLF)
                .append("Host: 127.0.0.1").append(CRLF)
                .append("Cache-Control: max-age=0").append(CRLF)
                .append("Upgrade-Insecure-Requests: 1").append(CRLF)
                .append("User-Agent: lipan-http").append(CRLF)
                .append("Content-Type: application/json").append(CRLF)
                .append("Cookie: JSESSIONID=A69750C661B4C1EDEA54B2E52E199F44").append(CRLF)
                .append("Content-Length: " + requestBody.length).append(CRLF);

        data.write(requestHeaders.toString().getBytes(DEFAULT_CHARSET));

        // 写入请求头与请求体CRLF分隔符
        data.write(CRLF.getBytes());

        // 写入请求体
        data.write(requestBody);

        byte[] httpData = data.toByteArray();

        // 发送Http请求
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("172.16.32.208", 8080));
        socket.setKeepAlive(true);
        socket.setSoTimeout(50000);
        socket.setTcpNoDelay(false);

        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();
        out.write(httpData);

        // 解析响应结果
        LineNumberReader reader = new LineNumberReader(new InputStreamReader(in, DEFAULT_CHARSET));
        // 解析响应行
        processResponseLine(reader.readLine());

        // 解析响应头
        String headerLine;
        Map<String, Object> headerMap = new HashMap<String, Object>();
        while(!(headerLine = reader.readLine()).equals("")) {
            processResponseHeader(headerMap, headerLine);
        }

        // 解析响应体
        int responseContentLen = 0;
        if(headerMap.containsKey("Content-Length")) {
            responseContentLen = Integer.parseInt(headerMap.get("Content-Length").toString());
        }
//        if(responseContentLen > 0) {
//        byte[] buffer = new byte[4096];
//        int i = 0;
//        int v = -1;
//        int length = in.read(buffer, 0, buffer.length);
//            while((v = reader.read()) != -1) {
//                body[i] = (byte) v;
//                i++;
//            }
//        if (-1 != length)
//            System.out.println("响应体Body: " + new String(buffer, 0, length, DEFAULT_CHARSET));
//        }
//        String str;
//        while (null != (str = reader.readLine())) {
//            System.out.println(str);
//        }

        // 关闭系统资源
        in.close();out.close();
        socket.close();
    }
}
