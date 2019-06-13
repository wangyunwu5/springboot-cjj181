
package com.springboot.biz;

import com.springboot.entity.SendEpLog;

/**
 * <p>
 * Title: SendEpLogBiz
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author 梁泽祥
 * @date 2019年5月10日
 */
public interface SendEpLogBiz {
	boolean sendSimpleMail(String to, String subject, String content, SendEpLog model);

	void sendHtmlMail(String to, String subject, String content);

	void sendAttachmentsMail(String to, String subject, String content, String filePath);

	boolean checkEpCode(String code, String ep, int time);
}
