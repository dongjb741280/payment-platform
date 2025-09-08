package com.payment.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.payment.account.entity.AccountFlow;
import org.apache.ibatis.annotations.Mapper;

/**
 * 账户流水Mapper接口
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
@Mapper
public interface AccountFlowMapper extends BaseMapper<AccountFlow> {

}
