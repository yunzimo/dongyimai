package com.offcn.sellergoods.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.entity.PageResult;
import com.offcn.entity.Result;
import com.offcn.pojo.TbBrand;
import com.offcn.search.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    @RequestMapping("/findAll")
    public List<TbBrand> findAll(){
        List<TbBrand> all = brandService.findAll();
        System.out.println("得到的all=========="+all);
        return all;
    }

    @RequestMapping("/findPage")
    public PageResult findPage(int pageNum, int pageSize,@RequestBody TbBrand brand){
        PageResult page = brandService.findPage(pageNum, pageSize,brand);
        System.out.println("获得的pageResult对象为：========"+page);
        return page;
    }

    @RequestMapping("/addBrand")
    public Result addBrand(@RequestBody TbBrand brand){
        boolean b = brandService.insertBrand(brand);
        if(b){
            return new Result(true,"插入成功");
        }else{
            return new Result(false,"插入失败");
        }
    }

    @RequestMapping("/findOne")
    public TbBrand findOne(long id){
        return brandService.findOne(id);
    }

    @RequestMapping("/updateBrand")
    public Result updateBrand(@RequestBody TbBrand brand){
        boolean b = brandService.updateBrand(brand);
        if(b){
            return new Result(true,"更新成功");
        }else{
            return new Result(false,"更新失败");
        }

    }

    @RequestMapping("/delete")
    public Result delete(int[] ids){
        for(int id:ids){
            boolean b = brandService.deleteBrand(id);
            if(!b){
                return new Result(false,"批量删除错误");
            }
        }
        return new Result(true,"批量删除成功");
    }

    @RequestMapping("findBrandOptions")
    public List<Map<String,Object>> findBrandOptions(){
        return brandService.findBrandOptions();
    }
}
