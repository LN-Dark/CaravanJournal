package com.luanegra.caravanjournal

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.luanegra.caravanjournal.models.Users
import com.luanegra.caravanjournal.ui.location.LocationFragment
import com.luanegra.caravanjournal.ui.location.NewLocationActivity
import com.luanegra.caravanjournal.ui.personanongrata.NewPersonaNonGrataLocationActivity
import com.luanegra.caravanjournal.ui.personanongrata.PersonaNonGrataDescriptionsActivity
import com.luanegra.caravanjournal.ui.personanongrata.PersonaNonGrataFragment
import com.luanegra.caravanjournal.ui.settings.SettingsFragment
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : AppCompatActivity() {
    var firebaseUser: FirebaseUser?= null
    private var refUsers: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar_main))
        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        val user_name: TextView = findViewById(R.id.username_toolbar_main)
        val profile_image: CircleImageView = findViewById(R.id.profileimage_toolbar_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        val bottomNav : BottomNavigationView = findViewById(R.id.bottomNav)

        bottomNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        if (savedInstanceState == null) {
            val fragment = LocationFragment()
            supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                .commit()
        }


        refUsers!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user: Users? = snapshot.getValue(Users::class.java)
                    user_name.text = user!!.getusername()
                    profile_image.load(user.getprofile())
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.navigation_location -> {
                val fragment = LocationFragment()
                supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                    .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_personanongrata -> {
                val fragment = PersonaNonGrataFragment()
                supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                    .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_settings -> {
                val fragment = SettingsFragment()
                supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                    .commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

}