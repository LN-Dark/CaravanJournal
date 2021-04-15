package com.luanegra.caravanjournal.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.luanegra.caravanjournal.R
import com.luanegra.caravanjournal.models.PersonanonGrata_Location
import com.luanegra.caravanjournal.ui.personanongrata.PersonaNonGrataDescriptionsActivity

class PersonaNonGrataLocationsAdapter(private val mContext: Context, private val mLocationsList: List<PersonanonGrata_Location>): RecyclerView.Adapter<PersonaNonGrataLocationsAdapter.ViewHolder?>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cityname_personanongrata_object: TextView

        init {
            cityname_personanongrata_object = itemView.findViewById(R.id.cityname_personanongrata_object)
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
    }

    override fun getItemCount(): Int {
        return mLocationsList.size
    }
}