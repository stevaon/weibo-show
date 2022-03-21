package com.tang;

import com.tang.crawler.WeiboParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class WeiboShowApplicationTests {

	@Autowired
	private WeiboParser weiboParser;
	@Test
	void contextLoads() throws IOException {
//		weiboParser.getHotList("https://m.weibo.cn/api/container/getIndex?containerid=106003type%3D25%26t%3D3%26disable_hot%3D1%26filter_type%3Drealtimehot&title=%E5%BE%AE%E5%8D%9A%E7%83%AD%E6%90%9C", 20).forEach(System.out::println);
//		weiboParser.getHotWeiboList("https://m.weibo.cn/search?containerid=100103type%3D1%26t%3D10%26q%3D%23%E4%B8%8D%E5%90%8C%E7%9A%84%E5%9F%8E%E5%B8%82%E7%9B%B8%E5%90%8C%E7%9A%84%E5%AE%88%E6%8A%A4%E8%80%85%23&stream_entry_id=31&isnewpage=1&extparam=seat%3D1%26pos%3D2%26realpos%3D3%26dgr%3D0%26c_type%3D31%26flag%3D0%26filter_type%3Drealtimehot%26cate%3D0%26lcate%3D5001%26display_time%3D1647515387%26pre_seqid%3D164751538746306842379&luicode=10000011&lfid=106003type%3D25%26t%3D3%26disable_hot%3D1%26filter_type%3Drealtimehot", 10).forEach(System.out::println);

	}

}
