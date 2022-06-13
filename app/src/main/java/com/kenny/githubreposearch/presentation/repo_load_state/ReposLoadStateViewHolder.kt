package com.kenny.githubreposearch.presentation.repo_load_state

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.kenny.githubreposearch.R
import com.kenny.githubreposearch.databinding.VhRepoLoadStateFooterItemBinding

class ReposLoadStateViewHolder private constructor(
    private val binding: VhRepoLoadStateFooterItemBinding,
    retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.btnRetry.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.tvErrorMsg.text = loadState.error.localizedMessage
        }
        binding.pgsCircle.isVisible = loadState is LoadState.Loading
        binding.btnRetry.isVisible = loadState is LoadState.Error
        binding.tvErrorMsg.isVisible = loadState is LoadState.Error
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): ReposLoadStateViewHolder {
            val binding = VhRepoLoadStateFooterItemBinding.bind(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.vh_repo_load_state_footer_item, parent, false)
            )
            return ReposLoadStateViewHolder(binding, retry)
        }
    }
}