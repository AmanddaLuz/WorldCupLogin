package com.login.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.launch
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.worldcuplogin.databinding.ActivityLoginBinding
import com.login.repository.LoginRepositoryImpl
import com.login.usecase.LoginUseCase
import com.login.viewmodel.LoginViewModel
import com.login.viewmodel.state.LoginUIState
import com.teams.view.TeamsActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.observeOn
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private val viewModel: LoginViewModel by lazy {
        LoginViewModel(
            LoginUseCase(
                LoginRepositoryImpl()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupListeners()
        loginObserver()
    }

    private fun setupListeners() {
        binding.buttonLogin.setOnClickListener {
            binding.buttonLogin.isEnabled = false
            viewModel.login(
                this,
                binding.editTextUser.text.toString(),
                binding.editPassword.text.toString()
            )
        }
    }

    private fun loginObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is LoginUIState.Idle -> {
                            binding.progressBar.visibility = View.GONE
                        }
                        is LoginUIState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is LoginUIState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            goToTeamsView(state.userNamer)
                        }
                        is LoginUIState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.buttonLogin.isEnabled = false
                            binding.editTextUser.text?.clear()
                            binding.editPassword.text?.clear()
                            Toast.makeText(this@LoginActivity, "state.message", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun goToTeamsView(userName: String) {
        val intent = Intent(this, TeamsActivity::class.java)
        intent.putExtra("userName", userName)
        startActivity(intent)
    }
}