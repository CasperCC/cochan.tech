package com.cochan.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author cochan
 */
@Controller
@RequestMapping(produces = "application/json; charset=UTF-8")
public class PageController {
	
	@RequestMapping("/index")
	public String index() {
		return "main/index";
	}

	@RequestMapping("/")
	public String welcome() {
		return "main/index";
	}

	@RequestMapping("/login")
	public String login() { return "main/login-register"; }

	@ResponseBody
	@RequestMapping("/success")
	public String success() { return "Login succeeded!"; }
}
