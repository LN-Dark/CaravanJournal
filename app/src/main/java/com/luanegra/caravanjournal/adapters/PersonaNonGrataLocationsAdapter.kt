package com.luanegra.caravanjournal.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.luanegra.caravanjournal.R
import com.luanegra.caravanjournal.models.Locations
import com.luanegra.caravanjournal.models.PersonanonGrata_Location
import com.luanegra.caravanjournal.models.Users
import com.luanegra.caravanjournal.ui.personanongrata.PersonaNonGrataDescriptionsActivity

class PersonaNonGrataLocationsAdapter(private val mContext: Context, private val mLocationsList: List<PersonanonGrata_Location>): RecyclerView.Adapter<PersonaNonGrataLocationsAdapter.ViewHolder?>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cityname_personanongrata_object: TextView
        var creatorname_personanongrata_object: TextView

        init {
            cityname_personanongrata_object = itemView.findViewById(R.id.cityname_personanongrata_object)
            creatorname_personanongrata_object = itemView.findViewById(R.id.creatorname_personanongrata_object)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View? = LayoutInflater.from(mContext).inflate(
            R.layout.personanongrata_object,
            parent,
            false
        )
        return ViewHolder(view!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location: PersonanonGrata_Location = mLocationsList[position]
        holder.cityname_personanongrata_object.text = location.getlocationName()
        holder.cityname_personanongrata_object.setOnClickListener {
            val intent = Intent(mContext, PersonaNonGrataDescriptionsActivity::class.java)
            intent.putExtra("uid", location.getUid())
            intent.putExtra("locationName", location.getlocationName())
            mContext.startActivity(intent)
        }
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
        return mLocationsList.size
    }
}