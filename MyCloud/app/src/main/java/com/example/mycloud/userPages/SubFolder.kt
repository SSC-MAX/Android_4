package com.example.mycloud.userPages

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.example.mycloud.R
import com.example.mycloud.activities.Profile
import com.example.mycloud.api.ApiService
import com.example.mycloud.app.AppContext
import com.example.mycloud.beans.PictureDTO
import com.example.mycloud.beans.Pictures
import com.example.mycloud.utils.RecyclerViewSettings
import com.example.mycloud.utils.*
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.layout_choose_upload_model.view.*
import kotlinx.android.synthetic.main.layout_show_chosen_image.view.*
import kotlinx.android.synthetic.main.title.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class SubFolder : AppCompatActivity() {

    private val fromCamera = 1
    private val fromAlbum = 2
    lateinit var imageUri : Uri
    lateinit var outputImage : File

    lateinit var  picturesList : ArrayList<Pictures>
    private val nullItems = ArrayList<Pictures>()
    lateinit var dialog:AlertDialog
    val UPLOAD_BASE_URL = "http://baidu.com/uploadPicture/"
    val GET_BASE_URL = "http://baidu.com/getPicture/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_folder)
        setToolbar()

        GetToast.ShowToast(this,"?????????????????????")
        val picId = intent.getStringExtra("pic_id")
        if (!picId!!.isEmpty()) {
            if(AppContext.cacheData.containsKey(picId)){
                picturesList = AppContext.cacheData.get(picId) as ArrayList<Pictures>
            }else{
                picturesList = ArrayList<Pictures>()
                AppContext.cacheData.put(picId,picturesList);
            }
        }
//
        //Recyclerveiw??????
        nullItems.add(
            Pictures(
                "null", "????????????????????????", BitmapFactory.decodeResource(
                    resources,
                    R.drawable.null_item
                )
            )
        )
        RecyclerViewSettings.ShowAllItems(picturesList, this, mainpage_recyclerview)

        //??????????????????
        editText_title.addTextChangedListener{
            var userInput = editText_title.text.toString()
            if (userInput.isEmpty()){
                RecyclerViewSettings.ShowAllItems(picturesList, this, mainpage_recyclerview)
            }else{
                RecyclerViewSettings.ShowSelectedItems(
                    userInput,
                    nullItems,
                    picturesList,
                    this,
                    mainpage_recyclerview
                )
            }
        }

        getPictrue()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //???????????????????????????RecyclerView
        when(requestCode){
            //?????????????????????
            fromAlbum -> {
                val chooseFromAlbum = LayoutInflater.from(this).inflate(
                    R.layout.layout_show_chosen_image,
                    null
                )
                lateinit var storeuri: Uri
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { uri ->
                        storeuri = uri
                        Glide.with(this).load(getBitmapFromUri(uri))
                            .into(chooseFromAlbum.imageView_ShowChosen)
                    }
                    AlertDialog.Builder(this).apply {
                        setTitle("????????????")
                        setView(chooseFromAlbum)
                        setPositiveButton("??????") { dialog, which ->
                            var newItems = Pictures(
                                UUID.randomUUID().toString(),
                                chooseFromAlbum.editText_ShowChosen.text.toString(),
                                getBitmapFromUri(
                                    storeuri
                                )
                            )
                            picturesList.add(newItems)
                            RecyclerViewSettings.ShowAllItems(
                                picturesList,
                                MainPage(),
                                mainpage_recyclerview
                            )
                        }
                        setNegativeButton("??????") { dialog, which -> }

                        show()
                    }
                }
            }
            //?????????????????????
            fromCamera -> {
                val chooseFromCamera = LayoutInflater.from(this).inflate(
                    R.layout.layout_show_chosen_image,
                    null
                )
                if (resultCode == Activity.RESULT_OK) {
                    val pictureFromCamera = BitmapFactory.decodeStream(
                        contentResolver.openInputStream(
                            imageUri
                        )
                    )
                    Glide.with(this).load(pictureFromCamera)
                        .into(chooseFromCamera.imageView_ShowChosen)
                    AlertDialog.Builder(this).apply {
                        setTitle("????????????")
                        setView(chooseFromCamera)
                        setPositiveButton("??????") { dialog, which ->
                            var newItem = Pictures(
                                UUID.randomUUID().toString(),
                                chooseFromCamera.editText_ShowChosen.text.toString(),
                                pictureFromCamera
                            )
                            picturesList.add(newItem)
                            RecyclerViewSettings.ShowAllItems(
                                picturesList,
                                MainPage(),
                                mainpage_recyclerview
                            )
                        }
                        setNegativeButton("??????") { dialog, which -> }
                        show()
                    }
                }

            }
        }
    }


    //Toolbar????????????????????????
    private fun setToolbar(){
        setSupportActionBar(toolbar)
        //???Home????????????????????????
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_upload)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

            //??????????????????
            android.R.id.home -> {
                val chooseModelLayout = LayoutInflater.from(this).inflate(
                    R.layout.layout_choose_upload_model,
                    null
                )
                //??????????????????
                chooseModelLayout.button_ChooseFromPhotos.setOnClickListener {
                    dialog.dismiss()
                    GetToast.ShowToast(this, "???????????????")
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    intent.type = "image/*"
                    startActivityForResult(intent, fromAlbum)
                }
                //??????????????????
                chooseModelLayout.button_ChooseFromCamera.setOnClickListener {
                    dialog.dismiss()
                    GetToast.ShowToast(this, "????????????")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                        outputImage = File(externalCacheDir, "output_image.jpg")
                    }
                    if (outputImage.exists()) {
                        outputImage.delete()
                    }
                    outputImage.createNewFile()
                    imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        FileProvider.getUriForFile(
                            this,
                            "com.example.mycloud.fileprovider",
                            outputImage
                        )
                    } else {
                        Uri.fromFile(outputImage)
                    }

                    //????????????
                    val intent = Intent("android.media.action.IMAGE_CAPTURE")
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    startActivityForResult(intent, fromCamera)
                }

                //???????????????
                var builder=AlertDialog.Builder(this);
                builder.setTitle("????????????")
                builder.setView(chooseModelLayout)
                builder.setNegativeButton("??????"){ dialog, which ->}
                dialog = builder.create();
                if (!dialog.isShowing) {
                    dialog.show()
                }
            }

            R.id.user -> {
                val intent = Intent(this, Profile::class.java)
                startActivity(intent)
            }
        }
        return true
    }

    private fun getBitmapFromUri(uri: Uri) = contentResolver.openFileDescriptor(uri, "r")?.use {
        BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
    }

    private fun uploadPicture(filePath:String?,image:Pictures){
        val builder = OkHttpClient.Builder()
        val client = builder.build()
        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(UPLOAD_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)
        val file = File(filePath)
        val photoRequestBody = RequestBody.create(MediaType.parse("image/jpg"), file)
        val photoPart = MultipartBody.Part.createFormData("image_file", file.name, photoRequestBody)
        val pic = RequestBody.create(MediaType.parse("text/plain"), image.id)
        val picName = RequestBody.create(MediaType.parse("text/plain"), image.name)
        val owerId = RequestBody.create(MediaType.parse("text/plain"),"")
        if (file.exists()) {
            val repos = service.uploadPicture(pic, picName, owerId, photoPart)
            repos.enqueue(object : Callback<PictureDTO> {
                override fun onResponse(call: Call<PictureDTO>?, response: Response<PictureDTO>?) {
                    if (response != null) {
                        Log.e("Main ??????", response.body().toString())
                    }

                }

                override fun onFailure(call: Call<PictureDTO>?, t: Throwable?) {
                    Log.e("Main  ??????", t.toString())

                }
            })
        }
    }

    private fun getPictrue(){
        val builder = OkHttpClient.Builder()
        val client = builder.build()
        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(GET_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)

        val repos = service.getListPicture("root")
        repos.enqueue(object : Callback<PictureDTO> {
            override fun onResponse(call: Call<PictureDTO>?, response: Response<PictureDTO>?) {
                if (response != null) {

                }

            }

            override fun onFailure(call: Call<PictureDTO>?, t: Throwable?) {
                Log.e("Main  ??????", t.toString())

            }
        })
    }
}
