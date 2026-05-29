package com.teams.presentation.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.worldcuplogin.databinding.ActivityTeamsBinding
import com.teams.presentation.adapter.TeamsAdapter
import com.teams.domain.repository.TeamRepositoryImpl
import com.teams.domain.usecase.TeamUseCase
import com.teams.presentation.viewmodel.TeamViewModel
import com.teams.presentation.viewmodel.state.TeamUiState
import kotlinx.coroutines.launch

class TeamsActivity : AppCompatActivity() {
    private val binding: ActivityTeamsBinding by lazy {
        ActivityTeamsBinding.inflate(layoutInflater)
    }

    private val viewModel: TeamViewModel by lazy {
        TeamViewModel(
            TeamUseCase(
                TeamRepositoryImpl()
            )
        )
    }

    private val adapter: TeamsAdapter by lazy {
        TeamsAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        observerState()
        initBundle()
        setupView()
        setupRecycler()
        adapter.showLoading(true)
        viewModel.getTeams(this)
    }

    private fun observerState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is TeamUiState.Idle -> {
                            adapter.showLoading(false)
                        }

                        is TeamUiState.Loading -> {
                            adapter.showLoading(true)
                        }

                        is TeamUiState.Success -> {
                            adapter.updateList(state.teams)
                        }

                        is TeamUiState.Error -> {
                            adapter.showLoading(false)
                            Toast.makeText(this@TeamsActivity, state.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }

    private fun setupRecycler() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun initBundle() {
        intent.getStringExtra("user_name")?.let { userName ->
            Toast.makeText(this, "Bem-vindo, $userName!", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupView() {
        binding.tvName.text = intent.getStringExtra("user_name")
    }
}
