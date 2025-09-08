package com.payment.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户响应DTO
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
@Data
public class UserResponse {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 身份证号（脱敏）
     */
    private String idCard;

    /**
     * 手机号（脱敏）
     */
    private String phone;

    /**
     * 邮箱（脱敏）
     */
    private String email;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 身份证号脱敏
     */
    public void setIdCard(String idCard) {
        if (idCard != null && idCard.length() >= 8) {
            this.idCard = idCard.substring(0, 4) + "****" + idCard.substring(idCard.length() - 4);
        } else {
            this.idCard = idCard;
        }
    }

    /**
     * 手机号脱敏
     */
    public void setPhone(String phone) {
        if (phone != null && phone.length() == 11) {
            this.phone = phone.substring(0, 3) + "****" + phone.substring(7);
        } else {
            this.phone = phone;
        }
    }

    /**
     * 邮箱脱敏
     */
    public void setEmail(String email) {
        if (email != null && email.contains("@")) {
            String[] parts = email.split("@");
            if (parts[0].length() > 2) {
                this.email = parts[0].substring(0, 2) + "***@" + parts[1];
            } else {
                this.email = email;
            }
        } else {
            this.email = email;
        }
    }
}
