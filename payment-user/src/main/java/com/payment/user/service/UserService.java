package com.payment.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.payment.user.dto.UserRegisterRequest;
import com.payment.user.dto.UserResponse;
import com.payment.user.entity.User;

/**
 * 用户服务接口
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
public interface UserService {

    /**
     * 用户注册
     */
    UserResponse register(UserRegisterRequest request);

    /**
     * 根据用户ID查询用户
     */
    UserResponse getUserById(String userId);

    /**
     * 根据用户名查询用户
     */
    UserResponse getUserByUsername(String username);

    /**
     * 根据手机号查询用户
     */
    UserResponse getUserByPhone(String phone);

    /**
     * 根据邮箱查询用户
     */
    UserResponse getUserByEmail(String email);

    /**
     * 分页查询用户
     */
    Page<UserResponse> getUserPage(int pageNum, int pageSize);

    /**
     * 更新用户信息
     */
    UserResponse updateUser(String userId, UserRegisterRequest request);

    /**
     * 删除用户
     */
    boolean deleteUser(String userId);

    /**
     * 冻结用户
     */
    boolean freezeUser(String userId);

    /**
     * 解冻用户
     */
    boolean unfreezeUser(String userId);

    /**
     * 检查用户名是否存在
     */
    boolean isUsernameExists(String username);

    /**
     * 检查手机号是否存在
     */
    boolean isPhoneExists(String phone);

    /**
     * 检查邮箱是否存在
     */
    boolean isEmailExists(String email);

    /**
     * 验证用户密码
     */
    boolean validatePassword(String userId, String password);

    /**
     * 修改密码
     */
    boolean changePassword(String userId, String oldPassword, String newPassword);
}
