package com.example.mycloud.adapters

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mycloud.R
import com.example.mycloud.activities.Login
import com.example.mycloud.activities.Register

import com.example.mycloud.beans.Pictures
import com.example.mycloud.userPages.MainPage
import com.example.mycloud.userPages.SubFolder
import com.example.mycloud.utils.Checks
import com.example.mycloud.utils.*
import com.example.mycloudtest.database.AppDatabase
import kotlinx.android.synthetic.main.layout_encryption.view.*
import kotlinx.android.synthetic.main.layout_preview_picture.view.*
import kotlin.concurrent.thread


class PicturesAdapter(val pictureslist: ArrayList<Pictures>) :
    RecyclerView.Adapter<PicturesAdapter.InnerViewHolder>() {

    inner class InnerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val picturesImage: ImageView = view.findViewById(R.id.imageView_picturesImage)
        val picturesName: TextView = view.findViewById(R.id.textView_picturesName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_pictures, parent, false)
        return InnerViewHolder(view)
    }

    override fun onBindViewHolder(holder: InnerViewHolder, position: Int) {
        val mycontext = holder.itemView.context
        val myDao = AppDatabase.getDatabase(mycontext).userDao()
        val picture = pictureslist[position]
        holder.picturesImage.setImageBitmap(picture.icon)
        holder.picturesName.text = picture.name
        holder.itemView.setOnClickListener {


            if (picture.id.equals("root")) {
                val encryption_Layout =
                    LayoutInflater.from(mycontext).inflate(R.layout.layout_encryption, null)
                val encryption_enter_layout =
                    LayoutInflater.from(mycontext).inflate(R.layout.layout_encryption_enter, null)


                var check = false
                thread {
                    for (users in myDao.loadAllUsers()) {
                        if (users.account.equals(Login.LoginUserStored) && users.encryption_password.equals(
                                ""
                            )
                        ) {

                            check = true
                        }
                    }
                    val myactivity = mycontext as Activity
                    //????????????????????????????????????
                    if (check) {
                        myactivity.runOnUiThread {
                            AlertDialog.Builder(mycontext).apply {
                                setView(encryption_Layout)
                                setPositiveButton("??????") { dialog, which ->
                                    val encryptionPassword =
                                        encryption_Layout.editText_encryption.text.toString()
                                    //???????????????????????????
                                    if (!encryptionPassword.equals("")) {
                                        GetToast.ShowToast(myactivity,"???????????????")
                                        thread {
                                            myDao.update(encryptionPassword, Login.LoginUserStored)
                                        }
                                    } else {
                                        AlertDialog.Builder(myactivity).apply {
                                            setTitle("????????????")
                                            setMessage("??????????????????")
                                            setNegativeButton("??????"){dialog, which ->}
                                        }
                                    }
                                }
                                setNegativeButton("??????") { dialog, which -> }
                                show()
                            }
                        }

                    } else {
                        //???????????????????????????AlertDialog??????????????????
                            myactivity.runOnUiThread {
                            AlertDialog.Builder(mycontext).apply {
                                setView(encryption_enter_layout)
                                setNegativeButton("??????") { dialog, which -> }
                                setPositiveButton("??????") { dialog, which ->
                                    val myactivity = mycontext as Activity
                                    val inputPassword =
                                        encryption_enter_layout.editText_encryption.text.toString()
                                    thread {
                                        if (Checks.checkPassword(
                                                Login.LoginUserStored,
                                                inputPassword,
                                                myDao
                                            )
                                        ) {
                                            val intent = Intent(mycontext, SubFolder::class.java)
                                            //??????????????????
                                            intent.putExtra("pic_id", picture.id)
                                            mycontext.startActivity(intent)
                                        } else {
                                            myactivity.runOnUiThread {
                                                GetToast.ShowToast(mycontext, "????????????????????????")
                                            }
                                        }
                                    }
                                }
                                show()
                            }
                        }

                    }
                }


            } else {
                val previewPictures =
                    LayoutInflater.from(mycontext).inflate(R.layout.layout_preview_picture, null)
                Glide.with(mycontext).load(picture.icon)
                    .into(previewPictures.imageView_PreviewImage)
                AlertDialog.Builder(mycontext).apply {
                    setTitle("????????????")
                    setView(previewPictures)
                    setNegativeButton("??????") { dialog, which -> }
                    show()
                }
            }


        }
    }

    override fun getItemCount() = pictureslist.size
}