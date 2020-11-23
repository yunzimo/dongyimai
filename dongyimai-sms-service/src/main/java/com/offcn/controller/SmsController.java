package com.offcn.controller;

import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.*;

@RestController
public class SmsController {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination smsDestination;

    @RequestMapping("/sendsms")
    public void sendSms(String mobile,String code){
        jmsTemplate.send(smsDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage=new ActiveMQMapMessage();
                mapMessage.setString("mobile",mobile);
                mapMessage.setString("code",code);
                return mapMessage;
            }
        });
    }
}
