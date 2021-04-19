package com.luanegra.caravanjournal.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.luanegra.caravanjournal.R
import com.luanegra.caravanjournal.models.History
import com.luanegra.caravanjournal.models.Locations
import org.imaginativeworld.whynotimagecarousel.CarouselItem
import org.imaginativeworld.whynotimagecarousel.ImageCarousel

class LocationHistoryAdapter(private val mContext: Context, private val mHistoryList: List<History>): RecyclerView.Adapter<LocationHistoryAdapter.ViewHolder?>(){
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image_history_object: ImageCarousel
        var date__history_object: TextView
        var discription_history: TextView

        init {
            image_history_object = itemView.findViewById(R.id.image_history_object)
            date__history_object = itemView.findViewById(R.id.date__history_object)
            discription_history = itemView.findViewById(R.id.discription_history)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View? = LayoutInflater.from(mContext).inflate(
                R.layout.city_history_object,
                parent,
                false
        )
        return ViewHolder(view!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val history: History = mHistoryList[position]
        val list = mutableListOf<CarouselItem>()
        val strs = history.getphotoUrl().split("¨")
        for (url in strs){
            list.add(
                CarouselItem(
                    imageUrl = url,
                    caption = ""
                )
            )
        }
        holder.image_history_object.addData(list)
        holder.date__history_object.text = history.getdata()
        holder.discription_history.text = history.getdescription()
    }

    override fun getItemCount(): Int {
        return mHistoryList.size
    }
}