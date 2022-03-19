package com.kang.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.json.jsonb.JsonbJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author: mr_kang66
 * @Email: kangz66@foxmail.com / movieatrevel@gmail.com
 * @Date: 2022-03-17  18:53
 */
@Configuration
public class ElasticsearchClientConfig {
	@Bean
	public ElasticsearchClient elasticsearchClient() {
		RestClient builder = RestClient.builder(
				new HttpHost("localhost", 9200)
		).build();
		RestClientTransport transport = new RestClientTransport(builder, new JacksonJsonpMapper());
		return new ElasticsearchClient(transport);
	}
}
