package com.springboot.controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.springboot.biz.SendEpLogBiz;
import com.springboot.entity.SendEpLog;
import com.springboot.entity.User;
import com.springboot.util.AppUtils;

/**
 * <p>
 * Title: 公共请求类处理
 * </p>
 * <p>
 * Description:公共请求类处理
 * </p>
 * 
 * @author 梁泽祥
 * @date 2019年5月10日
 */
@RestController
@RequestMapping(value = "/comm")
public class CommController {

	@Resource
	private SendEpLogBiz sendService;

	/**
	 * 
	 * <p>
	 * Title: createCodeImg
	 * </p>
	 * <p>
	 * Description:创建验证码
	 * </p>
	 * 
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@GetMapping("/createCodeImg")
	public void createImage(HttpServletResponse response, HttpSession session) throws IOException {
		BufferedImage image = new BufferedImage(80, 30, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		Random r = new Random();
		g.setColor(new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
		g.fillRect(0, 0, 80, 20);
		// 获取生成的验证码
		String code = AppUtils.getNumber();
		// 绑定验证码
		session.setAttribute("number", code);
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
		g.setColor(new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
		g.drawString(code, 5, 25);
		// 设置消息头
		response.setContentType("image/jpeg");
		OutputStream os = response.getOutputStream();
		ImageIO.write(image, "jpeg", os);
	}

	/**
	 * 发送邮箱验证码
	 * <p>
	 * Title: sendSimpleMail
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param email
	 * @return
	 */
	@RequestMapping(value = "/sendEMailCode", params = { "email" }, method = RequestMethod.POST)
	public Map<String, Object> sendEMailCode(String email) {
		Map<String, Object> reuslt = new HashMap<String, Object>();
		try {
			String subject = "MSDI邮箱验证";
			String Code = AppUtils.getNumber();
			String content = "【深圳麦斯迪埃】您正在使用邮箱进行校验，效验码：" + Code + "。有效时间20分钟，超时请重新获取。(如非本人操作，请忽略该信息)";
			SendEpLog modelLog = new SendEpLog();
			modelLog.setCheckcode(Code);
			User user = (User) AppUtils.findMap("user");
			if (user != null && user.getId() != 0)
				modelLog.setUserid(user.getId());
			else
				modelLog.setUserid(0);
			modelLog.setSendaccount(email);
			modelLog.setSendcontent(content);
			modelLog.setSenddate(new Date());
			modelLog.setSendtype(2);

			if (!sendService.sendSimpleMail(email, subject, content, modelLog)) {
				reuslt.put("code", 500);
				reuslt.put("errorMsg", "系统繁忙，稍后再试哦！");
			}
			reuslt.put("code", 200);
			reuslt.put("errorMsg", "发送成功！");
		} catch (Exception e) {
			reuslt.put("code", 500);
			reuslt.put("errorMsg", "系统繁忙，稍后再试哦！" + e);
		}
		return reuslt;
	}

	@RequestMapping(value = "/unauthorized")
	public ModelAndView unauthorized() {
		ModelAndView view = new ModelAndView("errorpage/401");
		return view;
	}
}
