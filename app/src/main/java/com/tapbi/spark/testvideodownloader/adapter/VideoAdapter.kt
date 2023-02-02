package com.tapbi.spark.testvideodownloader.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tapbi.spark.testvideodownloader.R
import com.tapbi.spark.testvideodownloader.model.VidInfo
import kotlinx.android.synthetic.main.item_video.view.*
import timber.log.Timber

class VideoAdapter(private val videoList: ArrayList<VidInfo>, private val inter: IVideoAdapter): RecyclerView.Adapter<VideoAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video,parent,false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val video = videoList[position]
        with(holder.itemView){
            video.videoImageThumbnailLink?.let {
                val picasso = Picasso.get()
                picasso.load(it)
                    .into(ivThumbnail)
            }
            tvVideoName.text = if(video.title.isNullOrEmpty() || video.title == "Instagram" || video.title == "· \uDB85\uDE77") "Video Number ${position+1}" else{
                if(video.title!!.contains(" See translation")){
                    video.title = video.title!!.replace(" See translation","")
                }
                if(video.title!!.contains("#")){
                    val title = video.title!!.split("#")[0]
                    if (title.isEmpty()) {
                        video.title = "Video Number ${position+1}"
                    }
                    else{
                        video.title = title
                    }
                }
                else if(video.title!!.contains("See More")){
                    video.title = video.title!!.replace("See More","")
                }
                video.title
            }
            Timber.e("giangld video.title: ${video.title}")
            setOnClickListener {
                video.videoId?.let { it1 -> inter.onItemClick(it1) }
            }
        }
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    interface IVideoAdapter{
        fun onItemClick(videoLink: String)
    }

}