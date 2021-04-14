package com.luanegra.caravanjournal.models

class Locations {
    private var uid: String = ""
    private var locationName: String = ""

    constructor()
    constructor(uid: String, locationName: String) {
        this.uid = uid
        this.locationName = locationName
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

}