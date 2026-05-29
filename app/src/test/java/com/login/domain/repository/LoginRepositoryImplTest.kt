package com.login.domain.repository

import com.login.data.network.LoginApi
import com.login.domain.repository.response.UserResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginRepositoryImplTest {

    private val api: LoginApi = mockk()

    private val repository =
        LoginRepositoryImpl(api)

    @Test
    fun `should return success when user exists`() =
        runTest {

            val users = listOf(

                UserResponse(
                    id = 1,
                    name = "Amanda",
                    username = "123",
                    email = "amanda@email.com"
                )
            )

            coEvery {
                api.getUsers()
            } returns users

            val result = repository.login(
                email = "amanda@email.com",
                password = "123"
            )

            assertTrue(
                result.isSuccess
            )

            val data =
                result.getOrNull()

            assertEquals(
                "Amanda",
                data?.user?.name
            )

            assertEquals(
                "1",
                data?.token
            )
        }

    @Test
    fun `should return failure when user does not exist`() =
        runTest {

            val users = listOf(

                UserResponse(
                    id = 1,
                    name = "Amanda",
                    username = "123",
                    email = "amanda@email.com"
                )
            )

            coEvery {
                api.getUsers()
            } returns users

            val result = repository.login(
                email = "wrong@email.com",
                password = "wrong"
            )

            assertTrue(
                result.isFailure
            )

            assertEquals(
                "Login inválido",
                result.exceptionOrNull()?.message
            )
        }

    @Test
    fun `should return failure when api throws exception`() =
        runTest {

            coEvery {
                api.getUsers()
            } throws RuntimeException(
                "Erro API"
            )

            val result = repository.login(
                email = "teste@email.com",
                password = "123"
            )

            assertTrue(
                result.isFailure
            )

            assertEquals(
                "Erro API",
                result.exceptionOrNull()?.message
            )
        }
}
