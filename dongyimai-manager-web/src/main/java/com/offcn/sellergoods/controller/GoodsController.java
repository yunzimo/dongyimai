package com.offcn.sellergoods.controller;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.offcn.entity.Goods;

import com.offcn.pojo.TbItem;
import com.offcn.search.service.GoodsService;

import com.offcn.search.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.pojo.TbGoods;


import com.offcn.entity.PageResult;
import com.offcn.entity.Result;

import javax.jms.*;

/**
 * 商品controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;

/*	@Reference
	private ItemSearchService itemSearchService;*/

	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
	private Destination queueSolrDestination;

	@Autowired
	private Destination queueSolrDestinationDelete;

	@Autowired
    private Destination topicPageDestination;

	@Autowired
	private Destination topicPageDestinationDelete;


	@Reference
	private ItemService itemService;

/*	@Reference(timeout = 30000)//设置超时时间
	private ItemPageService itemPageService;*/
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
/*	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.updateDelete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}*/

	@RequestMapping("/delete")
	public Result delete(final Long [] ids){
		try {
			goodsService.updateDelete(ids);
			//把所有的sku的状态置0
			List<TbItem> itemList = itemService.findByGoodsId(ids);
			for(TbItem item:itemList){
				item.setStatus("0");
				itemService.update(item);
			}
			//itemSearchService.deleteData(ids);
			jmsTemplate.send(queueSolrDestinationDelete, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});
			for(final Long id:ids){
				jmsTemplate.send(topicPageDestinationDelete, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						return session.createTextMessage(String.valueOf(id));
					}
				});
			}
			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param goods
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){

		return goodsService.findPage(goods, page, rows);		
	}

	@RequestMapping("/updateStatus")
	public Result updateStatus(Long[] ids,String status){

		try {
			goodsService.updateStatus(ids,status);

			//如果更新状态为审核通过添加所有商品到solr库
			if("1".equals(status)){
				final List<TbItem> itemList = itemService.findByGoodsId(ids);
				if(itemList!=null&&itemList.size()>0){
					//itemSearchService.importData(itemList);
					jmsTemplate.send(queueSolrDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(JSON.toJSONString(itemList));
						}
					});

				}
				//生成页面
				for(final Long id:ids){
/*					boolean b = itemPageService.genHtml(id);
					if(b){
						System.out.println(id+"页面生成成功");
					}else {
						System.out.println(id+"页面生成失败");
					}*/
                    jmsTemplate.send(topicPageDestination, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            return session.createTextMessage(String.valueOf(id));
                        }
                    });
				}
			}
			return new Result(true, "更新状态成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "更新状态失败");
		}
	}


	
}
