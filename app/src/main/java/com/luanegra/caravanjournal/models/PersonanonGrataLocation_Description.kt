package com.luanegra.caravanjournal.models

class PersonanonGrataLocation_Description {
    private var uid: String = ""
    private var locationUid: String = ""
    private var description: String = ""

    constructor()
    constructor(uid: String, locationUid: String, description: String) {
        this.uid = uid
        this.locationUid = locationUid
        this.description = description
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

}