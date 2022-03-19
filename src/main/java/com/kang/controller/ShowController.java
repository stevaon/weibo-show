package com.kang.controller;

import com.kang.service.WeiboShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: mr_kang66
 * @Email: kangz66@foxmail.com / movieatrevel@gmail.com
 * @Date: 2022-03-17  21:47
 */
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
