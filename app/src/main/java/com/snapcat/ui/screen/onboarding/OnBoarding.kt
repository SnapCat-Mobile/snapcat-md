package com.snapcat.ui.screen.onboarding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.WindowCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.snapcat.databinding.ActivityOnBoardingBinding

class OnBoarding : AppCompatActivity() {
    private lateinit var binding: ActivityOnBoardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            introViewPager.adapter = OnboardingAdapter(this@OnBoarding)
            TabLayoutMediator(tabs, introViewPager) { _, _ -> }.attach()

            introViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    buttonBack.visibility = if (position == 0) View.INVISIBLE else View.VISIBLE
                    buttonNext.visibility = if (position == 1) View.INVISIBLE else View.VISIBLE
                }
            })

            buttonNext.setOnClickListener{
                val nextIndex = introViewPager.currentItem + 1
                if(nextIndex < (introViewPager.adapter?.itemCount ?: 0)){
                    introViewPager.currentItem = nextIndex
                }
            }

            buttonBack.setOnClickListener{
                val prevIndex = introViewPager.currentItem - 1
                if(prevIndex >= 0){
                    introViewPager.currentItem = prevIndex
                }
            }

        }
    }
}