package com.tang.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.IndexOperation;
import co.elastic.clients.elasticsearch.core.search.Highlight;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.tang.crawler.WeiboParser;
import com.tang.entity.Hot;
import com.tang.entity.Weibo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


@Service
public class WeiboShowService {
	private final Log logger = LogFactory.getLog(WeiboShowService.class);
	@Autowired
	private WeiboParser weiboParser;
	@Autowired
	private ElasticsearchClient elasticsearchClient;

	/**
	 * 解析数据并存入es中
	 *
	 * @param size
	 * @return
	 */
	public boolean bulkSaveHot(Integer size) {
		try {
			List<Hot> hotList = weiboParser.getHotList("https://m.weibo.cn/api/container/getIndex?containerid=106003type%3D25%26t%3D3%26disable_hot%3D1%26filter_type%3Drealtimehot&title=%E5%BE%AE%E5%8D%9A%E7%83%AD%E6%90%9C", size);
			hotList.forEach(val -> {
				try {
					logger.debug(val.getTitle());
					List<Weibo> hotWeiboList = weiboParser.getHotWeiboList(val.getLink(), size);
					ArrayList<BulkOperation> operations = new ArrayList<>();
					hotWeiboList.forEach(weibo -> operations.add(
							new BulkOperation(IndexOperation.of(doc -> doc.document(weibo).index("hot_weibo")))
					));
					insert(operations);
					Thread.sleep(5000);
				} catch (Exception e) {
					logger.error("获取热门微博数据出错" + e);
				}
			});
			ArrayList<BulkOperation> bulkOperations = new ArrayList<>();
			hotList.forEach(val -> bulkOperations.add(
					new BulkOperation(IndexOperation.of(doc -> doc.document(val).index("hot")))
			));
			return insert(bulkOperations);
		} catch (IOException e) {
			logger.error("批量写入es失败" + e);
		}
		return false;
	}
	boolean insert(ArrayList<BulkOperation> bulkOperations) throws IOException {

		BulkRequest request = new BulkRequest.Builder().operations(bulkOperations).build();
		BulkResponse response = elasticsearchClient.bulk(request);
		return !response.errors();
	}
	/**
	 * 实现搜索功能
	 *
	 * @param key
	 * @param from
	 * @param size
	 * @return
	 */
	public List<Object> searchContentPage(String key, int from, int size) throws IOException {
		QueryStringQuery stringQuery = new QueryStringQuery.Builder()
				.fields("weiboList.content")
				.query(key)
				.build();
		Query query = new Query.Builder()
				.queryString(stringQuery)
				.build();

		SearchRequest request = new SearchRequest.Builder()
				.index("weibo_hot")
//				分页
				.from(from).size(size)
//			精准匹配
				.query(query)
				.build();
		SearchResponse<Object> response = elasticsearchClient.search(request, Object.class);
		ArrayList<Object> list = new ArrayList<>();
		for (Hit<Object> hit : response.hits().hits()) {
			list.add(hit.source());
		}
		return list;
	}

	/**
	 * 高亮显示关键字
	 *
	 * @param key
	 * @param from
	 * @param size
	 * @return
	 * @throws IOException
	 */
	public List<Object> searchContentHighLight(String key, int from, int size) throws IOException {
		QueryStringQuery stringQuery = new QueryStringQuery.Builder()
				.fields("content")
				.query(key)
				.build();
		Query query = new Query.Builder()
				.queryString(stringQuery)
				.build();
//		配置高亮参数
		HighlightField highlightField = new HighlightField.Builder()
				.requireFieldMatch(true)
				.preTags("<span style=\"color:red\">")
				.postTags("</span>")
				.build();
		Highlight highlight = new Highlight.Builder()
				.requireFieldMatch(true)
				.fields("content", highlightField)
				.boundaryMaxScan(5)
				.build();
		SearchRequest request = new SearchRequest.Builder()
				.index("hot_weibo")
//				分页
				.from(from).size(size)
//			精准匹配
				.query(query)
				.highlight(highlight)
				.build();
		SearchResponse<Object> response = elasticsearchClient.search(request, Object.class);
		ArrayList<Object> list = new ArrayList<>();
		for (Hit<Object> hit : response.hits().hits()) {
//			解析高亮关键字
			List<String> strings = hit.highlight().get("content");
			LinkedHashMap<String, Object> source = (LinkedHashMap<String, Object>) hit.source();
			source.put("content", strings.get(0));
			list.add(source);
		}
		return list;
	}

}
