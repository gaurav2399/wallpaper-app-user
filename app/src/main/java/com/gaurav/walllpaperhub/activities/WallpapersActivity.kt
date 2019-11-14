package com.gaurav.walllpaperhub.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.gaurav.walllpaperhub.R
import com.gaurav.walllpaperhub.adapters.WallpaperItem
import com.gaurav.walllpaperhub.modals.Wallpaper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_wallpapers.*

class WallpapersActivity : AppCompatActivity() {

    private lateinit var dbWallpapers: DatabaseReference
    private lateinit var dbFavs: DatabaseReference
    private val wallpaperList = ArrayList<Wallpaper>()
    private val wallpaperFavList = ArrayList<Wallpaper>()
    private lateinit var categoryName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallpapers)

        categoryName = intent?.extras?.getString("category_name")!!
        Log.e("category in wallpaper ",categoryName)

        wallpaper_toolbar.title = categoryName
        wallpaper_progress.visibility = View.VISIBLE
        setSupportActionBar(wallpaper_toolbar)
        dbWallpapers = FirebaseDatabase.getInstance().getReference("images").child(categoryName)

        if(FirebaseAuth.getInstance().currentUser != null){
            dbFavs = FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("favorites")
                .child(categoryName)
            fetchFavWallpapers()
        }else{
            fetchWallpapers()
        }

    }

    private fun initRecyclerView(items: List<WallpaperItem>) {
        val  groupAdapter = GroupAdapter<ViewHolder>().apply {
            addAll(items)
        }

        wallpaper_recycler_view.apply {
            layoutManager = LinearLayoutManager(this@WallpapersActivity)
            adapter = groupAdapter
        }
    }

    private fun ArrayList<Wallpaper>.toWallpaperItems(): List<WallpaperItem> {
        return this.map {
            WallpaperItem(this@WallpapersActivity, it)
        }
    }

    private fun fetchWallpapers(){
        dbWallpapers.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.e("error while reading: ",p0.toString())
                Toast.makeText(this@WallpapersActivity, p0.message, Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                wallpaper_progress.visibility = View.INVISIBLE
                if(dataSnapshot.exists()){
                    dataSnapshot.children.forEach {
                        val id = it.key.toString()
                        val title = it.child("title").value.toString()
                        val description = it.child("desc").value.toString()
                        val url = it.child("url").value.toString()
                        val wallpaper = Wallpaper(id, title, description, url, categoryName)
                        if (isFavorite(wallpaper)){
                            wallpaper.isFavorite = true
                        }
                        wallpaperList.add(wallpaper)
                    }
                    initRecyclerView(wallpaperList.toWallpaperItems())
                }else{
                    empty_wallpaper_text.visibility=View.VISIBLE
                }
            }
        })
    }

    private fun fetchFavWallpapers(){
        dbFavs.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.e("error while reading: ",p0.toString())
                Toast.makeText(this@WallpapersActivity, p0.message, Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                wallpaper_progress.visibility = View.INVISIBLE
                if(dataSnapshot.exists()){
                    dataSnapshot.children.forEach {
                        val id = it.key.toString()
                        val title = it.child("title").value.toString()
                        val description = it.child("desc").value.toString()
                        val url = it.child("url").value.toString()
                        val wallpaper = Wallpaper(id, title, description, url, categoryName)
                        wallpaperFavList.add(wallpaper)
                    }
                }
                fetchWallpapers()
            }
        })
    }

    private fun isFavorite(wallpaper: Wallpaper): Boolean{
        for ( f in wallpaperFavList){
            if (f.id == wallpaper.id){
                return true
            }
        }
        return false
    }
}
