package com.luanegra.caravanjournal.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.luanegra.caravanjournal.MainActivity
import com.luanegra.caravanjournal.R
import com.luanegra.caravanjournal.models.History
import com.luanegra.caravanjournal.models.Locations
import com.luanegra.caravanjournal.ui.location.HistoryLocationActivity
import com.luanegra.caravanjournal.ui.location.ViewFullImagesActivity
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
        val firebaseUser = FirebaseAuth.getInstance().currentUser!!.uid
        var refLocations: DatabaseReference? = null
        var strsImageUrl: String = ""
        refLocations = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser).child("Locations").child(location.getUid()).child("History")
        refLocations.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val list = mutableListOf<CarouselItem>()
                    for (history in snapshot.children) {
                        val newHistory: History? = history.getValue(History::class.java)
                        val strs = newHistory!!.getphotoUrl().split("¨")
                        if (strsImageUrl.isEmpty()){
                            strsImageUrl += newHistory.getphotoUrl()
                        }else{
                            strsImageUrl += "¨" + newHistory.getphotoUrl()
                        }
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
                            val options = arrayOf<CharSequence>(
                                    mContext.getString(R.string.viewfullimages),
                                    mContext.getString(R.string.deletelocation),
                                    mContext.getString(R.string.cancel)
                            )
                            val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
                            builder.setTitle(mContext.getString(R.string.chooseanoption))
                            builder.setItems(options) { dialog, which ->
                                if (which == 0) {
                                    val intent = Intent(mContext, ViewFullImagesActivity::class.java)
                                    intent.putExtra("uid", location.getUid())
                                    intent.putExtra("imagesURL", strsImageUrl)
                                    mContext.startActivity(intent)
                                } else if (which == 1) {
                                    val options = arrayOf<CharSequence>(
                                            mContext.getString(R.string.yes),
                                            mContext.getString(R.string.no)
                                    )
                                    val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
                                    builder.setTitle(mContext.getString(R.string.deletethislocation))
                                    builder.setItems(options) { dialog, which ->
                                        if (which == 0) {
                                            deleteLocation(location)
                                        } else if (which == 1) {
                                            dialog.dismiss()
                                        }
                                    }
                                    builder.show()
                                } else if (which == 2) {
                                    dialog.dismiss()
                                }
                            }
                            builder.show()
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
                            val options = arrayOf<CharSequence>(
                                    mContext.getString(R.string.deletelocation),
                                    mContext.getString(R.string.cancel)
                            )
                            val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
                            builder.setTitle(mContext.getString(R.string.chooseanoption))
                            builder.setItems(options) { dialog, which ->
                                if (which == 0) {
                                    val options = arrayOf<CharSequence>(
                                            mContext.getString(R.string.yes),
                                            mContext.getString(R.string.no)
                                    )
                                    val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
                                    builder.setTitle(mContext.getString(R.string.deletethislocation))
                                    builder.setItems(options) { dialog, which ->
                                        if (which == 0) {
                                            deleteLocation(location)
                                        } else if (which == 1) {
                                            dialog.dismiss()
                                        }
                                    }
                                    builder.show()
                                } else if (which == 1) {
                                    dialog.dismiss()
                                }
                            }
                            builder.show()
                        }

                    }
                    holder.image_history_object.addData(list)

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun deleteLocation(location: Locations){
        var firebaseUser: FirebaseUser?= null
        firebaseUser = FirebaseAuth.getInstance().currentUser
        FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser.uid).child("Locations").child(location.getUid()
        ).removeValue()

    }

    override fun getItemCount(): Int {
        return mLocationsList.size
    }

}