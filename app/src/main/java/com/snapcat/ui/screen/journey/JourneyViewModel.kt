package com.snapcat.ui.screen.journey

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.snapcat.data.ResultMessage
import com.snapcat.data.SnapCatRepository
import com.snapcat.data.remote.response.ResponseGetAllHistories

class JourneyViewModel(private val repository: SnapCatRepository) : ViewModel() {
    fun getAllHistories(token: String, id: String): LiveData<ResultMessage<ResponseGetAllHistories>> =
        repository.getAllHistories(token, id)
}
