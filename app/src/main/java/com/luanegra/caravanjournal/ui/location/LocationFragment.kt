package com.luanegra.caravanjournal.ui.location

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.luanegra.caravanjournal.LogInActivity
import com.luanegra.caravanjournal.MainActivity
import com.luanegra.caravanjournal.R
import com.luanegra.caravanjournal.adapters.LocationAdapter
import com.luanegra.caravanjournal.models.Locations
import com.luanegra.caravanjournal.ui.personanongrata.NewPersonaNonGrataLocationActivity
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList

class LocationFragment : Fragment() {
    private var root: View? = null
    private var locationAdapter: LocationAdapter? = null
    private var mLocations: List<Locations>?= null
    private var recycler_locations: RecyclerView? = null
    private var tf_searchlocations_main: TextInputEditText? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
       root = inflater.inflate(R.layout.fragment_location, container, false)
        val efab_location_new: com.nambimobile.widgets.efab.FabOption = root!!.findViewById(R.id.efab_location_new)
        val efab_personanongrata_new: com.nambimobile.widgets.efab.FabOption = root!!.findViewById(R.id.efab_personanongrata_new)
        efab_location_new.setOnClickListener {
            val intent = Intent(root!!.context, NewLocationActivity::class.java)
            startActivity(intent)
        }
        efab_personanongrata_new.setOnClickListener {
            val intent = Intent(root!!.context, NewPersonaNonGrataLocationActivity::class.java)
            startActivity(intent)
        }
        recycler_locations = root!!.findViewById(R.id.recycler_locations_main)
        recycler_locations!!.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.stackFromEnd = false
        recycler_locations!!.layoutManager = linearLayoutManager
        mLocations = ArrayList()
        tf_searchlocations_main = root!!.findViewById(R.id.tiet_search_locations_main)
        tf_searchlocations_main!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                retrieveSearchLocations(s.toString().toLowerCase(Locale.ROOT))
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        retrieveAllLocations()
        return root
    }

    private fun retrieveAllLocations() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser!!.uid
        var refLocations: DatabaseReference? = null
        refLocations = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser).child("Locations")
        refLocations.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (mLocations as ArrayList<Locations>).clear()
                recycler_locations?.adapter?.notifyDataSetChanged()
                if (tf_searchlocations_main!!.text.toString() == "") {
                    if (snapshot.exists()) {
                        for (user in snapshot.children) {
                            val newLocation: Locations? = user.getValue(Locations::class.java)
                            if ((newLocation!!.getUid()) != firebaseUser) {
                                (mLocations as ArrayList<Locations>).add(newLocation)
                            }
                        }
                        if (context != null) {
                            locationAdapter = LocationAdapter(context!!, mLocations!!)
                            recycler_locations!!.adapter = locationAdapter
                        }

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun retrieveSearchLocations(searchname: String) {
        if(searchname != ""){
            val firebaseUser = FirebaseAuth.getInstance().currentUser!!.uid
            val queryUsers = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser).child("Locations")
            queryUsers.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    (mLocations as ArrayList<Locations>).clear()
                    recycler_locations?.adapter?.notifyDataSetChanged()
                    if (snapshot.exists()) {
                        for (location in snapshot.children) {
                            val newLocation: Locations? = location.getValue(Locations::class.java)
                            if (newLocation!!.getlocationName().toLowerCase().contains(searchname)){
                                (mLocations as ArrayList<Locations>).add(newLocation!!)
                            }
                        }
                        locationAdapter = LocationAdapter(context!!, mLocations!!)
                        recycler_locations!!.adapter = locationAdapter
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }else{
            retrieveAllLocations()
        }
    }
}