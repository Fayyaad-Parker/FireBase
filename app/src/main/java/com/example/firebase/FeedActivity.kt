package com.example.firebase

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class FeedActivity : AppCompatActivity() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val posts = mutableListOf<Post>()
    private lateinit var adapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        val recyclerView = findViewById<RecyclerView>(R.id.postsRecyclerView)
        val createPostButton = findViewById<Button>(R.id.createPostButton)

        adapter = PostAdapter(posts)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        createPostButton.setOnClickListener {
            showCreatePostDialog()
        }

        loadPosts()
    }

    private fun loadPosts() {
        firestore.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                posts.clear()
                for (doc in result) {
                    val post = doc.toObject(Post::class.java)
                    posts.add(post)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    private fun showCreatePostDialog() {
        val editText = android.widget.EditText(this)
        editText.hint = "Enter caption"

        android.app.AlertDialog.Builder(this)
            .setTitle("Create Post")
            .setView(editText)
            .setPositiveButton("Post") { _, _ ->

                val caption = editText.text.toString().trim()

                if (caption.isEmpty()) return@setPositiveButton

                val post = hashMapOf(
                    "username" to (auth.currentUser?.email ?: "User"),
                    "caption" to caption,
                    "imageUrl" to "",
                    "timestamp" to System.currentTimeMillis()
                )

                firestore.collection("posts")
                    .add(post)
                    .addOnSuccessListener {
                        loadPosts()
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        loadPosts()
    }
}