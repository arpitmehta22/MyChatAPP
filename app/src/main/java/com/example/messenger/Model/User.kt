package com.example.messenger.Model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid:String,val username:String,val userProfileImageUrl:String):Parcelable
{
    constructor():this(uid="",username="",userProfileImageUrl="")


}