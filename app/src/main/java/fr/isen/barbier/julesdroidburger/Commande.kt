package fr.isen.barbier.julesdroidburger

data class CommandeFirebase(
    val appname: String,
    val firstname: String,
    val lastname: String,
    val address: String,
    val phone: String,
    val burger: String,
    val quantity: String,
    val deliverytime: String
)
