package com.springboot.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.springboot.biz.CompanyBiz;
import com.springboot.biz.SendEpLogBiz;
import com.springboot.biz.UserBiz;
import com.springboot.entity.Company;
import com.springboot.entity.User;
import com.springboot.util.AppUtils;

@RestController
@RequestMapping(value = "/user")
public class UserController {

	@Resource
	private UserBiz userBiz;
	@Resource
	private CompanyBiz companyBiz;

	@Resource
	private SendEpLogBiz sendService;

	private Map<String, Object> map = null;

	@RequestMapping(value = "/logon")
	public ModelAndView logon(User user, String companyName, String code) {
		ModelAndView view = new ModelAndView("user/logon");
		// 获取公司列表
		List<Company> companyList = companyBiz.findListCompany(null);
		view.addObject("companyList", companyList);
		if (user == null || companyName == null)
			return view;
		map = AppUtils.getMap("name", companyName);
		Company company = companyBiz.findInfoCompany(map);
		if (company == null) {
			view.addObject("tips", "*公司名称不正确，请重新输入！");
			view.addObject("companyName", companyName);
			view.addObject("user", user);
			view.addObject("code", code);
			return view;
		}

		map = AppUtils.getMap("username", user.getUsername());
		if (userBiz.findInfoUser(map) != null) {
			view.addObject("tips", "*登录账号已存在，请重新输入！");
			view.addObject("companyName", companyName);
			view.addObject("user", user);
			view.addObject("code", code);
			return view;
		}
		// 校验验证码是否正确,有效时间
		int time = 20;
		if (!sendService.checkEpCode(code, user.getEmail(), time)) {
			view.addObject("tips", "*邮箱校验码不正确！");
			view.addObject("companyName", companyName);
			view.addObject("user", user);
			view.addObject("code", code);
			return view;
		}

		user.setRole("role4");
		user.setDate(AppUtils.getDate(null));
		user.setCompany(company);
		userBiz.insertUser(user);
		view.setViewName("redirect:/user/complete");
		return view;
	}

	@RequestMapping(value = "/login")
	public ModelAndView login(HttpServletRequest request, String username, String password) {
		try {
			ModelAndView view = new ModelAndView("redirect:/project/showlist");
			if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
				view.setViewName("user/login");
				view.addObject("username", username);
				view.addObject("password", password);
				return view;
			}
			UsernamePasswordToken token = new UsernamePasswordToken(username, password);
			SecurityUtils.getSubject().login(token);
			SavedRequest location = WebUtils.getSavedRequest(request);
			if (!StringUtils.isEmpty(location)) {
				String path = location.getRequestUrl();
				path = path.substring(5, path.length());
				view.setViewName("redirect:" + path);
			}
			return view;
		} catch (IncorrectCredentialsException e) {
			ModelAndView view = new ModelAndView("user/login");
			view.addObject("tips", "*账号密码不正确，请重新输入！");
			view.addObject("username", username);
			view.addObject("password", password);
			return view;
		}
	}

	/**
	 * 个人信息
	 * 
	 * @Title: center
	 * @Description:
	 * @return
	 */
	@RequestMapping(value = "/center")
	public ModelAndView center() {
		ModelAndView view = new ModelAndView("user/center");
		User user = (User) AppUtils.findMap("user");
		if (user == null || StringUtils.isEmpty(user.getName())) {
			view.setViewName("user/login");
			return view;
		}
		if (user.getCompany() != null) {
			map = AppUtils.getMap("id", user.getCompany().getId());
			Company company = companyBiz.findInfoCompany(map);
			if (company != null)
				user.setCompany(company);
		}
		int safeNumber = 0;
		if (AppUtils.isExisNumber(user.getPassword()))
			safeNumber++;
		if (AppUtils.isExisLetter(user.getPassword()))
			safeNumber++;
		view.addObject("safe", safeNumber);
		view.addObject("userinfo", user);
		return view;
	}

	@RequestMapping(value = "/repass", method = RequestMethod.POST)
	public Map<String, Object> repass(String oldpass, String pass, String repass) {
		Map<String, Object> reuslt = new HashMap<String, Object>();
		User user = (User) AppUtils.findMap("user");
		if (user == null || StringUtils.isEmpty(user.getName())) {
			reuslt.put("code", 401);
			reuslt.put("msg", "您登录的信息已失效，请从新登录！");
		}
		if (oldpass == null || !oldpass.equals(user.getPassword())) {
			reuslt.put("code", 500);
			reuslt.put("msg", "登录密码输入不正确！");
		}
		if (pass == null || repass == null || !repass.equals(pass)) {
			reuslt.put("code", 500);
			reuslt.put("msg", "两次密码输入不止一只，请重新输入！");
		}

		user.setPassword(pass);
		if (userBiz.updatePass(user) > 0) {
			SecurityUtils.getSubject().logout();
			AppUtils.removeSession("user");// 修改成功后重新登录
			reuslt.put("code", 200);
			reuslt.put("msg", "修改成功！");
		}
		return reuslt;
	}

	@RequestMapping(value = "/reemil", method = RequestMethod.POST)
	public Map<String, Object> reEmil(String email, String code) {
		User user = (User) AppUtils.findMap("user");
		if (user == null || StringUtils.isEmpty(user.getName())) {
			map = AppUtils.getMap("code", 401);
			map.put("msg", "您登录的信息已失效，请从新登录！");
			return map;
		}
		if (email == null || code == null) {
			map = AppUtils.getMap("code", 500);
			map.put("msg", "参数不正确，请检查！");
			return map;
		}

		if (!sendService.checkEpCode(code, email, 20)) {
			map = AppUtils.getMap("code", 500);
			map.put("msg", "邮箱校验码不正确，请您核对在操作！");
			return map;
		}

		user.setEmail(email);
		if (userBiz.updateEmil(user) > 0) {
			map = AppUtils.getMap("code", 200);
			map.put("msg", "操作成功！");
			return map;
		}
		map = AppUtils.getMap("code", 500);
		map.put("msg", "操作失败，请稍后再试！");
		return map;
	}

	@RequestMapping(value = "/resetpass")
	public ModelAndView resetPass(User user, String email, String code) {
		ModelAndView view = new ModelAndView("user/resetview");
		if (user == null || user.getUsername() == null)
			return view;
		if (email == null || code == null) {
			view.addObject("tips", "*参数有误，请核对后再操作！");
			view.addObject("email", email);
			view.addObject("user", user);
			view.addObject("code", code);
			return view;
		}
		map = AppUtils.getMap("username", user.getUsername());
		User userInfo = userBiz.findInfoUser(map);
		if (userInfo == null) {
			view.addObject("tips", "*没有该登录账号，请核对后再操作！");
			view.addObject("email", email);
			view.addObject("user", user);
			view.addObject("code", code);
			return view;
		}
		if (userInfo.getEmail() == null || "".equals(userInfo.getEmail())) {
			view.addObject("tips", "*该账号还未绑定邮箱，请与工作人员联系重置密码！");
			view.addObject("email", email);
			view.addObject("user", user);
			view.addObject("code", code);
			return view;
		}
		if (!email.equals(userInfo.getEmail())) {
			view.addObject("tips", "*该邮箱与该账号绑定的邮箱不一致，请核对后再操作！");
			view.addObject("email", email);
			view.addObject("user", user);
			view.addObject("code", code);
			return view;
		}

		if (!sendService.checkEpCode(code, email, 20)) {
			view.addObject("tips", "*邮箱校验码无效，请核对后再操作！");
			view.addObject("email", email);
			view.addObject("user", user);
			view.addObject("code", code);
			return view;
		}

		// 修改密码
		userInfo.setPassword(user.getPassword());
		if (userBiz.updatePass(userInfo) > 0) {
			view.setViewName("redirect:/user/resetok");
			view.addObject("msg", "重置密码成功！");
			return view;
		} else {
			view.addObject("tips", "*系统繁忙，请稍后再试！");
			view.addObject("email", email);
			view.addObject("user", user);
			view.addObject("code", code);
			return view;
		}
	}

	@RequestMapping(value = "/resetok")
	public ModelAndView resetok() {
		ModelAndView view = new ModelAndView("user/resetok");
		return view;
	}

	@RequestMapping(value = "/userManage/getUserList")
	public ModelAndView getUserList(String keyword, @RequestParam(defaultValue = "1") int pageIndex,
			@RequestParam(defaultValue = "15") int pageSize, @RequestParam(defaultValue = "0") int companyid) {
		ModelAndView view = new ModelAndView("user/userList");
		if (pageSize == 0)
			pageSize = 15;
		if (pageIndex == 0)
			pageIndex = 1;
		User user = (User) AppUtils.findMap("user");
		map = AppUtils.getMap("user", user);
		List<Company> companys = companyBiz.findListCompany(map);
		if (!StringUtils.isEmpty(keyword))
			map.put("keyword", keyword);
		map.put("companyid", companyid);
		int cont = userBiz.getCountByPage(map, pageSize);
		pageIndex = pageIndex > cont ? cont : pageIndex;
		map.put("pageIndex", pageIndex);
		List<User> users = userBiz.getUserListByPage(map, pageSize);
		view.addObject("users", users);
		view.addObject("keyword", keyword);
		view.addObject("pageIndex", pageIndex);
		view.addObject("companyid", companyid);
		view.addObject("companys", companys);
		view.addObject("cont", cont);
		return view;
	}

	@RequestMapping(value = "/userManage/editUser")
	public ModelAndView editView(String action, @RequestParam(defaultValue = "0") int id) {
		ModelAndView view = new ModelAndView("user/editUser");
		map = new HashMap<>();
		List<Company> companys = companyBiz.findListCompany(map);
		view.addObject("companys", companys);
		User user = new User();
		if ("add".equals(action) || id == 0) {
			view.addObject("action", "add");
			view.addObject("user", user);
			return view;
		}
		// 编辑
		map = AppUtils.getMap("id", id);
		user = userBiz.findInfoUser(map);
		if (user == null) {
			view.addObject("action", "add");
			view.addObject("user", user);
			return view;
		} else {
			view.addObject("action", "edit");
			view.addObject("user", user);
			return view;
		}
	}

	@RequestMapping(value = "/userManage/editAction", method = RequestMethod.POST)
	public Map<String, Object> editAction(User user, @RequestParam(defaultValue = "add") String action) {
		String msg = "提交的参数有误";
		if (user == null) {
			map = AppUtils.getMap("code", 500, "msg", msg);
			return map;
		}
		if ("".equals(user.getUsername()) || user.getUsername() == null) {
			map = AppUtils.getMap("code", 500, "msg", msg);
			return map;
		}
		try {
			if ("add".equals(action)) {
				map = AppUtils.getMap("username", user.getUsername());
				if (userBiz.findInfoUser(map) != null) {
					map = AppUtils.getMap("code", 500, "msg", "登录账号已存在，请改变登录账号位数或者数字！");
					return map;
				}
				map = AppUtils.getMap("id", user.getCompanyid());
				Company company = companyBiz.findInfoCompany(map);
				if (company == null) {
					map = AppUtils.getMap("code", 500, "msg", msg);
					return map;
				}
				user.setCompany(company);
				user.setPassword("123456");
				user.setDate(AppUtils.getDate(null));
				userBiz.insertUser(user);
				map = AppUtils.getMap("code", 200, "msg", "添加成功！");
			} else if ("edit".equals(action)) {
				userBiz.updateUser(user);
				map = AppUtils.getMap("code", 200, "msg", "编辑成功！");
			}
			return map;
		} catch (Exception e) {
			msg += e;
		}

		map = AppUtils.getMap("code", 500, "msg", msg);
		return map;
	}

	/** 退出登录 */
	@RequestMapping(value = "leave")
	public ModelAndView leave() {
		ModelAndView view = new ModelAndView();
		view.setViewName("redirect:/user/login");
		SecurityUtils.getSubject().logout();
		AppUtils.removeSession("user");
		return view;
	}

}
