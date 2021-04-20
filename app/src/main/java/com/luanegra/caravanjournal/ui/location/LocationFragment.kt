package com.luanegra.caravanjournal.ui.location

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.luanegra.caravanjournal.R
import com.luanegra.caravanjournal.adapters.LocationAdapter
import com.luanegra.caravanjournal.models.Locations
import com.luanegra.caravanjournal.models.PersonanonGrata_Location
import java.util.*
import kotlin.collections.ArrayList

class LocationFragment : Fragment() {
    private var root: View? = null
    private var locationAdapter: LocationAdapter? = null
    private var mLocations: List<Locations>?= null
    private var recycler_locations: RecyclerView? = null
    private var tf_searchlocations_main: TextInputEditText? = null
    private var mPersonaNonGrata: List<PersonanonGrata_Location>?= null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
       root = inflater.inflate(R.layout.fragment_location, container, false)
        val efab_location_new: com.nambimobile.widgets.efab.FabOption = root!!.findViewById(R.id.efab_location_new)
        val efab_personanongrata_new: com.nambimobile.widgets.efab.FabOption = root!!.findViewById(R.id.efab_personanongrata_new)
        efab_location_new.setOnClickListener {
            createNewLocation()
        }
        efab_personanongrata_new.setOnClickListener {
            createNewPersonaNonGrataLocation()
        }
        recycler_locations = root!!.findViewById(R.id.recycler_locations_main)
        recycler_locations!!.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.stackFromEnd = false
        recycler_locations!!.layoutManager = linearLayoutManager
        mLocations = ArrayList()
        mPersonaNonGrata = ArrayList()
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
        retrieveAllPersonaNonGrata()
        return root
    }

    private fun createNewLocation(){
        val mDialogView = LayoutInflater.from(root!!.context).inflate(
                R.layout.dialog_new_location,
                null
        )

        val mBuilder = AlertDialog.Builder(context)
                .setView(mDialogView)
        val  mAlertDialog = mBuilder.show()
        val tiet_add_locations_dialog: TextInputEditText = mDialogView.findViewById(R.id.tiet_add_locations_dialog)
        mDialogView.findViewById<Button>(R.id.save_dialog).setOnClickListener {
            if (!tiet_add_locations_dialog.text.isNullOrEmpty()) {
                var alreadyExists = false
                for (locationName in mLocations!!){
                    if (locationName.getlocationName().toLowerCase().equals(tiet_add_locations_dialog.text.toString().toLowerCase())){
                        alreadyExists = true
                    }
                }
                if (!alreadyExists){
                    var refLocations: DatabaseReference? = null
                    refLocations = FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().currentUser.uid).child("Locations")
                    val userHashMap = HashMap<String, Any>()
                    val idBlock = refLocations.push().key.toString()
                    userHashMap["uid"] = idBlock
                    userHashMap["locationName"] = tiet_add_locations_dialog.text.toString()
                    refLocations.child(idBlock).updateChildren(userHashMap)
                    Toast.makeText(
                            context,
                            getString(R.string.locationcreated),
                            Toast.LENGTH_LONG
                    ).show()
                    mAlertDialog.dismiss()
                }else{
                    Toast.makeText(
                            context,
                            getString(R.string.thislocationalreadyexist),
                            Toast.LENGTH_LONG
                    ).show()
                }

            }else{
                Toast.makeText(
                        context,
                        getString(R.string.youhavetowritealocationname),
                        Toast.LENGTH_LONG
                ).show()
            }
        }
        mDialogView.findViewById<Button>(R.id.calcel_dialog).setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    private fun createNewPersonaNonGrataLocation(){
        val mDialogView = LayoutInflater.from(root!!.context).inflate(
                R.layout.dialog_new_personanongrata_location,
                null
        )

        val mBuilder = AlertDialog.Builder(context)
                .setView(mDialogView)
        val  mAlertDialog = mBuilder.show()
        val tiet_add_locations_dialog: TextInputEditText = mDialogView.findViewById(R.id.tiet_add_locations_dialog)
        mDialogView.findViewById<Button>(R.id.save_dialog).setOnClickListener {
            if (!tiet_add_locations_dialog.text.isNullOrEmpty()) {
                var alreadyExists = false
                for (locationName in mPersonaNonGrata!!){
                    if (locationName.getlocationName().toLowerCase().equals(tiet_add_locations_dialog.text.toString().toLowerCase())){
                        alreadyExists = true
                    }
                }
                if (!alreadyExists){
                    var refLocations: DatabaseReference? = null
                    refLocations = FirebaseDatabase.getInstance().reference.child("PersonaNonGrata")
                    val userHashMap = HashMap<String, Any>()
                    val idBlock = refLocations.push().key.toString()
                    userHashMap["uid"] = idBlock
                    userHashMap["locationName"] = tiet_add_locations_dialog.text.toString()
                    userHashMap["creatorUid"] = FirebaseAuth.getInstance().currentUser.uid
                    refLocations.child(idBlock).updateChildren(userHashMap)
                    Toast.makeText(
                            context,
                            getString(R.string.locationcreated),
                            Toast.LENGTH_LONG
                    ).show()
                    mAlertDialog.dismiss()
                }else{
                    Toast.makeText(
                            context,
                            getString(R.string.thislocationalreadyexist),
                            Toast.LENGTH_LONG
                    ).show()
                }
            }else{
                Toast.makeText(
                        context,
                        getString(R.string.youhavetowritealocationname),
                        Toast.LENGTH_LONG
                ).show()
            }
        }
        mDialogView.findViewById<Button>(R.id.calcel_dialog).setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    private fun retrieveAllPersonaNonGrata() {
        var refLocations: DatabaseReference? = null
        refLocations = FirebaseDatabase.getInstance().reference.child("PersonaNonGrata")
        refLocations.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (mPersonaNonGrata as ArrayList<PersonanonGrata_Location>).clear()
                if (snapshot.exists()) {
                    for (user in snapshot.children) {
                        val newLocation: PersonanonGrata_Location? = user.getValue(PersonanonGrata_Location::class.java)
                        (mPersonaNonGrata as ArrayList<PersonanonGrata_Location>).add(newLocation!!)

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
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