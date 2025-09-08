package com.payment.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.payment.account.entity.AccountBalance;
import org.apache.ibatis.annotations.Mapper;

/**
 * 账户余额Mapper接口
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
@Mapper
public interface AccountBalanceMapper extends BaseMapper<AccountBalance> {

}
