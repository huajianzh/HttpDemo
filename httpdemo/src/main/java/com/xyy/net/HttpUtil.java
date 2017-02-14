package com.xyy.net;

import com.xyy.net.imp.Callback;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class HttpUtil {

    /**
     * http请求的get方式(参数放在地址中)
     *
     * @param http
     * @return
     */
    public static <T> T get(String http, Callback<T> callback) {
        T t = null;
        try {
            URL url = new URL(http);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int code = conn.getResponseCode();
            InputStream in = conn.getInputStream();
            ResponceItem responce = new ResponceItem(code,in);
            //转换（将网络数据转为对象）
            t = callback.changeData(responce);
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * http的post请求(参数放在请求内容中)
     *
     * @param http 请求地址
     * @param map  请求参数列表
     * @return
     */
    public static <T> T post(String http, Map<String, String> map,Callback<T> callback) {
        T result = null;
        try {
            URL url = new URL(http);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000);
            // 设置请求方式为post
            conn.setRequestMethod("POST");
            conn.connect();
            if (map != null) {
                // 提交参数
                StringBuffer sb = new StringBuffer();
                Iterator<Entry<String, String>> it = map.entrySet().iterator();
                while (it.hasNext()) {
                    Entry<String, String> entry = it.next();
                    sb.append(entry.getKey()).append("=")
                            .append(entry.getValue()).append("&");
                }
                // 将最后一个&去掉
                sb.delete(sb.length() - 1, sb.length());
                String par = sb.toString();
                OutputStreamWriter out = new OutputStreamWriter(
                        conn.getOutputStream(), "UTF-8");
                out.write(par);
                out.flush();
                out.close();
            }
            int code = conn.getResponseCode();
            InputStream in = conn.getInputStream();
            // 判断是否连接成功
            ResponceItem responce = new ResponceItem(code,in);
            //转换（将网络数据转为对象）
            result = callback.changeData(responce);
            conn.disconnect();
        } catch (Exception e) {

        }
        return result;
    }

    public static <T> T post(String actionUrl, Map<String, String> params,
                              Map<String, File> files,Callback<T> callback) {
        return post(actionUrl, params, files,callback, null);
    }

    // 上传代码，第一个参数，为要使用的URL，第二个参数，为表单内容，第三个参数为要上传的文件，可以上传多个文件
    public static <T> T post(String actionUrl, Map<String, String> params,
                              Map<String, File> files,Callback<T> callback, OnUploadListener l) {
        if (null == files) {
            return post(actionUrl, params,callback);
        }
        T result = null;
        try {
            // 将内容以二进制流形式提交到服务器(需要服务器单独解析)
            String BOUNDARY = java.util.UUID.randomUUID().toString();
            String PREFIX = "--", LINEND = "\r\n";
            String MULTIPART_FROM_DATA = "multipart/form-data";
            String CHARSET = "UTF-8";
            URL uri = new URL(actionUrl);
            HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
            // 读取超时的设置
            conn.setReadTimeout(5 * 1000);
            conn.setDoInput(true);// 允许输入
            conn.setDoOutput(true);// 允许输出
            conn.setUseCaches(false);
            conn.setRequestMethod("POST"); // Post方式
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
                    + ";boundary=" + BOUNDARY);
            // 组装文本类型参数列表
            StringBuilder sb = new StringBuilder();
            if (null != params) {
                for (Entry<String, String> entry : params.entrySet()) {
                    sb.append(PREFIX);
                    sb.append(BOUNDARY);
                    sb.append(LINEND);
                    // 传递文本参数的key部分
                    sb.append("Content-Disposition: form-data; name=\""
                            + entry.getKey() + "\"" + LINEND);
                    sb.append("Content-Type: text/plain; charset=" + CHARSET
                            + LINEND);
                    sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
                    sb.append(LINEND);
                    // 传递文本参数的value部分
                    sb.append(entry.getValue());
                    sb.append(LINEND);
                }
            }
            DataOutputStream outStream = new DataOutputStream(
                    conn.getOutputStream());
            outStream.write(sb.toString().getBytes());
            // 发送文件数据
            if (files != null) {
                int totalSize = 0;
                int currentSize = 0;
                for (Entry<String, File> file : files.entrySet()) {
                    totalSize += file.getValue().length();
                }
                for (Entry<String, File> file : files.entrySet()) {
                    StringBuilder sb1 = new StringBuilder();
                    sb1.append(PREFIX);
                    sb1.append(BOUNDARY);
                    sb1.append(LINEND);
                    sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\""
                            + file.getKey() + "\"" + LINEND);
                    sb1.append("Content-Type: multipart/form-data; charset="
                            + CHARSET + LINEND);
                    sb1.append(LINEND);
                    outStream.write(sb1.toString().getBytes());
                    InputStream is = new FileInputStream(file.getValue());
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = is.read(buffer)) != -1) {
                        outStream.write(buffer, 0, len);
                        // 上传进度的计算
                        currentSize += len;
                        if (null != l) {
                            l.onProgress(currentSize, totalSize);
                        }
                    }
                    is.close();
                    outStream.write(LINEND.getBytes());
                }
            }
            // 请求结束标志
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
            outStream.write(end_data);
            outStream.flush();
            int code = conn.getResponseCode();
            InputStream in = conn.getInputStream();
            ResponceItem responce = new ResponceItem(code,in);
            //转换（将网络数据转为对象）
            result = callback.changeData(responce);
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public interface OnUploadListener {
        void onProgress(int current, int total);
    }

    /**
     * @param url      下载的地址
     * @param savePath 保存位置
     * @param from     下载的起始位置
     * @param to       下载的结束位置(如果该值为0则表示一直下载到结尾)
     */
    public static <T> T download(String url, String savePath, int from, int to,Callback<T>
            callback) {
        T result = null;
        try {
            URL link = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) link.openConnection();
            conn.setRequestMethod("GET");
            //配置断点请求的信息
            if (from > 0 || to > 0) {
                if (to > from) {
                    conn.setRequestProperty("Range", "bytes=" + from + "-" + to);
                } else {
                    conn.setRequestProperty("Range", "bytes=" + from + "-");
                }
            }
            int code = conn.getResponseCode();
            InputStream in = conn.getInputStream();
            ResponceItem responce = new ResponceItem(code,in);
            result = callback.changeData(responce);
//            if (code == 200 || code == 206) {
//                InputStream in = conn.getInputStream();
//                RandomAccessFile as = new RandomAccessFile(savePath, "rw");
//                if (from > 0) {
//                    //跳过多少开始写
//                    as.seek(from);
//                }
//                byte[] buffer = new byte[1024];
//                int num;
//                while ((num = in.read(buffer)) != -1) {
//                    as.write(buffer, 0, num);
//                }
//                as.close();
//                in.close();
//            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
