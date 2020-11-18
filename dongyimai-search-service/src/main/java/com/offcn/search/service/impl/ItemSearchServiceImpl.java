package com.offcn.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;


import com.alibaba.fastjson.JSONObject;
import com.offcn.pojo.TbItem;

import com.offcn.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;


import java.util.*;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        Map<String, Object> map=new HashMap<>();
        map=getItemList(searchMap);
        List list = getCategoryList(searchMap);
        map.put("categoryList",list);
        if(list!=null&&list.size()>0){
            Map brandAndSpecList = getBrandAndSpecList((String) list.get(0));
            map.putAll(brandAndSpecList);
        }
        return map;
    }

    private Map getBrandAndSpecList(String category){
        Map map=new HashMap();
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);
        List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);
        map.put("brandList",brandList);
        map.put("specList",specList);
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
        System.out.println("GroupResult===="+JSONObject.toJSONString(groupResult));

/*        {
            "groupEntries":{
            "content":[
            {
                "groupValue":"手机",
                    "result":{
                "content":[
                {
                    "brand":"三星",
                        "category":"手机",
                        "goodsId":1,
                        "id":1109730,
                        "image":"http://img10.360buyimg.com/n1/s450x450_jfs/t3457/294/236823024/102048/c97f5825/58072422Ndd7e66c4.jpg",
                        "price":115,
                        "specMap":{
                    "网络":"双卡",
                            "机身内存":"32G"
                },
                    "title":"三星 E1200R 黑色 移动联通2G",
                        "updateTime":1425821281000
                }
                    ],
                "first":true,
                        "last":true,
                        "number":0,
                        "numberOfElements":1,
                        "size":0,
                        "totalElements":135,
                        "totalPages":1
            }
            },
            {
                "groupValue":"平板电视",
                    "result":{
                "content":[
                {
                    "brand":"三星",
                        "category":"平板电视",
                        "goodsId":1,
                        "id":1093051,
                        "image":"http://img10.360buyimg.com/n1/s450x450_jfs/t3457/294/236823024/102048/c97f5825/58072422Ndd7e66c4.jpg",
                        "price":7099,
                        "specMap":{
                    "电视屏幕尺寸":"50英寸"
                },
                    "title":"三星(SAMSUNG) UA50HU7000J 50英寸UHD 4K超高清智能电视",
                        "updateTime":1425821265000
                }
                    ],
                "first":true,
                        "last":true,
                        "number":0,
                        "numberOfElements":1,
                        "size":0,
                        "totalElements":13,
                        "totalPages":1
            }
            }
        ],
            "first":true,
                    "last":true,
                    "number":0,
                    "numberOfElements":2,
                    "size":0,
                    "totalElements":2,
                    "totalPages":1
        },
            "matches":148,
                "name":"item_category"
        }*/



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


        //添加筛选条件
        if(!"".equals(searchMap.get("category"))){
            Criteria criteria=new Criteria("item_category");
            criteria.is(searchMap.get("category"));
            query.addCriteria(criteria);
        }
        if(!"".equals(searchMap.get("brand"))){
            Criteria criteria=new Criteria("item_brand");
            criteria.is(searchMap.get("brand"));
            query.addCriteria(criteria);
        }
        if(!"".equals(searchMap.get("price"))){
            String str= (String) searchMap.get("price");
            String[] split = str.split("-");

            if(!split[0].equals("0")){
                Criteria criteria=new Criteria("item_price").greaterThanEqual(split[0]);
                query.addCriteria(criteria);
            }
            if(!split[1].equals("*")){
                Criteria criteria=new Criteria("item_price").lessThanEqual(split[1]);
                query.addCriteria(criteria);
            }
        }
        Map<String,String> specMap= (Map<String, String>) searchMap.get("spec");
        if(specMap.size()>0){
            for(String key:specMap.keySet()){
                Criteria criteria=new Criteria("item_spec_"+key);
                criteria.is(specMap.get(key));
                query.addCriteria(criteria);
            }
        }

        HighlightOptions options=new HighlightOptions().addField("item_title");
        options.setSimplePrefix("<span style='color:red;'>");
        options.setSimplePostfix("</span>");
        query.setHighlightOptions(options);

        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);

        //显示效果为img/page-json.png
        System.out.println("page===="+JSONObject.toJSONString(page));

/*        {
            "allFacets":[
            null
    ],
            "content":[

            {
                "brand":"三星",
                    "category":"手机",
                    "goodsId":1,
                    "id":691300,
                    "image":"http://img10.360buyimg.com/n1/s450x450_jfs/t3457/294/236823024/102048/c97f5825/58072422Ndd7e66c4.jpg",
                    "price":4399,
                    "specMap":{
                "网络":"联通3G",
                        "机身内存":"16G"
            },
                "title":"三星 B9120 钛灰色 联通3G手机 双卡双待双通",
                    "updateTime":1425821367000
            },
            {
                "brand":"三星",
                    "category":"手机",
                    "goodsId":1,
                    "id":738388,
                    "image":"http://img10.360buyimg.com/n1/s450x450_jfs/t3457/294/236823024/102048/c97f5825/58072422Ndd7e66c4.jpg",
                    "price":1699,
                    "specMap":{
                "网络":"联通3G",
                        "机身内存":"16G"
            },
                "title":"三星 Note II (N7100) 云石白 联通3G手机",
                    "updateTime":1425821296000
            },

                "highlighted":[
            {
                "entity":{
                "$ref":"$.content[0]"
            },
                "highlights":[
                {
                    "field":{
                    "name":"item_title"
                },
                    "snipplets":[
                    "<span style='color:red;'>三星</span> E1200R 黑色 移动联通2G"
                    ]
                }
            ]
            },
            {
                "entity":{
                "$ref":"$.content[1]"
            },
                "highlights":[
                {
                    "field":{
                    "name":"item_title"
                },
                    "snipplets":[
                    "<span style='color:red;'>三星</span> G3608 白色 移动4G手机"
                    ]
                }
            ]
            },



    ]
        }*/


        //高亮入口对象
        for (HighlightEntry<TbItem> entry : page.getHighlighted()) {
            TbItem item =  entry.getEntity();//商品sku

            //entry.getHighlights().get(0)第一行符合标题文本
            //entry.getHighlights().get(0).getSnipplets().get(0)第一行符合标题文本的小片段




            if (entry.getHighlights().size() > 0 && entry.getHighlights().get(0).getSnipplets().size()>0){
                item.setTitle(entry.getHighlights().get(0).getSnipplets().get(0));
                //System.out.println("itemChange===="+item);
            }
        }

        map.put("rows",page.getContent());
        return map;
    }
}
