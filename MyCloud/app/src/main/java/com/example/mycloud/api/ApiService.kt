package com.example.mycloud.api

import com.example.mycloud.beans.PictureDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface  ApiService {
    @Multipart
    @POST("POSTPIC")
    fun uploadPicture(
        @Part("pic_id") id: RequestBody,
        @Part("pic_name") name: RequestBody,
        @Part("ower_id") ower_id: RequestBody,
        @Part part: MultipartBody.Part ): Call<PictureDTO>

    @FormUrlEncoded
    @POST("GETPIC")
    fun getListPicture(@Field("ower_id") ower_id: String):Call<PictureDTO>
}