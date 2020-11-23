package com.offcn.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;



@Service
public class ItemSearchDeleteListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage= (ObjectMessage) message;
        try {
           Long[] ids = (Long[]) objectMessage.getObject();
            itemSearchService.deleteData(ids);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
