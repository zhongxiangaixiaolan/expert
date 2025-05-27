package com.qing.expert.service;

/**
 * 微信API服务接口
 */
public interface WechatApiService {

    /**
     * 通过code获取微信用户的openid和session_key
     *
     * @param code 微信登录授权码
     * @return 微信用户信息
     */
    WechatUserInfo getWechatUserInfo(String code);

    /**
     * 解密微信用户信息
     *
     * @param sessionKey    会话密钥
     * @param encryptedData 加密数据
     * @param iv            初始向量
     * @return 解密后的用户信息
     */
    WechatUserDetail decryptUserInfo(String sessionKey, String encryptedData, String iv);

    /**
     * 微信用户基础信息
     */
    class WechatUserInfo {
        private String openid;
        private String sessionKey;
        private String unionid;

        public WechatUserInfo() {
        }

        public WechatUserInfo(String openid, String sessionKey, String unionid) {
            this.openid = openid;
            this.sessionKey = sessionKey;
            this.unionid = unionid;
        }

        public String getOpenid() {
            return openid;
        }

        public void setOpenid(String openid) {
            this.openid = openid;
        }

        public String getSessionKey() {
            return sessionKey;
        }

        public void setSessionKey(String sessionKey) {
            this.sessionKey = sessionKey;
        }

        public String getUnionid() {
            return unionid;
        }

        public void setUnionid(String unionid) {
            this.unionid = unionid;
        }
    }

    /**
     * 微信用户详细信息
     */
    class WechatUserDetail {
        private String openId;
        private String nickName;
        private String avatarUrl;
        private Integer gender;
        private String city;
        private String province;
        private String country;
        private String language;
        private String unionId;

        public String getOpenId() {
            return openId;
        }

        public void setOpenId(String openId) {
            this.openId = openId;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public Integer getGender() {
            return gender;
        }

        public void setGender(Integer gender) {
            this.gender = gender;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getUnionId() {
            return unionId;
        }

        public void setUnionId(String unionId) {
            this.unionId = unionId;
        }
    }
}
