package me.jamilalrasyidis.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.jamilalrasyidis.myapplication.databinding.ItemSongBinding

/**
 * Created by Jamil on 1/22/2020.
 */
class PlaylistAdapter : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    var songList = ArrayList<HashMap<String, String>>()
    var itemClickListener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSongBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return songList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(songList[position])
    }

    inner class ViewHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        override fun onClick(v: View?) {
            v?.let { itemClickListener?.onClick(it, adapterPosition) }
        }

        init {
            binding.root.setOnClickListener(this)
        }

        fun bind(song: HashMap<String, String>) {
            binding.textSongTitle.text = song["songTitle"]
        }
    }
}