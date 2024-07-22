package com.snapcat.ui.screen.shop

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.snapcat.data.ResultMessage
import com.snapcat.data.SnapCatRepository
import com.snapcat.data.remote.response.ResponseGetAllShop
import retrofit2.Response

class ShopViewModel(private val repository: SnapCatRepository) : ViewModel() {

    fun getAllShop(token: String, id: String): LiveData<ResultMessage<ResponseGetAllShop>> =
        repository.getAllShop(token, id)
}