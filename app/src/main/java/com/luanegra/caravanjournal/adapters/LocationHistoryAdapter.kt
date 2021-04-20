package com.luanegra.caravanjournal.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anilokcun.uwmediapicker.UwMediaPicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.luanegra.caravanjournal.R
import com.luanegra.caravanjournal.models.History
import com.luanegra.caravanjournal.models.Locations
import com.luanegra.caravanjournal.ui.location.NewHistoryActivity
import com.luanegra.caravanjournal.ui.location.ViewFullImagesActivity
import org.imaginativeworld.whynotimagecarousel.CarouselItem
import org.imaginativeworld.whynotimagecarousel.ImageCarousel
import org.imaginativeworld.whynotimagecarousel.OnItemClickListener
import java.util.ArrayList

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
        holder.image_history_object.onItemClickListener = object : OnItemClickListener {
            override fun onClick(position: Int, carouselItem: CarouselItem) {
            }

            override fun onLongClick(position: Int, dataObject: CarouselItem) {
                val options = arrayOf<CharSequence>(
                        mContext.getString(R.string.viewfullimages),
                        mContext.getString(R.string.edit),
                        mContext.getString(R.string.deletehistory),
                        mContext.getString(R.string.cancel)
                )
                val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
                builder.setTitle(mContext.getString(R.string.chooseanoption))
                builder.setItems(options) { dialog, which ->
                    if (which == 0) {
                        val intent = Intent(mContext, ViewFullImagesActivity::class.java)
                        intent.putExtra("uid", history.getlocationUid())
                        intent.putExtra("imagesURL", history.getphotoUrl())
                        mContext.startActivity(intent)
                    }else if(which == 1){
                        val intent = Intent(mContext, NewHistoryActivity::class.java)
                        intent.putExtra("uid", history.getlocationUid())
                        intent.putExtra("locationName", history.getphotoUrl())
                        intent.putExtra("photoUrl", history.getphotoUrl())
                        intent.putExtra("historyDate", history.getdata())
                        intent.putExtra("historyDescription", history.getdescription())
                        intent.putExtra("type", "2")
                        mContext.startActivity(intent)
                    }else if (which == 2) {
                        val options = arrayOf<CharSequence>(
                                mContext.getString(R.string.yes),
                                mContext.getString(R.string.no)
                        )
                        val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
                        builder.setTitle(mContext.getString(R.string.removethishistory))
                        builder.setItems(options) { dialog, which ->
                            if (which == 0) {
                                deleteHistory(history)
                            } else if (which == 1) {
                                dialog.dismiss()
                            }
                        }
                        builder.show()
                    } else if (which == 3) {
                        dialog.dismiss()
                    }
                }
                builder.show()
            }

        }
        holder.image_history_object.addData(list)
        holder.date__history_object.text = history.getdata()
        holder.discription_history.text = history.getdescription()
    }

    private fun deleteHistory(history: History){
        var firebaseUser: FirebaseUser?= null
        firebaseUser = FirebaseAuth.getInstance().currentUser
        FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser.uid).child("Locations").child(history.getlocationUid()).child("History").child(
                history.getUid()
        ).removeValue()

    }

    override fun getItemCount(): Int {
        return mHistoryList.size
    }
}