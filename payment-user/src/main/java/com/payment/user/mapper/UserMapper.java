package com.payment.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.payment.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户Mapper接口
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM t_user WHERE username = #{username} AND deleted = 0")
    User selectByUsername(@Param("username") String username);

    /**
     * 根据手机号查询用户
     */
    @Select("SELECT * FROM t_user WHERE phone = #{phone} AND deleted = 0")
    User selectByPhone(@Param("phone") String phone);

    /**
     * 根据邮箱查询用户
     */
    @Select("SELECT * FROM t_user WHERE email = #{email} AND deleted = 0")
    User selectByEmail(@Param("email") String email);

    /**
     * 根据用户ID查询用户
     */
    @Select("SELECT * FROM t_user WHERE user_id = #{userId} AND deleted = 0")
    User selectByUserId(@Param("userId") String userId);

    /**
     * 检查用户名是否存在
     */
    @Select("SELECT COUNT(1) FROM t_user WHERE username = #{username} AND deleted = 0")
    int countByUsername(@Param("username") String username);

    /**
     * 检查手机号是否存在
     */
    @Select("SELECT COUNT(1) FROM t_user WHERE phone = #{phone} AND deleted = 0")
    int countByPhone(@Param("phone") String phone);

    /**
     * 检查邮箱是否存在
     */
    @Select("SELECT COUNT(1) FROM t_user WHERE email = #{email} AND deleted = 0")
    int countByEmail(@Param("email") String email);
}
