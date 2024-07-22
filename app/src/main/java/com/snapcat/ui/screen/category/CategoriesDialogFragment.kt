package com.snapcat.ui.screen.category

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.snapcat.R
import com.snapcat.data.model.CatCategory
import com.snapcat.data.remote.response.Data
import com.snapcat.databinding.FragmentBottomCategoriesBinding
import com.snapcat.ui.screen.auth.verifikasi.VerifikasiDialogFragment
import com.snapcat.ui.screen.home.CategoriesAdapter
import com.snapcat.ui.screen.home.CategoriesAdapter2
import com.snapcat.ui.screen.shop.ShopAdapter

class CategoriesDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {

    private var binding: FragmentBottomCategoriesBinding? = null
    private var isFunctionEnabled = false
    private lateinit var categoriesAdapter2: CategoriesAdapter2
    private val filteredCategories: MutableList<CatCategory> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentBottomCategoriesBinding.inflate(inflater, container, false)
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
        categoriesAdapter2 = CategoriesAdapter2(filteredCategories)
        val bottomSheet: FrameLayout =
            dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
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
        filteredCategories.addAll(getListCat())

        val spanCount = 3
        val layoutManagerCategory = GridLayoutManager(requireActivity(), spanCount)
        layoutManagerCategory.orientation = GridLayoutManager.VERTICAL
        binding?.rvCategoriesAll?.layoutManager = layoutManagerCategory
        binding?.rvCategoriesAll?.adapter = categoriesAdapter2
        binding?.closeLogin?.setOnClickListener {
            dismiss()
        }
        binding?.buttonSearch?.setOnClickListener {
            isFunctionEnabled = !isFunctionEnabled
            binding?.cardView?.visibility = if (isFunctionEnabled) View.VISIBLE else View.GONE
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
                verifikasiDialog.show(
                    (context as AppCompatActivity).supportFragmentManager,
                    "ForgetDialog"
                )
            }
            R.id.button_search -> {

            }
        }
    }

    private fun getListCat(): ArrayList<CatCategory> {
        val dataName = resources.getStringArray(R.array.data_name)
        val dataPhoto = resources.obtainTypedArray(R.array.data_photo)
        val listHero = ArrayList<CatCategory>()
        for (i in dataName.indices) {
            val hero = CatCategory(dataName[i], dataPhoto.getResourceId(i, -1))
            listHero.add(hero)
        }
        return listHero
    }

}
