package com.luanegra.caravanjournal.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView.ScaleType
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.luanegra.caravanjournal.R
import com.luanegra.caravanjournal.models.History
import com.luanegra.caravanjournal.models.Locations
import com.luanegra.caravanjournal.ui.location.HistoryLocationActivity
import technolifestyle.com.imageslider.FlipperView

class LocationAdapter(private val mContext: Context, private val mLocationsList: List<Locations>): RecyclerView.Adapter<LocationAdapter.ViewHolder?>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image_history_object: technolifestyle.com.imageslider.FlipperLayout
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
        val arrayList = ArrayList<String>()
        val firebaseUser = FirebaseAuth.getInstance().currentUser!!.uid
        var refLocations: DatabaseReference? = null
        refLocations = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser).child("Locations").child(location.getUid()).child("History")
        refLocations.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (history in snapshot.children) {
                        val newHistory: History? = history.getValue(History::class.java)
                        val strs = newHistory!!.getphotoUrl().split("¨")
                        for (url in strs){
                            arrayList.add(url)
                        }
                    }
                    val num_of_pages = arrayList.size
                    for (i in 0 until num_of_pages) {
                        val view = FlipperView(mContext)
                        view.setImageScaleType(ScaleType.CENTER_CROP)
                                .setImage(R.drawable.ic_image_black_24dp) { imageView, image ->
                                    imageView.load(arrayList[i])
                                }
                                .setOnFlipperClickListener(object : FlipperView.OnFlipperClickListener {
                                    override fun onFlipperClick(flipperView: FlipperView) {
                                        val intent = Intent(mContext, HistoryLocationActivity::class.java)
                                        mContext.startActivity(intent)
                                    }

                                })
                        holder.image_history_object.scrollTimeInSec = 5
                        holder.image_history_object.addFlipperView(view)
                    }
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