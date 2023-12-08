package com.example.amazon_map.Classes

class Searches {
    private var latitude:Double=0.0
    private var longitude:Double=0.0
    private var searchId:String=""
    private var userId:String=""
    private var searchWord:String=""
    constructor(latitude:Double,longitude:Double,searchId:String,userId:String,searchWord:String){
        this.latitude=latitude
        this.longitude=longitude
        this.searchId=searchId
        this.userId=userId
        this.searchWord=searchWord
    }
    constructor(latitude:Double,longitude:Double){
        this.latitude=latitude
        this.longitude=longitude
    }
    fun getLatitude():Double{
        return this.latitude
    }
    fun getLongitude():Double{
        return this.longitude
    }
    fun getSearchId():String{
        return this.searchId
    }
    fun getUserId():String{
        return this.userId
    }
    fun getSearchWord():String{
        return this.searchWord
    }
    fun setLatitude(latitude:Double){
        this.latitude=latitude
    }
    fun setLongitude(longitude:Double){
        this.longitude=longitude
    }
    fun setSearchId(searchId:String){
        this.searchId=searchId
    }
    fun setUserId(userId:String){
        this.userId=userId
    }
    fun setSearchWord(searchWord:String){
        this.searchWord=searchWord
    }

}