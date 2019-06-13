package com.springboot.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.springboot.biz.CompanyBiz;
import com.springboot.entity.Company;
import com.springboot.entity.User;
import com.springboot.util.AppUtils;

@RestController
@RequestMapping(value = "/company")
public class CompanyController {

	@Resource
	private CompanyBiz companyBiz;

	private Map<String, Object> map = null;

	/**
	 * 获取公司列表
	 */
	@RequestMapping(value = "/getComanyList")
	public ModelAndView getComanyList(String companyName, @RequestParam(defaultValue = "1") int pageIndex,
			@RequestParam(defaultValue = "15") int pageSize) {
		ModelAndView view = new ModelAndView("company/companyList");
		if (pageSize == 0)
			pageSize = 15;
		if (pageIndex == 0)
			pageIndex = 1;
		User user = (User) AppUtils.findMap("user");
		map = AppUtils.getMap("user", user);
		if (!StringUtils.isEmpty(companyName))
			map.put("companyName", companyName);
		int cont = companyBiz.getCountPage(map, pageSize);
		pageIndex = pageIndex > cont ? cont : pageIndex;
		map.put("pageIndex", pageIndex);
		List<Company> companys = companyBiz.findListCompanyByPage(map, pageSize);
		view.addObject("companys", companys);
		view.addObject("companyName", companyName);
		view.addObject("pageIndex", pageIndex);
		view.addObject("cont", cont);
		return view;
	}

	@RequestMapping(value = "/editCompany")
	public ModelAndView editView(String action, @RequestParam(defaultValue = "0") int id) {
		ModelAndView view = new ModelAndView("company/editCompany");
		Company company = new Company();
		if ("add".equals(action) || id == 0) {
			view.addObject("action", "add");
			view.addObject("company", company);
			return view;
		}
		// 编辑
		map = AppUtils.getMap("id", id);
		company = companyBiz.findInfoCompany(map);
		if (company == null) {
			view.addObject("action", "add");
			view.addObject("company", company);
			return view;
		} else {
			view.addObject("action", "edit");
			view.addObject("company", company);
			return view;
		}
	}

	@RequestMapping(value = "/editAction", method = RequestMethod.POST)
	public Map<String, Object> editAction(Company company, @RequestParam(defaultValue = "add") String action) {
		String msg = "提交的参数有误";
		if (company == null) {
			map = AppUtils.getMap("code", 500, "msg", msg);
			return map;
		}
		if ("".equals(company.getName()) || company.getName() == null) {
			map = AppUtils.getMap("code", 500, "msg", msg);
			return map;
		}
		try {
			if ("add".equals(action)) {
				companyBiz.insertCompany(company);
				map = AppUtils.getMap("code", 200, "msg", "添加成功！");
			} else if ("edit".equals(action)) {

				companyBiz.updateCompany(company);
				map = AppUtils.getMap("code", 200, "msg", "编辑成功！");
			}
			return map;
		} catch (Exception e) {
			msg += e;
		}

		map = AppUtils.getMap("code", 500, "msg", msg);
		return map;
	}

	@RequestMapping(value = "/delCompany", method = RequestMethod.POST)
	public Map<String, Object> delCompany(int id) {
		if (id == 0) {
			map = AppUtils.getMap("code", 500, "msg", "参数错误！");
			return map;
		}
		map = AppUtils.getMap("id", id);
		Company company = companyBiz.findInfoCompany(map);
		if (company == null) {
			map = AppUtils.getMap("code", 500, "msg", "无法删除不存在的公司！");
			return map;
		}
		try {
			companyBiz.deleteCompany(company);
			map = AppUtils.getMap("code", 200, "msg", "操作成功！");
			return map;
		} catch (Exception e) {
			map = AppUtils.getMap("code", 500, "msg", "系统繁忙，请稍后再试！" + e);
			return map;
		}

	}
}
