package com.luanegra.caravanjournal.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.luanegra.caravanjournal.R
import com.luanegra.caravanjournal.models.PersonanonGrataLocation_Description
import com.luanegra.caravanjournal.models.Users


class PersonaNonGrataLocationsDescriptionsAdapter(private val mContext: Context, private val mLocationsDescriptionList: List<PersonanonGrataLocation_Description>): RecyclerView.Adapter<PersonaNonGrataLocationsDescriptionsAdapter.ViewHolder?>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_discription_personanongrata_object: TextView
        var creatorname_personanongrata_object: TextView

        init {
            tv_discription_personanongrata_object = itemView.findViewById(R.id.tv_discription_personanongrata_object)
            creatorname_personanongrata_object = itemView.findViewById(R.id.creatorname_personanongrata_object)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View? = LayoutInflater.from(mContext).inflate(
                R.layout.personanongrata_discription_object,
                parent,
                false
        )
        return ViewHolder(view!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location: PersonanonGrataLocation_Description = mLocationsDescriptionList[position]
        holder.tv_discription_personanongrata_object.text = location.getdescription()
        var refLocations: DatabaseReference? = null
        refLocations = FirebaseDatabase.getInstance().reference.child("users").child(location.getcreatorUid())
        refLocations.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val newLocation: Users? = snapshot.getValue(Users::class.java)
                    holder.creatorname_personanongrata_object.text = newLocation!!.getusername()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun getItemCount(): Int {
        return mLocationsDescriptionList.size
    }
}
