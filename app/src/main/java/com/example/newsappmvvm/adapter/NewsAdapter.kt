package com.example.newsappmvvm.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.newsappmvvm.databinding.ItemArticleBinding
import com.example.newsappmvvm.model.Article
import com.example.newsappmvvm.utils.DateTimeUtilities

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    inner class ViewHolder (val binding : ItemArticleBinding) : RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemArticleBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currItem = differ.currentList[position]

        Glide.with(holder.itemView)
            .load(currItem.urlToImage)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.binding.loadingImage.visibility = View.GONE
                    holder.binding.imageNotFound.visibility = View.VISIBLE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.binding.loadingImage.visibility = View.GONE
                    holder.binding.imageNotFound.visibility = View.GONE
                    return false
                }

            })
            .into(holder.binding.ivArticleImage)

        holder.binding.tvTitle.text = currItem.title
        holder.binding.tvDescription.text = currItem.description
        holder.binding.tvSource.text = currItem.source?.name
        holder.binding.tvPublishedAt.text = DateTimeUtilities.changeFormat(currItem.publishedAt,"yyyy-MM-dd'T'HH:mm:ss'Z'","MMM dd, yyyy hh:ss")

        holder.itemView.setOnClickListener {
            onItemClickListener?.let { it(currItem) }
            /*if(currItem.title != null){
                if(!currItem.title.equals("[Removed]", ignoreCase = true)){
                    onItemClickListener?.let { it(currItem) }
                }else{
                    Snackbar.make(holder.itemView,"Article is Removed.", Snackbar.LENGTH_SHORT).show()
                }
            }else{
                Snackbar.make(holder.itemView,"Article is Removed.", Snackbar.LENGTH_SHORT).show()
            }*/
        }

    }

    private var onItemClickListener : ((Article) -> Unit)? = null

    fun setOnItemClickListener (listener : (Article) -> Unit) {
        onItemClickListener = listener
    }

}