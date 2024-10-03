package com.example.base_clean_architecture.ui

import androidx.lifecycle.viewModelScope
import com.example.base_clean_architecture.data.data_source.local.AppPreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import com.example.base_clean_architecture.common.base.BaseViewModel

@HiltViewModel
class MainViewModel @Inject constructor(
    mainViewModelDelegate: MainViewModelDelegate,
    private val appPreManager: AppPreManager
) : BaseViewModel(), MainViewModelDelegate by mainViewModelDelegate {

    private val _appPreManager = MutableStateFlow(appPreManager)
    val appPre: StateFlow<AppPreManager>
        get() = _appPreManager

    init {
        viewModelScope.launch {
            _appPreManager.emit(appPreManager)
        }
    }

    val navigationActions = mainNavigationActions.receiveAsFlow()
}

sealed class MainNavigationAction {
    object NavigateToHome : MainNavigationAction()
}
