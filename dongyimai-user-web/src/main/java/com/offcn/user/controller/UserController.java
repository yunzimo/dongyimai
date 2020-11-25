package com.offcn.user.controller;
import java.util.Date;
import java.util.List;

import com.offcn.user.service.UserService;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.pojo.TbUser;

import com.offcn.entity.PageResult;
import com.offcn.entity.Result;

@RestController
@RequestMapping("/user")
public class UserController {

	@Reference(timeout = 40000)
	private UserService userService;

	@RequestMapping("/sendSms")
	public Result sendSms(String mobile){
		try {
			userService.sendSms(mobile);
			return new Result(true,"验证码发送成功");
		}catch (Exception e){
			e.printStackTrace();
			return new Result(false,e.getMessage());
		}
	}


	/**
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbUser> findAll(){			
		return userService.findAll();
	}
	
	
	/**
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return userService.findPage(page, rows);
	}
	
	/**
	 * @param user
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(String smscode,@RequestBody TbUser user){

			String phone = user.getPhone();
			boolean b = userService.checkCode(phone, smscode);
			if(b){
				try{
					user.setCreated(new Date());
					user.setUpdated(new Date());

					//md5加密
					String password = DigestUtils.md5Hex(user.getPassword());
					user.setPassword(password);
					userService.add(user);
					return new Result(true,"注册成功");
				}catch (Exception e){
					e.printStackTrace();
					return new Result(false,"注册失败");
				}
			}else {
				return new Result(false,"验证码不匹配");
			}
	}
	
	/**
	 * @param user
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbUser user){
		try {
			userService.update(user);
			return new Result(true, "�޸ĳɹ�");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "�޸�ʧ��");
		}
	}	
	
	/**
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbUser findOne(Long id){
		return userService.findOne(id);		
	}
	
	/**
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			userService.delete(ids);
			return new Result(true, "ɾ���ɹ�"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "ɾ��ʧ��");
		}
	}
	
	/**
	 * @param user
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbUser user, int page, int rows  ){
		return userService.findPage(user, page, rows);		
	}
	
}
