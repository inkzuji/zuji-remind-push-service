package com.zuji.remind.biz.client;

import cn.hutool.json.JSONUtil;
import com.zuji.remind.biz.model.bo.MailBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * 邮件推送.
 *
 * @author jianjun.wang@theone.art
 * @create 2023-09-12 22:06
 **/
@Component
@Slf4j
public class EmailPushClient {
    private MailProperties mailProperties;
    private JavaMailSender mailSender;

    @Autowired
    public void setMailProperties(MailProperties mailProperties) {
        this.mailProperties = mailProperties;
    }

    @Autowired
    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * 发送邮件。
     */
    public void sendMessage(MailBO bo) {
        log.info("发送邮件入参: {}", JSONUtil.toJsonStr(bo));
        this.verifyBo(bo);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, Boolean.TRUE, StandardCharsets.UTF_8.toString());
            helper.setFrom(mailProperties.getUsername());
            helper.setTo(bo.getTo());
            helper.setSubject(bo.getSubject());
            if (StringUtils.hasText(bo.getText())) {
                helper.setText(bo.getText(), Boolean.TRUE);
            }
            if (!ObjectUtils.isEmpty(bo.getCc())) {
                helper.setCc(bo.getCc());
            }
            if (!ObjectUtils.isEmpty(bo.getFilenames())) {
                for (String filename : bo.getFilenames()) {
                    File file = new File(filename);
                    helper.addAttachment(file.getName(), file);
                }
            }
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("发送邮件失败, errMsg={}", e.getMessage(), e);
        }
    }

    private void verifyBo(MailBO bo) {
        Assert.notEmpty(bo.getTo(), "收件人不能为空");
        Assert.hasLength(bo.getSubject(), "邮件主题不能为空");
    }
}
