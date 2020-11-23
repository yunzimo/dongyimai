package com.offcn.utils;

import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class SmsUtil {

    private String host = "http://dingxin.market.alicloudapi.com";

    @Value("${appcode}")
    private String appcode;

    @Value("${tpl_id}")
    private String tpl_id;

    /**
     * 发送短信方法
     *
     * @param mobile 手机号码
     * @param code   验证码
     */
    public void sendSms(String mobile, String code) {
        String method = "post";
        String path = "/dx/sendSms";

        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", mobile);
        querys.put("param", "code:" + code);
        querys.put("tpl_id", tpl_id);
        Map<String, String> bodys = new HashMap<String, String>();

        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
