package com.huang.store.mapper;

import com.huang.store.entity.order.Expense;
import org.springframework.stereotype.Repository;

/**
 * @description:
 */
@Repository
public interface ExpenseMapper {
    int addExpense(Expense expense);

    Expense findExpenseByOrderId(String orderId);
}
