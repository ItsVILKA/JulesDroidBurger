package fr.isen.barbier.julesdroidburger

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONObject

class ConfirmationActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_confirmation)

        // UI Elements
        val logoImageView = findViewById<ImageView>(R.id.logoImageView)
        val confirmationMessage = findViewById<TextView>(R.id.confirmationMessage)
        val detailsCommande = findViewById<TextView>(R.id.detailsCommande)
        val btnNouvelleCommande = findViewById<Button>(R.id.btnNouvelleCommande)
        val recyclerViewCommandes = findViewById<RecyclerView>(R.id.recyclerViewCommandes)

        recyclerViewCommandes.layoutManager = LinearLayoutManager(this)

        val jsonCommande = intent.getStringExtra("commandeJson")
        var prenomUtilisateur: String? = null

        if (jsonCommande != null) {
            val commande = Gson().fromJson(jsonCommande, CommandeFirebase::class.java)

            confirmationMessage.text = "Merci ${commande.firstname}, votre commande a été prise en compte !"

            detailsCommande.text = """
                🍔 Burger : ${commande.burger}
                📦 Quantité : ${commande.quantity}
                ⏰ Livraison : ${commande.deliverytime}
                🏠 Adresse : ${commande.address}
                📞 Téléphone : ${commande.phone}
            """.trimIndent()

            prenomUtilisateur = commande.firstname
        } else {
            confirmationMessage.text = "Commande non trouvée."
            detailsCommande.text = ""
        }

        // Appel au webservice Firebase
        val url = "https://isen-droid-burger-default-rtdb.firebaseio.com/orders.json"
        val queue = Volley.newRequestQueue(this)

        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response: JSONObject ->
                val commandes = mutableListOf<CommandeFirebase>()

                val keys = response.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val obj = response.getJSONObject(key)

                    val commande = CommandeFirebase(
                        appname = obj.optString("appname", ""),
                        firstname = obj.optString("firstname", ""),
                        lastname = obj.optString("lastname", ""),
                        address = obj.optString("address", ""),
                        phone = obj.optString("phone", ""),
                        burger = obj.optString("burger", ""),
                        quantity = obj.optString("quantity", ""),
                        deliverytime = obj.optString("deliverytime", "")
                    )

                    // Filtrer uniquement les commandes du même prénom
                    if (prenomUtilisateur == null || commande.firstname.equals(prenomUtilisateur, ignoreCase = true)) {
                        commandes.add(commande)
                    }
                }

                recyclerViewCommandes.adapter = CommandeAdapter(commandes)
            },
            { error ->
                Toast.makeText(this, "Erreur de récupération des commandes", Toast.LENGTH_SHORT).show()
                Log.e("GET_COMMANDES", error.toString())
            }
        )

        queue.add(request)

        // Retour à l’accueil
        btnNouvelleCommande.setOnClickListener {
            finish()
        }
    }
}
