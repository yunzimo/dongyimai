package com.offcn.page.Impl;

import com.alibaba.dubbo.config.annotation.Service;

import com.offcn.mapper.TbGoodsDescMapper;
import com.offcn.mapper.TbGoodsMapper;
import com.offcn.mapper.TbItemCatMapper;
import com.offcn.mapper.TbItemMapper;
import com.offcn.page.service.ItemPageService;
import com.offcn.pojo.*;

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
import java.util.List;
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

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;

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

            String itemCatName1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
            String itemCatName2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
            String itemCatName3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();

            map.put("itemCatName1",itemCatName1);
            map.put("itemCatName2",itemCatName2);
            map.put("itemCatName3",itemCatName3);


            //查询itemList
            TbItemExample example=new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(goodsId);
            criteria.andStatusEqualTo("1");
            example.setOrderByClause("is_default desc");
            List<TbItem> itemList = itemMapper.selectByExample(example);
            map.put("itemList",itemList);


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

    @Override
    public boolean DeleHtml(Long goodsId) {
        File file=new File(FILE_DIR+goodsId+".html");
        try {
            return file.delete();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        //System.out.println();

    }
}
