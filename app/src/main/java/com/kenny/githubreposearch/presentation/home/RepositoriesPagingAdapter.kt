package com.kenny.githubreposearch.presentation.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.kenny.githubreposearch.R
import com.kenny.githubreposearch.data.local.RepoDateVo
import com.kenny.githubreposearch.databinding.VhRepositoryBinding

class RepositoriesPagingAdapter(
    private val context: Context
) : PagingDataAdapter<RepoDateVo, RepositoriesPagingAdapter.RepositoryViewHolder>(RepositoryDiff) {

    class RepositoryViewHolder(
        binding: VhRepositoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val avatar = binding.imgAvatar
        private val title = binding.tvRepoTitle
        private val author = binding.tvAuthorName
        private val desc = binding.tvDesc
        private val tagsGroup = binding.tagsGroup
        private val dvdTags = binding.dvdDescTag

        fun bindData(data: RepoDateVo, context: Context) {
            data.apply {
                title.text = repositoryName
                desc.text = description
                author.text = context.resources.getString(R.string.author, authorName)
                dvdTags.isVisible = !tags.isNullOrEmpty()
                tagsGroup.removeAllViews()

                tags?.map {
                    Chip(context).apply { text = it.name }
                }?.forEach {
                    tagsGroup.addView(it)
                }

                Glide.with(context)
                    .load(authorAvatar)
                    .placeholder(R.drawable.ic_img_placeholder_24)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(avatar)

            }
        }
    }

    override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
        getItem(position)?.apply {
            holder.bindData(this, context = context)
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