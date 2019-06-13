package com.springboot.biz.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.springboot.biz.CompanyBiz;
import com.springboot.dao.CompanyDao;
import com.springboot.entity.Company;

@Service
public class CompanytBizImpl implements CompanyBiz {

	@Resource
	private CompanyDao companyDao;

	public void insertCompany(Company company) {
		companyDao.insertCompany(company);
	}

	public void updateCompany(Company company) {
		companyDao.updateCompany(company);
	}

	public void deleteCompany(Company company) {
		companyDao.deleteCompany(company);
	}

	public Company findInfoCompany(Map<String, Object> map) {
		return companyDao.findInfoCompany(map);
	}

	public List<Company> findListCompany(Map<String, Object> map) {
		return companyDao.findListCompany(map);
	}

	public int getCountPage(Map<String, Object> map, int pageSize) {
		if (!StringUtils.isEmpty(map.get("companyName")))
			map.put("companyName", "%" + map.get("companyName") + "%");
		int count = companyDao.getCount(map);
		return (int) Math.ceil((double) count / pageSize);
	}

	public List<Company> findListCompanyByPage(Map<String, Object> map, int pageSize) {
		if (!StringUtils.isEmpty(map.get("companyName")))
			map.put("companyName", "%" + map.get("companyName") + "%");
		if (!StringUtils.isEmpty(map.get("pageIndex")))
			map.put("pageIndex", ((int) map.get("pageIndex") - 1) * pageSize);
		map.put("pageSize", pageSize);
		return companyDao.findListCompanyByPage(map);
	}

}
