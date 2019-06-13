package com.springboot.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.springboot.biz.CodeBiz;
import com.springboot.biz.DeviceBiz;
import com.springboot.biz.ItemBiz;
import com.springboot.biz.ItemInfoABiz;
import com.springboot.biz.ItemInfoBBiz;
import com.springboot.biz.ItemInfoCBiz;
import com.springboot.biz.PersonBiz;
import com.springboot.biz.PipeBiz;
import com.springboot.biz.ProjectBiz;
import com.springboot.entity.Code;
import com.springboot.entity.Device;
import com.springboot.entity.Item;
import com.springboot.entity.ItemInfoA;
import com.springboot.entity.ItemInfoB;
import com.springboot.entity.ItemInfoC;
import com.springboot.entity.Person;
import com.springboot.entity.Pipe;
import com.springboot.entity.Project;
import com.springboot.entity.User;
import com.springboot.util.AppUtils;
import com.springboot.util.WordUtils;

@RestController
@RequestMapping(value = "/project")
public class ProjectController {

	@Resource
	private ProjectBiz projectBiz;
	@Resource
	private ItemInfoABiz itemInfoABiz;
	@Resource
	private ItemInfoBBiz itemInfoBBiz;
	@Resource
	private ItemInfoCBiz itemInfoCBiz;
	@Resource
	private DeviceBiz deviceBiz;
	@Resource
	private PersonBiz personBiz;
	@Resource
	private PipeBiz pipeBiz;
	@Resource
	private ItemBiz itemBiz;
	@Resource
	private CodeBiz codeBiz;

	private Map<String, Object> map = null;

	@RequestMapping(value = "/insert")
	public ModelAndView insert(Project project) {
		ModelAndView view = new ModelAndView("redirect:/failure");
		if (StringUtils.isEmpty(project.getName()))
			return view;
		User user = (User) AppUtils.findMap("user");
		project.setDate(AppUtils.getDate(null));
		project.setCompany(user.getCompany());
		project.setUser(user);
		int id = projectBiz.appendProject(project);
		view.setViewName("redirect:/pipe/makepipe?id=" + id);
		return view;
	}

	@RequestMapping(value = "/update")
	public ModelAndView update(Project project) {
		ModelAndView view = new ModelAndView("redirect:/failure");
		User user = (User) AppUtils.findMap("user");
		map = AppUtils.getMap("id", project.getId(), "user", user);
		if (projectBiz.findInfoProject(map) == null)
			return view;
		project.setUser(user);
		project.setCompany(user.getCompany());
		projectBiz.updateProject(project);
		view.setViewName("redirect:/success");
		return view;
	}

	@RequestMapping(value = "/delete")
	public boolean delete(int id) {
		User user = (User) AppUtils.findMap("user");
		map = AppUtils.getMap("id", id, "user", user);
		Project project = projectBiz.findInfoProject(map);
		if (!StringUtils.isEmpty(project))
			projectBiz.deleteProject(project);
		return true;
	}

	@RequestMapping(value = "/remove")
	public boolean remove(int id) {
		User user = (User) AppUtils.findMap("user");
		map = AppUtils.getMap("id", id, "company", user.getCompany());
		Project project = projectBiz.findInfoProject(map);
		if (!StringUtils.isEmpty(project))
			projectBiz.deleteProject(project);
		return true;
	}

	@RequestMapping(value = "/insertview")
	public ModelAndView insertview() {
		ModelAndView view = new ModelAndView("project/insert");
		User user = (User) AppUtils.findMap("user");
		map = AppUtils.getMap("company", user.getCompany());
		List<Person> persons = personBiz.findListPerson(map);
		view.addObject("persons", persons);
		return view;
	}

	@RequestMapping(value = "/updateview")
	public ModelAndView updateview(int id) {
		ModelAndView view = new ModelAndView("user/failure");
		User user = (User) AppUtils.findMap("user");
		map = AppUtils.getMap("id", id, "user", user);
		Project project = projectBiz.findInfoProject(map);
		if (StringUtils.isEmpty(project))
			return view;
		map = AppUtils.getMap("company", user.getCompany());
		List<Person> persons = personBiz.findListPerson(map);
		view.setViewName("project/update");
		view.addObject("project", project);
		view.addObject("persons", persons);
		return view;
	}

	@RequestMapping(value = "/showlist")
	public ModelAndView showlist(String name, @RequestParam(defaultValue = "1") int page) {
		ModelAndView view = new ModelAndView("project/showlist");
		User user = (User) AppUtils.findMap("user");
		map = AppUtils.getMap("user", user);
		if (!StringUtils.isEmpty(name))
			map.put("name", name);
		int cont = projectBiz.getCountPage(map, 15);
		page = page > cont ? cont : page;
		map.put("page", page);
		List<Project> projects = projectBiz.findListProject(map);
		view.addObject("projects", projects);
		view.addObject("page", page);
		view.addObject("cont", cont);
		return view;
	}

	@RequestMapping(value = "/operator")
	public ModelAndView operator(String name, @RequestParam(defaultValue = "1") int page) {
		ModelAndView view = new ModelAndView("project/operator");
		User user = (User) AppUtils.findMap("user");
		map = AppUtils.getMap("company", user.getCompany());
		if (!StringUtils.isEmpty(name))
			map.put("name", name);
		int cont = projectBiz.getCountPage(map, 15);
		page = page > cont ? cont : page;
		map.put("page", page);
		List<Project> projects = projectBiz.findListProject(map);
		for (Project project : projects)
			project.setUser(user);
		view.addObject("projects", projects);
		view.addObject("page", page);
		view.addObject("cont", cont);

		return view;
	}

	@RequestMapping(value = "/findinfoA")
	public ModelAndView findInfoA(int id) {
		ModelAndView view = new ModelAndView("user/failure");
		User user = (User) AppUtils.findMap("user");
		map = AppUtils.getMap("id", id, "user", user);
		Project project = projectBiz.findInfoProject(map);
		if (StringUtils.isEmpty(project))
			return view;
		map = AppUtils.getMap("project", project);
		ItemInfoA itemInfoA = itemInfoABiz.findInfoItemInfoA(map);
		if (StringUtils.isEmpty(itemInfoA)) {
			itemInfoA = new ItemInfoA();
			itemInfoA.setConditio("");
			itemInfoA.setMaterial("");
			itemInfoA.setProject(project);
			itemInfoABiz.insertItemInfoA(itemInfoA);
		}
		view.setViewName("project/findinfoA");
		view.addObject("itemInfoA", itemInfoA);
		view.addObject("project", project);
		return view;
	}

	@RequestMapping(value = "/findinfoB")
	public ModelAndView findInfoB(int id) {
		ModelAndView view = new ModelAndView("user/failure");
		User user = (User) AppUtils.findMap("user");
		map = AppUtils.getMap("id", id, "user", user);
		Project project = projectBiz.findInfoProject(map);
		if (StringUtils.isEmpty(project))
			return view;
		map = AppUtils.getMap("project", project);
		ItemInfoB itemInfoB = itemInfoBBiz.findInfoItemInfoB(map);
		if (StringUtils.isEmpty(itemInfoB)) {
			itemInfoB = new ItemInfoB();
			itemInfoB.setProject(project);
			itemInfoBBiz.insertItemInfoB(itemInfoB);
		}
		map = AppUtils.getMap("company", user.getCompany());
		List<Device> devices = deviceBiz.findListDevice(map);
		List<Person> persons = personBiz.findListPerson(map);
		view.setViewName("project/findinfoB");
		view.addObject("itemInfoB", itemInfoB);
		view.addObject("project", project);
		view.addObject("devices", devices);
		view.addObject("persons", persons);
		return view;
	}

	@RequestMapping(value = "/findinfoC")
	public ModelAndView findInfoC(int id) {
		ModelAndView view = new ModelAndView("user/failure");
		User user = (User) AppUtils.findMap("user");
		map = AppUtils.getMap("id", id, "user", user);
		Project project = projectBiz.findInfoProject(map);
		if (StringUtils.isEmpty(project))
			return view;
		map = AppUtils.getMap("project", project);
		ItemInfoC itemInfoC = itemInfoCBiz.findInfoItemInfoC(map);
		if (StringUtils.isEmpty(itemInfoC)) {
			itemInfoC = new ItemInfoC();
			itemInfoC.setProject(project);
			itemInfoCBiz.insertItemInfoC(itemInfoC);
		}
		view.setViewName("project/findinfoC");
		view.addObject("itemInfoC", itemInfoC);
		view.addObject("project", project);
		return view;
	}

	/**
	 * 生成报告--id 为project中的ID
	 */
	@RequestMapping(value = "/generteReport")
	public ModelAndView generateReport(int id) {
		ModelAndView view = new ModelAndView("project/generateReport");
		map = AppUtils.getMap("id", id);
		Project project = projectBiz.findInfoProject(map);// 项目主体信息

		if (project == null) {
			view.setViewName("user/failure");
			return view;
		}
		// itemA信息
		map = AppUtils.getMap("project", project);
		ItemInfoA itemInfoA = itemInfoABiz.findInfoItemInfoA(map);

		// itemB信息 参与设备与人员
		List<Person> inword = new ArrayList<Person>();
		List<Device> inDevices = new ArrayList<Device>();
		map = AppUtils.getMap("project", project);
		ItemInfoB itemInfoB = itemInfoBBiz.findInfoItemInfoB(map);
		if (itemInfoB != null) {
			String pidStr = itemInfoB.getPersons();
			if (!"".equals(pidStr) && pidStr != null) {
				String[] ids = pidStr.split(",");
				for (String idstr : ids) {
					map = AppUtils.getMap("id", idstr);
					Person person = personBiz.findInfoPerson(map);
					if (person != null) {
						inword.add(person);
					}
				}
			}

			String didStr = itemInfoB.getDevices();
			if (!"".equals(didStr) && didStr != null) {
				String[] ids = didStr.split(",");
				for (String idstr : ids) {
					map = AppUtils.getMap("id", idstr);
					Device device = deviceBiz.findInfoDevice(map);
					if (device != null) {
						inDevices.add(device);
					}
				}
			}
		}
		// itemC信息
		map = AppUtils.getMap("project", project);
		ItemInfoC itemInfoC = itemInfoCBiz.findInfoItemInfoC(map);

		// 管道信息Pipe - 包括 item
		map = AppUtils.getMap("project", project);
		List<Pipe> pipeList = pipeBiz.findListPipe(map);
		double totalLength = 0;// 管道长度
		double testLength = 0;// 测量长度
		double rainLength = 0;// 雨水长度
		double sewageLength = 0;// 污水长度
		double confluenceLenth = 0;// 合流长度
		int defectNumber = 0;// 缺陷总数
		int functionNumber = 0;// 功能性缺陷数
		int structureNumber = 0; // 结构性缺陷
		int otherNumber = 0;// 其他缺陷
		for (Pipe pipe : pipeList) {
			List<Item> items = itemBiz.findListItem(pipe);
			pipe.setItems(items);
			for (Item m : items) {
				if (m.getCode() != null && !"".equals(m.getCode())) {
					defectNumber++;
					map = AppUtils.getMap("code", "%" + m.getCode(), "grade", m.getGrade());
					Code code = codeBiz.findInfoCode(map);
					if (code != null) {
						if ("type1".equals(code.getType())) {
							functionNumber++;
						} else
							structureNumber++;
					} else
						otherNumber++;
				}
			}

			if (AppUtils.isNumeric(pipe.getPipelength()))
				totalLength += Double.parseDouble(pipe.getPipelength());

			if (AppUtils.isNumeric(pipe.getTestlength())) {
				double length = Double.parseDouble(pipe.getTestlength());
				testLength += length;
				if ("污水".equals(pipe.getPipetype())) {
					sewageLength += length;
				} else if ("雨水".equals(pipe.getPipetype())) {
					rainLength += length;
				} else if ("合流".equals(pipe.getPipetype())) {
					confluenceLenth += length;
				}
			}

		}

		view.addObject("defectNumber", defectNumber);
		view.addObject("functionNumber", functionNumber);
		view.addObject("structureNumber", structureNumber);
		view.addObject("otherNumber", otherNumber);

		view.addObject("rainLength", rainLength);
		view.addObject("sewageLength", sewageLength);
		view.addObject("confluenceLenth", confluenceLenth);
		view.addObject("totalLength", totalLength);
		view.addObject("testLength", testLength);
		view.addObject("itemInfoA", itemInfoA);
		view.addObject("inword", inword);
		view.addObject("inDevices", inDevices);
		view.addObject("itemInfoC", itemInfoC);
		view.addObject("pipeList", pipeList);
		view.addObject("project", project);
		return view;
	}

	/**
	 * 生成报告
	 */
	@RequestMapping(value = "/report.action")
	public @ResponseBody String report(int id, @RequestParam(defaultValue = "1") int templateType,
			HttpServletRequest request, HttpServletResponse response) {
		map = AppUtils.getMap("id", id);
		Project project = projectBiz.findInfoProject(map);
		if (project == null) {
			return "参数有误，无法生成报告，请您重新操作！";
		}

		// itemA信息
		map = AppUtils.getMap("project", project);
		ItemInfoA itemInfoA = itemInfoABiz.findInfoItemInfoA(map);

		// itemB信息 参与设备与人员
		List<Person> inword = new ArrayList<Person>();
		List<Device> inDevices = new ArrayList<Device>();
		map = AppUtils.getMap("project", project);
		ItemInfoB itemInfoB = itemInfoBBiz.findInfoItemInfoB(map);
		if (itemInfoB != null) {
			String pidStr = itemInfoB.getPersons();
			if (!"".equals(pidStr) && pidStr != null) {
				String[] ids = pidStr.split(",");
				for (String idstr : ids) {
					map = AppUtils.getMap("id", idstr);
					Person person = personBiz.findInfoPerson(map);
					if (person != null) {
						inword.add(person);
					}
				}
			}

			String didStr = itemInfoB.getDevices();
			if (!"".equals(didStr) && didStr != null) {
				String[] ids = didStr.split(",");
				for (String idstr : ids) {
					map = AppUtils.getMap("id", idstr);
					Device device = deviceBiz.findInfoDevice(map);
					if (device != null) {
						inDevices.add(device);
					}
				}
			}
		}
		// itemC信息
		map = AppUtils.getMap("project", project);
		ItemInfoC itemInfoC = itemInfoCBiz.findInfoItemInfoC(map);

		// 管道信息Pipe - 包括 item
		map = AppUtils.getMap("project", project);
		List<Pipe> pipeList = pipeBiz.findListPipe(map);
		for (Pipe pipe : pipeList) {
			List<Item> items = itemBiz.findListItem(pipe);
			pipe.setItems(items);
		}

		Map<String, Object> wordDataMap = new HashMap<String, Object>();// 存储报表全部数据
		Map<String, Object> parametersMap = new HashMap<String, Object>();// 存储报表中不循环的数据

		// 项目主体信息
		parametersMap.put("name", project.getName()); // 工程标题
		parametersMap.put("client", project.getClient());

//		wordDataMap.put("Table1", table1);
//		wordDataMap.put("Table2", table2);
		wordDataMap.put("parametersMap", parametersMap);

		if (templateType == 2) {

		} else if (templateType == 3) {

		} else if (templateType == 4) {

		} else if (templateType == 5) {

		} else {

			try {

				File file = ResourceUtils.getFile("classpath:words/test.docx");
				// 读取word模板
				FileInputStream fileInputStream = new FileInputStream(file);
				WordUtils template = new WordUtils(fileInputStream);
				template.replaceDocumentRestructure(wordDataMap, response, "test");
				return "生成成功";
			} catch (IOException e) {
				return "系统繁忙，请您稍后再试！";
			}
		}
		return "参数有误，无法生成报告，请您重新操作！";
	}

}
