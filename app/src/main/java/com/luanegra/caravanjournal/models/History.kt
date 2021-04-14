package com.luanegra.caravanjournal.models

class History {

    private var uid: String = ""
    private var locationUid: String = ""
    private var description: String = ""
    private var photoUrl: String = ""

    constructor()
    constructor(uid: String, locationUid: String, description: String, photoUrl: String) {
        this.uid = uid
        this.locationUid = locationUid
        this.description = description
        this.photoUrl = photoUrl
    }


    fun getUid(): String {
        return uid
    }

    fun setUid(uid: String){
        this.uid = uid
    }

    fun getlocationUid(): String {
        return locationUid
    }

    fun setlocationUid(locationUid: String){
        this.locationUid = locationUid
    }

    fun getdescription(): String {
        return description
    }

    fun setdescription(description: String){
        this.description = description
    }

    fun getphotoUrl(): String {
        return photoUrl
    }

    fun setphotoUrl(photoUrl: String){
        this.photoUrl = photoUrl
    }
}