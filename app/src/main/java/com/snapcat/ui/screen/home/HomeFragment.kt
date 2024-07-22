package com.snapcat.ui.screen.home

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.snapcat.R
import com.snapcat.data.ResultMessage
import com.snapcat.data.ViewModelFactory
import com.snapcat.data.local.preferences.UserDataStore
import com.snapcat.data.model.CatCategory
import com.snapcat.data.remote.response.DataItem
import com.snapcat.data.remote.response.DataPrediction
import com.snapcat.databinding.FragmentHomeBinding
import com.snapcat.ui.screen.auth.AuthViewModel
import com.snapcat.ui.screen.category.CategoriesDialogFragment
import com.snapcat.ui.screen.detail.DetailJourneyDialogFragment
import com.snapcat.ui.screen.journey.JourneyDialogFragment
import com.snapcat.ui.screen.journey.JourneyViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var userDataStore: UserDataStore
    private lateinit var journeyAdapter: JourneyAdapter
    private val list = ArrayList<CatCategory>()
    private val viewModel by viewModels<JourneyViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    private val viewModel2 by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentNightMode = AppCompatDelegate.getDefaultNightMode()
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

        lifecycleScope.launch {
            userDataStore.getUserData().collect{
                binding.username.text = it.username
            }
        }
        list.addAll(getListCat())
        val layoutManagerCategory =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvCategories.layoutManager = layoutManagerCategory
        binding.rvCategories.adapter = CategoriesAdapter(list)

        val layoutManagerJourney =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rvJourney.layoutManager = layoutManagerJourney
        binding.rvJourney.adapter = journeyAdapter
        binding.rvJourney.isNestedScrollingEnabled = false

        binding.showAllCategories.setOnClickListener {
            val categoriesDialog = CategoriesDialogFragment()
            categoriesDialog.show(
                (context as AppCompatActivity).supportFragmentManager,
                "CategoriesDialog"
            )
        }
        binding.showAllJourney.setOnClickListener {
            val journeyDialog = JourneyDialogFragment()
            journeyDialog.show(
                (context as AppCompatActivity).supportFragmentManager,
                "JourneyDialog"
            )
        }
        if(currentNightMode == AppCompatDelegate.MODE_NIGHT_YES){
            val backgroundDrawable = binding.profileImage.background as? GradientDrawable

            if (backgroundDrawable != null) {
                backgroundDrawable.setStroke(2, Color.WHITE) // 2dp stroke width
                binding.profileImage.background = backgroundDrawable
            }
        }
        if(currentNightMode == AppCompatDelegate.MODE_NIGHT_NO){
            val backgroundDrawable = binding.profileImage.background as? GradientDrawable

            if (backgroundDrawable != null) {
                backgroundDrawable.setStroke(2, Color.BLACK) // 2dp stroke width
                binding.profileImage.background = backgroundDrawable
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        })

        lifecycleScope.launch {
            userDataStore.getUserData().collect {
                viewModel.getAllHistories(it.token, it.userId).observe(viewLifecycleOwner) {
                    if (it != null) {
                        when (it) {
                            is ResultMessage.Success -> {
                                binding.apply {
                                    rvJourney.layoutManager = LinearLayoutManager(context)
                                    rvJourney.setHasFixedSize(true)
                                    journeyAdapter.submitList(it.data.dataItem)
                                    rvJourney.adapter = journeyAdapter
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
        lifecycleScope.launch {
            userDataStore.getUserData().collect{ datastore ->
                viewModel2.getUser(datastore.token, datastore.userId).observe(viewLifecycleOwner){ user ->
                    if(user != null){
                        when(user){
                            is ResultMessage.Success -> {

                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    }

    private fun getListCat(): ArrayList<CatCategory> {
        val dataName = resources.getStringArray(R.array.data_name)
        val dataPhoto = resources.obtainTypedArray(R.array.data_photo)
        val listHero = ArrayList<CatCategory>()
        for (i in 0 until 4) {
            val hero = CatCategory(dataName[i], dataPhoto.getResourceId(i, -1))
            listHero.add(hero)
        }
        return listHero
    }
}
