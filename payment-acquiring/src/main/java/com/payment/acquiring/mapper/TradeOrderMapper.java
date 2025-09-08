package com.payment.acquiring.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.payment.acquiring.entity.TradeOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TradeOrderMapper extends BaseMapper<TradeOrder> {
    // 这里保留接口以便需要自定义SQL时扩展
}


