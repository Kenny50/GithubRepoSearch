package com.kenny.githubreposearch.presentation.repo_load_state

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.kenny.githubreposearch.R
import com.kenny.githubreposearch.databinding.RepoLoadStateFooterItemBinding

class ReposLoadStateViewHolder(
    private val binding: RepoLoadStateFooterItemBinding,
    retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.retryButton.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.errorMsg.text = loadState.error.localizedMessage
        }
        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.retryButton.isVisible = loadState is LoadState.Error
        binding.errorMsg.isVisible = loadState is LoadState.Error
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): ReposLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.repo_load_state_footer_item, parent, false)
            val binding = RepoLoadStateFooterItemBinding.bind(view)
            return ReposLoadStateViewHolder(binding, retry)
        }
    }
}