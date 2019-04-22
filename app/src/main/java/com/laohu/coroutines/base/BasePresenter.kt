package com.laohu.coroutines.base

import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers

open class BasePresenter<V: MvpView> : MvpPresenter<V> {
    lateinit var view: V
    val presenterScope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Main + Job())
    }

    override fun attachView(view: V) {
        this.view = view
    }

    override fun detachView() {
        presenterScope.cancel()
    }
}