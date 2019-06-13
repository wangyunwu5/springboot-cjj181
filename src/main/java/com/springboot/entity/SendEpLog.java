package com.springboot.entity;

import java.util.Date;

public class SendEpLog {

	/**
	 * 表ID
	 */
	private Integer id;
	/**
	 * 接收者userid（对应表Users中ID）如不在登录状态则为0
	 */
	private Integer userid;
	/**
	 * 接收者账号（可以是手机号或者邮箱账号）
	 */
	private String sendaccount;
	/**
	 * 检验码（不需要校验业务可为空）
	 */
	private String checkcode;
	/**
	 * 发送内容（当发送内容为文件时，可备注：发送xxx文件）
	 */
	private String sendcontent;
	/**
	 * 发送类型（1-手机验证码发送，2-e-mall发送）
	 */
	private Integer sendtype;
	/**
	 * 发送时间
	 */
	private Date senddate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	public String getSendaccount() {
		return sendaccount;
	}

	public void setSendaccount(String sendaccount) {
		this.sendaccount = sendaccount == null ? null : sendaccount.trim();
	}

	public String getCheckcode() {
		return checkcode;
	}

	public void setCheckcode(String checkcode) {
		this.checkcode = checkcode == null ? null : checkcode.trim();
	}

	public String getSendcontent() {
		return sendcontent;
	}

	public void setSendcontent(String sendcontent) {
		this.sendcontent = sendcontent == null ? null : sendcontent.trim();
	}

	public Integer getSendtype() {
		return sendtype;
	}

	public void setSendtype(Integer sendtype) {
		this.sendtype = sendtype;
	}

	public Date getSenddate() {
		return senddate;
	}

	public void setSenddate(Date senddate) {
		this.senddate = senddate;
	}
}