package com.example.salesapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.salesapp.databinding.HomeListItemBinding
import com.example.salesapp.databinding.PromoItemBinding

class PromoAdapter : RecyclerView.Adapter<PromoAdapter.PromoHolder>() {

    private val promosList = mutableListOf<Product>()

    private var onItemClickCallback: OnItemClickCallback? = null
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    inner class PromoHolder(private val binding: PromoItemBinding) :
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromoHolder {
        val binding = PromoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return PromoHolder(binding)
    }

    override fun getItemCount(): Int = promosList.size

    override fun onBindViewHolder(holder: PromoHolder, position: Int) {
        val promos = promosList[position]

        holder.itemView.setOnClickListener {
            onItemClickCallback?.onItemClicked(promos)
        }

        holder.bind(promos)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setPromos(products: List<Product>) {
        promosList.clear()
        promosList.addAll(products)
        notifyDataSetChanged()
    }

    interface OnItemClickCallback{
        fun onItemClicked(data: Product)
    }

}
