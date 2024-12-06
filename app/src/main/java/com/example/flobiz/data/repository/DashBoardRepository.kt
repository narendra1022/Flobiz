package com.example.flobiz.data.repository

import com.example.flobiz.data.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DashBoardRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val currentUserId: String
        get() = auth.currentUser?.uid
            ?: throw IllegalStateException("User not authenticated")

    private val transactionsCollection
        get() = firestore.collection("users")
            .document(currentUserId)
            .collection("transactions")

    suspend fun addTransaction(transaction: Transaction) {
        val documentRef = transactionsCollection.document()
        val updatedTransaction = transaction.copy(id = documentRef.id)
        documentRef.set(updatedTransaction).await()
    }

    suspend fun deleteTransaction(transactionId: String) {
        transactionsCollection.document(transactionId).delete().await()
    }

    fun getAllTransactions(): Flow<List<Transaction>> = callbackFlow {
        val listenerRegistration = transactionsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)  // Handle errors (optional)
            } else {
                snapshot?.let {
                    val transactions = it.toObjects(Transaction::class.java)
                    trySend(transactions)
                }
            }
        }
        awaitClose { listenerRegistration.remove() }
    }
}
