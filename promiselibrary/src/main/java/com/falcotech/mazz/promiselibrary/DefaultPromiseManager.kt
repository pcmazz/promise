package com.falcotech.mazz.promiselibrary

import androidx.annotation.CallSuper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class DefaultPromiseManager : PromiseManager{

    private val coldList: ArrayList<Promise<*>> = arrayListOf()

    @CallSuper
    @Synchronized
    override suspend fun <T> async(promise: Promise<T>): Promise<T> {
        coldList.add(promise)
        promise.invokeOnCompletion { coldList.remove(promise) }
        return promise
    }

    @CallSuper
    @Synchronized
    override suspend fun <T> asyncAwait(promise: Promise<T>): T {
        return async(promise).await()
    }

    @CallSuper
    @Synchronized
    override fun cancelAllPromises() {
        if(coldList.isNotEmpty()){
            coldList.forEach {
                it.cancel()
            }
        }
    }

    @CallSuper
    @Synchronized
    override fun cleanUp() {
        cancelAllPromises()
    }
}