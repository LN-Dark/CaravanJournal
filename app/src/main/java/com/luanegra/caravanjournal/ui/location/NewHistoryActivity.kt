package com.luanegra.caravanjournal.ui.location

import android.R.attr.data
import android.graphics.Bitmap
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import coil.load
import com.afollestad.date.DatePicker
import com.afollestad.date.dayOfMonth
import com.afollestad.date.month
import com.afollestad.date.year
import com.anilokcun.uwmediapicker.UwMediaPicker
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.luanegra.caravanjournal.R
import com.luanegra.caravanjournal.models.Users
import de.hdodenhof.circleimageview.CircleImageView
import org.imaginativeworld.whynotimagecarousel.CarouselItem
import org.imaginativeworld.whynotimagecarousel.ImageCarousel
import org.imaginativeworld.whynotimagecarousel.OnItemClickListener
import java.util.*


class NewHistoryActivity : AppCompatActivity() {
    var image_history_object: ImageCarousel? = null
    var tiet_description_history: TextInputEditText? = null
    var datePicker: DatePicker? = null
    var btn_save_new_history: Button? = null
    var locationId: String = ""
    var locationName: String = ""
    var firebaseUser: FirebaseUser?= null
    var listUri: List<String>?= null
    private var refUsers: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_history)
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
                if (snapshot.exists()) {
                    val user: Users? = snapshot.getValue(Users::class.java)
                    user_name.text = locationName
                    profile_image.load(user!!.getprofile())
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        image_history_object = findViewById(R.id.image_history_object)
        tiet_description_history = findViewById(R.id.tiet_description_history)
        datePicker = findViewById(R.id.datePicker)
        btn_save_new_history = findViewById(R.id.btn_save_new_history)
        val list = mutableListOf<CarouselItem>()
        list.add(
                CarouselItem(
                        imageDrawable = R.drawable.imagenotfound,
                        caption = "Add Images"
                )
        )
        listUri = ArrayList()
        image_history_object!!.onItemClickListener = object : OnItemClickListener {
            override fun onClick(position: Int, carouselItem: CarouselItem) {
                UwMediaPicker
                    .with(this@NewHistoryActivity)
                    .setGalleryMode(UwMediaPicker.GalleryMode.ImageGallery)
                    .setGridColumnCount(4)
                    .setMaxSelectableMediaCount(10)
                    .setLightStatusBar(true)
                    .enableImageCompression(true)
                    .setCompressionMaxWidth(1280F)
                    .setCompressionMaxHeight(720F)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .setCompressionQuality(85)
                    .launch{ selectedMediaList->
                        val list = mutableListOf<CarouselItem>()
                        (listUri as ArrayList<String>).clear()
                        for(uriImage in selectedMediaList!!){
                            list.add(
                                    CarouselItem(
                                            imageUrl = uriImage.mediaPath
                                    )
                            )
                            (listUri as ArrayList<String>).add(uriImage.mediaPath)
                        }
                        image_history_object!!.addData(list)
                    }
            }

            override fun onLongClick(position: Int, dataObject: CarouselItem) {

            }

        }
        image_history_object!!.addData(list)
        btn_save_new_history!!.setOnClickListener {
            saveNewHistory()
        }
        datePicker!!.setDate(
                year = 2019,
                month = Calendar.JUNE,
                selectedDate = 17
        )
        datePicker!!.setDate(Calendar.getInstance())
    }

    private fun saveNewHistory(){
        if (!tiet_description_history!!.text.isNullOrEmpty()){
            if ((listUri as ArrayList<String>).size != 0){
                val selectedDate: Calendar? = datePicker!!.getDate()
                var refLocations: DatabaseReference? = null
                refLocations = FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().currentUser.uid).child("Locations").child(locationId).child("History")
                val userHashMap = HashMap<String, Any>()
                val idBlock = refLocations.push().key.toString()
                userHashMap["uid"] = idBlock
                userHashMap["data"] = "${selectedDate!!.dayOfMonth}-${selectedDate.month}-${selectedDate.year}"
                userHashMap["description"] = tiet_description_history!!.text.toString()
                userHashMap["locationUid"] = locationId
                uploadv2(idBlock)
                refLocations.child(idBlock).updateChildren(userHashMap)
            }else{
                Toast.makeText(
                        this@NewHistoryActivity,
                        getString(R.string.selectanimagefirst),
                        Toast.LENGTH_LONG
                ).show()
            }
        }else{
            Toast.makeText(
                    this@NewHistoryActivity,
                    getString(R.string.writeadescriptionfirst),
                    Toast.LENGTH_LONG
            ).show()
        }
    }

    private var storageReference: StorageReference? = null
    var strUri = ""

    fun uploadv2(idBlock: String){
        var refLocations: DatabaseReference? = null
        refLocations = FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().currentUser.uid).child("Locations").child(locationId).child("History").child(idBlock)
        val userHashMap = HashMap<String, Any>()
        storageReference = FirebaseStorage.getInstance().reference
        val uri = arrayOfNulls<Uri>((listUri as ArrayList<String>).size)
        for (i in 0 until (listUri as ArrayList<String>).size) {
            uri[i] = Uri.parse("file://" + (listUri as ArrayList<String>)[i])
            val ref = storageReference?.child("$idBlock/" + UUID.randomUUID().toString())
            val uploadTask = uri[i]?.let { ref?.putFile(it) }

            val urlTask = uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref!!.downloadUrl
            })?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    if (strUri.isEmpty()){
                        strUri += "$downloadUri"
                    }else{
                        strUri += "¨$downloadUri"
                    }
                    userHashMap["photoUrl"] = strUri
                    refLocations.updateChildren(userHashMap)
                    if (i == (listUri as ArrayList<String>).size - 1){
                        Toast.makeText(
                                this@NewHistoryActivity,
                                "New history created.",
                                Toast.LENGTH_LONG
                        ).show()
                        onBackPressed()
                    }
                } else {
                    Toast.makeText(
                            this@NewHistoryActivity,
                            task.exception!!.message,
                            Toast.LENGTH_LONG
                    ).show()
                }
            }?.addOnFailureListener{
                Toast.makeText(
                        this@NewHistoryActivity,
                        it.message,
                        Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}