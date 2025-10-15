package com.aura

import com.aura.data.model.LoginResponse
import com.aura.data.repository.AuraRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import app.cash.turbine.test
import com.aura.data.model.LoginRequest
import com.aura.data.network.APIService
import com.aura.data.repository.Result
import com.aura.data.session.SessionManager
import com.aura.ui.login.LoginVewModel
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException

/**
 * Unit tests for the repository's method linked to the login screen.
 */
class LoginRepositoryUnitTest {
    private lateinit var repository: AuraRepository
    private lateinit var apiService: APIService

    @Before
    fun setup() {
        apiService = mockk()
        repository = AuraRepository(apiService)
    }

    /**
     * Test the login function of the repository when the api call is successful and the login response is true.
     *
     * Acceptance criteria:
     * - The result should be a success result
     * - The result data should be the login response
     * - The loading state should be emitted before the success state
     *
     */
    @Test
    fun `login should return success when credentials are correct`() = runTest {
        val loginRequest = LoginRequest("correctID", "correctPass")
        val loginResponse = LoginResponse(granted = true)
        coEvery { apiService.login(loginRequest) } returns loginResponse

        repository.login("correctID", "correctPass").test {
            val loadingItem = awaitItem()
            assertTrue(loadingItem is Result.Loading)

            val successItem = awaitItem()
            assertTrue(successItem is Result.Success)
            assertEquals(true, (successItem as Result.Success).data.granted)

            awaitComplete()
        }
    }

    /**
     * Test the login function of the repository when the api call is successful but the login response is false.
     *
     * Acceptance criteria:
     * - The result should be a success result
     * - The result data should be the login response
     * - The loading state should be emitted before the success state
     */
    @Test
    fun `login should return success when credentials are Incorrect`() = runTest {
        val loginResponse = LoginResponse(granted = false)
        coEvery { apiService.login(any()) } returns loginResponse

        repository.login("incorrectID", "incorrectPass").test {
            val loadingItem = awaitItem()
            assertTrue(loadingItem is Result.Loading)

            val successItem = awaitItem()
            assertTrue(successItem is Result.Success)
            assertEquals(false, (successItem as Result.Success).data.granted)

            awaitComplete()
        }
    }

    /**
     * Test the login function of the repository when the api call fails.
     *
     * Acceptance criteria:
     * - The result should be an error result
     * - The result exception should be the exception thrown by the api
     * - The loading state should be emitted before the error state
     */
    @Test
    fun `login should return error when api call fails`() = runTest {
        coEvery { apiService.login(any()) } throws Exception("Network error")

        repository.login("id", "pass").test {
            val loadingItem = awaitItem()
            assertTrue(loadingItem is Result.Loading)

            val errorItem = awaitItem()
            assertTrue(errorItem is Result.Error)
            assertEquals("Network error", (errorItem as Result.Error).exception.message)

            awaitComplete()
        }
    }
}

/**
 * Unit tests for the LoginViewModel.
 */
class LoginViewModelUnitTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: LoginVewModel
    private lateinit var repository: AuraRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = LoginVewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        SessionManager.clearSession()
    }

    /**
     * Test the login function of the viewModel when the login is successful.
     *
     * Acceptance criteria:
     * - The loginResult should be true
     * - The loading should be false
     * - The errorMessage should be null
     * - The current user session should be started with it's identifier recorder.
     */
    @Test
    fun `login granted true should start session and update LoginState correctly`() = runTest {
        val loginResponse = LoginResponse(granted = true)
        coEvery { repository.login(any(), any()) } returns flow{
            emit(Result.Loading)
            emit(Result.Success(loginResponse))
        }

        viewModel.login("identifier", "password")

        advanceUntilIdle()

        val state = viewModel.loginState.value
        assertTrue(state.loginResult == true)
        assertFalse(state.loading)
        assertNull(state.errorMessage)
        assertEquals("identifier", SessionManager.getCurrentUserId())

    }


    /**
     * Test the login function of the viewModel when the login is not successful but the api called was successful.
     *
     * Acceptance criteria:
     * - The loginResult should be false
     * - The loading should be false
     * - The errorMessage should be null
     * - The current user session should not be started -> the identifier should remained null.
     */
    @Test
    fun `login granted false should not start session and update LoginState correctly`() = runTest {
        val loginResponse = LoginResponse(granted = false)
        coEvery { repository.login(any(), any()) } returns flow{
            emit(Result.Loading)
            emit(Result.Success(loginResponse))
        }

        viewModel.login("identifier", "password")

        advanceUntilIdle()

        val state = viewModel.loginState.value
        assertTrue(state.loginResult == false)
        assertFalse(state.loading)
        assertNull(state.errorMessage)
        assertNull(SessionManager.getCurrentUserId())
    }

    /**
     * Test the login function of the viewModel when receiving a error state from the repository.
     *
     * Acceptance criteria:
     * - The loginResult should be null
     * - The loading should be false
     * - The errorMessage should be the defaut error message.
     */
    @Test
    fun `login should return error_network when api call fails`() = runTest {
        val ioException = IOException("Network error")
        coEvery { repository.login(any(), any()) } returns flow{
            emit(Result.Loading)
            emit(Result.Error(ioException))
        }

        viewModel.login("identifier", "password")

        advanceUntilIdle()

        val state = viewModel.loginState.value
        assertTrue(state.loginResult == null)
        assertFalse(state.loading)
        assertEquals(R.string.error_network, state.errorMessage)
    }

    /**
     * Test the login function of the viewModel when the login is in progress.
     *
     * Acceptance criteria:
     * - The loginResult should be null
     * - The loading should be true
     * - The errorMessage should be null
     */
    @Test
    fun `login should return loading when login is in progress`() = runTest {
        coEvery { repository.login(any(), any()) } returns flow{
            emit(Result.Loading)
            emit(Result.Loading)
        }
        viewModel.login("identifier", "password")
        advanceUntilIdle()

        val state = viewModel.loginState.value
        assertTrue(state.loginResult == null)
        assertTrue(state.loading)
        assertNull(state.errorMessage)
    }

}
