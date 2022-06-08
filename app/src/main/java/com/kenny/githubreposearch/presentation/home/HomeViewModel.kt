package com.kenny.githubreposearch.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kenny.githubreposearch.domain.use_case.SearchRepositoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val searchRepositoriesUseCase: SearchRepositoriesUseCase
) : ViewModel() {

    init {
        viewModelScope.launch {
            Timber.d(searchRepositoriesUseCase("kotlin").repositories.first().toString())
        }
    }
}