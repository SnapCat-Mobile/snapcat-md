package com.snapcat.ui.screen.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.snapcat.data.ResultMessage
import com.snapcat.data.SnapCatRepository
import com.snapcat.data.model.History
import com.snapcat.data.remote.response.ResponseGetUser
import com.snapcat.data.remote.response.ResponseHistoryById
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

class DetailViewModel(private val repository: SnapCatRepository) : ViewModel() {
    fun saveToHistory(token: String, history: History) = repository.saveToHistory(token, history)

    fun getHistoryById(token: String, id: String): LiveData<ResultMessage<ResponseHistoryById>> =
        repository.getHistoryById(token, id)
}
