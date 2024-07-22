package com.snapcat.ui.screen.shop

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.snapcat.data.ResultMessage
import com.snapcat.data.ViewModelFactory
import com.snapcat.data.local.preferences.UserDataStore
import com.snapcat.data.remote.response.Data
import com.snapcat.databinding.FragmentShopBinding
import com.snapcat.ui.screen.auth.AuthViewModel
import com.snapcat.ui.screen.home.JourneyAdapter
import com.snapcat.util.ToastUtils
import kotlinx.coroutines.launch
import java.util.Locale

class ShopFragment : Fragment() {
    private lateinit var binding: FragmentShopBinding
    private var isFunctionEnabled = false
    private lateinit var shopAdapter: ShopAdapter
    private lateinit var userDataStore: UserDataStore
    private val viewModel by viewModels<ShopViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentShopBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        shopAdapter = ShopAdapter()

        userDataStore = UserDataStore.getInstance(requireContext())

        binding.buttonSearch.setOnClickListener {
            isFunctionEnabled = !isFunctionEnabled
            binding.cardView.visibility = if (isFunctionEnabled) View.VISIBLE else View.GONE
            binding.hero.visibility = if (isFunctionEnabled) View.GONE else View.VISIBLE
            if(isFunctionEnabled){
                binding.linearLayout5.visibility = View.VISIBLE
                binding.heroShop.setPadding(16,0,16,0)
            }else{
                binding.linearLayout5.visibility = View.GONE
                binding.heroShop.setPadding(16,20,16,0)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        })

        lifecycleScope.launch {
            userDataStore.getUserData().collect{
                viewModel.getAllShop(it.token, it.userId).observe(viewLifecycleOwner){
                    if(it != null){
                        when(it){
                            is ResultMessage.Success -> {
                                binding.apply{
                                    rvShop.layoutManager = GridLayoutManager(context, 2)
                                    rvShop.setHasFixedSize(true)
                                    shopAdapter.submitList(it.data.data)
                                    rvShop.adapter = shopAdapter
                                    binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                                        override fun onQueryTextSubmit(query: String?): Boolean {
                                            return false
                                        }

                                        override fun onQueryTextChange(newText: String?): Boolean {
                                            var xx: List<Data> = it.data.data.filter { data ->
                                                data.name.contains(newText.orEmpty(), ignoreCase = true)
                                            }
                                            if(newText != ""){
                                                shopAdapter.submitList(xx)
                                            }else{
                                                shopAdapter.submitList(it.data.data)
                                            }
                                            return true
                                        }
                                    })
                                }
                            }

                            is ResultMessage.Error -> {
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    }
}

