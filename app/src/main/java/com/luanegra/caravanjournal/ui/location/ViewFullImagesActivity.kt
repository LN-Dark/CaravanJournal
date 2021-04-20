package com.luanegra.caravanjournal.ui.location

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import coil.load
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.luanegra.caravanjournal.R
import com.luanegra.caravanjournal.models.Locations
import com.luanegra.caravanjournal.models.Users
import de.hdodenhof.circleimageview.CircleImageView
import org.imaginativeworld.whynotimagecarousel.CarouselItem
import org.imaginativeworld.whynotimagecarousel.ImageCarousel

class ViewFullImagesActivity : AppCompatActivity() {
    var image_history_object: ImageCarousel? = null
    var locationId: String = ""
    var imagesURL: String = ""
    var firebaseUser: FirebaseUser?= null
    private var refUsers: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_full_images)
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
        imagesURL = intent.getStringExtra("imagesURL").toString()
        refUsers!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user: Users? = snapshot.getValue(Users::class.java)
                    val location: Locations? = snapshot.child("Locations").child(locationId).getValue(Locations::class.java)
                    user_name.text = location!!.getlocationName()
                    profile_image.load(user!!.getprofile())
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        image_history_object = findViewById(R.id.image_history_object)

        val strs = imagesURL.split("¨")
        val list = mutableListOf<CarouselItem>()
        for (url in strs){
            list.add(
                    CarouselItem(
                            imageUrl = url
                    )
            )
        }
        image_history_object!!.addData(list)
    }
}