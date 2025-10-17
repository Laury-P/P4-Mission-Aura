package com.aura

import app.cash.turbine.test
import com.aura.data.model.AccountResponse
import com.aura.data.network.APIService
import com.aura.data.repository.AuraRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import com.aura.data.repository.Result
import com.aura.data.session.SessionManager
import com.aura.ui.UIMessage

import com.aura.ui.home.HomeViewModel
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher


import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import java.io.IOException

class HomeRepositoryUnitTest {
    private lateinit var repository: AuraRepository
    private lateinit var apiService: APIService

    @Before
    fun setup() {
        apiService = mockk()
        repository = AuraRepository(apiService)
    }

    /**
     * Test of getAccount when the api call is successful.
     *
     *Acceptance criteria:
     *  - The result should be a success result
     *  - The result data should be the list of account response
     *  - The loading state should be emitted before the success state
     */
    @Test
    fun `getAccount should return success when api call is successful`() = runTest {
        val accountResult = listOf(mockk<AccountResponse>(), mockk<AccountResponse>())

        coEvery { apiService.getAccount(any()) } returns accountResult
        repository.getAccount("identifier").test {
            val loadingItem = awaitItem()
            assert(loadingItem is Result.Loading)
            val successItem = awaitItem()
            assert(successItem is Result.Success)
            assert((successItem as Result.Success).data == accountResult)
            awaitComplete()
        }

    }

    /**
     * Test of getAccount when the api call fails.
     *
     * Acceptance criteria:
     * - The result should be an error result
     * - The result exception should be the exception thrown by the api
     */
    @Test
    fun `getAccount should return error when api call fails`() = runTest {
        coEvery { apiService.getAccount(any()) } throws Exception("Network error")

        repository.getAccount("identifier").test {
            val loadingItem = awaitItem()
            assert(loadingItem is Result.Loading)
            val errorItem = awaitItem()
            assert(errorItem is Result.Error)
            assert((errorItem as Result.Error).exception.message == "Network error")
            awaitComplete()
        }
    }

}

class HomeViewModelUnitTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: HomeViewModel
    private lateinit var repository: AuraRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        mockkObject(SessionManager)
        every { SessionManager.getCurrentUserId() } returns "identifier"
        viewModel = HomeViewModel(repository)


    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    /**
     * Test of getAccount when the api call is successful.
     *
     * Acceptance criteria:
     * - The uiState should be updated with the list of account response
     * - the loading state should be false after the success state
     * - The uiMessage shouldn't emit anything
     * - The balanceMain should be the balance of the main account
     */
    @Test
    fun `successful getAccount should update state correctly`() = runTest {
        val fakeAccounts = listOf(
            AccountResponse(1, true, 123.4),
            AccountResponse(2, false,567.8 )
        )
        coEvery { repository.getAccount(any()) } returns flow {
            emit(Result.Loading)
            emit(Result.Success(fakeAccounts))
        }
        viewModel.uiMessageFlow.test {
            viewModel.getAccount()

            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state.accounts == fakeAccounts)
            assertTrue(!state.loading)
            assertEquals(123.4, state.balanceMain)

            expectNoEvents()
            cancelAndConsumeRemainingEvents()
        }
    }

    /**
     * Test of getAccount when the api call fails.
     *
     * Acceptance criteria:
     * - The loading state should be false after the error state
     * - The balanceMain should be 0.0
     * - The accounts should be empty
     * - UiMessage should emit a NETWORK message
     */
    @Test
    fun `failed getAccount should update state correctly and emit NETWORK message`() = runTest {
        val ioException = IOException("Network error")
        coEvery { repository.getAccount(any()) } returns flow {
            emit(Result.Loading)
            emit(Result.Error(ioException))
        }
        viewModel.uiMessageFlow.test {
            viewModel.getAccount()

            advanceUntilIdle()

            val state = viewModel.uiState.value
            val message = awaitItem()

            assertTrue(!state.loading)
            assertEquals(0.0, state.balanceMain)
            assertTrue(state.accounts.isEmpty())
            assertEquals(UIMessage.NETWORK, message)

            cancelAndConsumeRemainingEvents()
        }


    }

    /**
     * Test of getAccount when the balance is negative.
     *
     * Acceptance criteria:
     * - The balanceMain should be the balance of the main account
     */
    @Test
    fun `Negative account balance should also be displayed` () = runTest {
        val fakeAccounts = listOf(
            AccountResponse(1, true, -123.4),
            AccountResponse(2, false,567.8 )
        )
        coEvery { repository.getAccount(any()) } returns flow {
            emit(Result.Loading)
            emit(Result.Success(fakeAccounts))
        }
        viewModel.getAccount()
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertEquals(-123.4, state.balanceMain)
    }
}