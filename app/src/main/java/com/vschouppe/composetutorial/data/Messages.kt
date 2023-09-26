package com.vschouppe.composetutorial.data


data class Message (var author: String, var text : String, var story: String)

fun getMessage () : List<Message>{
    val messages = listOf<Message>(
        Message("Vince","When do we start training with JOIN?",
            "I've been waiting for quite some time now"),
        Message("Jim","What? You haven't started yet?",
            "We intend to get people started ASAP"),
        Message("Vince","Woopsie",
            "Sometimes I'm a bit slow, but not on the bike!!"),
        Message("Vince","So many blockies",
            "Train hard. No pain, no gain."),
        Message("Vince","Need to get a workout score 10",
            "Try to achieve perfection")
    )
    return messages
}