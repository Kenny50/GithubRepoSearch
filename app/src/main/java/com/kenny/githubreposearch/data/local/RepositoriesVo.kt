package com.kenny.githubreposearch.data.local

data class RepositoriesVo(
    val incompleteResults: Boolean,
    val repositories: List<RepoDateVo>,
    val totalCount: Int
)

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

data class Tag(
    val name: String
)

data class LicenseVo(
    val name: String
)