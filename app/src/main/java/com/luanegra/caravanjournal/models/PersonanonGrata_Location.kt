package com.luanegra.caravanjournal.models

class PersonanonGrata_Location {
    private var uid: String = ""
    private var locationName: String = ""
    private var creatorUid: String = ""

    constructor()
    constructor(uid: String, locationName: String, creatorUid: String) {
        this.uid = uid
        this.locationName = locationName
        this.creatorUid = creatorUid
    }


    fun getUid(): String {
        return uid
    }

    fun setUid(uid: String){
        this.uid = uid
    }

    fun getlocationName(): String {
        return locationName
    }

    fun setlocationName(locationName: String){
        this.locationName = locationName
    }

    fun getcreatorUid(): String {
        return creatorUid
    }

    fun setcreatorUid(creatorUid: String){
        this.creatorUid = creatorUid
    }
}