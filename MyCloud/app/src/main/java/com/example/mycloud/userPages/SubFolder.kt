package com.example.mycloud.userPages

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.example.mycloud.R
import com.example.mycloud.beans.Pictures
import com.example.mycloud.utils.RecyclerViewSettings
import com.example.mycloud.utils.ShowToast
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.layout_choose_upload_model.view.*
import kotlinx.android.synthetic.main.layout_show_chosen_image.view.*
import kotlinx.android.synthetic.main.title.*
import java.io.File

class SubFolder : AppCompatActivity() {

    private val fromCamera = 1
    private val fromAlbum = 2
    lateinit var imageUri : Uri
    lateinit var outputImage : File

    private val picturesList = ArrayList<Pictures>()
    private val nullItems = ArrayList<Pictures>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_folder)
        setToolbar()

        //Recyclerveiw显示
        nullItems.add(Pictures("没有找到相关文件", BitmapFactory.decodeResource(resources,R.drawable.null_item)))
        RecyclerViewSettings.ShowAllItems(picturesList,this, mainpage_recyclerview)

        //搜索功能实现
        editText_title.addTextChangedListener{
            var userInput = editText_title.text.toString()
            if (userInput.isEmpty()){
                RecyclerViewSettings.ShowAllItems(picturesList,this, mainpage_recyclerview)
            }else{
                RecyclerViewSettings.ShowSelectedItems(userInput,nullItems,picturesList,this,mainpage_recyclerview)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //将选择的照片放置到RecyclerView
        when(requestCode){
            //显示从相册选择
            fromAlbum -> {
                val chooseFromAlbum = LayoutInflater.from(this).inflate(R.layout.layout_show_chosen_image,null)
                lateinit var storeuri : Uri
                if (resultCode == Activity.RESULT_OK && data != null){
                    data.data?.let { uri ->
                        storeuri = uri
                        Glide.with(this).load(getBitmapFromUri(uri)).into(chooseFromAlbum.imageView_ShowChosen)
                    }
                    AlertDialog.Builder(this).apply {
                        setTitle("上传文件")
                        setView(chooseFromAlbum)
                        setPositiveButton("确认"){dialog, which ->
                            var newItems = Pictures(chooseFromAlbum.editText_ShowChosen.text.toString(), getBitmapFromUri(storeuri))
                            picturesList.add(newItems)
                            RecyclerViewSettings.ShowAllItems(picturesList, MainPage(), mainpage_recyclerview)
                        }
                        setNegativeButton("取消"){ dialog, which ->}

                        show()
                    }
                }
            }
            //显示从相机拍摄
            fromCamera -> {
                val chooseFromCamera = LayoutInflater.from(this).inflate(R.layout.layout_show_chosen_image,null)
                if (resultCode == Activity.RESULT_OK){
                    val pictureFromCamera = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
                    Glide.with(this).load(pictureFromCamera).into(chooseFromCamera.imageView_ShowChosen)
                    AlertDialog.Builder(this).apply {
                        setTitle("上传文件")
                        setView(chooseFromCamera)
                        setPositiveButton("确认"){dialog, which ->
                            var newItem = Pictures(chooseFromCamera.editText_ShowChosen.text.toString(), pictureFromCamera)
                            picturesList.add(newItem)
                            RecyclerViewSettings.ShowAllItems(picturesList,MainPage(),mainpage_recyclerview)
                        }
                        setNegativeButton("取消"){ dialog, which ->}
                        show()
                    }
                }

            }
        }
    }


    //Toolbar的设置及点击事件
    private fun setToolbar(){
        setSupportActionBar(toolbar)
        //将Home键图标设置为上传
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
            R.id.add -> ShowToast(this,"准备新建")
            //点击上传按钮
            android.R.id.home -> {
                val chooseModelLayout = LayoutInflater.from(this).inflate(R.layout.layout_choose_upload_model,null)
                //相册选择按钮
                chooseModelLayout.button_ChooseFromPhotos.setOnClickListener {
                    ShowToast(this,"从相册选择")
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    intent.type="image/*"
                    startActivityForResult(intent, fromAlbum)
                }
                //照相选择按钮
                chooseModelLayout.button_ChooseFromCamera.setOnClickListener {
                    ShowToast(this,"照相选择")
                    outputImage = File(externalCacheDir,"output_image.jpg")
                    if (outputImage.exists()){
                        outputImage.delete()
                    }
                    outputImage.createNewFile()
                    imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                        FileProvider.getUriForFile(this, "com.example.mycloud.fileprovider",outputImage)
                    }else{
                        Uri.fromFile(outputImage)
                    }

                    //启动相机
                    val intent = Intent("android.media.action.IMAGE_CAPTURE")
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    startActivityForResult(intent, fromCamera)
                }
                //跳出选择框

                AlertDialog.Builder(this).apply {
                    setTitle("上传文件")
                    setView(chooseModelLayout)
                    setNegativeButton("取消"){ dialog, which ->}
                    show()
                }
            }
        }
        return true
    }

    private fun getBitmapFromUri(uri: Uri) = contentResolver.openFileDescriptor(uri, "r")?.use {
        BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
    }
}