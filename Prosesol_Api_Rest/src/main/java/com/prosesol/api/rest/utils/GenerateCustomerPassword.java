package com.prosesol.api.rest.utils;

import com.prosesol.api.rest.models.dao.ICustomerKeyDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class GenerateCustomerPassword {

    @Autowired
    ICustomerKeyDao customerKeyDao;

    @Test
    public void generateCustomerPassword(){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String hashValue = "9b995744-15d4-442a-94f3-ae4402c4d81c";

        String password = "";

        for(int i = 0;i < 2; i++) {
            password = bCryptPasswordEncoder.encode(hashValue);
        }
        System.out.println(password);
    }
}
