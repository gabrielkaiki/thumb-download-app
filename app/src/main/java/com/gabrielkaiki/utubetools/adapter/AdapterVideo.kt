package com.gabrielkaiki.utubetools.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gabrielkaiki.utubetools.R
import com.gabrielkaiki.utubetools.model.Video
import com.squareup.picasso.Picasso

class AdapterVideo(var listVideos: ArrayList<Video>) :
    RecyclerView.Adapter<AdapterVideo.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var thumbnail: ImageView = itemView.findViewById(R.id.imageThumbnail)
        var title: TextView = itemView.findViewById(R.id.textAdapterTitle)
        var publishedAt: TextView = itemView.findViewById(R.id.textAdapterPublishedAt)
        var description: TextView = itemView.findViewById(R.id.textAdapterDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_video, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val video = listVideos.get(position)
        val urlThumbnail = video.thumbnails!!.high!!.url

        Picasso.get().load(Uri.parse(urlThumbnail)).into(holder.thumbnail)
        holder.title.text = video.title
        holder.publishedAt.text = video.publishedAt
        holder.description.text = video.description
    }

    override fun getItemCount(): Int {
        return listVideos.size
    }
}