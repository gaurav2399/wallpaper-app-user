package com.gaurav.walllpaperhub.fragmets

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.gaurav.walllpaperhub.adapters.CategoryItem
import com.gaurav.walllpaperhub.R
import com.gaurav.walllpaperhub.activities.WallpapersActivity
import com.gaurav.walllpaperhub.modals.Category
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment: Fragment() {

    private val dbCategories = FirebaseDatabase.getInstance().getReference("categories")
    private val categoryList = ArrayList<Category>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        category_progress.visibility=View.VISIBLE
        dbCategories.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.e("error while reading: ",p0.toString())
                Toast.makeText(activity, p0.message, Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                category_progress.visibility = View.INVISIBLE
                if(snapshot.exists()) {
                    val children = snapshot.children

                    children.forEach {
                        val name = it.key.toString()
                        val description = it.child("desc").value.toString()
                        val imageUrl = it.child("thumbnail").value.toString()
                        val category = Category(name, description, imageUrl)
                        categoryList.add(category)
                    }
                    val categoryItemList = categoryList.toCategoryItems()
                    initRecyclerView(categoryItemList)
                    Log.e("total categories: ", children.count().toString())
                }else{
                    empty_category_text.visibility=View.VISIBLE
                }
            }
        })
    }

    private fun initRecyclerView(items: List<CategoryItem>) {
        val  groupAdapter = GroupAdapter<ViewHolder>().apply {
            addAll(items)
        }

        category_recycler_view.apply {
            layoutManager = GridLayoutManager(activity as? AppCompatActivity, 3)
            adapter = groupAdapter
        }

        groupAdapter.setOnItemClickListener { item, _ ->

            (item as? CategoryItem)?.let {
                Toast.makeText(activity,it.categoryItem.name,Toast.LENGTH_LONG).show()
                val intent = Intent(activity,WallpapersActivity::class.java)
                intent.putExtra("category_name",it.categoryItem.name)
                activity?.startActivity(intent)
            }
        }
    }

    private fun ArrayList<Category>.toCategoryItems(): List<CategoryItem> {
        return this.map {
            CategoryItem(it)
        }
    }

}

// HOW USING GROUPIE

/*
* make the list of data class and make a class for handling groupie as
* combination of adapter and view holder and pass there data class as constructor parameter
* and then convert that data class list into that class list by mapping*/

