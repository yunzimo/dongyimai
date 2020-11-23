package com.offcn.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

@Service
public class ItemSearchListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage= (TextMessage) message;
        try {
            List<TbItem> itemList = JSON.parseArray(textMessage.getText(), TbItem.class);
            itemSearchService.importData(itemList);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
