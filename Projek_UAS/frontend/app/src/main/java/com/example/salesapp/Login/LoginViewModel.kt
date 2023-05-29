package com.example.salesapp.Login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.salesapp.API.RetrofitClient
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {

    private val _verificationStatus = MutableLiveData<Boolean>()
    val verificationStatus: LiveData<Boolean> get() = _verificationStatus

    fun postlogin(salesUsername : String, salesPassword : String){
        viewModelScope.launch {
            val response = RetrofitClient.login_signup_instance.postLogin(LoginItem(salesUsername, salesPassword))
            if (response.isSuccessful){
                val sales_username = response.body()?.sales_username
                _verificationStatus.value = sales_username != null
            }
        }
    }

    fun logout(salesUsername : String){
        viewModelScope.launch {
            Log.d("HomeFragment","Masuk Sini!")
            val response = RetrofitClient.login_signup_instance.postLogout(LogOutRequest(salesUsername))
            Log.d("HomeFragment",response.toString()+" ini response di atas isSukses")
            if (response.isSuccessful){
                Log.d("HomeFragment",response.body().toString())
            }
        }
    }

}