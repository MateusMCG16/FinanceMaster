package com.example.data

import kotlinx.coroutines.flow.Flow

class FinanceRepository(private val dao: TransactionDao) {
    val allTransactions: Flow<List<Transaction>> = dao.getAllTransactions()

    suspend fun insert(transaction: Transaction) {
        dao.insertTransaction(transaction)
    }

    suspend fun deleteById(id: Int) {
        dao.deleteTransactionById(id)
    }
}
