package com.gaurav.walllpaperhub.modals

import com.google.firebase.database.Exclude

data class Wallpaper(
    @get:Exclude
    val id:String, val title:String, val desc: String, val url:String,
    @get:Exclude
    val category: String){
    constructor() : this("","","","","")
    @get: Exclude
    var isFavorite: Boolean = false
}