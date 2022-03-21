package com.tang.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
