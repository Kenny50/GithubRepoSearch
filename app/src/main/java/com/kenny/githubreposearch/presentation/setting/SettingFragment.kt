package com.kenny.githubreposearch.presentation.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.kenny.githubreposearch.databinding.FragmentSettingBinding
import com.kenny.githubreposearch.presentation.BindingFragment
import com.kenny.githubreposearch.util.DataStoreManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : BindingFragment<FragmentSettingBinding>() {

    override val bindingInflater: (LayoutInflater) -> FragmentSettingBinding
        get() = FragmentSettingBinding::inflate

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            updateUiByDataStore()
        }
        setUi()
    }

    private fun setUi() {
        binding.swAutoSearch.setOnCheckedChangeListener { _, b ->
            lifecycleScope.launch {
                dataStoreManager.writeAutoSearchEnable(b)
                updateUiByDataStore()
            }
        }
    }

    private suspend fun updateUiByDataStore() {
        dataStoreManager.readAutoSearchEnable().let {
            binding.swAutoSearch.isChecked = it
        }
    }
}