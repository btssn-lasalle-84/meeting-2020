package com.lasalle.meeting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @file SalleViewHolder.java
 * @brief Déclaration de la classe SalleViewHolder
 * @author Vincent DEVINE
 */

/**
 * @class SalleViewHolder
 * @brief Déclaration de la classe SalleViewHolder
 */
public class SalleViewHolder extends RecyclerView.ViewHolder
{
    /**
     * Constantes
     */
    private static final String TAG = "SalleViewHolder";        //!< TAG utilisé pour les logs
    /**
     * Ressources layout activity_main
     */
    private TextView nomSalle;                                  //!< layout texte du nom de la salle
    private TextView confortSalle;                              //!< layout texte du confort de la salle
    private TextView disponibiliteSalle;                        //!< layout texte de la disponibilité de la salle
    private ImageView imageDisponibiliteSalle;                  //!< layout image de la disponibilité de la salle
    /**
     * Attributs
     */
    private Context context;                                    //!< attribut permettant de communiquer avec une autre classe
    private Salle salle;                                        //!< attribut salle


    /**
     * @brief constructeur de SalleViewHolder
     * @param itemView final View
     */
    public SalleViewHolder(final View itemView)
    {
        super(itemView);

        Log.d(TAG, "SalleViewHolder(final View itemView)");

        nomSalle= ((TextView)itemView.findViewById(R.id.nomSalle));
        confortSalle = ((TextView)itemView.findViewById(R.id.confortSalle));
        disponibiliteSalle = ((TextView)itemView.findViewById(R.id.disponibiliteSalle));
        imageDisponibiliteSalle = ((ImageView)itemView.findViewById(R.id.imageDisponibiliteSalle));
        context = itemView.getContext();

        itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(context, SalleActivity.class);
                intent.putExtra("Salle", (Serializable) salle);
                context.startActivity(intent);
            }
        });
    }

    /**
     * @brief Méthode affichant les informations de la salle dans les layouts
     * @return void
     */
    public void afficher(Salle salle)
    {
        Log.d(TAG, "afficher ()");

        this.salle= salle;
        nomSalle.setText(salle.getNom());
        nomSalle.setTextSize(15);
        confortSalle.setText(salle.getConfortIHM());
        confortSalle.setTextSize(15);
        disponibiliteSalle.setText("La salle est "+ salle.getLibreIHM());
        disponibiliteSalle.setTextSize(15);

        if(salle.getLibre())
        {
            imageDisponibiliteSalle.setImageResource(R.drawable.rond_vert);
        }
        else
        {
            imageDisponibiliteSalle.setImageResource(R.drawable.rond_rouge);
        }
    }
}
