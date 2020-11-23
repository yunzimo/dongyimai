package com.offcn.service;

import com.offcn.utils.SmsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

@Component("smsListener")
public class SmsListener implements MessageListener {

    @Autowired
    private SmsUtil smsUtil;


    @Override
    public void onMessage(Message message) {
        MapMessage mapMessage= (MapMessage) message;
        try {
            String mobile = mapMessage.getString("mobile");
            String code = mapMessage.getString("code");
            smsUtil.sendSms(mobile,code);

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
