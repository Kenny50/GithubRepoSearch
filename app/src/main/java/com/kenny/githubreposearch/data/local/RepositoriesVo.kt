package com.kenny.githubreposearch.data.local

import androidx.annotation.Keep

@Keep
data class RepositoriesVo(
    val incompleteResults: Boolean,
    val repositories: List<RepoDateVo>,
    val totalCount: Int
)

@Keep
data class RepoDateVo(
    val repositoryName: String,
    val startCount: Int,
    val forkCount: Int,
    val description: String?,
    val license: LicenseVo?,
    val tags: List<Tag>?,
    val authorAvatar: String?,
    val authorName: String,
    val id: Int
)

@Keep
data class Tag(
    val name: String
)

@Keep
data class LicenseVo(
    val name: String
)