package com.luanegra.caravanjournal.ui.location

import android.app.ProgressDialog
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
    var historyDate: String = ""
    var photoUrl: String = ""
    var typeNew: String = ""
    var locationName: String = ""
    var historyDescription: String = ""
    var firebaseUser: FirebaseUser?= null
    var listUri: List<String>?= null
    private var refUsers: DatabaseReference? = null
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_history)
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Uploading ...")
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
        typeNew = intent.getStringExtra("type").toString()
        locationId = intent.getStringExtra("uid").toString()
        locationName = intent.getStringExtra("locationName").toString()
        historyDate = intent.getStringExtra("historyDate").toString()
        historyDescription = intent.getStringExtra("historyDescription").toString()
        photoUrl = intent.getStringExtra("photoUrl").toString()
        refUsers!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user: Users? = snapshot.getValue(Users::class.java)
                    if (typeNew.equals("2")){
                        user_name.text = getString(R.string.edithistory)
                    }else{
                        user_name.text = getString(R.string.newhistory)
                    }
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
        btn_save_new_history!!.setOnClickListener {
            saveNewHistory()
        }
        if (typeNew.equals("2")){
            val list = mutableListOf<CarouselItem>()
            val strs = photoUrl.split("¨")
            for (url in strs){
                list.add(
                        CarouselItem(
                                imageUrl = url,
                                caption = ""
                        )
                )
            }
            image_history_object!!.addData(list)
            tiet_description_history!!.setText(historyDescription)
            var dateDay = ""
            var dateMonth = ""
            var dateYear = ""
            val strDate = historyDate.split("-")
            dateDay = strDate[0]
            dateMonth = strDate[1]
            dateYear = strDate[2]
            datePicker!!.setDate(
                    year = dateYear.toInt(),
                    month = dateMonth.toInt(),
                    selectedDate = dateDay.toInt()
            )

        }else{
            val list = mutableListOf<CarouselItem>()
            list.add(
                    CarouselItem(
                            imageDrawable = R.drawable.imagenotfound,
                            caption = getString(R.string.addimages)
                    )
            )
            image_history_object!!.addData(list)
            datePicker!!.setDate(Calendar.getInstance())
        }

    }

    private fun saveNewHistory(){
        progressDialog!!.show()
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
                uploadImages(idBlock)
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

    fun uploadImages(idBlock: String){
        var refLocations: DatabaseReference? = null
        refLocations = FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().currentUser.uid).child("Locations").child(locationId).child("History").child(idBlock)
        val userHashMap = HashMap<String, Any>()
        storageReference = FirebaseStorage.getInstance().reference
        val uri = arrayOfNulls<Uri>((listUri as ArrayList<String>).size)
        for (i in 0 until (listUri as ArrayList<String>).size) {
            progressDialog!!.show()
            uri[i] = Uri.parse("file://" + (listUri as ArrayList<String>)[i])
            val ref = storageReference?.child("History_Images/$idBlock/" + UUID.randomUUID().toString())
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
                        progressDialog!!.dismiss()
                        Toast.makeText(
                                this@NewHistoryActivity,
                                getString(R.string.newhistorycreated),
                                Toast.LENGTH_LONG
                        ).show()
                        onBackPressed()
                    }
                } else {
                    progressDialog!!.dismiss()
                    Toast.makeText(
                            this@NewHistoryActivity,
                            task.exception!!.message,
                            Toast.LENGTH_LONG
                    ).show()
                }
            }?.addOnFailureListener{
                progressDialog!!.dismiss()
                Toast.makeText(
                        this@NewHistoryActivity,
                        it.message,
                        Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}