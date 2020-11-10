package egovframework.example.sample.user;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import egovframework.example.sample.service.impl.SampleDAO;

@Controller
@RequestMapping("/user")
public class UserController {
	@Resource(name = "sampleDAO")
	private SampleDAO sampleDAO;

	@RequestMapping(value="/main.do")
	public String main() {
		return "user/main";
	}
}
