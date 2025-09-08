package com.payment.common.util;

import com.payment.common.constant.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 业务ID生成器
 * 生成32位业务ID，包含业务语义信息
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class BusinessIdGenerator {

    /**
     * 序列号生成器，每个业务类型独立维护
     */
    private final AtomicLong userSequence = new AtomicLong(0);
    private final AtomicLong merchantSequence = new AtomicLong(0);
    private final AtomicLong accountSequence = new AtomicLong(0);
    private final AtomicLong tradeOrderSequence = new AtomicLong(0);
    private final AtomicLong paymentSequence = new AtomicLong(0);
    private final AtomicLong channelSequence = new AtomicLong(0);

    /**
     * 业务ID生成请求
     */
    public static class BusinessIdRequest {
        private String systemCode;
        private String businessCode;
        private String dataVersion = CommonConstants.DATA_VERSION;
        private String systemVersion = CommonConstants.SYSTEM_VERSION;
        private String envFlag = CommonConstants.ENV_TEST;
        private String siteId = "00";
        private String dbShard = "00";
        private String tableShard = "00";

        public BusinessIdRequest(String systemCode, String businessCode) {
            this.systemCode = systemCode;
            this.businessCode = businessCode;
        }

        // Getters and Setters
        public String getSystemCode() { return systemCode; }
        public void setSystemCode(String systemCode) { this.systemCode = systemCode; }
        public String getBusinessCode() { return businessCode; }
        public void setBusinessCode(String businessCode) { this.businessCode = businessCode; }
        public String getDataVersion() { return dataVersion; }
        public void setDataVersion(String dataVersion) { this.dataVersion = dataVersion; }
        public String getSystemVersion() { return systemVersion; }
        public void setSystemVersion(String systemVersion) { this.systemVersion = systemVersion; }
        public String getEnvFlag() { return envFlag; }
        public void setEnvFlag(String envFlag) { this.envFlag = envFlag; }
        public String getSiteId() { return siteId; }
        public void setSiteId(String siteId) { this.siteId = siteId; }
        public String getDbShard() { return dbShard; }
        public void setDbShard(String dbShard) { this.dbShard = dbShard; }
        public String getTableShard() { return tableShard; }
        public void setTableShard(String tableShard) { this.tableShard = tableShard; }
    }

    /**
     * 生成用户ID
     */
    public String generateUserId() {
        BusinessIdRequest request = new BusinessIdRequest(
                CommonConstants.SYSTEM_CODE_USER,
                CommonConstants.BUSINESS_CODE_USER_REGISTER
        );
        return generateBusinessId(request, userSequence);
    }

    /**
     * 生成商户ID
     */
    public String generateMerchantId() {
        BusinessIdRequest request = new BusinessIdRequest(
                CommonConstants.SYSTEM_CODE_MERCHANT,
                CommonConstants.BUSINESS_CODE_MERCHANT_REGISTER
        );
        return generateBusinessId(request, merchantSequence);
    }

    /**
     * 生成账户号
     */
    public String generateAccountNo() {
        BusinessIdRequest request = new BusinessIdRequest(
                CommonConstants.SYSTEM_CODE_ACCOUNT,
                CommonConstants.BUSINESS_CODE_ACCOUNT_CREATE
        );
        return generateBusinessId(request, accountSequence);
    }

    /**
     * 生成交易订单号
     */
    public String generateTradeOrderNo() {
        BusinessIdRequest request = new BusinessIdRequest(
                CommonConstants.SYSTEM_CODE_ACQUIRING,
                CommonConstants.BUSINESS_CODE_PAYMENT
        );
        return generateBusinessId(request, tradeOrderSequence);
    }

    /**
     * 生成支付单号
     */
    public String generatePaymentNo() {
        BusinessIdRequest request = new BusinessIdRequest(
                CommonConstants.SYSTEM_CODE_PAYMENT,
                CommonConstants.BUSINESS_CODE_PAYMENT
        );
        return generateBusinessId(request, paymentSequence);
    }

    /**
     * 生成渠道单号
     */
    public String generateChannelOrderNo() {
        BusinessIdRequest request = new BusinessIdRequest(
                CommonConstants.SYSTEM_CODE_CHANNEL,
                CommonConstants.BUSINESS_CODE_PAYMENT
        );
        return generateBusinessId(request, channelSequence);
    }

    /**
     * 简化的业务ID生成方法
     */
    public String generateBusinessId(String prefix) {
        return prefix + "_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }

    /**
     * 生成业务ID
     * 格式: YYYYMMDD + 数据版本(1位) + 系统版本(1位) + 系统标识(3位) + 业务标识(2位) + 
     *       机房位(2位) + 分库位(2位) + 分表位(2位) + 环境位(1位) + 预留位(2位) + 序列号(8位)
     */
    public String generateBusinessId(BusinessIdRequest request, AtomicLong sequence) {
        try {
            // 1. 日期 (8位)
            String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            
            // 2. 数据版本 (1位)
            String dataVersion = request.getDataVersion();
            
            // 3. 系统版本 (1位)
            String systemVersion = request.getSystemVersion();
            
            // 4. 系统标识 (3位)
            String systemCode = request.getSystemCode();
            
            // 5. 业务标识 (2位)
            String businessCode = request.getBusinessCode();
            
            // 6. 机房位 (2位)
            String siteId = request.getSiteId();
            
            // 7. 分库位 (2位)
            String dbShard = request.getDbShard();
            
            // 8. 分表位 (2位)
            String tableShard = request.getTableShard();
            
            // 9. 环境位 (1位)
            String envFlag = request.getEnvFlag();
            
            // 10. 预留位 (2位)
            String reserved = "00";
            
            // 11. 序列号 (8位)
            long seq = sequence.incrementAndGet() % 100000000;
            String sequenceStr = String.format("%08d", seq);
            
            // 组装业务ID
            StringBuilder businessId = new StringBuilder();
            businessId.append(date)
                    .append(dataVersion)
                    .append(systemVersion)
                    .append(systemCode)
                    .append(businessCode)
                    .append(siteId)
                    .append(dbShard)
                    .append(tableShard)
                    .append(envFlag)
                    .append(reserved)
                    .append(sequenceStr);
            
            String result = businessId.toString();
            log.debug("Generated business ID: {}", result);
            return result;
            
        } catch (Exception e) {
            log.error("Failed to generate business ID", e);
            throw new RuntimeException("业务ID生成失败", e);
        }
    }

    /**
     * 解析业务ID
     */
    public BusinessIdInfo parseBusinessId(String businessId) {
        if (businessId == null || businessId.length() != 32) {
            throw new IllegalArgumentException("Invalid business ID format");
        }
        
        try {
            BusinessIdInfo info = new BusinessIdInfo();
            info.setDate(businessId.substring(0, 8));
            info.setDataVersion(businessId.substring(8, 9));
            info.setSystemVersion(businessId.substring(9, 10));
            info.setSystemCode(businessId.substring(10, 13));
            info.setBusinessCode(businessId.substring(13, 15));
            info.setSiteId(businessId.substring(15, 17));
            info.setDbShard(businessId.substring(17, 19));
            info.setTableShard(businessId.substring(19, 21));
            info.setEnvFlag(businessId.substring(21, 22));
            info.setReserved(businessId.substring(22, 24));
            info.setSequence(businessId.substring(24, 32));
            
            return info;
        } catch (Exception e) {
            log.error("Failed to parse business ID: {}", businessId, e);
            throw new RuntimeException("业务ID解析失败", e);
        }
    }

    /**
     * 业务ID信息
     */
    public static class BusinessIdInfo {
        private String date;
        private String dataVersion;
        private String systemVersion;
        private String systemCode;
        private String businessCode;
        private String siteId;
        private String dbShard;
        private String tableShard;
        private String envFlag;
        private String reserved;
        private String sequence;

        // Getters and Setters
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public String getDataVersion() { return dataVersion; }
        public void setDataVersion(String dataVersion) { this.dataVersion = dataVersion; }
        public String getSystemVersion() { return systemVersion; }
        public void setSystemVersion(String systemVersion) { this.systemVersion = systemVersion; }
        public String getSystemCode() { return systemCode; }
        public void setSystemCode(String systemCode) { this.systemCode = systemCode; }
        public String getBusinessCode() { return businessCode; }
        public void setBusinessCode(String businessCode) { this.businessCode = businessCode; }
        public String getSiteId() { return siteId; }
        public void setSiteId(String siteId) { this.siteId = siteId; }
        public String getDbShard() { return dbShard; }
        public void setDbShard(String dbShard) { this.dbShard = dbShard; }
        public String getTableShard() { return tableShard; }
        public void setTableShard(String tableShard) { this.tableShard = tableShard; }
        public String getEnvFlag() { return envFlag; }
        public void setEnvFlag(String envFlag) { this.envFlag = envFlag; }
        public String getReserved() { return reserved; }
        public void setReserved(String reserved) { this.reserved = reserved; }
        public String getSequence() { return sequence; }
        public void setSequence(String sequence) { this.sequence = sequence; }

        @Override
        public String toString() {
            return "BusinessIdInfo{" +
                    "date='" + date + '\'' +
                    ", dataVersion='" + dataVersion + '\'' +
                    ", systemVersion='" + systemVersion + '\'' +
                    ", systemCode='" + systemCode + '\'' +
                    ", businessCode='" + businessCode + '\'' +
                    ", siteId='" + siteId + '\'' +
                    ", dbShard='" + dbShard + '\'' +
                    ", tableShard='" + tableShard + '\'' +
                    ", envFlag='" + envFlag + '\'' +
                    ", reserved='" + reserved + '\'' +
                    ", sequence='" + sequence + '\'' +
                    '}';
        }
    }
}
