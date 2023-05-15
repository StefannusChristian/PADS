package com.example.salesapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.salesapp.databinding.HomeListItemBinding

class HomePageAdapter(private val list: ArrayList<HomeResponse>) :
    RecyclerView.Adapter<HomePageAdapter.HomeViewHolder>() {

    inner class HomeViewHolder(private val binding: HomeListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val productImage = binding.productImage
        private val productDesc = binding.productDescription

        fun bind(response: HomeResponse) {
            with(binding) {
                Glide.with(productImage.context)
                    .load(response.imageUrl)
                    .into(productImage)
                productDesc.text = response.description
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = HomeListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(list[position])
    }
}
