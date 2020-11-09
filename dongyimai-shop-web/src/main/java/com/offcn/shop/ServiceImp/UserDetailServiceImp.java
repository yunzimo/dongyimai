package com.offcn.shop.ServiceImp;


import com.alibaba.dubbo.config.annotation.Reference;



import com.offcn.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;



import java.util.ArrayList;
import java.util.List;

@Component
public class UserDetailServiceImp implements UserDetailsService {

    @Reference
    private SellerService sellerService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        List<GrantedAuthority> list=new ArrayList<>();
        sellerService.findOne(s);
        System.out.println("================================");
        return null;
    }
}
