package com.tang.controller;

import com.tang.service.WeiboShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;


@Controller
public class ShowController {
	@Autowired
	private WeiboShowService weiboShowService;

	@ResponseBody
	@GetMapping("/get/hot/{size}")
	public boolean getHot(@PathVariable("size") String size){
		boolean status = weiboShowService.bulkSaveHot(Integer.parseInt(size));
		return status;
	}
	@ResponseBody
	@GetMapping("/search/{keyword}/{from}/{size}")
	public List<Object> search(@PathVariable("keyword") String keyword,
	                                        @PathVariable("from") int from,
	                                        @PathVariable("size") int size) throws IOException {
		return weiboShowService.searchContentHighLight(keyword, from, size);
	}
}
