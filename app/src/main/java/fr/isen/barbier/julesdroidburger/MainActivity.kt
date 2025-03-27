package fr.isen.barbier.julesdroidburger

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editTextNom = findViewById<EditText>(R.id.editTextNom)
        val editTextPrenom = findViewById<EditText>(R.id.editTextPrenom)
        val editTextAdresse = findViewById<EditText>(R.id.editTextAdresse)
        val editTextTelephone = findViewById<EditText>(R.id.editTextTelephone)
        val editTextQuantite = findViewById<EditText>(R.id.editTextQuantite)
        val editTextHeure = findViewById<EditText>(R.id.editTextHeure)
        val spinnerBurger = findViewById<Spinner>(R.id.spinnerBurger)
        val btnValider = findViewById<Button>(R.id.btnValider)

        val burgers = resources.getStringArray(R.array.burger_list)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, burgers)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerBurger.adapter = adapter

        editTextHeure.setOnClickListener {
            val cal = Calendar.getInstance()
            val heure = cal.get(Calendar.HOUR_OF_DAY)
            val minute = cal.get(Calendar.MINUTE)

            val timePicker = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                val heureStr = String.format("%02d:%02d", selectedHour, selectedMinute)
                editTextHeure.setText(heureStr)
            }, heure, minute, true)

            timePicker.show()
        }

        btnValider.setOnClickListener {
            val nom = editTextNom.text.toString().trim()
            val prenom = editTextPrenom.text.toString().trim()
            val adresse = editTextAdresse.text.toString().trim()
            val telephone = editTextTelephone.text.toString().trim()
            val quantiteStr = editTextQuantite.text.toString().trim()
            val heureLivraison = editTextHeure.text.toString().trim()
            val burger = spinnerBurger.selectedItem.toString()

            val champsManquants = mutableListOf<String>()
            if (nom.isEmpty()) champsManquants.add("Nom")
            if (prenom.isEmpty()) champsManquants.add("Prénom")
            if (adresse.isEmpty()) champsManquants.add("Adresse")
            if (telephone.isEmpty()) champsManquants.add("Téléphone")
            if (quantiteStr.isEmpty()) champsManquants.add("Quantité")
            if (heureLivraison.isEmpty()) champsManquants.add("Heure de livraison")

            if (champsManquants.isNotEmpty()) {
                Toast.makeText(
                    this,
                    "Champs manquants : ${champsManquants.joinToString(", ")}",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val quantite = quantiteStr.toIntOrNull() ?: 1
                val dateDuJour = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                val deliveryTime = "$dateDuJour $heureLivraison"

                val commande = CommandeFirebase(
                    appname = "JulesDroidBurger",
                    firstname = prenom,
                    lastname = nom,
                    address = adresse,
                    phone = telephone,
                    burger = burger,
                    quantity = quantite.toString(),
                    deliverytime = deliveryTime
                )

                val gson = Gson()
                val jsonCommande = gson.toJson(commande)
                val jsonObject = JSONObject(jsonCommande)

                val queue = Volley.newRequestQueue(this)
                val url = "https://isen-droid-burger-default-rtdb.firebaseio.com/orders.json"

                val request = JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonObject,
                    { response ->
                        Toast.makeText(this, "Commande envoyée avec succès !", Toast.LENGTH_SHORT).show()
                        Log.d("REPONSE_SERVEUR", response.toString())

                        val intent = Intent(this, ConfirmationActivity::class.java)
                        intent.putExtra("commandeJson", jsonCommande)
                        startActivity(intent)
                    },
                    { error ->
                        Toast.makeText(this, "Erreur : commande non envoyée. Réessayez plus tard.", Toast.LENGTH_LONG).show()
                        Log.e("ERREUR_ENVOI", error.toString())
                    }
                )

                queue.add(request)
            }
        }
    }
}
