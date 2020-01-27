package com.imgurtoppicks.ui.search

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.imgurtoppicks.R
import com.imgurtoppicks.data.ImgurGallery
import java.text.SimpleDateFormat
import java.util.*

class SearchTopPicsAdapter : RecyclerView.Adapter<SearchTopPicsAdapter.ViewHolder>() {

    private val differ = AsyncListDiffer<SearchGalleryListItem>(this, Callback())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.gallery_item_view, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    fun refreshItems(items: List<SearchGalleryListItem>) {
        differ.submitList(items)
    }

    class Callback : DiffUtil.ItemCallback<SearchGalleryListItem>() {

        override fun areItemsTheSame(oldItem: SearchGalleryListItem, newItem: SearchGalleryListItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SearchGalleryListItem, newItem: SearchGalleryListItem): Boolean {
            return oldItem == newItem
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SimpleDateFormat")
        private val dateFormat = SimpleDateFormat("dd/MM/YYYY h:mm a", Locale.getDefault())
        private val imageView = itemView.findViewById<ImageView>(R.id.gallery_image_view)
        private val titleView = itemView.findViewById<TextView>(R.id.gallery_title)
        private val dateView = itemView.findViewById<TextView>(R.id.gallery_date)
        private val countView = itemView.findViewById<TextView>(R.id.gallery_count)

        fun bind(gallery: SearchGalleryListItem) {
            Log.d("ViewHolder", gallery.toString())
            titleView.text = gallery.title
            dateView.text = gallery.getDateString(dateFormat)
            countView.text = gallery.getImageCount(itemView.context)
            Glide.with(itemView)
                .load(gallery.getCoverImageUrl())
                .error(R.drawable.ic_launcher_foreground)
                .centerCrop()
                .into(imageView)
        }
    }
}