package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.GameRoundRecord
import com.example.data.model.UserProfile
import com.example.data.model.WalletTransaction
import com.example.data.remote.OperationType
import com.example.data.remote.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlin.math.abs
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class CasinoRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun generatePlayerId(uid: String): String {
        val hash = abs(uid.hashCode())
        val sevenDigit = 1000000 + (hash % 8999999)
        return sevenDigit.toString()
    }

    fun observeUserProfile(userId: String = requireUserId()): Flow<UserProfile?> = flow {
        val path = "users/$userId"
        emitAll(
            db.collection("users").document(userId)
                .snapshots()
                .map { snapshot ->
                    if (snapshot.exists()) {
                        snapshot.toObject(
                            UserProfile::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        )
                    } else {
                        null
                    }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null) {
                        handleFirestoreError(exceptionToLog, OperationType.GET, path)
                    }
                    throw error
                }
        )
    }

    suspend fun getOrCreateProfile(
        displayName: String = "RoyalPlayer",
        avatarIndex: Int = 0
    ): Result<UserProfile> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        try {
            val existingSnap = docRef.get().await()
            if (existingSnap.exists()) {
                val parsed = existingSnap.toObject(
                    UserProfile::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
                if (parsed != null) return@runCatching parsed
            }
            val playerId = generatePlayerId(uid)
            val safeName = displayName.trim().ifBlank { "Player$playerId" }.take(40)
            val refCode = "RGC${playerId.takeLast(4)}"
            val payload = mapOf(
                "userId" to uid,
                "playerId" to playerId,
                "displayName" to safeName,
                "avatarIndex" to avatarIndex.coerceIn(0, 10),
                "mainBalance" to 1000.0,
                "bonusBalance" to 100.0,
                "safeBalance" to 250.0,
                "vipLevel" to 1,
                "vipPoints" to 100L,
                "streakDays" to 1,
                "lastSpinDay" to 0L,
                "referralCode" to refCode,
                "paymentMethod" to "JAZZCASH",
                "accountNumber" to "",
                "accountHolder" to "",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            val freshSnap = docRef.get().await()
            freshSnap.toObject(
                UserProfile::class.java,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            ) ?: UserProfile(
                id = uid,
                userId = uid,
                playerId = playerId,
                displayName = safeName,
                avatarIndex = avatarIndex,
                mainBalance = 1000.0,
                bonusBalance = 100.0,
                safeBalance = 250.0,
                vipLevel = 1,
                vipPoints = 100L,
                streakDays = 1,
                lastSpinDay = 0L,
                referralCode = refCode
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }

    suspend fun getProfileByUserId(targetUserId: String): Result<UserProfile?> = runCatching {
        val docRef = db.collection("users").document(targetUserId)
        try {
            val snap = docRef.get().await()
            if (snap.exists()) {
                snap.toObject(
                    UserProfile::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            } else {
                null
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            throw e
        }
    }

    suspend fun updateWalletAndStats(
        mainBalance: Double,
        bonusBalance: Double,
        safeBalance: Double,
        vipLevel: Int,
        vipPoints: Long,
        streakDays: Int,
        lastSpinDay: Long
    ): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        try {
            val updates = mapOf(
                "mainBalance" to mainBalance.coerceAtLeast(0.0),
                "bonusBalance" to bonusBalance.coerceAtLeast(0.0),
                "safeBalance" to safeBalance.coerceAtLeast(0.0),
                "vipLevel" to vipLevel.coerceIn(0, 20),
                "vipPoints" to vipPoints.coerceAtLeast(0L),
                "streakDays" to streakDays.coerceIn(0, 30),
                "lastSpinDay" to lastSpinDay.coerceAtLeast(0L),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.update(updates).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun updateProfileIdentityAndBank(
        displayName: String,
        avatarIndex: Int,
        paymentMethod: String,
        accountNumber: String,
        accountHolder: String
    ): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        try {
            val updates = mapOf(
                "displayName" to displayName.trim().ifBlank { "RoyalPlayer" }.take(60),
                "avatarIndex" to avatarIndex.coerceIn(0, 10),
                "paymentMethod" to paymentMethod.take(30),
                "accountNumber" to accountNumber.trim().take(40),
                "accountHolder" to accountHolder.trim().take(60),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.update(updates).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    fun observeTransactions(userId: String = requireUserId()): Flow<List<WalletTransaction>> = flow {
        val path = "users/$userId/transactions"
        emitAll(
            db.collection("users").document(userId).collection("transactions")
                .whereEqualTo("userId", userId)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        WalletTransaction::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null) {
                        handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun createTransaction(
        type: String,
        method: String,
        amount: Double,
        status: String,
        referenceNote: String
    ): Result<String> = runCatching {
        val uid = requireUserId()
        val colRef = db.collection("users").document(uid).collection("transactions")
        val docRef = colRef.document()
        try {
            val payload = mapOf(
                "userId" to uid,
                "type" to type,
                "method" to method,
                "amount" to amount,
                "status" to status,
                "referenceNote" to referenceNote.take(120),
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            docRef.id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, colRef.path)
            throw e
        }
    }

    suspend fun updateTransactionStatus(
        txId: String,
        newStatus: String,
        referenceNote: String
    ): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
            .collection("transactions").document(txId)
        try {
            docRef.update(
                mapOf(
                    "status" to newStatus,
                    "referenceNote" to referenceNote.take(120),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    fun observeGameHistory(userId: String = requireUserId()): Flow<List<GameRoundRecord>> = flow {
        val path = "users/$userId/game_history"
        emitAll(
            db.collection("users").document(userId).collection("game_history")
                .whereEqualTo("userId", userId)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        GameRoundRecord::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null) {
                        handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun recordGameRound(
        gameId: String,
        gameTitle: String,
        betAmount: Double,
        winAmount: Double,
        outcomeText: String
    ): Result<String> = runCatching {
        val uid = requireUserId()
        val colRef = db.collection("users").document(uid).collection("game_history")
        val docRef = colRef.document()
        try {
            val payload = mapOf(
                "userId" to uid,
                "gameId" to gameId.take(40),
                "gameTitle" to gameTitle.take(60),
                "betAmount" to betAmount.coerceAtLeast(0.0),
                "winAmount" to winAmount.coerceAtLeast(0.0),
                "outcomeText" to outcomeText.take(120),
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            docRef.id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, colRef.path)
            throw e
        }
    }
}
