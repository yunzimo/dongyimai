package com.offcn.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;


import javax.swing.text.Highlighter;
import java.util.*;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        Map<String, Object> map=new HashMap<>();
        map=getItemList(searchMap);
        List list = getCategoryList(searchMap);
        map.put("categoryList",list);
        return map;
    }

    private List getCategoryList(Map searchMap) {
        List list = new ArrayList();

        Query query = new SimpleQuery();
        //分组选项 select * from tb_item group by item_category 分组
        GroupOptions options = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(options);
        //添加条件 select * from tb_item where item_keywords=? group by item_category
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //分组分页对象
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //分组结果集
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //分组入口对象，去重复
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //分组集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        //遍历分组集合
        for (GroupEntry<TbItem> entry : content) {
            //getGroupValue()将分组的值添加list集合中
            list.add(entry.getGroupValue());
        }
        return list;
    }

    public Map<String, Object> getItemList(Map searchMap) {
        Map<String, Object> map=new HashMap<>();
        HighlightQuery query=new SimpleHighlightQuery();//默认查所有
        HighlightOptions options=new HighlightOptions().addField("item_title");
        options.setSimplePrefix("<span style='color:red;'>");
        options.setSimplePostfix("</span>");
        query.setHighlightOptions(options);

        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);

        //显示效果为img/page-json.png
        System.out.println("page===="+JSONObject.toJSONString(page));


        //高亮入口对象
        for (HighlightEntry<TbItem> entry : page.getHighlighted()) {
            TbItem item =  entry.getEntity();//商品sku

            //entry.getHighlights().get(0)第一行符合标题文本
            //entry.getHighlights().get(0).getSnipplets().get(0)第一行符合标题文本的小片段



/*            List<HighlightEntry.Highlight> highlightList = entry.getHighlights();
            for(HighlightEntry.Highlight highlight:highlightList){
                List<String> snipplets = highlight.getSnipplets();
                for(String s:snipplets){
                    System.out.println(s);
                }
            }*/

            if (entry.getHighlights().size() > 0 && entry.getHighlights().get(0).getSnipplets().size()>0){
                item.setTitle(entry.getHighlights().get(0).getSnipplets().get(0));
                //System.out.println("itemChange===="+item);
            }
        }

        map.put("rows",page.getContent());
        return map;
    }
}
