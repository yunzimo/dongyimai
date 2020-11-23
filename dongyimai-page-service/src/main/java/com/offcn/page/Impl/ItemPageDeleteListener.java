package com.offcn.page.Impl;

import com.offcn.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.xml.soap.Text;


@Component
public class ItemPageDeleteListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage= (TextMessage) message;
        try {
            String id = textMessage.getText();
            boolean b = itemPageService.DeleHtml(Long.valueOf(id));
            if(b){
                System.out.println(id+"页面删除成功");
            }else {
                System.out.println(id+"页面删除失败");
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
