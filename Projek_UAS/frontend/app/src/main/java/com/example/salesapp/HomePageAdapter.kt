package com.example.salesapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.salesapp.databinding.HomeListItemBinding
import com.example.salesapp.databinding.HomeProductPopupBinding

class HomePageAdapter(private val homeViewModel: HomeViewModel) : RecyclerView.Adapter<HomePageAdapter.HomeViewHolder>() {

    private lateinit var popupBinding: HomeProductPopupBinding
    private val productList = mutableListOf<Product>()
    private var onItemClickCallback: OnItemClickCallback? = null


    interface OnItemClickCallback{
        fun onItemClicked(data: Product)
        fun onAddToCartClicked(salesUsername: String, productId: Int, qty:Int)
    }
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    inner class HomeViewHolder(private val binding: HomeListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val productImage = binding.productImage
        private val productDesc = binding.productDescription
        init {
            val inflater = LayoutInflater.from(binding.root.context)
            popupBinding = HomeProductPopupBinding.inflate(inflater)
            val addToCartBtn: Button = popupBinding.addToCartBtn
            val qtyEditText: EditText = popupBinding.addToCartQty
            addToCartBtn.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val product = productList[position]
                    val qty = qtyEditText.text.toString().toIntOrNull() ?: 0
                    onItemClickCallback?.onAddToCartClicked(
                        homeViewModel.salesUsername,product.id,qty
                    )
                }
            }
        }

        fun bind(product: Product) {
            val requestOptions = RequestOptions().transform(RoundedCorners(8))
            with(binding) {
                Glide.with(productImage.context)
                    .load(product.img_link)
                    .apply(requestOptions)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(productImage)
                binding.root.setOnClickListener{onItemClickCallback?.onItemClicked(product)}
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

        holder.itemView.setOnClickListener {
            onItemClickCallback?.onItemClicked(product)
        }

        holder.bind(product)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setProducts(products: List<Product>) {
        productList.clear()
        productList.addAll(products)
        notifyDataSetChanged()
    }


}