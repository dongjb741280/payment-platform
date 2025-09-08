package com.payment.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.payment.common.constant.CommonConstants;
import com.payment.common.util.BusinessIdGenerator;
import com.payment.user.dto.UserRegisterRequest;
import com.payment.user.dto.UserResponse;
import com.payment.user.entity.User;
import com.payment.user.mapper.UserMapper;
import com.payment.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final BusinessIdGenerator businessIdGenerator;
    private final MessageSource messageSource;

    @Override
    public UserResponse register(UserRegisterRequest request) {
        log.info("用户注册开始，用户名：{}", request.getUsername());
        
        // 检查用户名是否已存在
        if (isUsernameExists(request.getUsername())) {
            throw new RuntimeException(messageSource.getMessage("user.username.exists", null, LocaleContextHolder.getLocale()));
        }
        
        // 检查手机号是否已存在（如果提供了手机号）
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty() && isPhoneExists(request.getPhone())) {
            throw new RuntimeException(messageSource.getMessage("user.phone.exists", null, LocaleContextHolder.getLocale()));
        }
        
        // 检查邮箱是否已存在（如果提供了邮箱）
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty() && isEmailExists(request.getEmail())) {
            throw new RuntimeException(messageSource.getMessage("user.email.exists", null, LocaleContextHolder.getLocale()));
        }
        
        // 创建用户实体
        User user = new User();
        user.setUserId(businessIdGenerator.generateUserId());
        user.setUsername(request.getUsername());
        user.setPassword(DigestUtils.md5DigestAsHex(request.getPassword().getBytes()));
        user.setSalt("default_salt"); // 设置默认盐值
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(CommonConstants.STATUS_ACTIVE);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        // 保存用户
        userMapper.insert(user);
        
        log.info("用户注册成功，用户ID：{}", user.getUserId());
        
        // 返回用户信息
        return convertToResponse(user);
    }

    @Override
    public UserResponse getUserById(String userId) {
        log.info("根据用户ID查询用户：{}", userId);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserId, userId);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new RuntimeException(messageSource.getMessage("user.not.exist", null, LocaleContextHolder.getLocale()));
        }
        
        return convertToResponse(user);
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        log.info("根据用户名查询用户：{}", username);
        
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new RuntimeException(messageSource.getMessage("user.not.exist", null, LocaleContextHolder.getLocale()));
        }
        
        return convertToResponse(user);
    }

    @Override
    public UserResponse getUserByPhone(String phone) {
        log.info("根据手机号查询用户：{}", phone);
        
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, phone);
        
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new RuntimeException(messageSource.getMessage("user.not.exist", null, LocaleContextHolder.getLocale()));
        }
        
        return convertToResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        log.info("根据邮箱查询用户：{}", email);
        
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);
        
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new RuntimeException(messageSource.getMessage("user.not.exist", null, LocaleContextHolder.getLocale()));
        }
        
        return convertToResponse(user);
    }

    @Override
    public Page<UserResponse> getUserPage(int pageNum, int pageSize) {
        log.info("分页查询用户，页码：{}，页大小：{}", pageNum, pageSize);
        
        Page<User> page = new Page<>(pageNum, pageSize);
        Page<User> userPage = userMapper.selectPage(page, null);
        
        // 转换为响应对象
        Page<UserResponse> responsePage = new Page<>();
        BeanUtils.copyProperties(userPage, responsePage);
        responsePage.setRecords(userPage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(java.util.stream.Collectors.toList()));
        
        return responsePage;
    }

    @Override
    public UserResponse updateUser(String userId, UserRegisterRequest request) {
        log.info("更新用户信息，用户ID：{}", userId);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserId, userId);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new RuntimeException(messageSource.getMessage("user.not.exist", null, LocaleContextHolder.getLocale()));
        }
        
        // 更新用户信息
        if (request.getRealName() != null) {
            user.setRealName(request.getRealName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        user.setUpdateTime(LocalDateTime.now());
        
        userMapper.updateById(user);
        
        log.info("用户信息更新成功，用户ID：{}", userId);
        
        return convertToResponse(user);
    }

    @Override
    public boolean deleteUser(String userId) {
        log.info("删除用户，用户ID：{}", userId);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserId, userId);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        int result = userMapper.deleteById(user.getId());
        
        log.info("用户删除结果：{}", result > 0 ? "成功" : "失败");
        
        return result > 0;
    }

    @Override
    public boolean freezeUser(String userId) {
        log.info("冻结用户，用户ID：{}", userId);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserId, userId);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        user.setStatus(CommonConstants.STATUS_FROZEN);
        user.setUpdateTime(LocalDateTime.now());
        
        int result = userMapper.updateById(user);
        
        log.info("用户冻结结果：{}", result > 0 ? "成功" : "失败");
        
        return result > 0;
    }

    @Override
    public boolean unfreezeUser(String userId) {
        log.info("解冻用户，用户ID：{}", userId);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserId, userId);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        user.setStatus(CommonConstants.STATUS_ACTIVE);
        user.setUpdateTime(LocalDateTime.now());
        
        int result = userMapper.updateById(user);
        
        log.info("用户解冻结果：{}", result > 0 ? "成功" : "失败");
        
        return result > 0;
    }

    @Override
    public boolean isUsernameExists(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        
        return userMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean isPhoneExists(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, phone);
        
        return userMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean isEmailExists(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);
        
        return userMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean validatePassword(String userId, String password) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserId, userId);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            return false;
        }
        
        String encryptedPassword = DigestUtils.md5DigestAsHex(password.getBytes());
        return encryptedPassword.equals(user.getPassword());
    }

    @Override
    public boolean changePassword(String userId, String oldPassword, String newPassword) {
        log.info("修改密码，用户ID：{}", userId);
        
        // 验证旧密码
        if (!validatePassword(userId, oldPassword)) {
            throw new RuntimeException(messageSource.getMessage("user.password.old.incorrect", null, LocaleContextHolder.getLocale()));
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserId, userId);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new RuntimeException(messageSource.getMessage("user.not.exist", null, LocaleContextHolder.getLocale()));
        }
        
        // 更新密码
        user.setPassword(DigestUtils.md5DigestAsHex(newPassword.getBytes()));
        user.setUpdateTime(LocalDateTime.now());
        
        int result = userMapper.updateById(user);
        
        log.info("密码修改结果：{}", result > 0 ? "成功" : "失败");
        
        return result > 0;
    }

    /**
     * 转换为响应对象
     */
    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        // 不返回密码，UserResponse中没有password字段
        return response;
    }
}