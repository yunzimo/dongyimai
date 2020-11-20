package com.offcn.page.Impl;

import com.alibaba.dubbo.config.annotation.Service;

import com.offcn.mapper.TbGoodsDescMapper;
import com.offcn.mapper.TbGoodsMapper;
import com.offcn.page.service.ItemPageService;
import com.offcn.pojo.TbGoods;
import com.offcn.pojo.TbGoodsDesc;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Service
public class ItemPageServiceImp implements ItemPageService {

    @Value("${filedir}")
    private String FILE_DIR;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Override
    public boolean genHtml(Long goodsId) {
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        try {
            Template template = configuration.getTemplate("item.ftl");
            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            Map map=new HashMap();
            map.put("goods",goods);
            map.put("goodsDesc",goodsDesc);
            File file=new File(FILE_DIR+goodsId+".html");
            FileWriter out = new FileWriter(file);
            template.process(map,out);
            out.close();
            return true;

        } catch (IOException | TemplateException e) {
            e.printStackTrace();
            return false;
        }
    }
}
