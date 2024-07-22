package com.snapcat.ui.screen.scan

import androidx.lifecycle.ViewModel
import com.snapcat.data.SnapCatRepository
import com.snapcat.data.model.History
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

class ScanViewModel(private val repository: SnapCatRepository) : ViewModel() {
    fun prediction(image: File) = repository.prediction(image)
}
