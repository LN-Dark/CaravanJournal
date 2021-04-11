package com.luanegra.caravanjournal.models

class Users {
    private var uid: String = ""
    private var username: String = ""
    private var profile: String = ""

    constructor()
    constructor(uid: String, username: String, profile: String) {
        this.uid = uid
        this.username = username
        this.profile = profile
    }

    fun getUid(): String {
        return uid
    }

    fun setUid(uid: String){
        this.uid = uid
    }

    fun getusername(): String {
        return username
    }

    fun setusername(username: String){
        this.username = username
    }

    fun getprofile(): String {
        return profile
    }

    fun setprofile(profile: String){
        this.profile = profile
    }


}