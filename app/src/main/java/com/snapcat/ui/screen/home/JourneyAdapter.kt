package com.snapcat.ui.screen.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.bumptech.glide.Glide
import com.snapcat.data.remote.response.DataItem
import com.snapcat.databinding.ItemJourneyBinding


class JourneyAdapter(private val onClick: (DataItem) -> Unit)
    : ListAdapter<DataItem, JourneyAdapter.MyViewHolder>(DIFF_CALLBACK){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemJourneyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val review = getItem(position)
        holder.bind(review, onClick)
    }

    class MyViewHolder(val binding: ItemJourneyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DataItem, onClick: (DataItem) -> Unit){
            binding.apply{
                titleItemJourney.text = data.breed
                timeItemJourney.text = "${data.createdAt.take(10)}"
                imgPhotoJourney.load(data.image){

                }
                detailJourney.setOnClickListener {
                    onClick(data)
                }


//                Glide.with(itemView.context)
//                    .load(data.image) // URL Gambar
//                    .into(imgPhotoJourney)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataItem>() {
            override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
