package com.kang.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: mr_kang66
 * @Email: kangz66@foxmail.com / movieatrevel@gmail.com
 * @Date: 2022-03-17  22:21
 */
@Controller
public class IndexController {

	@GetMapping({"/", "/index"})
	public String index(){
		return "index";
	}


}
