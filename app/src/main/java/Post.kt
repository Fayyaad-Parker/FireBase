package com.example.firebase

data class Post(
    val username: String = "",
    val caption: String = "",
    val imageUrl: String = "" // Had to use local storage as firebase had storage bucket errors.

)