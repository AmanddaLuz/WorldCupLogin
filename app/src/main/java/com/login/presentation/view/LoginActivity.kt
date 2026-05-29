package com.login.presentation.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.worldcuplogin.databinding.ActivityLoginBinding
import com.login.presentation.viewmodel.LoginViewModel
import com.login.presentation.viewmodel.LoginViewModelFactory
import com.login.presentation.viewmodel.state.LoginUIState
import com.teams.presentation.view.TeamsActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private val viewModel:
            LoginViewModel by viewModels {
        LoginViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupListeners()
        setupTextWatcher()
        loginObserver()
    }

    private fun setupListeners() {
        binding.buttonLogin.setOnClickListener {
            viewModel.login(
                binding.editTextUser.text
                    .toString(),
                binding.editPassword.text
                    .toString()
            )
        }
    }

    private fun setupTextWatcher() {
        binding.editTextUser.doAfterTextChanged {
            updateButtonState()
        }
        binding.editPassword.doAfterTextChanged {
            updateButtonState()
        }
    }

    private fun refresh() {
        binding.editTextUser.text?.clear()
        binding.editPassword.text?.clear()
    }

    private fun updateButtonState() {
        binding.buttonLogin.isEnabled =
            binding.editTextUser.text
                ?.isNotBlank() == true &&
                    binding.editPassword.text
                        ?.isNotBlank() == true
    }

    private fun loginObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is LoginUIState.Idle -> {
                            hideButtonLoading()
                        }
                        is LoginUIState.Loading -> {
                            showButtonLoading()
                        }
                        is LoginUIState.Success -> {
                            hideButtonLoading()
                            goToTeamsView(state.userNamer)
                            refresh()
                        }

                        is LoginUIState.Error -> {
                            hideButtonLoading()
                            refresh()
                            Toast.makeText(
                                this@LoginActivity,
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun showButtonLoading() {
        binding.buttonLogin.text = ""
        binding.buttonLogin.isEnabled = false
        binding.progressBar.isVisible = true
    }

    private fun hideButtonLoading() {
        binding.buttonLogin.text = "Entrar"
        binding.progressBar.isVisible = false
        updateButtonState()
    }

    private fun goToTeamsView(
        userName: String
    ) {
        val intent = Intent(this, TeamsActivity::class.java).apply {
            putExtra(
                "user_name",
                userName
            )
        }
        startActivity(intent)
    }
}
