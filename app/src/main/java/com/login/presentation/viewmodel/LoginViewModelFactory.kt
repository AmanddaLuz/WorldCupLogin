package com.login.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.login.data.network.RetrofitFactory
import com.login.domain.repository.LoginRepositoryImpl
import com.login.domain.usecase.LoginUseCase

class LoginViewModelFactory :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        val api =
            RetrofitFactory.create()

        val repository =
            LoginRepositoryImpl(api)

        val useCase =
            LoginUseCase(repository)

        return LoginViewModel(
            useCase
        ) as T
    }
}
