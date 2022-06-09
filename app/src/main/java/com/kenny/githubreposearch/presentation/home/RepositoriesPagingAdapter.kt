package com.kenny.githubreposearch.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kenny.githubreposearch.data.local.RepoDateVo
import com.kenny.githubreposearch.databinding.VhRepositoryBinding

class RepositoriesPagingAdapter :
    PagingDataAdapter<RepoDateVo, RepositoriesPagingAdapter.RepositoryViewHolder>(
        RepositoryDiff
    ) {

    class RepositoryViewHolder(binding: VhRepositoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val fake = binding.fake
        private val index = binding.index

        fun bindData(data: RepoDateVo, position: Int) {
            fake.text = data.repositoryName
            index.text = position.toString()
        }
    }

    override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
        getItem(position)?.apply {
            holder.bindData(this, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder {
        return RepositoryViewHolder(
            VhRepositoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    companion object {
        val RepositoryDiff = object : DiffUtil.ItemCallback<RepoDateVo>() {
            override fun areItemsTheSame(oldItem: RepoDateVo, newItem: RepoDateVo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: RepoDateVo, newItem: RepoDateVo): Boolean {
                return oldItem == newItem
            }

        }
    }
}