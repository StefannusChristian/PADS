package com.example.salesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val productList = ArrayList<GetProductResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showProducts()
    }

    private fun showProducts() {
        val rv_Post: RecyclerView = findViewById(R.id.rvPost)
        rv_Post.setHasFixedSize(true)
        rv_Post.layoutManager = LinearLayoutManager(this)
        RetrofitClient.instance.getAllProducts().enqueue(object:
            Callback<ArrayList<GetProductResponse>> {
            val tv_response_code: TextView = findViewById(R.id.tvResponseCode)
            override fun onResponse(
                call: Call<ArrayList<GetProductResponse>>,
                response: Response<ArrayList<GetProductResponse>>
            ) {
                tv_response_code.text = response.code().toString()
                response.body()?.let { productList.addAll(it) }
                val adapter = GetProductAdapter(productList)
                rv_Post.adapter = adapter
            }

            override fun onFailure(call: Call<ArrayList<GetProductResponse>>, t: Throwable) {
                tv_response_code.text = t.message
            }

        })
    }

}