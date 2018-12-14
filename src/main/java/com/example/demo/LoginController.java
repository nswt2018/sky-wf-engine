package com.example.demo;

import org.apache.ibatis.session.SqlSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cy.mybatis.beans.UserBean;
import com.cy.mybatis.mapper.UserMapper;
import com.cy.mybatis.tools.DBTools;

@RestController
public class LoginController {
	
	@GetMapping("/sayHello")
    public String login() {
		SqlSession session=DBTools.getSession();
        UserMapper mapper=session.getMapper(UserMapper.class);
        try {
	        UserBean user=mapper.selectUserById(2);
	        System.out.println(user.toString());
	        session.commit();
	        return user.toString();
        } catch (Exception e) {
            e.printStackTrace();
            session.rollback();
        }
        return "hello world!!!";
    }
}
