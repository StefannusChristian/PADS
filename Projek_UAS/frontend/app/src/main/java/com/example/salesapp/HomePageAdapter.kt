package com.example.salesapp

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.salesapp.databinding.HomeListItemBinding

class HomePageAdapter : RecyclerView.Adapter<HomePageAdapter.HomeViewHolder>() {

    private val productList = mutableListOf<Product>()

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

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setProducts(products: List<Product>) {
        productList.clear()
        productList.addAll(products)
        notifyDataSetChanged()
    }


}