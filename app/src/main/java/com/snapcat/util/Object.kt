package com.snapcat.util

import com.snapcat.ui.screen.detail.DetailJourneyDialogFragment
import com.snapcat.ui.screen.scan.OnDialogDismissListener

class Object {
    companion object {
        fun newInstanceFragmentResultScan(listener: OnDialogDismissListener): DetailJourneyDialogFragment {
            val fragment = DetailJourneyDialogFragment()
            fragment.onDialogDismissed = listener
            return fragment
        }
    }
}