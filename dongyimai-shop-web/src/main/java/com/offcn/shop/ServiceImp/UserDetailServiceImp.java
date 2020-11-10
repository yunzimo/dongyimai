package com.offcn.shop.ServiceImp;

import com.offcn.pojo.TbSeller;
import com.offcn.service.SellerService;
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
