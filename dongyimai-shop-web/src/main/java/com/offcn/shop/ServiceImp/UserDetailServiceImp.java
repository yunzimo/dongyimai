package com.offcn.shop.ServiceImp;

import com.offcn.pojo.TbSeller;
import com.offcn.search.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;




import java.util.ArrayList;
import java.util.List;


public class UserDetailServiceImp implements UserDetailsService {


    private SellerService sellerService;

    // sellerService是可以用@reference注解自动导入的，只需要在spring-sercurity配置文件里配置dubbo扫描就行了，
    // 注意这里的dubbo扫描不可以设置在springmvc配置文件中，在web.xml文件中springmvc配置文件的引入时机是在dispatcherServlet触发的时候
    // 当直接访问网页的时候会因为读取不到配置而导致验证的时候的空指针

    public SellerService getSellerService() {
        return sellerService;
    }

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<GrantedAuthority> list=new ArrayList<>();
        list.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        TbSeller one = sellerService.findOne(username);

        if(one!=null){
            //状态判断 0:未审核，1:审核通过，2:未通过，3:关闭商家
            if(one.getStatus().equals("1")){
                return new User(username,one.getPassword(),list);
            }
        }
        return null;
    }
}
