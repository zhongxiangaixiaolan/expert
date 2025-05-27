package com.qing.expert.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.qing.expert.common.exception.BusinessException;
import com.qing.expert.service.WechatApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 微信API服务实现
 */
@Slf4j
@Service
public class WechatApiServiceImpl implements WechatApiService {

    @Value("${expert.wechat.miniapp.app-id}")
    private String appId;

    @Value("${expert.wechat.miniapp.app-secret}")
    private String appSecret;

    private static final String WECHAT_CODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Override
    public WechatUserInfo getWechatUserInfo(String code) {
        if (StrUtil.isBlank(code)) {
            throw new BusinessException("微信授权码不能为空");
        }

        try {
            // 构建请求参数
            String url = String.format("%s?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                    WECHAT_CODE2SESSION_URL, appId, appSecret, code);

            log.info("调用微信API获取用户信息：appId={}, code={}", appId, code);

            // 发送HTTP请求
            String response = HttpUtil.get(url);
            log.info("微信API响应：{}", response);

            if (StrUtil.isBlank(response)) {
                throw new BusinessException("微信API响应为空");
            }

            // 解析响应
            JSONObject jsonResponse = JSONUtil.parseObj(response);

            // 检查是否有错误
            if (jsonResponse.containsKey("errcode")) {
                Integer errcode = jsonResponse.getInt("errcode");
                String errmsg = jsonResponse.getStr("errmsg");
                log.error("微信API返回错误：errcode={}, errmsg={}", errcode, errmsg);
                throw new BusinessException("微信登录失败：" + errmsg);
            }

            // 提取用户信息
            String openid = jsonResponse.getStr("openid");
            String sessionKey = jsonResponse.getStr("session_key");
            String unionid = jsonResponse.getStr("unionid");

            if (StrUtil.isBlank(openid)) {
                throw new BusinessException("获取微信用户openid失败");
            }

            log.info("获取微信用户信息成功：openid={}", openid);
            return new WechatUserInfo(openid, sessionKey, unionid);

        } catch (Exception e) {
            log.error("调用微信API失败", e);
            throw new BusinessException("微信登录失败：" + e.getMessage());
        }
    }

    @Override
    public WechatUserDetail decryptUserInfo(String sessionKey, String encryptedData, String iv) {
        if (StrUtil.isBlank(sessionKey) || StrUtil.isBlank(encryptedData) || StrUtil.isBlank(iv)) {
            log.warn("解密参数不完整，跳过用户信息解密");
            return null;
        }

        try {
            // Base64解码
            byte[] sessionKeyBytes = Base64.getDecoder().decode(sessionKey);
            byte[] encryptedDataBytes = Base64.getDecoder().decode(encryptedData);
            byte[] ivBytes = Base64.getDecoder().decode(iv);

            // AES解密
            SecretKeySpec keySpec = new SecretKeySpec(sessionKeyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            byte[] decryptedBytes = cipher.doFinal(encryptedDataBytes);
            String decryptedData = new String(decryptedBytes, StandardCharsets.UTF_8);

            log.info("解密用户信息成功：{}", decryptedData);

            // 解析JSON
            JSONObject userJson = JSONUtil.parseObj(decryptedData);

            WechatUserDetail userDetail = new WechatUserDetail();
            userDetail.setOpenId(userJson.getStr("openId"));
            userDetail.setNickName(userJson.getStr("nickName"));
            userDetail.setAvatarUrl(userJson.getStr("avatarUrl"));
            userDetail.setGender(userJson.getInt("gender"));
            userDetail.setCity(userJson.getStr("city"));
            userDetail.setProvince(userJson.getStr("province"));
            userDetail.setCountry(userJson.getStr("country"));
            userDetail.setLanguage(userJson.getStr("language"));
            userDetail.setUnionId(userJson.getStr("unionId"));

            return userDetail;

        } catch (Exception e) {
            log.error("解密微信用户信息失败", e);
            // 解密失败不抛异常，返回null让调用方处理
            return null;
        }
    }
}
