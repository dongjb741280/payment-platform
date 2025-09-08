package com.payment.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.payment.common.result.Result;
import com.payment.user.dto.UserRegisterRequest;
import com.payment.user.dto.UserResponse;
import com.payment.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 用户控制器
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
@Api(tags = "用户管理")
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    /**
     * 用户注册
     */
    @ApiOperation(value = "用户注册", notes = "注册新用户")
    @PostMapping("/register")
    public Result<UserResponse> register(@ApiParam(value = "用户注册请求", required = true) @Valid @RequestBody UserRegisterRequest request) {
        log.info("User register request: {}", request.getUsername());
        UserResponse response = userService.register(request);
        return Result.success("注册成功", response);
    }

    /**
     * 根据用户ID查询用户
     */
    @ApiOperation(value = "根据用户ID查询用户", notes = "通过用户ID获取用户详细信息")
    @GetMapping("/{userId}")
    public Result<UserResponse> getUserById(@ApiParam(value = "用户ID", required = true) @PathVariable @NotBlank String userId) {
        UserResponse response = userService.getUserById(userId);
        return Result.success(response);
    }

    /**
     * 根据用户名查询用户
     */
    @ApiOperation(value = "根据用户名查询用户", notes = "通过用户名获取用户详细信息")
    @GetMapping("/username/{username}")
    public Result<UserResponse> getUserByUsername(@ApiParam(value = "用户名", required = true) @PathVariable @NotBlank String username) {
        UserResponse response = userService.getUserByUsername(username);
        return Result.success(response);
    }

    /**
     * 根据手机号查询用户
     */
    @ApiOperation(value = "根据手机号查询用户", notes = "通过手机号获取用户详细信息")
    @GetMapping("/phone/{phone}")
    public Result<UserResponse> getUserByPhone(@ApiParam(value = "手机号", required = true) @PathVariable @NotBlank String phone) {
        UserResponse response = userService.getUserByPhone(phone);
        return Result.success(response);
    }

    /**
     * 根据邮箱查询用户
     */
    @ApiOperation(value = "根据邮箱查询用户", notes = "通过邮箱获取用户详细信息")
    @GetMapping("/email/{email}")
    public Result<UserResponse> getUserByEmail(@ApiParam(value = "邮箱", required = true) @PathVariable @NotBlank String email) {
        UserResponse response = userService.getUserByEmail(email);
        return Result.success(response);
    }

    /**
     * 分页查询用户
     */
    @ApiOperation(value = "分页查询用户", notes = "分页获取用户列表")
    @GetMapping("/page")
    public Result<Page<UserResponse>> getUserPage(
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") int pageNum,
            @ApiParam(value = "每页大小", defaultValue = "20") @RequestParam(defaultValue = "20") int pageSize) {
        Page<UserResponse> response = userService.getUserPage(pageNum, pageSize);
        return Result.success(response);
    }

    /**
     * 更新用户信息
     */
    @ApiOperation(value = "更新用户信息", notes = "更新指定用户的信息")
    @PutMapping("/{userId}")
    public Result<UserResponse> updateUser(
            @ApiParam(value = "用户ID", required = true) @PathVariable @NotBlank String userId,
            @ApiParam(value = "用户更新请求", required = true) @Valid @RequestBody UserRegisterRequest request) {
        UserResponse response = userService.updateUser(userId, request);
        return Result.success("更新成功", response);
    }

    /**
     * 删除用户
     */
    @ApiOperation(value = "删除用户", notes = "删除指定用户")
    @DeleteMapping("/{userId}")
    public Result<Boolean> deleteUser(@ApiParam(value = "用户ID", required = true) @PathVariable @NotBlank String userId) {
        boolean result = userService.deleteUser(userId);
        return Result.success("删除成功", result);
    }

    /**
     * 冻结用户
     */
    @ApiOperation(value = "冻结用户", notes = "冻结指定用户账户")
    @PutMapping("/{userId}/freeze")
    public Result<Boolean> freezeUser(@ApiParam(value = "用户ID", required = true) @PathVariable @NotBlank String userId) {
        boolean result = userService.freezeUser(userId);
        return Result.success("冻结成功", result);
    }

    /**
     * 解冻用户
     */
    @ApiOperation(value = "解冻用户", notes = "解冻指定用户账户")
    @PutMapping("/{userId}/unfreeze")
    public Result<Boolean> unfreezeUser(@ApiParam(value = "用户ID", required = true) @PathVariable @NotBlank String userId) {
        boolean result = userService.unfreezeUser(userId);
        return Result.success("解冻成功", result);
    }

    /**
     * 检查用户名是否存在
     */
    @ApiOperation(value = "检查用户名是否存在", notes = "检查指定用户名是否已被使用")
    @GetMapping("/check/username/{username}")
    public Result<Boolean> checkUsername(@ApiParam(value = "用户名", required = true) @PathVariable @NotBlank String username) {
        boolean exists = userService.isUsernameExists(username);
        return Result.success(exists);
    }

    /**
     * 检查手机号是否存在
     */
    @ApiOperation(value = "检查手机号是否存在", notes = "检查指定手机号是否已被使用")
    @GetMapping("/check/phone/{phone}")
    public Result<Boolean> checkPhone(@ApiParam(value = "手机号", required = true) @PathVariable @NotBlank String phone) {
        boolean exists = userService.isPhoneExists(phone);
        return Result.success(exists);
    }

    /**
     * 检查邮箱是否存在
     */
    @ApiOperation(value = "检查邮箱是否存在", notes = "检查指定邮箱是否已被使用")
    @GetMapping("/check/email/{email}")
    public Result<Boolean> checkEmail(@ApiParam(value = "邮箱", required = true) @PathVariable @NotBlank String email) {
        boolean exists = userService.isEmailExists(email);
        return Result.success(exists);
    }

    /**
     * 修改密码
     */
    @ApiOperation(value = "修改密码", notes = "修改指定用户的密码")
    @PutMapping("/{userId}/password")
    public Result<Boolean> changePassword(
            @ApiParam(value = "用户ID", required = true) @PathVariable @NotBlank String userId,
            @ApiParam(value = "旧密码", required = true) @RequestParam @NotBlank String oldPassword,
            @ApiParam(value = "新密码", required = true) @RequestParam @NotBlank String newPassword) {
        boolean result = userService.changePassword(userId, oldPassword, newPassword);
        return Result.success("密码修改成功", result);
    }
}
