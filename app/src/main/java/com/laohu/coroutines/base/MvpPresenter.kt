package com.laohu.coroutines.base

import android.support.annotation.UiThread

interface MvpPresenter<V: MvpView> {

    @UiThread
    fun attachView(view: V)

    @UiThread
    fun detachView()
}