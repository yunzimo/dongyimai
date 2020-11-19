package com.offcn.shop.controller;
import java.util.List;

import com.offcn.entity.Goods;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.GoodsService;
import com.offcn.search.service.ItemSearchService;
import com.offcn.search.service.ItemService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.pojo.TbGoods;


import com.offcn.entity.PageResult;
import com.offcn.entity.Result;

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
	@Reference
	private ItemSearchService itemSearchService;

	@Reference
	private ItemService itemService;
	
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
			goods.getGoods().setAuditStatus("0");//未审核
			String name = SecurityContextHolder.getContext().getAuthentication().getName();
			goods.getGoods().setSellerId(name);
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
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.updateDelete(ids);
			//把所有的sku的状态置0
			List<TbItem> itemList = itemService.findByGoodsId(ids);
			for(TbItem item:itemList){
				item.setStatus("0");
				itemService.update(item);
			}
			itemSearchService.deleteData(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页+根据登录的id返回自己的商品列表
	 * @param goods
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		goods.setSellerId(SecurityContextHolder.getContext().getAuthentication().getName());
		return goodsService.findPage(goods, page, rows);		
	}

	@RequestMapping("/updateMarket")
	public Result updateMarket(Long[] ids,String market){
		try {
			if(market.equals("undefined")){
				market=null;
			}
			goodsService.updateMarket(ids,market);
/*			//market==null是下架，反之是上架
			if(market==null){
				itemSearchService.deleteData(ids);
			}else{
				itemSearchService.importData(itemService.findByGoodsId(ids));
			}*/
			return new Result(true, "状态更新成功成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "状态更新失败");
		}
	}
	
}
