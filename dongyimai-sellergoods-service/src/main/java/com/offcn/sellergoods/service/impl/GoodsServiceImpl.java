package com.offcn.sellergoods.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.offcn.entity.Goods;
import com.offcn.mapper.*;
import com.offcn.pojo.*;
import com.offcn.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.pojo.TbGoodsExample.Criteria;


import com.offcn.entity.PageResult;

/**
 * 商品服务实现层
 *
 * @author Administrator
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
	private TbItemMapper itemMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;


    @Autowired
	private TbBrandMapper brandMapper;

    @Autowired
	private TbSellerMapper sellerMapper;

    @Autowired
	private TbItemCatMapper itemCatMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(Goods goods) {
        goodsMapper.insert(goods.getGoods());
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
        goodsDescMapper.insert(goods.getGoodsDesc());

		System.out.println("Goods表的属性======"+goods.getGoods());
		System.out.println("GoodsDesc表的属性======="+goods.getGoodsDesc());

        //判断是否启用规格,设置固有属性，并通过setItem函数设置通用属性
        if("1".equals(goods.getGoods().getIsEnableSpec())){
			List<TbItem> itemList = goods.getItemList();
			for(TbItem item:itemList){
				Map map = JSON.parseObject(item.getSpec(), Map.class);
				String title=goods.getGoods().getGoodsName();
				for(Object key:map.keySet()){
					title+=" "+map.get(key);
				}
				item.setTitle(title);
				item=setItem(item,goods);
				//System.out.println("==========选择规格的"+item);
				itemMapper.insert(item);
			}

		}else {
        	TbItem item=new TbItem();
        	item.setTitle(goods.getGoods().getGoodsName());
        	item.setStatus("1");
        	item.setIsDefault("0");
        	item.setPrice(new BigDecimal(0));
        	item.setNum(999);
        	item=setItem(item,goods);
			//System.out.println("========未选择规格的"+item);
        	itemMapper.insert(item);
		}
    }

    public TbItem setItem(TbItem item,Goods goods){
    	item.setCreateTime(new Date());
    	item.setUpdateTime(new Date());
    	item.setGoodsId(goods.getGoods().getId());
    	item.setCategoryid(goods.getGoods().getCategory3Id());
    	item.setSellerId(goods.getGoods().getSellerId());
    	//brand
		TbBrand tbBrand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
		item.setBrand(tbBrand.getName());

		//seller
		TbSeller tbSeller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
		item.setSeller(tbSeller.getName());

		//category
		TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
		item.setCategory(tbItemCat.getName());

		//image
		List<Map> maps = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
		if(maps!=null&&maps.size()>0){
			item.setImage((String) maps.get(0).get("url"));
		}
		return item;
	}


    /**
     * 修改
     */
    @Override
    public void update(TbGoods goods) {
        goodsMapper.updateByPrimaryKey(goods);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Goods findOne(Long id) {
        Goods goods=new Goods();
        goods.setGoods(goodsMapper.selectByPrimaryKey(id));
        goods.setGoodsDesc(goodsDescMapper.selectByPrimaryKey(id));
        TbItemExample example=new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<TbItem> itemList = itemMapper.selectByExample(example);
        goods.setItemList(itemList);
        System.out.println("findOne============"+goods);
        return goods;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            goodsMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();

        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                //criteria.andSellerIdLike("%" + goods.getSellerId() + "%");
				criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                //criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
				criteria.andAuditStatusEqualTo(goods.getAuditStatus());
            }
        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

}
