package com.kang.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: mr_kang66
 * @Email: kangz66@foxmail.com / movieatrevel@gmail.com
 * @Date: 2022-03-17  19:00
 */
@Component
public class ParseUtil {
	public String request(String url) throws IOException {
		OkHttpClient client = new OkHttpClient().newBuilder()
				.connectTimeout(10, TimeUnit.SECONDS)
				.readTimeout(30, TimeUnit.SECONDS)
				.build();
		Request request = new Request.Builder()
				.url(url)
				.header("Cache-Control", "max-age=60")
				.header("Accept", "*/*")
				.header("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6")
				.header("Connection", "keep-alive")
				.header("Content-Type", "application/x-www-form-urlencoded")
				.build();

		Response response = client.newCall(request).execute();
		return response.body().string();
	}

}
