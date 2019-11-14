package com.gaurav.walllpaperhub.adapters

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.audiofx.Equalizer
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.transition.Transition
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.gaurav.walllpaperhub.BuildConfig
import com.gaurav.walllpaperhub.R
import com.gaurav.walllpaperhub.glide.GlideApp
import com.gaurav.walllpaperhub.modals.Category
import com.gaurav.walllpaperhub.modals.Wallpaper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.activity_wallpapers.*
import kotlinx.android.synthetic.main.wallpaper_item.*
import java.io.File
import java.io.FileOutputStream

class WallpaperItem (
    private val context: Context,
    private val wallpaperItem: Wallpaper
    ): Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            wallpaper_name_text.text = wallpaperItem.title
            setImage()
            checkedFavorite()
            if (wallpaperItem.isFavorite){
                checkbox_favorite.isChecked = true
            }
            shareWallpaper()
            downloadWallpaper(wallpaperItem)
        }
    }

    override fun getLayout() = R.layout.wallpaper_item

    private fun ViewHolder.setImage() {
        Glide.with(this.containerView)
            .load(wallpaperItem.url)
            .into(wallpaper_image_view)
    }


    private fun ViewHolder.checkedFavorite() {
        checkbox_favorite.setOnCheckedChangeListener { compoundButton, b ->
            if(FirebaseAuth.getInstance().currentUser == null){
                compoundButton.isChecked = false
                Toast.makeText(context,"Please login first",Toast.LENGTH_LONG).show()
                return@setOnCheckedChangeListener
            }

            checkbox_favorite.isChecked = true
            val favoriteReference = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("favorites")
                .child(wallpaperItem.category)

            if(b){
                favoriteReference.child(wallpaperItem.id).setValue(wallpaperItem)
                checkbox_favorite.isActivated = true

            }else{
                checkbox_favorite.isChecked = false
                favoriteReference.child(wallpaperItem.id).setValue(null)
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun ViewHolder.shareWallpaper() {
        button_share.setOnClickListener {
            Log.e("share clicked","yes")
            wallpaper_item_progress.visibility = View.VISIBLE

            GlideApp.with(context)
                .asBitmap()
                .load(wallpaperItem.url)
                .listener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e("on load failed ", "yes")
                        wallpaper_item_progress.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Bitmap?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e("image share start ", "done")
                        wallpaper_item_progress.visibility = View.GONE

                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "image/*"
                        intent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(resource!!))
                        context.startActivity(Intent.createChooser(intent, "Wallpapers Hub"))
                        return false
                    }

                })
                .submit()
            Toast.makeText(context,"Image shared.",Toast.LENGTH_LONG).show()
        }

    }

    private fun getLocalBitmapUri(bmp:Bitmap):Uri{
        val bmpUri: Uri?
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "wallpaper_hub_" + System.currentTimeMillis()+".png")
        val outputStream = FileOutputStream(file)
        bmp.compress(Bitmap.CompressFormat.PNG, 90, outputStream)
        outputStream.close()
        bmpUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file)
        return bmpUri
    }

    private fun ViewHolder.downloadWallpaper(wallpaper: Wallpaper){

        button_download.setOnClickListener {
            Log.e("download clicked","yes")
            wallpaper_item_progress.visibility = View.VISIBLE

            GlideApp.with(context)
                .asBitmap()
                .load(wallpaperItem.url)
                .listener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e("on load failed ", "yes")
                        wallpaper_item_progress.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Bitmap?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e("image download start ", "done")
                        wallpaper_item_progress.visibility = View.GONE

                        val intent = Intent(Intent.ACTION_VIEW)

                        val uri = saveWallpaperAndGetUri(resource!!, wallpaper.id)
                        Log.e("uri",uri.toString())
                        if (uri != null){
                            Log.e("uri null","no")
                            intent.setDataAndType(uri,"image/*")
                            context.startActivity(Intent.createChooser(intent, "Wallpapers Hub"))
                        }else{
                            Log.e("uri null","yes")
                        }
                        return false
                    }

                })
                .submit()
            Toast.makeText(context,"Image downloaded.",Toast.LENGTH_LONG).show()
        }


    }

    private fun saveWallpaperAndGetUri(bitmap: Bitmap, id: String): Uri? {
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.
                    shouldShowRequestPermissionRationale(
                        context as Activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)){

                val  intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package",context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            }else{
                ActivityCompat.requestPermissions(context, Array(1){Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100)
            }
            return null
        }

        val folder = File(Environment.getExternalStorageDirectory().toString() + "/Wallpaper Hub")
        folder.mkdirs()
        try {
            val imageFile = File(folder, "$id.jpg")
            val outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            return Uri.fromFile(imageFile)
            //return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", imageFile)
        }catch (e: Exception){
            Log.e("exception rises", e.message)
            Toast.makeText(context,e.message,Toast.LENGTH_LONG).show()
        }
        return null
    }

}




