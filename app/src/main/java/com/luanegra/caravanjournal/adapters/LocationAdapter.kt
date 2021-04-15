package com.luanegra.caravanjournal.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.luanegra.caravanjournal.MainActivity
import com.luanegra.caravanjournal.R
import com.luanegra.caravanjournal.models.History
import com.luanegra.caravanjournal.models.Locations
import com.luanegra.caravanjournal.ui.location.HistoryLocationActivity
import org.imaginativeworld.whynotimagecarousel.CarouselItem
import org.imaginativeworld.whynotimagecarousel.ImageCarousel
import org.imaginativeworld.whynotimagecarousel.OnItemClickListener

class LocationAdapter(private val mContext: Context, private val mLocationsList: List<Locations>): RecyclerView.Adapter<LocationAdapter.ViewHolder?>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image_history_object: ImageCarousel
        var cityname_history_object: TextView
        init {
            cityname_history_object = itemView.findViewById(R.id.cityname_history_object)
            image_history_object = itemView.findViewById(R.id.image_history_object)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View? = LayoutInflater.from(mContext).inflate(
                R.layout.history_object,
                parent,
                false
        )
        return ViewHolder(view!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location: Locations = mLocationsList[position]
        holder.cityname_history_object.text = location.getlocationName()
        getImages(holder, location)
    }

    fun getImages(holder: ViewHolder, location: Locations){
        val firebaseUser = FirebaseAuth.getInstance().currentUser!!.uid
        var refLocations: DatabaseReference? = null
        refLocations = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser).child("Locations").child(location.getUid()).child("History")
        refLocations.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val list = mutableListOf<CarouselItem>()
                    for (history in snapshot.children) {
                        val newHistory: History? = history.getValue(History::class.java)
                        val strs = newHistory!!.getphotoUrl().split("¨")
                        for (url in strs){
                            list.add(
                                    CarouselItem(
                                            imageUrl = url,
                                            caption = newHistory.getdata()
                                    )
                            )
                        }

                    }
                    holder.image_history_object.onItemClickListener = object : OnItemClickListener {
                        override fun onClick(position: Int, carouselItem: CarouselItem) {
                            val intent = Intent(mContext, HistoryLocationActivity::class.java)
                            intent.putExtra("uid", location.getUid())
                            intent.putExtra("locationName", location.getlocationName())
                            mContext.startActivity(intent)
                        }

                        override fun onLongClick(position: Int, dataObject: CarouselItem) {
                            // ...
                        }

                    }
                    holder.image_history_object.addData(list)
                }else{
                    val list = mutableListOf<CarouselItem>()
                        list.add( CarouselItem(
                            imageUrl = mContext.resources.getDrawable(R.drawable.imagenotfound).toString(),
                            caption = mContext.getString(R.string.youdonthaveahistoryinthislocation)
                        ))
                    holder.image_history_object.showIndicator = false
                    holder.image_history_object.showNavigationButtons = false
                    holder.image_history_object.onItemClickListener = object : OnItemClickListener {
                        override fun onClick(position: Int, carouselItem: CarouselItem) {
                            val intent = Intent(mContext, HistoryLocationActivity::class.java)
                            intent.putExtra("uid", location.getUid())
                            intent.putExtra("locationName", location.getlocationName())
                            mContext.startActivity(intent)
                        }

                        override fun onLongClick(position: Int, dataObject: CarouselItem) {
                            // ...
                        }

                    }
                    holder.image_history_object.addData(list)

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun getItemCount(): Int {
        return mLocationsList.size
    }

}