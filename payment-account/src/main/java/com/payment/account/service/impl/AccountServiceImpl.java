package com.payment.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.payment.common.constant.CommonConstants;
import com.payment.common.exception.BusinessException;
import com.payment.common.constant.ErrorCodes;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import com.payment.common.util.BusinessIdGenerator;
import com.payment.account.dto.AccountCreateRequest;
import com.payment.account.dto.AccountResponse;
import com.payment.account.dto.BalanceOperationRequest;
import com.payment.account.entity.Account;
import com.payment.account.entity.AccountBalance;
import com.payment.account.entity.AccountFlow;
import com.payment.account.mapper.AccountBalanceMapper;
import com.payment.account.mapper.AccountFlowMapper;
import com.payment.account.mapper.AccountMapper;
import com.payment.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 账户服务实现类
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountMapper accountMapper;
    private final AccountBalanceMapper accountBalanceMapper;
    private final AccountFlowMapper accountFlowMapper;
    private final BusinessIdGenerator businessIdGenerator;
    private final MessageSource messageSource;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AccountResponse createAccount(AccountCreateRequest request) {
        log.info("开始创建账户，用户ID：{}，账户类型：{}", request.getUserId(), request.getAccountType());

        // 检查是否已存在相同类型的账户
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getUserId, request.getUserId())
                   .eq(Account::getAccountType, request.getAccountType())
                   .eq(Account::getCurrency, request.getCurrency())
                   .eq(Account::getDeleted, false);
        
        Account existingAccount = accountMapper.selectOne(queryWrapper);
        if (existingAccount != null) {
            throw new BusinessException(ErrorCodes.ACCOUNT_EXISTS, messageSource.getMessage("account.type.exists", null, LocaleContextHolder.getLocale()));
        }

        // 创建账户实体
        Account account = new Account();
        // 生成账户ID（32位业务ID）
        account.setAccountId(businessIdGenerator.generateAccountNo());
        account.setUserId(request.getUserId());
        account.setMerchantId(request.getMerchantId());
        account.setAccountType(request.getAccountType());
        account.setCurrency(request.getCurrency());
        account.setStatus(CommonConstants.STATUS_ACTIVE);
        account.setCreateTime(LocalDateTime.now());
        account.setUpdateTime(LocalDateTime.now());
        account.setDeleted(false);

        // 保存账户信息
        int result = accountMapper.insert(account);
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.ACCOUNT_CREATE_FAILED, messageSource.getMessage("account.create.failed", null, LocaleContextHolder.getLocale()));
        }

        // 创建账户余额记录
        AccountBalance accountBalance = new AccountBalance();
        accountBalance.setAccountId(account.getAccountId());
        accountBalance.setAvailableBalance(BigDecimal.ZERO);
        accountBalance.setFrozenBalance(BigDecimal.ZERO);
        accountBalance.setTotalBalance(BigDecimal.ZERO);
        accountBalance.setCurrency(request.getCurrency());
        accountBalance.setVersion(0);
        accountBalance.setCreateTime(LocalDateTime.now());
        accountBalance.setUpdateTime(LocalDateTime.now());

        int balanceResult = accountBalanceMapper.insert(accountBalance);
        if (balanceResult <= 0) {
            throw new BusinessException(ErrorCodes.ACCOUNT_BALANCE_CREATE_FAILED, messageSource.getMessage("account.balance.create.failed", null, LocaleContextHolder.getLocale()));
        }

        log.info("账户创建成功，账户ID：{}", account.getAccountId());

        // 转换为响应DTO
        return convertToResponse(account, accountBalance);
    }

    @Override
    public AccountResponse getAccountById(String accountId) {
        log.info("根据账户ID获取账户信息，账户ID：{}", accountId);

        Account account = getAccountEntityById(accountId);
        AccountBalance accountBalance = getAccountBalanceByAccountId(accountId);

        return convertToResponse(account, accountBalance);
    }

    @Override
    public List<AccountResponse> getAccountsByUserId(String userId) {
        log.info("根据用户ID获取账户列表，用户ID：{}", userId);

        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getUserId, userId)
                   .eq(Account::getDeleted, false);
        
        List<Account> accounts = accountMapper.selectList(queryWrapper);
        
        return accounts.stream().map(account -> {
            AccountBalance accountBalance = getAccountBalanceByAccountId(account.getAccountId());
            return convertToResponse(account, accountBalance);
        }).collect(Collectors.toList());
    }

    @Override
    public List<AccountResponse> getAccountsByMerchantId(String merchantId) {
        log.info("根据商户ID获取账户列表，商户ID：{}", merchantId);

        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getMerchantId, merchantId)
                   .eq(Account::getDeleted, false);
        
        List<Account> accounts = accountMapper.selectList(queryWrapper);
        
        return accounts.stream().map(account -> {
            AccountBalance accountBalance = getAccountBalanceByAccountId(account.getAccountId());
            return convertToResponse(account, accountBalance);
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deposit(BalanceOperationRequest request) {
        log.info("账户充值，账户ID：{}，金额：{}", request.getAccountId(), request.getAmount());

        AccountBalance accountBalance = getAccountBalanceByAccountId(request.getAccountId());
        
        // 更新余额
        BigDecimal newAvailableBalance = accountBalance.getAvailableBalance().add(request.getAmount());
        BigDecimal newTotalBalance = accountBalance.getTotalBalance().add(request.getAmount());
        
        accountBalance.setAvailableBalance(newAvailableBalance);
        accountBalance.setTotalBalance(newTotalBalance);
        accountBalance.setUpdateTime(LocalDateTime.now());

        int result = accountBalanceMapper.updateById(accountBalance);
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.BALANCE_UPDATE_FAILED, messageSource.getMessage("balance.update.failed", null, LocaleContextHolder.getLocale()));
        }

        // 记录流水
        recordAccountFlow(request.getAccountId(), CommonConstants.TRANSACTION_TYPE_DEPOSIT, request.getAmount(), 
                         accountBalance.getAvailableBalance().subtract(request.getAmount()), 
                         newAvailableBalance, request.getRemark());

        log.info("账户充值成功，账户ID：{}，新余额：{}", request.getAccountId(), newAvailableBalance);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean withdraw(BalanceOperationRequest request) {
        log.info("账户扣款，账户ID：{}，金额：{}", request.getAccountId(), request.getAmount());

        AccountBalance accountBalance = getAccountBalanceByAccountId(request.getAccountId());
        
        // 检查余额是否充足
        if (accountBalance.getAvailableBalance().compareTo(request.getAmount()) < 0) {
            throw new BusinessException(ErrorCodes.INSUFFICIENT_BALANCE, messageSource.getMessage("insufficient.balance", null, LocaleContextHolder.getLocale()));
        }

        // 更新余额
        BigDecimal newAvailableBalance = accountBalance.getAvailableBalance().subtract(request.getAmount());
        BigDecimal newTotalBalance = accountBalance.getTotalBalance().subtract(request.getAmount());
        
        accountBalance.setAvailableBalance(newAvailableBalance);
        accountBalance.setTotalBalance(newTotalBalance);
        accountBalance.setUpdateTime(LocalDateTime.now());

        int result = accountBalanceMapper.updateById(accountBalance);
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.BALANCE_UPDATE_FAILED, messageSource.getMessage("balance.update.failed", null, LocaleContextHolder.getLocale()));
        }

        // 记录流水
        recordAccountFlow(request.getAccountId(), CommonConstants.TRANSACTION_TYPE_WITHDRAW, request.getAmount(), 
                         accountBalance.getAvailableBalance().add(request.getAmount()), 
                         newAvailableBalance, request.getRemark());

        log.info("账户扣款成功，账户ID：{}，新余额：{}", request.getAccountId(), newAvailableBalance);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean freeze(String accountId, BigDecimal amount) {
        log.info("账户冻结，账户ID：{}，金额：{}", accountId, amount);

        AccountBalance accountBalance = getAccountBalanceByAccountId(accountId);
        
        // 检查可用余额是否充足
        if (accountBalance.getAvailableBalance().compareTo(amount) < 0) {
            throw new BusinessException(ErrorCodes.INSUFFICIENT_AVAILABLE_BALANCE, messageSource.getMessage("insufficient.available.balance", null, LocaleContextHolder.getLocale()));
        }

        // 更新余额
        BigDecimal newAvailableBalance = accountBalance.getAvailableBalance().subtract(amount);
        BigDecimal newFrozenBalance = accountBalance.getFrozenBalance().add(amount);
        
        accountBalance.setAvailableBalance(newAvailableBalance);
        accountBalance.setFrozenBalance(newFrozenBalance);
        accountBalance.setUpdateTime(LocalDateTime.now());

        int result = accountBalanceMapper.updateById(accountBalance);
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.BALANCE_UPDATE_FAILED, messageSource.getMessage("balance.update.failed", null, LocaleContextHolder.getLocale()));
        }

        // 记录流水
        recordAccountFlow(accountId, CommonConstants.ACCOUNT_FLOW_FREEZE, amount, 
                         accountBalance.getAvailableBalance().add(amount), 
                         newAvailableBalance, "账户冻结");

        log.info("账户冻结成功，账户ID：{}，冻结金额：{}", accountId, amount);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unfreeze(String accountId, BigDecimal amount) {
        log.info("账户解冻，账户ID：{}，金额：{}", accountId, amount);

        AccountBalance accountBalance = getAccountBalanceByAccountId(accountId);
        
        // 检查冻结余额是否充足
        if (accountBalance.getFrozenBalance().compareTo(amount) < 0) {
            throw new BusinessException(ErrorCodes.INSUFFICIENT_FROZEN_BALANCE, messageSource.getMessage("insufficient.frozen.balance", null, LocaleContextHolder.getLocale()));
        }

        // 更新余额
        BigDecimal newAvailableBalance = accountBalance.getAvailableBalance().add(amount);
        BigDecimal newFrozenBalance = accountBalance.getFrozenBalance().subtract(amount);
        
        accountBalance.setAvailableBalance(newAvailableBalance);
        accountBalance.setFrozenBalance(newFrozenBalance);
        accountBalance.setUpdateTime(LocalDateTime.now());

        int result = accountBalanceMapper.updateById(accountBalance);
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.BALANCE_UPDATE_FAILED, messageSource.getMessage("balance.update.failed", null, LocaleContextHolder.getLocale()));
        }

        // 记录流水
        recordAccountFlow(accountId, CommonConstants.ACCOUNT_FLOW_UNFREEZE, amount, 
                         accountBalance.getAvailableBalance().subtract(amount), 
                         newAvailableBalance, "账户解冻");

        log.info("账户解冻成功，账户ID：{}，解冻金额：{}", accountId, amount);
        return true;
    }

    @Override
    public AccountResponse getAccountBalance(String accountId) {
        log.info("获取账户余额，账户ID：{}", accountId);

        Account account = getAccountEntityById(accountId);
        AccountBalance accountBalance = getAccountBalanceByAccountId(accountId);

        return convertToResponse(account, accountBalance);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAccount(String accountId) {
        log.info("删除账户，账户ID：{}", accountId);

        Account existingAccount = getAccountEntityById(accountId);
        
        // 软删除
        existingAccount.setDeleted(true);
        existingAccount.setUpdateTime(LocalDateTime.now());

        int result = accountMapper.updateById(existingAccount);
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.ACCOUNT_DELETE_FAILED, messageSource.getMessage("account.delete.failed", null, LocaleContextHolder.getLocale()));
        }

        log.info("账户删除成功，账户ID：{}", accountId);
    }

    /**
     * 根据账户ID获取账户实体
     */
    private Account getAccountEntityById(String accountId) {
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getAccountId, accountId)
                   .eq(Account::getDeleted, false);
        
        Account account = accountMapper.selectOne(queryWrapper);
        if (account == null) {
            throw new BusinessException(ErrorCodes.ACCOUNT_NOT_FOUND, messageSource.getMessage("account.not.exist", null, LocaleContextHolder.getLocale()));
        }
        return account;
    }

    /**
     * 根据账户ID获取账户余额
     */
    private AccountBalance getAccountBalanceByAccountId(String accountId) {
        LambdaQueryWrapper<AccountBalance> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountBalance::getAccountId, accountId);
        
        AccountBalance accountBalance = accountBalanceMapper.selectOne(queryWrapper);
        if (accountBalance == null) {
            throw new BusinessException(ErrorCodes.ACCOUNT_BALANCE_NOT_FOUND, messageSource.getMessage("account.balance.not.exist", null, LocaleContextHolder.getLocale()));
        }
        return accountBalance;
    }

    /**
     * 记录账户流水
     */
    private void recordAccountFlow(String accountId, String transactionType, BigDecimal amount, 
                                 BigDecimal beforeBalance, BigDecimal afterBalance, String remark) {
        AccountFlow accountFlow = new AccountFlow();
        accountFlow.setFlowNo(businessIdGenerator.generateBusinessId("FLOW"));
        accountFlow.setAccountId(accountId);
        accountFlow.setTransactionType(transactionType);
        accountFlow.setAmount(amount);
        accountFlow.setBeforeBalance(beforeBalance);
        accountFlow.setAfterBalance(afterBalance);
        accountFlow.setCurrency(CommonConstants.CURRENCY_CNY); // 默认人民币
        accountFlow.setRemark(remark);
        accountFlow.setCreateTime(LocalDateTime.now());

        accountFlowMapper.insert(accountFlow);
    }

    /**
     * 转换为响应DTO
     */
    private AccountResponse convertToResponse(Account account, AccountBalance accountBalance) {
        AccountResponse response = new AccountResponse();
        BeanUtils.copyProperties(account, response);
        if (accountBalance != null) {
            response.setAvailableBalance(accountBalance.getAvailableBalance());
            response.setFrozenBalance(accountBalance.getFrozenBalance());
            response.setTotalBalance(accountBalance.getTotalBalance());
        }
        return response;
    }
}
