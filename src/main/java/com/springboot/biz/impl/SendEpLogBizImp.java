package com.springboot.biz.impl;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.springboot.biz.SendEpLogBiz;
import com.springboot.dao.SendEpLogDao;
import com.springboot.entity.SendEpLog;
import com.springboot.util.AppUtils;

/**
 * <p>
 * Title: SendEpLogBizImp
 * </p>
 * <p>
 * Description:发送手机验证码或者邮箱等服务
 * </p>
 * 
 * @author 梁泽祥
 * @date 2019年5月10日
 */

@Service
@Component
public class SendEpLogBizImp implements SendEpLogBiz {
	@Autowired
	private JavaMailSender mailSender;

	@Resource
	private SendEpLogDao sendEpLogDao;

	@Value("${spring.mail.username}")
	private String from;

	/**
	 * @Description:发送简单邮件(收件人，主题，内容)
	 * @return
	 * @author:梁泽祥
	 * @date:2018年3月16日
	 */
	public boolean sendSimpleMail(String to, String subject, String content, SendEpLog model) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(from);
		message.setTo(to);
		message.setSubject(subject);
		message.setText(content);
		try {
			mailSender.send(message);
			// 插入数据
			sendEpLogDao.insert(model);
			return true;
		} catch (Exception e) {
			System.out.println("发送给“" + to + "”简单邮件时发生异常！" + e);
			return false;
		}
	}

	/**
	 * @Description:发送Html邮件(收件人，主题，内容都暂时写死)
	 * @return
	 * @author:zoey
	 * @date:2018年3月16日
	 */
	public void sendHtmlMail(String to, String subject, String content) {
		MimeMessage message = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true); // true表示需要创建一个multipart message
			helper.setFrom(from);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(content, true);
			mailSender.send(message);
			System.out.println("html邮件发送成功");
		} catch (MessagingException e) {
			System.out.println("发送html邮件时发生异常！" + e);
		}
	}

	/**
	 * 发送带附件的邮件
	 * 
	 * @param to
	 * @param subject
	 * @param content
	 * @param filePath
	 */
	public void sendAttachmentsMail(String to, String subject, String content, String filePath) {
		MimeMessage message = mailSender.createMimeMessage();

		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setFrom(from);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(content, true);

			FileSystemResource file = new FileSystemResource(new File(filePath));
			String fileName = filePath.substring(filePath.lastIndexOf(File.separator));
			helper.addAttachment(fileName, file);
			// helper.addAttachment("test"+fileName, file);

			mailSender.send(message);
			System.out.println("带附件的邮件已经发送。");
		} catch (MessagingException e) {
			System.out.println("发送带附件的邮件时发生异常！" + e);
		}
	}

	public boolean checkEpCode(String code, String account, int time) {
		Map<String, Object> map = new HashMap<>();

		if (!StringUtils.isEmpty(code))
			map.put("checkcode", code.trim());
		if (!StringUtils.isEmpty(account))
			map.put("sendaccount", account.trim());

		SendEpLog sendLog = sendEpLogDao.findInfoLog(map);

		if (sendLog != null && AppUtils.longTimeToDay(sendLog.getSenddate(), new Date(), "min") < 20)
			return true;
		return false;
	}
}
