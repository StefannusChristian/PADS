package com.example.salesapp.Home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.salesapp.R
import com.example.salesapp.databinding.HomeRvListItemBinding

class HomePagePromoAdapter(private val context: Context?) : RecyclerView.Adapter<HomePagePromoAdapter.HomeViewHolder>() {

    private val promosList = mutableListOf<ProductResponse>()

    private var onItemClickCallback: OnItemClickCallback? = null
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    inner class HomeViewHolder(private val binding: HomeRvListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val productDesc = binding.productDescription
        private val productPriceNoDisc = binding.noPromoPrice
        private val productDisc = binding.promoPercentage


        fun bind(productResponse: ProductResponse) {
            with(binding) {
                Glide.with(productImage.context)
                    .load(productResponse.img_link)
                    .into(productImage)
                productDesc.text = productResponse.name
                productPrice.text =
                    context?.getString(R.string.price_tag,productResponse.promo_price.toString()) ?: ""
                productPriceNoDisc.text = context?.getString(R.string.price_tag,productResponse.price.toString()) ?: ""
                productDisc.text = context?.getString(R.string.promo_percent_string,productResponse.promo.toString()) ?: ""
                productPriceNoDisc.paintFlags = productPriceNoDisc.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = HomeRvListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun getItemCount(): Int = promosList.size

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val promos = promosList[position]
        holder.bind(promos)

        holder.itemView.setOnClickListener {
            onItemClickCallback?.onItemClicked(promos)
        }

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
