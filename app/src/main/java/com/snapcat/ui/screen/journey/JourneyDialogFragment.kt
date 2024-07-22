package com.snapcat.ui.screen.journey

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.snapcat.R
import com.snapcat.data.ResultMessage
import com.snapcat.data.ViewModelFactory
import com.snapcat.data.local.preferences.UserDataStore
import com.snapcat.data.remote.response.Data
import com.snapcat.data.remote.response.DataItem
import com.snapcat.data.remote.response.DataPrediction
import com.snapcat.databinding.FragmentBottomCategoriesBinding
import com.snapcat.databinding.FragmentBottomForgetPasswordBinding
import com.snapcat.databinding.FragmentBottomJourneyBinding
import com.snapcat.ui.screen.auth.verifikasi.VerifikasiDialogFragment
import com.snapcat.ui.screen.detail.DetailJourneyDialogFragment
import com.snapcat.ui.screen.home.CategoriesAdapter
import com.snapcat.ui.screen.home.JourneyAdapter
import com.snapcat.ui.screen.shop.ShopAdapter
import com.snapcat.ui.screen.shop.ShopViewModel
import com.snapcat.util.Object
import kotlinx.coroutines.launch

class JourneyDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {

    private var binding: FragmentBottomJourneyBinding? = null
    private lateinit var userDataStore: UserDataStore
    private lateinit var journeyAdapter: JourneyAdapter
    private var isFunctionEnabled = false
    private val viewModel by viewModels<JourneyViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomJourneyBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                dismiss()
                return@setOnKeyListener true
            }
            false
        }
        journeyAdapter = JourneyAdapter { dataItem: DataItem ->

            val data = DataPrediction(
                catBreedPredictions = dataItem.breed,
                catBreedDescription = dataItem.description,
                uploadImage = dataItem.image
            )
            val args = Bundle().apply {
                putParcelable("data_prediction", data)
                putBoolean("is_from_scan", false)
            }

            val detailDialog = DetailJourneyDialogFragment()
            detailDialog.arguments = args
            detailDialog.show(
                (context as AppCompatActivity).supportFragmentManager,
                "DetailDialog"
            )
        }

        userDataStore = UserDataStore.getInstance(requireContext())
        val bottomSheet: FrameLayout = dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
        bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT

        val behavior = BottomSheetBehavior.from(bottomSheet)
        behavior.apply {
            peekHeight = resources.displayMetrics.heightPixels
            state = BottomSheetBehavior.STATE_EXPANDED

            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {}

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }

        binding?.closeLogin?.setOnClickListener {
            dismiss()
        }

        binding?.buttonSearch?.setOnClickListener {
            // Toggle status on/off
            isFunctionEnabled = !isFunctionEnabled
            binding?.cardView?.visibility = if (isFunctionEnabled) View.VISIBLE else View.GONE
        }

        lifecycleScope.launch {
            userDataStore.getUserData().collect {
                viewModel.getAllHistories(it.token, it.userId).observe(viewLifecycleOwner) {
                    if (it != null) {
                        when (it) {
                            is ResultMessage.Success -> {
                                binding?.apply {
                                    rvJourneyAll.layoutManager = LinearLayoutManager(context)
                                    rvJourneyAll.setHasFixedSize(true)

                                    // Akses properti data terlebih dahulu, kemudian akses dataItem
                                    journeyAdapter.submitList(it.data.dataItem)

                                    rvJourneyAll.adapter = journeyAdapter
                                    searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                                        override fun onQueryTextSubmit(query: String?): Boolean {
                                            return false
                                        }

                                        override fun onQueryTextChange(newText: String?): Boolean {
                                            val xx: List<DataItem> = it.data.dataItem.filter { data ->
                                                data.breed.contains(newText.orEmpty(), ignoreCase = true)
                                            }
                                            if(newText != ""){
                                                journeyAdapter.submitList(xx)
                                            }else{
                                                journeyAdapter.submitList(it.data.dataItem)
                                            }
                                            return true
                                        }
                                    })
                                }
                            }
                            is ResultMessage.Error -> {
                                // Handle error case if needed
                            }
                            else -> {
                                // Handle other cases if needed
                            }
                        }
                    }
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.forget -> {
                val verifikasiDialog = VerifikasiDialogFragment()
                verifikasiDialog.show((context as AppCompatActivity).supportFragmentManager, "ForgetDialog")
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        // binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
