package com.snapcat.ui.screen.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.snapcat.data.ResultMessage
import com.snapcat.data.SnapCatRepository
import com.snapcat.data.remote.response.ResponseGetUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

class ProfileViewModel(private val repository: SnapCatRepository) : ViewModel() {
    fun getUser(token: String, id: String): LiveData<ResultMessage<ResponseGetUser>> =
        repository.getUser(token, id)
}
