package com.qing.expert.common.constant;

/**
 * 通用常量
 */
public class CommonConstant {

    /**
     * 成功状态码
     */
    public static final int SUCCESS_CODE = 200;

    /**
     * 失败状态码
     */
    public static final int ERROR_CODE = 500;

    /**
     * 未授权状态码
     */
    public static final int UNAUTHORIZED_CODE = 401;

    /**
     * 禁止访问状态码
     */
    public static final int FORBIDDEN_CODE = 403;

    /**
     * 资源不存在状态码
     */
    public static final int NOT_FOUND_CODE = 404;

    /**
     * 参数错误状态码
     */
    public static final int BAD_REQUEST_CODE = 400;

    /**
     * 默认页码
     */
    public static final int DEFAULT_PAGE_NUM = 1;

    /**
     * 默认页面大小
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 最大页面大小
     */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * 启用状态
     */
    public static final int STATUS_ENABLE = 1;

    /**
     * 禁用状态
     */
    public static final int STATUS_DISABLE = 0;

    /**
     * 逻辑删除 - 未删除
     */
    public static final int NOT_DELETED = 0;

    /**
     * 逻辑删除 - 已删除
     */
    public static final int DELETED = 1;

    /**
     * 默认密码
     */
    public static final String DEFAULT_PASSWORD = "123456";

    /**
     * UTF-8编码
     */
    public static final String UTF8 = "UTF-8";

    /**
     * JSON内容类型
     */
    public static final String JSON_CONTENT_TYPE = "application/json;charset=UTF-8";

    /**
     * 文件上传路径
     */
    public static final String UPLOAD_PATH = "/uploads/";

    /**
     * 头像上传路径
     */
    public static final String AVATAR_PATH = "/uploads/avatar/";

    /**
     * 签名上传路径
     */
    public static final String SIGNATURE_PATH = "/uploads/signature/";

    /**
     * 服务图片上传路径
     */
    public static final String SERVICE_PATH = "/uploads/service/";

    /**
     * 轮播图上传路径
     */
    public static final String BANNER_PATH = "/uploads/banner/";
}
