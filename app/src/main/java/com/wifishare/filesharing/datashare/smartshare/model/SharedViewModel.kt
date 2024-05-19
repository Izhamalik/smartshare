package com.wifishare.filesharing.datashare.smartshare.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class SharedViewModel : ViewModel() {
    val selected = MutableLiveData<String>()

    fun select(item: String) {
        selected.value = item
    }
}
