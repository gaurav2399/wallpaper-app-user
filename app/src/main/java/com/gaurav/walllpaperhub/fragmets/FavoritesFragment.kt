package com.gaurav.walllpaperhub.fragmets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.gaurav.walllpaperhub.R
import com.gaurav.walllpaperhub.adapters.WallpaperItem
import com.gaurav.walllpaperhub.modals.Wallpaper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_wallpapers.*
import kotlinx.android.synthetic.main.fragment_favorites.*

class FavoritesFragment: Fragment() {

    private lateinit var dbFavs: DatabaseReference
    private val favList = ArrayList<Wallpaper>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(FirebaseAuth.getInstance().currentUser == null){
            (activity as AppCompatActivity)
                .supportFragmentManager
                .beginTransaction()
                .replace(R.id.content_area, SettingsFragment())
                .commit()
            return
        }

        dbFavs = FirebaseDatabase.getInstance().getReference("users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("favorites")

        favorites_progress.visibility = View.VISIBLE

        dbFavs.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                favorites_progress.visibility = View.GONE
                dataSnapshot.children.forEach { category ->
                    category.children.forEach {

                        val id = it.key.toString()
                        val title = it.child("title").value.toString()
                        val description = it.child("desc").value.toString()
                        val url = it.child("url").value.toString()
                        val wallpaper = Wallpaper(id, title, description, url, category.key.toString())
                        favList.add(wallpaper)
                    }
                }
                initRecyclerView(favList.toWallpaperItems())
            }

        })
    }

    private fun initRecyclerView(items: List<WallpaperItem>) {
        val  groupAdapter = GroupAdapter<ViewHolder>().apply {
            addAll(items)
        }

        favorites_recycler_view.apply {
            layoutManager = LinearLayoutManager(activity as AppCompatActivity)
            adapter = groupAdapter
        }
    }

    private fun ArrayList<Wallpaper>.toWallpaperItems(): List<WallpaperItem> {
        return this.map {
            WallpaperItem(activity as AppCompatActivity, it)
        }
    }

}