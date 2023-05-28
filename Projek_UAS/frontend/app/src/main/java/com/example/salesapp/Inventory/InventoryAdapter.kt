package com.example.salesapp.Inventory

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.salesapp.Home.ProductResponse
import com.example.salesapp.R
import com.example.salesapp.databinding.InventoryRvListItemBinding

class InventoryAdapter: RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val binding = InventoryRvListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InventoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return prodList.size
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val currentItem = prodList[position]
        val context = holder.itemView.context
        holder.nameprod.text = currentItem.name
        holder.availqty.text = context.getString(R.string.avail_qty, currentItem.available_qty.toString())
        holder.orderqty.text = context.getString(R.string.ordered_qty, currentItem.ordered_qty.toString())
        holder.totalqty.text = context.getString(R.string.total_qty, currentItem.total_qty.toString())

        Glide.with(context)
            .load(currentItem.img_link)
            .into(holder.binding.titleImage)
    }

    class InventoryViewHolder(val binding: InventoryRvListItemBinding) : RecyclerView.ViewHolder(binding.root){
        val nameprod : TextView = binding.namaprod
        val availqty : TextView = binding.availqty
        val orderqty : TextView = binding.orderqty
        val totalqty : TextView = binding.totalqty
    }

    private val diffCallback = object : DiffUtil.ItemCallback<ProductResponse>(){
        override fun areItemsTheSame(oldItem: ProductResponse, newItem: ProductResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductResponse, newItem: ProductResponse): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var prodList: List<ProductResponse>
        get() = differ.currentList
        set(value) {differ.submitList(value)}
}