package com.example.salesapp.Promo

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.salesapp.Home.ProductResponse
import com.example.salesapp.databinding.PromoRvListItemBinding

class PromoAdapter : RecyclerView.Adapter<PromoAdapter.PromoHolder>() {

    private val promosList = mutableListOf<ProductResponse>()

    private var onItemClickCallback: OnItemClickCallback? = null
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    inner class PromoHolder(private val binding: PromoRvListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val productImage = binding.productImage
        private val productDesc = binding.productDescription

        fun bind(productResponse: ProductResponse) {
            with(binding) {
                Glide.with(productImage.context)
                    .load(productResponse.img_link)
                    .into(productImage)
                productDesc.text = productResponse.name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromoHolder {
        val binding = PromoRvListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

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
    fun setPromos(productResponses: List<ProductResponse>) {
        promosList.clear()
        promosList.addAll(productResponses)
        notifyDataSetChanged()
    }

    interface OnItemClickCallback{
        fun onItemClicked(data: ProductResponse)
    }

}
