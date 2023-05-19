package com.example.salesapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.salesapp.databinding.HomeListItemBinding

class HomePagePromoAdapter : RecyclerView.Adapter<HomePagePromoAdapter.HomeViewHolder>() {

    private val promosList = mutableListOf<Product>()

    inner class HomeViewHolder(private val binding: HomeListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val productImage = binding.productImage
        private val productDesc = binding.productDescription

        fun bind(product: Product) {
            with(binding) {
                Glide.with(productImage.context)
                    .load(product.img_link)
                    .into(productImage)
                productDesc.text = product.name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = HomeListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun getItemCount(): Int = promosList.size

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val promos = promosList[position]
        holder.bind(promos)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setPromos(products: List<Product>) {
        promosList.clear()
        promosList.addAll(products)
        notifyDataSetChanged()
    }
}
