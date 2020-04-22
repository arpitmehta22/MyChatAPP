package com.example.messenger.Model


class ChatMessage(val text:String,val id:String,val fromid:String,val toid:String,val timestamp:Long)
{
    constructor():this(text="",id="",fromid = "",toid = "",timestamp = -1)
}