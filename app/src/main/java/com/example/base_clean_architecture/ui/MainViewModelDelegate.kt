package com.example.base_clean_architecture.ui

import javax.inject.Inject
import kotlinx.coroutines.channels.Channel

interface MainViewModelDelegate {
    val mainNavigationActions: Channel<MainNavigationAction>
    fun goToPageHome()

}

internal class MainViewModelDelegateImpl @Inject constructor() : MainViewModelDelegate {
    override val mainNavigationActions = Channel<MainNavigationAction>(Channel.CONFLATED)



    override fun goToPageHome() {
        mainNavigationActions.trySend(MainNavigationAction.NavigateToHome)
    }

}
