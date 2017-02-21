package com.xyy.httpdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xyy.model.DiaryItem;
import com.xyy.model.User;
import com.xyy.net.NetManager;
import com.xyy.net.ResponceItem;
import com.xyy.net.StringRequestItem;
import com.xyy.net.imp.Callback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

		//结果显示文本框
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_get).setOnClickListener(this);
        findViewById(R.id.btn_post).setOnClickListener(this);
        findViewById(R.id.btn_download_1).setOnClickListener(this);
        findViewById(R.id.btn_download_2).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
        tvResult = (TextView) findViewById(R.id.tv_result);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_get:
                httpGet();
                break;
            case R.id.btn_post:
                httpPost();
                break;
            case R.id.btn_download_1:
                partDowload("http://192.168.1.189:8080/MyServletDemo001/download?filename=temp" +
                        ".jpg", "/mnt/sdcard/a.jpg", 0, 100);
                break;
            case R.id.btn_download_2:
                partDowload("http://192.168.1.189:8080/MyServletDemo001/download?filename=temp" +
                        ".jpg", "/mnt/sdcard/a.jpg", 100, 0);
                break;
            case R.id.btn_login:
                StringRequestItem request = new StringRequestItem.Builder().url("http://192.168.1" +
                        ".189:8080/AskMeServer/login")
                        .method("post")
                        .addStringParam("name", "ff")
                        .addStringParam("psw", "123")
                        .build();
                NetManager.getInstance().execute(request, new Callback<User>() {
                    @Override
                    public User changeData(ResponceItem responce) {
                        if (responce.getCode() == 200) {
                            try {
                            		//Json解析
                                JSONObject obj = new JSONObject(responce.getString());
                                User u = new User();
                                int id = obj.optInt("id");
                                String nick = obj.optString("nick");
                                u.setId(id);
                                u.setNick(nick);
                                return u;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        return null;
                    }

                    @Override
                    public void onResult(User result) {
                        if (result != null) {
                            Toast.makeText(MainActivity.this, result.getNick(), Toast
                                    .LENGTH_LONG).show
                                    ();
                        } else {
                            Toast.makeText(MainActivity.this, "请求失败", Toast
                                    .LENGTH_LONG).show
                                    ();
                        }
                    }
                });
                break;
        }
    }

    /**
     * @param url      下载的地址
     * @param savePath 保存位置
     * @param from     下载的起始位置
     * @param to       下载的结束位置(如果该值为0则表示一直下载到结尾)
     */
    private void partDowload(final String url, final String savePath, final int from, final int
            to) {
        new Thread() {
            @Override
            public void run() {
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
                    if (code == 200 || code == 206) {
                        InputStream in = conn.getInputStream();
                        RandomAccessFile as = new RandomAccessFile(savePath, "rw");
                        if (from > 0) {
                            //跳过多少开始写
                            as.seek(from);
                        }
                        byte[] buffer = new byte[1024];
                        int num;
                        while ((num = in.read(buffer)) != -1) {
                            as.write(buffer, 0, num);
                        }
                        as.close();
                        in.close();
                    }
                    conn.disconnect();
                    Log.e("m_tag", "下载成功");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void httpPost() {
        new Thread() {
            @Override
            public void run() {
                //处理网络请求
                try {
                    URL url = new URL("http://192.168.1.189:8080/MyServelet/test");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //配置连接信息
                    conn.setConnectTimeout(3000);
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    //设置请求方式为post
                    conn.setRequestMethod("POST");
                    conn.connect();
                    //写参数,多个参数用&连接起来
                    String par = "name=android_post&psw=123456";
                    //得到写到服务端的输出流
                    OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(),
                            "utf-8");
                    osw.write(par);
                    osw.flush();
                    osw.close();
                    //处理结果
                    if (conn.getResponseCode() == 200) {
                        InputStream in = conn.getInputStream();
                        //将结果转为字符串（借助ByteArrayOutputStream）
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int num;
                        while ((num = in.read(buffer)) != -1) {
                            out.write(buffer, 0, num);
                        }
                        out.flush();
                        String str = new String(out.toByteArray());
                        mHandler.obtainMessage(1, str).sendToTarget();
                        out.close();
                        in.close();
                    }
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void httpGet() {
        new Thread() {
            @Override
            public void run() {
                //处理网络请求
                try {
                    // 创建请求的链接对象
                    URL url = new URL("http://192.168.1.189:8080/MyServletDemo001/userinfo");
                    //打开连接过去HttpConnection（请求的客户端）
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //可以配置打开的信息
                    conn.setConnectTimeout(3000);
                    //判断请求结果
                    if (conn.getResponseCode() == 200) {
                        InputStream in = conn.getInputStream();
                        //将结果转为字符串（借助ByteArrayOutputStream）
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int num;
                        while ((num = in.read(buffer)) != -1) {
                            out.write(buffer, 0, num);
                        }
                        out.flush();
                        String str = new String(out.toByteArray());
                        //解析json结果
                        List<DiaryItem> list = new ArrayList<DiaryItem>();
                        JSONArray ary = new JSONArray(str);
                        int len = ary.length();
                        for (int i = 0; i < len; i++) {
                            JSONObject obj = ary.optJSONObject(i);
                            String content = obj.optString("content");
                            int id = obj.optInt("id");
                            String title = obj.optString("title");
                            String time = obj.optString("pub_time");
                            list.add(new DiaryItem(content, id, time, title));
                        }
                        mHandler.obtainMessage(1, list).sendToTarget();
                        out.close();
                        in.close();
                    }
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.obj instanceof String) {
                tvResult.setText(msg.obj.toString());
            } else {
                List<DiaryItem> list = (List<DiaryItem>) msg.obj;
                tvResult.setText("收到内容的长度：" + list.size());
            }
        }
    };
}
