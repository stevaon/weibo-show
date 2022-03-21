package com.tang.crawler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tang.entity.Hot;
import com.tang.entity.Weibo;
import com.tang.util.ParseUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class WeiboParser {
	@Autowired
	private ParseUtil parseUtil;
	private final Log logger = LogFactory.getLog(WeiboParser.class);

	public List<Hot> getHotList(String url, int size) {
		JsonNode rootNode = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			String request = parseUtil.request(url);
			rootNode = mapper.readTree(request);
		} catch (IOException e) {
			logger.error("获取热搜出错" + e);
		}

		JsonNode treeNode = rootNode.get("data").get("cards").get(0).get("card_group");

		ArrayList<Hot> hots = new ArrayList<>();
		Iterator<JsonNode> iterator = treeNode.elements();
		int index = 0;
		while (iterator.hasNext()) {
//			跳过置顶微博
			if (index == 0) {
				iterator.next();
				index++;
				continue;
			}
			if (++index > size)
				break;
			JsonNode node = iterator.next();
			String title = node.get("desc").asText();
			String link = node.get("scheme").asText();
			Hot hot = new Hot();
			hot.setTitle(title);
			hot.setLink(link);
			hots.add(hot);
		}
		return hots;
	}

	public List<Weibo> getHotWeiboList(String link, int size) throws IOException {
		String api = link.replace("https://m.weibo.cn/search",
				"https://m.weibo.cn/api/container/getIndex") + "&page_type=searchall";
		ArrayList<Weibo> weibos = new ArrayList<>();
		String request = parseUtil.request(api);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(request);
		int index = 0;
		Iterator<JsonNode> iterator = rootNode.get("data").get("cards").iterator();
		while (iterator.hasNext()) {
			JsonNode card = iterator.next();
			int type = card.get("card_type").asInt();
			if (type != 9 ){
				if (type != 11)
					continue;
			}
			if (++index > size)
				break;
//			特出情况
			if (type == 11) {
				Iterator<JsonNode> card_group = card.get("card_group").iterator();
				while (card_group.hasNext()) {
					JsonNode subCard = card_group.next();
					if (subCard.get("card_type").intValue() == 9)
						weibos.add(parseJson(subCard));
				}
//				正常情况
			} else
				weibos.add(parseJson(card));
		}
		return weibos;
	}

	private Weibo parseJson(JsonNode card) throws IOException {
		String id = card.get("mblog").get("id").asText();
		String user = card.get("mblog").get("user").get("screen_name").asText();
		String avatar = card.get("mblog").get("user").get("profile_image_url").asText();
		String source = card.get("mblog").get("source").asText();
		String repostsCount = card.get("mblog").get("reposts_count").asText();
		String commentsCount = card.get("mblog").get("comments_count").asText();
		String attitudesCount = card.get("mblog").get("attitudes_count").asText();
		int picNum = card.get("mblog").get("pic_num").asInt();
//			微博图片
		List<String> pics = new ArrayList<>();
		Iterator<JsonNode> picIter = card.get("mblog").get("pic_ids").iterator();
		while (picIter.hasNext()) {
			pics.add("https://wx2.sinaimg.cn/large/" + picIter.next().asText() + ".jpg");
		}
		String url = "https://m.weibo.cn/status/" + id;
		// 通过正则从html中获取相应的数据
		String html = parseUtil.request(url); // 请求微博的详情页，获取微博全文和详情信息需要
		String time = getTime(html);
		String content = getContent(html);
		Weibo weibo = new Weibo(id, user, avatar, pics, time, url, content, source, repostsCount, commentsCount, attitudesCount);
		return weibo;
	}

	private static String getTime(String html) {
		Pattern pattern = Pattern.compile("\"created_at\": \".*\",");
		Matcher m = pattern.matcher(html);
		if (m.find()) {
			String create_at = m.group();
			return create_at.substring(15, create_at.length() - 13);
		}
		return "获取时间失败";
	}

	private static String getContent(String html) {
		Pattern pattern = Pattern.compile("\"text\": \".*\",");
		Matcher m = pattern.matcher(html);
		if (m.find()) {
			String text = m.group();
			String content = text.substring(9, text.length() - 2);

			// 过滤html标签
			return processText(content);
		}
		return "获取正文失败";
	}

	private static String processText(String text) {
		// 匹配微博表情包
		Pattern emoji = Pattern.compile("<span class=\"url-icon\"><img alt=([^\\s]+).*?</span>");
		// 匹配所有html标签
		Pattern html = Pattern.compile("<[^>]+>");
		Matcher m = emoji.matcher(text);
		while (m.find()) {
			text = m.replaceFirst(m.group(1));
			m = emoji.matcher(text);
		}
		m = html.matcher(text);
		return m.replaceAll("");
	}
}
