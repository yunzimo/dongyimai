package com.offcn.sellergoods.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import com.offcn.entity.PageResult;
import com.offcn.mapper.TbBrandMapper;
import com.offcn.pojo.TbBrand;
import com.offcn.pojo.TbBrandExample;
import com.offcn.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImp implements BrandService {

    @Autowired
    private TbBrandMapper tbBrandMapper;

    @Override
    public List<TbBrand> findAll() {
        List<TbBrand> tbBrands = tbBrandMapper.selectByExample(null);
        return tbBrands;
    }

    @Override
    public PageResult findPage(int pageNum, int pageSize,TbBrand brand) {
        PageHelper.startPage(pageNum,pageSize);
        TbBrandExample example=null;
        if(brand!=null){
            example=new TbBrandExample();
            TbBrandExample.Criteria criteria = example.createCriteria();
            if(brand.getName()!=null&&!brand.getName().trim().equals("")){
                criteria.andNameLike("%"+brand.getName()+"%");
            }
            if(brand.getFirstChar()!=null&&!brand.getFirstChar().trim().equals("")){
                criteria.andFirstCharEqualTo(brand.getFirstChar().trim());
            }
        }
        Page<TbBrand> page= (Page<TbBrand>) tbBrandMapper.selectByExample(example);
        PageResult pageResult=new PageResult(page.getTotal(),page.getResult());
        return pageResult;
    }

    @Override
    public boolean insertBrand(TbBrand brand) {
        int insert = tbBrandMapper.insert(brand);
        return insert>0;
    }

    @Override
    public TbBrand findOne(long id) {
        return tbBrandMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean updateBrand(TbBrand brand) {

        return tbBrandMapper.updateByPrimaryKey(brand)>0;
    }

    @Override
    public boolean deleteBrand(long id) {
        int i = tbBrandMapper.deleteByPrimaryKey(id);
        return i>0;
    }

    @Override
    public List<Map<String, Object>> findBrandOptions() {
        List<Map<String, Object>> brandOptions = tbBrandMapper.findBrandOptions();
        return brandOptions;
    }


}
