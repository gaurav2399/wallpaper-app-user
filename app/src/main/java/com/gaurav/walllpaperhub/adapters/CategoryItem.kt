package com.gaurav.walllpaperhub.adapters

import com.bumptech.glide.Glide
import com.gaurav.walllpaperhub.R
import com.gaurav.walllpaperhub.modals.Category
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.category_item.*

class CategoryItem(
    val categoryItem: Category
): Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            category_name_text.text = categoryItem.name
            setImage()
        }
    }

    override fun getLayout() = R.layout.category_item

    private fun ViewHolder.setImage() {
        Glide.with(this.containerView)
            .load(categoryItem.thumbnail)
            .into(category_image_view)
    }
}


