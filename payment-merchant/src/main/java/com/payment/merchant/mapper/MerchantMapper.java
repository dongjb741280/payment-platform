package com.payment.merchant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.payment.merchant.entity.Merchant;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商户Mapper接口
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
@Mapper
public interface MerchantMapper extends BaseMapper<Merchant> {

}
