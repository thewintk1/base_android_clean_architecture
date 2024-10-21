package com.example.base_clean_architecture.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.base_clean_architecture.R
import timber.log.Timber

class UserAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val userList = mutableListOf<String>()


    // Define View Types
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newUsers: List<String>) {
        userList.clear()
        userList.addAll(newUsers)
        notifyDataSetChanged()
    }

    fun addLoadingFooter() {
        userList.add("") // Add a placeholder for the loading view
        Timber.tag("###BaseProject").d("User list size ${userList.size}")
        notifyItemInserted(userList.size - 1)
    }

    override fun getItemViewType(position: Int): Int {
        return if (userList[position] == "") {
            VIEW_TYPE_LOADING
        } else {
            VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val inflater = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
            UserViewHolder(inflater)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is UserViewHolder) {
            holder.bind(userList[position])
        }
    }

    override fun getItemCount(): Int = userList.size

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userNameTextView: TextView = itemView.findViewById(R.id.tvUser)

        fun bind(user: String) {
            userNameTextView.text = user
        }
    }

    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
