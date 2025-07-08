package com.huang.store.service;

import com.huang.store.entity.order.Expense;
import com.huang.store.mapper.ExpenseMapper;
import com.huang.store.service.imp.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 */
@Service("expense")
public class ExpenseServiceImpl implements ExpenseService {

    @Autowired
    ExpenseMapper expenseMapper;

    @Override
    public int addExpense(Expense expense) {
        return expenseMapper.addExpense(expense);
    }
}
