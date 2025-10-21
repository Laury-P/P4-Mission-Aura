package com.aura

import app.cash.turbine.test
import com.aura.data.model.TransferRequest
import com.aura.data.model.TransferResponse
import com.aura.data.network.APIService
import com.aura.data.repository.AuraRepository
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import com.aura.data.repository.Result
import com.aura.data.session.SessionManager
import com.aura.ui.UIMessage
import com.aura.ui.transfer.TransferViewModel
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After

/**
 * Unit tests for the repository's method linked to the transfer screen.
 */
class TransferRepositoryUnitTest {
    private lateinit var repository: AuraRepository
    private lateinit var apiService: APIService

    @Before
    fun setup() {
        apiService = mockk()
        repository = AuraRepository(apiService)
    }

    /**
     * Test the transfer function of the repository when the api call is successful
     * and the transfer response is true.
     *
     * Acceptance criteria:
     * - The loading state should be emitted first
     * - The result state should be a success state
     * - the result data should be the transfer response
     */
    @Test
    fun `transfer should return success (granted = true) when transfer is successful`() = runTest {
        val transferRequest = TransferRequest("senderId", "receiverId", 100.0)
        val transferResponse = TransferResponse(granted = true)
        coEvery { apiService.transfer(transferRequest) } returns transferResponse

        repository.transfer("senderId", "receiverId", 100.0).test {
            val loadingItem = awaitItem()
            assertTrue(loadingItem is Result.Loading)

            val successItem = awaitItem()
            assertTrue(successItem is Result.Success)
            assertTrue((successItem as Result.Success).data.granted)

            awaitComplete()
        }
    }

    /**
     * Test the transfer function of the repository when the api call is successful
     * and the transfer response is false
     *
     * Acceptance criteria:
     * - The loading state should be emitted first
     * - The success state should be emitted with the transfer response
     * - The result data should be the transfer response
     */
    @Test
    fun `transfer should return success (granted = false) when transfer is refused by the api`() =
        runTest {
            val transferRequest = TransferRequest("senderId", "receiverId", 100.0)
            val transferResponse = TransferResponse(granted = false)
            coEvery { apiService.transfer(transferRequest) } returns transferResponse

            repository.transfer("senderId", "receiverId", 100.0).test {
                val loadingItem = awaitItem()
                assertTrue(loadingItem is Result.Loading)

                val successItem = awaitItem()
                assertTrue(successItem is Result.Success)
                assertTrue(!(successItem as Result.Success).data.granted)

                awaitComplete()
            }
        }

    /**
     * Test de transfer methode of the repository when the api emit an error
     *
     * Acceptance criteria:
     * - The loading state should be emitted first
     * - The error state should be emitted with the exception
     * - The result data should be the exception
     */
    @Test
    fun `transfer should return error when api call fails`() = runTest {
        val transferRequest = TransferRequest("senderId", "receiverId", -100.0)
        coEvery { apiService.transfer(transferRequest) } throws IllegalArgumentException("amount can't be negatif")

        repository.transfer("senderId", "receiverId", -100.0).test {
            val loadingItem = awaitItem()
            assertTrue(loadingItem is Result.Loading)

            val errorItem = awaitItem()
            assertTrue(errorItem is Result.Error)
            assertTrue((errorItem as Result.Error).exception is IllegalArgumentException)

            awaitComplete()
        }
    }

}

/**
 * Unit tests for the TransferViewModel.
 */
class TransferViewModelUnitTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: TransferViewModel
    private lateinit var repository: AuraRepository

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        mockkObject(SessionManager)
        every { SessionManager.getCurrentUserId() } returns "identifier"
        viewModel = TransferViewModel(repository)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    /**
     * Test the transfer function of the viewModel when the transfer is successful.
     *
     * Acceptance criteria:
     * - The loading should be false
     * - The transfer state should be update to isTransferGranted = true
     * - The uiMessage should be TRANSFER_ACCEPTED
     * - There shouldn't be any error message in uiMessage
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `successful transfer should update state correctly`() = runTest {
        val transferResponse = TransferResponse(granted = true)
        coEvery { repository.transfer(any(), any(), any()) } returns flow {
            emit(Result.Loading)
            emit(Result.Success(transferResponse))
        }

        viewModel.transfer("senderId", "receiverId", 100.0)

        advanceUntilIdle()

        val state = viewModel.transferState.value
        val message = viewModel.uiMessage.value

        assertTrue(!state.isLoading)
        assertTrue(state.isTransferGranted == true)

        assertEquals(UIMessage.TRANSFER_ACCEPTED, message.type)
        assertTrue(message.errorMessage == null)

    }

    /**
     * Test the transfer method of the viewModel when the transfer is refused
     *
     * Acceptance criteria:
     * - The loading should be false
     * - The transfer state should be updated to isTransferGranted = false
     * - The uiMessage should be updated to TRANSFER_DENIED
     * - The errorMessage should be null
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `denied transfer should update state correctly`() = runTest {
        val transferResponse = TransferResponse(granted = false)
        coEvery { repository.transfer(any(), any(), any()) } returns flow {
            emit(Result.Loading)
            emit(Result.Success(transferResponse))
        }

        viewModel.transfer("senderId", "receiverId", 100.0)

        advanceUntilIdle()

        val state = viewModel.transferState.value
        val message = viewModel.uiMessage.value

        assertTrue(!state.isLoading)
        assertTrue(state.isTransferGranted == false)

        assertEquals(UIMessage.TRANSFER_DENIED, message.type)
        assertTrue(message.errorMessage == null)
    }

    /**
     * Test the transfer methode of the viewModel when the transfer request contain error
     *
     * Acceptance criteria:
     * - The loading should be false
     * - The transfer state should be null
     * - The uiMessage type should be TRANSFER_ERROR
     * - The errorMessage should be the error message received from the repository
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `transfer error should update state correctly`() = runTest {
        coEvery { repository.transfer(any(), any(), any()) } returns flow {
            emit(Result.Loading)
            emit(Result.Error(IllegalArgumentException("amount can't be negative")))
        }
        viewModel.transfer("senderId", "receiverId", -100.0)

        advanceUntilIdle()

        val state = viewModel.transferState.value
        val message = viewModel.uiMessage.value

        assertTrue(!state.isLoading)
        assertTrue(state.isTransferGranted == null)

        assertEquals(UIMessage.TRANSFER_ERROR, message.type)
        assertEquals("amount can't be negative", message.errorMessage)

    }

}



