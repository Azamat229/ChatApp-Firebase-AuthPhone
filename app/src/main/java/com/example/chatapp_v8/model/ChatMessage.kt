package com.example.chatapp_v8.model

class ChatMessage(val id:String, val text: String, val fromId: String, val toId: String, val timestamp: Long) {
    constructor() : this("", "", "", "", -1)
}