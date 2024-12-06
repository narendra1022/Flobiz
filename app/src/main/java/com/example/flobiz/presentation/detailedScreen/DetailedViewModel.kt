package com.example.flobiz.presentation.detailedScreen

import androidx.lifecycle.ViewModel
import com.example.flobiz.data.model.Transaction
import com.example.flobiz.presentation.authentication.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authViewModel: FirebaseAuth
) : ViewModel() {

    // Fetch transaction by ID - Returns a Flow
    fun getTransactionById(transactionId: String): Flow<Transaction?> = flow {
        val userId = authViewModel.currentUser?.uid
        val snapshot = userId?.let {
            firestore.collection("users")
                .document(it)
                .collection("transactions")
                .document(transactionId)
                .get()
                .await() // Suspending function, so the thread isn't blocked
        }

        val transaction = snapshot?.toObject(Transaction::class.java)
        emit(transaction) // Emit the result as part of the flow
    }

    // Update the transaction - Suspended function to perform async operation
    suspend fun updateTransaction(transaction: Transaction) {
        val userId = authViewModel.currentUser?.uid
        userId?.let {
            firestore.collection("users")
                .document(it)
                .collection("transactions")
                .document(transaction.id)
                .set(transaction)
                .await() // Suspending function to await Firestore operation
        }
    }

    // Delete the transaction - Suspended function to perform async operation
    suspend fun deleteTransaction(transactionId: String) {
        val userId = authViewModel.currentUser?.uid
        userId?.let {
            firestore.collection("users")
                .document(it)
                .collection("transactions")
                .document(transactionId)
                .delete()
                .await() // Suspending function to await Firestore operation
        }
    }
}
