package com.login.presentation.viewmodel

import app.cash.turbine.test
import com.login.data.model.LoginModel
import com.login.data.model.User
import com.login.domain.usecase.LoginUseCase
import com.login.presentation.viewmodel.state.LoginUIState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val loginUseCase: LoginUseCase = mockk()

    private val dispatcher = StandardTestDispatcher()

    private val viewModel by lazy {
        LoginViewModel(
            loginUseCase
        )
    }

    @Before
    fun setup() {

        Dispatchers.setMain(
            dispatcher
        )
    }

    @After
    fun tearDown() {

        Dispatchers.resetMain()
    }

    @Test
    fun `should emit loading and success`() =
        runTest {

            val loginModel =
                LoginModel(
                    user = User(
                        name = "Amanda"
                    ),
                    success = true,
                    token = "1"
                )

            coEvery {

                loginUseCase.login(
                    any(),
                    any()
                )

            } returns Result.success(
                loginModel
            )

            viewModel.uiState.test {

                assertEquals(
                    LoginUIState.Idle,
                    awaitItem()
                )

                viewModel.login(
                    "email",
                    "123"
                )

                assertEquals(
                    LoginUIState.Loading,
                    awaitItem()
                )

                assertEquals(
                    LoginUIState.Success(
                        "Amanda"
                    ),
                    awaitItem()
                )
            }
        }

    @Test
    fun `should emit loading and error`() =
        runTest {

            coEvery {

                loginUseCase.login(
                    any(),
                    any()
                )

            } returns Result.failure(
                Throwable(
                    "Login inválido"
                )
            )

            viewModel.uiState.test {

                assertEquals(
                    LoginUIState.Idle,
                    awaitItem()
                )

                viewModel.login(
                    "email",
                    "123"
                )

                assertEquals(
                    LoginUIState.Loading,
                    awaitItem()
                )

                assertEquals(
                    LoginUIState.Error(
                        "Login inválido"
                    ),
                    awaitItem()
                )
            }
        }
}
