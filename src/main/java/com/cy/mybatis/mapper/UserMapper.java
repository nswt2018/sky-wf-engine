package com.cy.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import com.cy.mybatis.beans.UserBean;

@Mapper
public interface UserMapper {
    /**
     * �����Ñ�
     * @param user
     * @return
     * @throws Exception
     */
    public int insertUser(UserBean user) throws Exception;
    /**
     * �޸��Ñ�
     * @param user
     * @param id
     * @return
     * @throws Exception
     */
    public int updateUser(@Param("user")UserBean user, @Param("id")int id) throws Exception;
     /**
      * �h���Ñ�
      * @param id
      * @return
      * @throws Exception
      */
    public int deleteUser(int id) throws Exception;
    /**
     * ����id��ѯ�û���Ϣ
     * @param id
     * @return
     * @throws Exception
     */
    public UserBean selectUserById(int id) throws Exception;
     /**
      * ��ѯ���е��û���Ϣ
      * @return
      * @throws Exception
      */
    public List<UserBean> selectAllUser() throws Exception;
    
    @Update("update t_user set username=#{user.username},password=#{user.password},account=#{user.account} where id=#{id}")
    public int updateUser1(@Param("user")UserBean user, @Param("id")int id) throws Exception;
}