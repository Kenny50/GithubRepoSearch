package com.kenny.githubreposearch.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.kenny.githubreposearch.data.local.RepoDateVo
import com.kenny.githubreposearch.data.remote.paging_source.UiAction
import com.kenny.githubreposearch.data.remote.paging_source.UiState
import com.kenny.githubreposearch.databinding.FragmentHomeBinding
import com.kenny.githubreposearch.presentation.BindingFragment
import com.kenny.githubreposearch.presentation.repo_load_state.ReposLoadStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BindingFragment<FragmentHomeBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentHomeBinding::inflate

    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var pagingAdapter: RepositoriesPagingAdapter
    private var queryJob: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUi()
        binding.bindState(
            uiState = viewModel.state,
            pagingData = viewModel.pagingDataFlow,
            uiAction = viewModel.accept
        )
    }

    private fun FragmentHomeBinding.bindState(
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<RepoDateVo>>,
        uiAction: (UiAction) -> Unit,
    ) {
        bindSearch(
            onQueryChange = uiAction
        )
        bindList(
            pagingData = pagingData,
        )
        bindScrollAction(
            adapter = pagingAdapter,
            uiState = uiState,
            onScrollChange = uiAction
        )
    }

    private fun FragmentHomeBinding.bindScrollAction(
        adapter: RepositoriesPagingAdapter,
        uiState: StateFlow<UiState>,
        onScrollChange: (UiAction.Scroll) -> Unit
    ) {
        rvRepository.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy != 0) onScrollChange(UiAction.Scroll(uiState.value.query))
                }
            }
        )

        val notLoading = adapter.loadStateFlow
            .distinctUntilChangedBy { it.source.refresh }
            .map { it.source.refresh is LoadState.NotLoading }

        val hasNotScrollForCurrentState = uiState
            .map { it.hasNotScrolledForCurrentSearch }
            .distinctUntilChanged()

        //當 adapter沒在載入，且當用戶搜尋了新值，表示用戶沒在滾動列表
        val shouldScrollToTop = combine(
            notLoading,
            hasNotScrollForCurrentState,
            Boolean::and
        ).distinctUntilChanged()

        lifecycleScope.launch {
            shouldScrollToTop.collect { shouldScroll ->
                if (shouldScroll) rvRepository.scrollToPosition(0)
            }
        }
    }

    private fun FragmentHomeBinding.bindList(
        pagingData: Flow<PagingData<RepoDateVo>>,
    ) {
        lifecycleScope.launch {
            pagingData.collectLatest { pagingAdapter.submitData(it) }
        }
        lifecycleScope.launch {
            pagingAdapter.loadStateFlow.collect { loadState ->
                bindUiWithLoadStateFlow(loadState)
                // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
                errorToaster(loadState)
            }
        }

    }

    private fun errorToaster(loadState: CombinedLoadStates) {
        val errorState = loadState.source.append as? LoadState.Error
            ?: loadState.source.prepend as? LoadState.Error
            ?: loadState.append as? LoadState.Error
            ?: loadState.prepend as? LoadState.Error
        errorState?.let {
            Toast.makeText(
                requireContext(),
                "\uD83D\uDE28 Wooops ${it.error}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun FragmentHomeBinding.bindUiWithLoadStateFlow(loadState: CombinedLoadStates) {
        val isListEmpty =
            loadState.refresh is LoadState.NotLoading && pagingAdapter.itemCount == 0
        // show no result list
        noResult.isVisible = isListEmpty
        // show prompt hint
        typePrompt.isVisible = isListEmpty && etQueryInput.text.isBlank()
        // Only show the list if refresh succeeds.
        rvRepository.isVisible = !isListEmpty
        // Show loading spinner during initial load or refresh.
        progressBar.isVisible = loadState.source.refresh is LoadState.Loading
        rvRepository.alpha = if (loadState.source.refresh is LoadState.Loading) 0.5f else 1f
        // Show the retry state if initial load or refresh fails.
        retryButton.isVisible = loadState.source.refresh is LoadState.Error
    }

    private fun FragmentHomeBinding.bindSearch(
        onQueryChange: (UiAction.Search) -> Unit
    ) {

        etQueryInput.doOnTextChanged { _, _, _, _ ->
            queryJob?.cancel()
            queryJob = lifecycleScope.launch {
                delay(500)
                updateRepoListFromInput(onQueryChange)
            }
        }
        etQueryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                updateRepoListFromInput(onQueryChange)
                true
            } else {
                false
            }
        }
    }

    private fun FragmentHomeBinding.updateRepoListFromInput(
        onQueryChanged: (UiAction.Search) -> Unit
    ) {
        etQueryInput.text.trim().let {
            if (it.isNotBlank()) {
                onQueryChanged(UiAction.Search(it.toString()))
            } else {
                typePrompt.isVisible = true
                rvRepository.isVisible = false
            }
        }
    }


    private fun setUi() {
        pagingAdapter = RepositoriesPagingAdapter()
        pagingAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        binding.rvRepository.apply {
            adapter = pagingAdapter.withLoadStateHeaderAndFooter(
                header = ReposLoadStateAdapter { pagingAdapter.retry() },
                footer = ReposLoadStateAdapter { pagingAdapter.retry() }
            )
        }

        binding.retryButton.setOnClickListener { pagingAdapter.retry() }
    }

}