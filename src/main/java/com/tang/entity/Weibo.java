package com.tang.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Weibo {
	// 元数据
	private String id;
	private String user;
	private String avatar;

	private List<String> pic;
	private String time;
	private String url;
	private String content;
	private String source;

	private String repostsCount;
	private String commentsCount;
	private String attitudesCount;
}
