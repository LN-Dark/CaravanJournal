package com.luanegra.caravanjournal.models

class PersonanonGrataLocation_Description {
    private var uid: String = ""
    private var locationUid: String = ""
    private var description: String = ""
    private var data: String = ""
    private var creatorUid: String = ""

    constructor()
    constructor(uid: String, locationUid: String, description: String, data: String, creatorUid: String) {
        this.uid = uid
        this.locationUid = locationUid
        this.description = description
        this.data = data
        this.creatorUid = creatorUid
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

    fun getdata(): String {
        return data
    }

    fun setdata(data: String){
        this.data = data
    }

    fun getcreatorUid(): String {
        return creatorUid
    }

    fun setcreatorUid(creatorUid: String){
        this.creatorUid = creatorUid
    }

}