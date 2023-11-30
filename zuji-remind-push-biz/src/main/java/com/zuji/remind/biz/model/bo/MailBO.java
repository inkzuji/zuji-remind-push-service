package com.zuji.remind.biz.model.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 邮件bo.
 *
 * @author inkzuji@gmail.com
 * @create 2023-10-18 17:03
 **/
@Data
public class MailBO implements Serializable {
    private static final long serialVersionUID = 1849253377094726758L;

    /**
     * 收件人。
     */
    private String[] to;

    /**
     * 抄送人。
     */
    private String[] cc;

    /**
     * 主题。
     */
    private String subject;

    /**
     * 文本内容。
     */
    private String text;

    /**
     * 文件路径。
     */
    private String[] filenames;
}
