package com.jparnaudo.p22admin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jparnaudo.p22admin.Usuario
import com.jparnaudo.p22admin.domain.data.Repo

class MainViewModel : ViewModel() {

    val repo = Repo()
    fun fetchUserData(): LiveData<MutableList<Usuario>> {

        val mutableData = MutableLiveData<MutableList<Usuario>>()
        repo.getUserData().observeForever { userList ->
            mutableData.value = userList
        }
        return mutableData
    }
}