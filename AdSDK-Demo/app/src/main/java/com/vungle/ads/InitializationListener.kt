package com.vungle.ads

public  interface InitializationListener {
    fun onSuccess()
    fun onError(vungleError: VungleError)
}