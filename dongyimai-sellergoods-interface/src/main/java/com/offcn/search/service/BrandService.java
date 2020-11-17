package com.offcn.search.service;

import com.offcn.entity.PageResult;
import com.offcn.pojo.TbBrand;

import java.util.List;
import java.util.Map;

public interface BrandService {
    public List<TbBrand> findAll();
    public PageResult findPage(int pageNum, int pageSize,TbBrand brand);
    public boolean insertBrand(TbBrand brand);
    public TbBrand findOne(long id);
    public boolean updateBrand(TbBrand brand);
    public boolean deleteBrand(long id);
    public List<Map<String,Object>> findBrandOptions();
}
