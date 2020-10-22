package com.example.chitchat.ModelClasses

class users {

    private var uid: String = ""
    private var username: String = ""
    private var profile: String = ""
    private var instagram: String = ""
    private var facebook: String = ""
    private var snapchat: String = ""
    private var search: String = ""
    private var cover: String = ""
    private var status: String = ""

    constructor()
    constructor(
        uid: String,
        username: String,
        profile: String,
        instagram: String,
        facebook: String,
        snapchat: String,
        search: String,
        cover: String,
        status: String
    ) {
        this.uid = uid
        this.username = username
        this.profile = profile
        this.instagram = instagram
        this.facebook = facebook
        this.snapchat = snapchat
        this.search = search
        this.cover = cover
        this.status = status
    }

    fun getUID(): String? {
        return uid
    }

    fun setUID(uid: String) {
        this.uid = uid
    }

    fun getUserName(): String? {
        return username
    }

    fun setUserName(username: String) {
        this.username = username
    }

    fun getProfile(): String? {
        return profile
    }

    fun setProfile(profile: String) {
        this.profile = profile
    }

    fun getFacebook(): String? {
        return facebook
    }

    fun setFacebook(facebook: String) {
        this.facebook = facebook
    }

    fun getInstagram(): String? {
        return instagram
    }

    fun setInstagram(instagram: String) {
        this.instagram = instagram
    }

    fun getSnapchat(): String? {
        return snapchat
    }

    fun setSnapchat(snapchat: String) {
        this.snapchat = snapchat
    }

    fun getSearch(): String? {
        return search
    }

    fun setSearch(search: String) {
        this.search = search
    }

    fun getCover(): String? {
        return cover
    }

    fun setCover(cover: String) {
        this.cover = cover
    }

    fun getStatus(): String? {
        return status
    }

    fun setStaus(status: String) {
        this.status = status
    }




}