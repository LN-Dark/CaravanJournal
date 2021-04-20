package com.luanegra.caravanjournal.ui.personanongrata

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
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
import com.luanegra.caravanjournal.adapters.PersonaNonGrataLocationsDescriptionsAdapter
import com.luanegra.caravanjournal.models.PersonanonGrataLocation_Description
import com.luanegra.caravanjournal.models.Users
import com.nambimobile.widgets.efab.ExpandableFab
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList

class PersonaNonGrataDescriptionsActivity : AppCompatActivity() {
    var locationId: String = ""
    var locationName: String = ""
    var firebaseUser: FirebaseUser?= null
    private var refUsers: DatabaseReference? = null
    private var mDescriptions: List<PersonanonGrataLocation_Description>?= null
    private var descriptionAdapter: PersonaNonGrataLocationsDescriptionsAdapter? = null
    private var recycler_descriptions: RecyclerView? = null
    private var tiet_search_descriptions_personanongrata: TextInputEditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_persona_non_grata_descriptions)
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar_locationdescriptions)
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
        val new_descriptions_personanongrata: ExpandableFab = findViewById(R.id.new_descriptions_personanongrata)
        new_descriptions_personanongrata.setOnClickListener {
            createNewPersonaNonGrataDexcription()
        }
        mDescriptions = ArrayList()
        recycler_descriptions = findViewById(R.id.recycler_descriptions_personanongrata)
        recycler_descriptions!!.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = false
        recycler_descriptions!!.layoutManager = linearLayoutManager
        tiet_search_descriptions_personanongrata = findViewById(R.id.tiet_search_descriptions_personanongrata)
        tiet_search_descriptions_personanongrata!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                retrieveSearchDescriptions(s.toString().toLowerCase(Locale.ROOT))
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        retrieveAllDescriptions()
    }

    private fun retrieveAllDescriptions() {
        var refLocations: DatabaseReference? = null
        refLocations = FirebaseDatabase.getInstance().reference.child("PersonaNonGrata").child(locationId).child("Descriptions")
        refLocations.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (mDescriptions as ArrayList<PersonanonGrataLocation_Description>).clear()
                if (snapshot.exists()) {
                    for (user in snapshot.children) {
                        val newLocation: PersonanonGrataLocation_Description? = user.getValue(PersonanonGrataLocation_Description::class.java)
                        (mDescriptions as ArrayList<PersonanonGrataLocation_Description>).add(newLocation!!)
                        descriptionAdapter = PersonaNonGrataLocationsDescriptionsAdapter(this@PersonaNonGrataDescriptionsActivity, mDescriptions!!)
                        recycler_descriptions!!.adapter = descriptionAdapter
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun retrieveSearchDescriptions(searchname: String) {
        if(searchname != ""){
            val queryUsers = FirebaseDatabase.getInstance().reference.child("PersonaNonGrata").child(locationId).child("Descriptions")
            queryUsers.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    (mDescriptions as ArrayList<PersonanonGrataLocation_Description>).clear()
                    recycler_descriptions?.adapter?.notifyDataSetChanged()
                    if (snapshot.exists()) {
                        for (location in snapshot.children) {
                            val newLocation: PersonanonGrataLocation_Description? = location.getValue(PersonanonGrataLocation_Description::class.java)
                            if (newLocation!!.getdescription().toLowerCase().contains(searchname)){
                                (mDescriptions as ArrayList<PersonanonGrataLocation_Description>).add(newLocation)
                            }
                        }
                        descriptionAdapter = PersonaNonGrataLocationsDescriptionsAdapter(this@PersonaNonGrataDescriptionsActivity, mDescriptions!!)
                        recycler_descriptions!!.adapter = descriptionAdapter
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }else{
            retrieveAllDescriptions()
        }
    }

    private fun createNewPersonaNonGrataDexcription(){
        val mDialogView = LayoutInflater.from(this).inflate(
            R.layout.dialog_personanongrata_discription,
            null
        )

        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
        val  mAlertDialog = mBuilder.show()
        val tiet_add_discription_dialog: TextInputEditText = mDialogView.findViewById(R.id.tiet_add_discription_dialog)
        mDialogView.findViewById<Button>(R.id.save_dialog).setOnClickListener {
            if (!tiet_add_discription_dialog.text.isNullOrEmpty()) {
                var refLocations: DatabaseReference? = null
                refLocations = FirebaseDatabase.getInstance().reference.child("PersonaNonGrata").child(locationId).child("Descriptions")
                val userHashMap = HashMap<String, Any>()
                val idBlock = refLocations.push().key.toString()
                userHashMap["uid"] = idBlock
                userHashMap["creatorUid"] = FirebaseAuth.getInstance().currentUser.uid
                userHashMap["description"] = tiet_add_discription_dialog.text.toString()
                refLocations.child(idBlock).updateChildren(userHashMap)
                Toast.makeText(
                    this,
                    getString(R.string.descriptionofpersonanongratacreated),
                    Toast.LENGTH_LONG
                ).show()
                mAlertDialog.dismiss()
            }else{
                Toast.makeText(
                    this,
                    getString(R.string.writesomethingtobepersonanongrata),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        mDialogView.findViewById<Button>(R.id.calcel_dialog).setOnClickListener {
            mAlertDialog.dismiss()
        }
    }
}