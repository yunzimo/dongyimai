package com.offcn.page.Impl;

import com.offcn.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Component
public class ItemPageListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage= (TextMessage) message;
        try {
            String id = textMessage.getText();
            boolean b = itemPageService.genHtml(Long.valueOf(id));
            if(b){
                System.out.println(id+"页面生成成功");
            }else {
                System.out.println(id+"页面生成失败");
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
