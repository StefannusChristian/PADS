package com.example.salesapp.Home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.init
import com.example.salesapp.R
import com.example.salesapp.SharedViewModel.SharedViewModel
import com.example.salesapp.databinding.HomeRvListItemBinding
import com.example.salesapp.databinding.HomeProductPopupBinding

class HomePageAdapter(private val context: Context?, private val sharedViewModel: SharedViewModel) : RecyclerView.Adapter<HomePageAdapter.HomeViewHolder>() {

    private lateinit var popupBinding: HomeProductPopupBinding
    private val productResponseList = mutableListOf<ProductResponse>()
    private var onItemClickCallback: OnItemClickCallback? = null


    interface OnItemClickCallback{
        fun onItemClicked(data: ProductResponse)
        fun onAddToCartClicked(salesUsername: String, productId: Int, qty:Int)
    }
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    inner class HomeViewHolder(private val binding: HomeRvListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val productDesc = binding.productDescription
        private val productPriceNoDisc = binding.noPromoPrice
        private val productDisc = binding.promoPercentage

        init {
            val inflater = LayoutInflater.from(binding.root.context)
            popupBinding = HomeProductPopupBinding.inflate(inflater)
            val addToCartBtn: Button = popupBinding.addToCartBtn
            val qtyEditText: EditText = popupBinding.addToCartQty
            addToCartBtn.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val product = productResponseList[position]
                    val qty = qtyEditText.text.toString().toIntOrNull() ?: 0
                    onItemClickCallback?.onAddToCartClicked(
                        sharedViewModel.salesUsername,product.id,qty
                    )
                }
            }
        }

        fun bind(productResponse: ProductResponse) {
            with(binding) {
                Glide.with(productImage.context)
                    .load(productResponse.img_link)
                    .into(productImage)
                binding.root.setOnClickListener{onItemClickCallback?.onItemClicked(productResponse)}
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

    override fun getItemCount(): Int = productResponseList.size

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val product = productResponseList[position]

        holder.itemView.setOnClickListener {
            onItemClickCallback?.onItemClicked(product)
        }

        holder.bind(product)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setProducts(productResponses: List<ProductResponse>) {
        productResponseList.clear()
        productResponseList.addAll(productResponses)
        notifyDataSetChanged()
    }


}