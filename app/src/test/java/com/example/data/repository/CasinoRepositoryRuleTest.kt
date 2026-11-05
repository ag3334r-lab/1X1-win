package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
@LooperMode(LooperMode.Mode.INSTRUMENTATION_TEST)
class CasinoRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun getOrCreateProfile_andUpdateWallet_succeedsForAuthenticatedOwner() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val repository = CasinoRepository(firestore)

        val profileResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.getOrCreateProfile("AliceRoyal", 1)
        }
        assertTrue("Expected profile creation to succeed: ${profileResult.exceptionOrNull()}", profileResult.isSuccess)
        val profile = profileResult.getOrThrow()
        assertEquals(aliceUid, profile.userId)

        val updateResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.updateWalletAndStats(
                mainBalance = 2500.0,
                bonusBalance = 150.0,
                safeBalance = 500.0,
                vipLevel = 2,
                vipPoints = 350L,
                streakDays = 3,
                lastSpinDay = 10L
            )
        }
        assertTrue("Expected wallet update to succeed: ${updateResult.exceptionOrNull()}", updateResult.isSuccess)
    }

    @Test
    fun createTransaction_andObserveTransactions_succeedsForOwner() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val repository = CasinoRepository(firestore)

        val txResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.createTransaction(
                type = "DEPOSIT",
                method = "JAZZCASH",
                amount = 1000.0,
                status = "PENDING",
                referenceNote = "TID-987654"
            )
        }
        assertTrue(txResult.isSuccess)
        val txId = txResult.getOrThrow()

        val observed = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeTransactions(aliceUid).first { list -> list.any { it.id == txId } }
        }
        assertTrue(observed.any { it.id == txId })

        val approveResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.updateTransactionStatus(txId, "APPROVED", "TID-987654 Approved")
        }
        assertTrue(approveResult.isSuccess)
    }

    @Test
    fun getProfileByUserId_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = CasinoRepository(firestore)
        withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.getOrCreateProfile("AliceRoyal", 1).getOrThrow()
        }

        signInTestUser(BOB_EMAIL)
        val bobRepo = CasinoRepository(firestore)
        val crossRead = withTimeout(DEFAULT_TIMEOUT_MS) {
            bobRepo.getProfileByUserId(aliceUid)
        }
        assertTrue(crossRead.isFailure)
        val fsError = crossRead.exceptionOrNull()?.unwrapFirestoreException()
        assertNotNull(fsError)
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, fsError?.code)
    }

    @Test
    fun observeTransactions_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        auth.signOut()
        val repository = CasinoRepository(firestore)

        var caught: Throwable? = null
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeTransactions(aliceUid).first()
            }
        } catch (e: Throwable) {
            caught = e
        }
        assertNotNull("Expected unauthenticated query to fail", caught)
        val fsError = caught?.unwrapFirestoreException()
        assertNotNull("Expected FirebaseFirestoreException cause, got $caught", fsError)
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, fsError?.code)
    }

    private companion object {
        const val ALICE_EMAIL = "alice_royal@test.com"
        const val BOB_EMAIL = "bob_royal@test.com"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 3500L
    }
}
