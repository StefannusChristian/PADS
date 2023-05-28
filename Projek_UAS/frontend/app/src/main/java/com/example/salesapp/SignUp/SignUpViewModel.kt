package com.example.salesapp.SignUp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.salesapp.API.RetrofitClient
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {

    private val _regisStat = MutableLiveData<String>()
    val regisStat : LiveData<String> get() = _regisStat

    fun regis(username : String, password : String, name : String, email: String){
        viewModelScope.launch {
            val response = RetrofitClient.login_signup_instance.postSignUp(SignupItem(username, password, name, email))

            if (response.isSuccessful){
                val status = response.body()?.status
                if (status == "success") {
                    _regisStat.value = response.body()?.message
                }else {
                    _regisStat.value = response.body()?.message
                }
            }
        }
    }

}

