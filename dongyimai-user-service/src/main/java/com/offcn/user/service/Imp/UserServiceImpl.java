package com.offcn.user.service.Imp;
import java.util.List;
import java.util.Random;

import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.mapper.TbUserMapper;
import com.offcn.pojo.TbUser;
import com.offcn.pojo.TbUserExample;
import com.offcn.pojo.TbUserExample.Criteria;
import com.offcn.user.service.UserService;

import com.offcn.entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;

/**
 * 用户表服务实现层
 * @author Administrator
 *
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private TbUserMapper userMapper;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination smsDestination;

    @Autowired
    private RedisTemplate redisTemplate;

    /*
     * 发送验证码短信
     */

    @Override
    public void sendSms(final String mobile) {
        //生成6位验证码 Math.random() [0,1) 100000 999999
        //0~~899999  100000~9999999
        //int code = (int)((Math.random() * 900000) + 100000);
        final int code = new Random().nextInt(1000000);

        //保存数据到redis中用作验证用
        redisTemplate.boundHashOps("smscode").put(mobile,String.valueOf(code));

        //jms发送消息传输手机号和code
        jmsTemplate.send(smsDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("mobile",mobile);
                mapMessage.setString("code",String.valueOf(code));
                return mapMessage;
            }
        });

    }

    @Override
    public boolean checkCode(String mobile, String code) {
        String smscode = (String) redisTemplate.boundHashOps("smscode").get(mobile);
        if(smscode.equals(code)){
            return true;
        }
        return false;
    }

    /**
     * 查询全部
     */
    @Override
    public List<TbUser> findAll() {
        return userMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbUser> page=   (Page<TbUser>) userMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbUser user) {
        userMapper.insert(user);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbUser user){
        userMapper.updateByPrimaryKey(user);
    }

    /**
     * 根据ID获取实体
     * @param id
     * @return
     */
    @Override
    public TbUser findOne(Long id){
        return userMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for(Long id:ids){
            userMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbUser user, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbUserExample example=new TbUserExample();
        Criteria criteria = example.createCriteria();

        if(user!=null){
            if(user.getUsername()!=null && user.getUsername().length()>0){
                criteria.andUsernameLike("%"+user.getUsername()+"%");
            }			if(user.getPassword()!=null && user.getPassword().length()>0){
                criteria.andPasswordLike("%"+user.getPassword()+"%");
            }			if(user.getPhone()!=null && user.getPhone().length()>0){
                criteria.andPhoneLike("%"+user.getPhone()+"%");
            }			if(user.getEmail()!=null && user.getEmail().length()>0){
                criteria.andEmailLike("%"+user.getEmail()+"%");
            }			if(user.getSourceType()!=null && user.getSourceType().length()>0){
                criteria.andSourceTypeLike("%"+user.getSourceType()+"%");
            }			if(user.getNickName()!=null && user.getNickName().length()>0){
                criteria.andNickNameLike("%"+user.getNickName()+"%");
            }			if(user.getName()!=null && user.getName().length()>0){
                criteria.andNameLike("%"+user.getName()+"%");
            }			if(user.getStatus()!=null && user.getStatus().length()>0){
                criteria.andStatusLike("%"+user.getStatus()+"%");
            }			if(user.getHeadPic()!=null && user.getHeadPic().length()>0){
                criteria.andHeadPicLike("%"+user.getHeadPic()+"%");
            }			if(user.getQq()!=null && user.getQq().length()>0){
                criteria.andQqLike("%"+user.getQq()+"%");
            }			if(user.getIsMobileCheck()!=null && user.getIsMobileCheck().length()>0){
                criteria.andIsMobileCheckLike("%"+user.getIsMobileCheck()+"%");
            }			if(user.getIsEmailCheck()!=null && user.getIsEmailCheck().length()>0){
                criteria.andIsEmailCheckLike("%"+user.getIsEmailCheck()+"%");
            }			if(user.getSex()!=null && user.getSex().length()>0){
                criteria.andSexLike("%"+user.getSex()+"%");
            }
        }

        Page<TbUser> page= (Page<TbUser>)userMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

}
