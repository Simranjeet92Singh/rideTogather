package com.ridetogather.ridetogather.viewModel

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel:ViewModel(),Observable {

    @Bindable
    val userid=MutableLiveData<String>()
    @Bindable
    val password=MutableLiveData<String>()


    fun Login(){

    }

    fun LoginWithFacebook(){

    }

    fun SignUp(){

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
//        TODO("Not yet implemented")
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
//        TODO("Not yet implemented")
    }
}