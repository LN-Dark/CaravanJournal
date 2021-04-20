package com.luanegra.caravanjournal.ui.location

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.luanegra.caravanjournal.MainActivity
import com.luanegra.caravanjournal.R
import com.luanegra.caravanjournal.adapters.LocationHistoryAdapter
import com.luanegra.caravanjournal.adapters.PersonaNonGrataLocationsDescriptionsAdapter
import com.luanegra.caravanjournal.models.History
import com.luanegra.caravanjournal.models.PersonanonGrataLocation_Description
import com.luanegra.caravanjournal.models.Users
import com.nambimobile.widgets.efab.ExpandableFab
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList

class HistoryLocationActivity : AppCompatActivity() {
    var locationId: String = ""
    var locationName: String = ""
    var firebaseUser: FirebaseUser?= null
    private var refUsers: DatabaseReference? = null
    private var mHistory: List<History>?= null
    private var historyAdapter: LocationHistoryAdapter? = null
    private var recycler_history: RecyclerView? = null
    private var tiet_search_locations_history_main: TextInputEditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_location)
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        val user_name: TextView = findViewById(R.id.username_toolbar_main)
        val profile_image: CircleImageView = findViewById(R.id.profileimage_toolbar_main)
        supportActionBar!!.title = ""
        intent = intent
        locationId = intent.getStringExtra("uid").toString()
        locationName = intent.getStringExtra("locationName").toString()
        refUsers!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user: Users? = snapshot.getValue(Users::class.java)
                    user_name.text = locationName
                    profile_image.load(user!!.getprofile())
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        val new_history_location: ExpandableFab = findViewById(R.id.new_history_location)
        new_history_location.setOnClickListener {
            val intent = Intent(this@HistoryLocationActivity, NewHistoryActivity::class.java)
            intent.putExtra("uid", locationId)
            intent.putExtra("locationName", locationName)
            intent.putExtra("type", "1")
            startActivity(intent)
        }
        mHistory = ArrayList()
        recycler_history = findViewById(R.id.recycler_locations_history_main)
        recycler_history!!.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = false
        recycler_history!!.layoutManager = linearLayoutManager
        tiet_search_locations_history_main = findViewById(R.id.tiet_search_locations_history_main)
        tiet_search_locations_history_main!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                retrieveSearchHistory(s.toString().toLowerCase(Locale.ROOT))
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        retrieveAllHistory()
    }

    private fun retrieveAllHistory() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser!!.uid
        var refLocations: DatabaseReference? = null
        refLocations = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser).child("Locations").child(locationId).child("History")
        refLocations.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (mHistory as ArrayList<History>).clear()
                if (snapshot.exists()) {
                    for (user in snapshot.children) {
                        val newHistory: History? = user.getValue(History::class.java)
                        if (!mHistory!!.contains(newHistory)){
                            (mHistory as ArrayList<History>).add(newHistory!!)
                            historyAdapter = LocationHistoryAdapter(this@HistoryLocationActivity, mHistory!!)
                            recycler_history!!.adapter = historyAdapter
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun retrieveSearchHistory(searchname: String) {
        if(searchname != ""){
            val firebaseUser = FirebaseAuth.getInstance().currentUser!!.uid
            val queryUsers = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser).child("Locations").child(locationId).child("History")
            queryUsers.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    (mHistory as ArrayList<History>).clear()
                    recycler_history?.adapter?.notifyDataSetChanged()
                    if (snapshot.exists()) {
                        for (location in snapshot.children) {
                            val newLocation: History? = location.getValue(History::class.java)
                            if (newLocation!!.getdescription().toLowerCase().contains(searchname)){
                                (mHistory as ArrayList<History>).add(newLocation)
                            }
                        }
                        historyAdapter = LocationHistoryAdapter(this@HistoryLocationActivity, mHistory!!)
                        recycler_history!!.adapter = historyAdapter
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }else{
            retrieveAllHistory()
        }
    }
}