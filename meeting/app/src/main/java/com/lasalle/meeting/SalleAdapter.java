package com.lasalle.meeting;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Vector;

/**
 * @file SalleAdapter.java
 * @brief Déclaration de la classe SalleAdapter
 * @author Vincent DEVINE
 */

/**
 * @class SalleAdapter
 * @brief Déclaration de la classe SalleAdapter
 */
public class SalleAdapter extends RecyclerView.Adapter<SalleViewHolder>
{
    /**
     * Constantes
     */
    private static final String TAG = "SalleAdapter";           //!< TAG utilisé pour les logs
    /**
     * Attributs
     */
    private Vector<Salle> mesSalles = null;                     //!< Vecteur contenant mes salles

    /**
     * @brief constructeur de SalleAdapter
     * @param mesSalles Vector<Salle>
     */
    public SalleAdapter(Vector<Salle> mesSalles)
    {
        Log.d(TAG, "SalleAdapter (Vector<Salle>)");

        if (mesSalles != null) {
            this.mesSalles = mesSalles;
        }
    }

    /**
     * @brief Méthode appelée à la création de l'activité SalleAdapter
     * @param parent ViewGroup, viewType int
     * @return SalleViewHolder
     */
    @NonNull
    @Override
    public SalleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Log.d(TAG, "SalleViewHolder onCreateViewHolder()");
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.salle, parent, false);
        return new SalleViewHolder(view);
    }

    /**
     * @brief Méthode appelée à la création de l'activité SalleAdapter
     * @param holder SalleViewHolder, position int
     * @return void
     */
    @Override
    public void onBindViewHolder(@NonNull SalleViewHolder holder, int position)
    {
        Log.d(TAG, "onBindViewHolder()");
        Salle salle = mesSalles.get(position);
        holder.afficher(salle);
    }

    /**
     * @brief Méthode appelée à la création de l'activité SalleAdapter
     * @return int
     */
    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount()");
        if (mesSalles != null)
            return mesSalles.size();
        return 0;
    }
}