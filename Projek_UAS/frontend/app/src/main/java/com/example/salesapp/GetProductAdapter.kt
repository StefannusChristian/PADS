package com.example.salesapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GetProductAdapter (private val list: ArrayList<GetProductResponse>):
    RecyclerView.Adapter<GetProductAdapter.GetProductsViewHolder>() {
    inner class GetProductsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val productsTv: TextView = itemView.findViewById(R.id.products_tv)
        fun bind(postResponse: GetProductResponse){
            with(itemView){
                val text = "${postResponse.data}\n"
                productsTv.text = text
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GetProductsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.get_products,parent,false)
        return GetProductsViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: GetProductsViewHolder, position: Int) {
        holder.bind(list[position])
    }
}