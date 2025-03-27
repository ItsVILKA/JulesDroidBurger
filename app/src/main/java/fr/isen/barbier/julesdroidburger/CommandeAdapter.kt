package fr.isen.barbier.julesdroidburger

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CommandeAdapter(private val commandes: List<ConfirmationActivity.CommandeFirebase>) :
    RecyclerView.Adapter<CommandeAdapter.CommandeViewHolder>() {

    class CommandeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titre: TextView = itemView.findViewById(R.id.commandeTitre)
        val sousTitre: TextView = itemView.findViewById(R.id.commandeSousTitre)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommandeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_commande, parent, false)
        return CommandeViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommandeViewHolder, position: Int) {
        val commande = commandes[position]
        holder.titre.text = "${commande.burger} x${commande.quantity}"
        holder.sousTitre.text = "Pour ${commande.firstname} – Livraison le ${commande.deliverytime}"
    }

    override fun getItemCount(): Int = commandes.size
}
