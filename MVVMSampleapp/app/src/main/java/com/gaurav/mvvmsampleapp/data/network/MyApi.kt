package com.gaurav.mvvmsampleapp.data.network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface MyApi {

    //this will put the value of email and password
    // from this to api and from api get response
    @FormUrlEncoded
    @POST("login")
    fun userLogin(
        @Field("email") email:String,
        @Field("password") password:String
    ): Call<ResponseBody>

    //it will create Api obbject
    //and companion object will work as static
    //here yo don't need to create instance you call through with name only
    companion object{
        operator fun invoke(): MyApi{
            return Retrofit.Builder()
                .baseUrl("")
        }
    }
}