package com.huang.store.service.imp;

import com.huang.store.entity.order.Expense;
import org.springframework.stereotype.Service;

/**
 * @description:
 */
@Service
public interface ExpenseService {
    int addExpense(Expense expense);
}
