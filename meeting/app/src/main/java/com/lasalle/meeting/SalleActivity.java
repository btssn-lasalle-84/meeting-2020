package com.lasalle.meeting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * @file SalleActivity.java
 * @brief Déclaration de la classe SalleActivity
 * @author Vincent DEVINE
 */

/**
 * @class SalleActivity
 * @brief Déclaration de la classe SalleActivity
 */
public class SalleActivity extends AppCompatActivity
{
    /**
     * Constantes
     */
    private static final String TAG = "SalleActivity";      //!< TAG utilisé pour les logs
    /**
     * Ressources layout activity_main
     */
    private Button boutonChangeEtat;                        //!< layout du prendre/liberer
    private TextView textNom;                               //!< layout texte du nom de la salle
    private TextView textEmplacement;                       //!< layout texte de l'emplacement de la salle
    private TextView textLibre;                             //!< layout texte de la disponibilité de la salle
    private TextView textConfort;                           //!< layout texte du confort de la salle
    private TextView textSurface;                           //!< layout texte de la surface de la salle
    private TextView textTemperature;                       //!< layout texte de la température de la salle
    private TextView textDescription;                       //!< layout texte de la description de la salle
    private SwipeRefreshLayout swipeRefreshLayout;          //!< layout permettant de rafraichir
    private BottomNavigationView bottomNavigationView;      //!< layout permettant d'avoir un menu de navigation (en haut)
    /**
     * Attributs
     */
    private Salle maSalle = null;                           //!< attribut salle
    private Communication communication = null;             //!< attribut permetant d'envoyer des requêtes

    /**
     * @brief Méthode appelée à la création de l'activité SalleActivity
     * @param savedInstanceState
     * @return void
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        setContentView(R.layout.activity_salle);

        Intent intent = getIntent();
        maSalle = (Salle)intent.getSerializableExtra("Salle");

        if(maSalle == null)
            Log.d(TAG, "Salle : " + maSalle.getNom());

        communication = MainActivity.getCommunication();

        initialiserRessourceIHM();
        setBoutonChangeEtat();
        afficherInformationSalle();
        setListener();
    }

    /**
     * @brief Méthode changeant le bouton dépendant de la disponibilité de la salle
     * @return void
     */
    public void setBoutonChangeEtat()
    {
        if (maSalle.getLibre() == true)
        {
            boutonChangeEtat.setText("Prendre");
            boutonChangeEtat.setBackgroundColor(Color.rgb(39,195,26));
        }
        else
        {
            boutonChangeEtat.setText("Libérer");
            boutonChangeEtat.setBackgroundColor(Color.rgb(222,55,25));
        }
    }

    /**
     * @brief Méthode affichant les informations de la salle dans les layouts
     * @return void
     */
    public void afficherInformationSalle()
    {
        textNom.setText(maSalle.getNom());
        textNom.setTextSize(35);
        textDescription.setText(maSalle.getDescription());
        textDescription.setTextSize(25);
        textEmplacement.setText(maSalle.getEmplacement());
        textEmplacement.setTextSize(25);
        textConfort.setText(maSalle.getConfortIHM());
        textConfort.setTextSize(25);
        textSurface.setText(Integer.toString(maSalle.getSurface()) + " m²");
        textSurface.setTextSize(25);
        textLibre.setTextSize(25);
        if (maSalle.getLibre() == true)
        {
            textLibre.setText("État : Libre");
        }
        else
        {
            textLibre.setText("État : Occupée");
        }
        textTemperature.setText(Float.toString(maSalle.getTemperature()) + " °C");
        textTemperature.setTextSize(25);
    }

    /**
     * @brief applique les listener sur les layouts approprié
     * @return void
     */
    public void setListener()
    {

        boutonChangeEtat.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        maSalle.setLibre();
                        setBoutonChangeEtat();
                        afficherInformationSalle();

                        if(communication != null)
                        {
                            communication.envoyer("$SET;3;" + maSalle.getLibreTrame() + "\r\n", maSalle.getAdresseIP());
                        }
                    }
                }
        );

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                afficherInformationSalle();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.Salle:
                        Toast.makeText(getApplicationContext(), "Salle", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.Favoris:
                        Toast.makeText(getApplicationContext(), "La fonctionnalité favoris n'est pas encore disponible !", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.Rechercher:
                        Toast.makeText(getApplicationContext(), "La fonctionnalité rechercher n'est pas encore disponible !", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });
    }

    /**
     * @brief Récupère et initialise les widgets du layout activity_salle
     * @return void
     */
    public void initialiserRessourceIHM()
    {
        boutonChangeEtat = (Button)findViewById(R.id.buttonChangeEtat);
        textNom = (TextView)findViewById(R.id.textViewNom);
        textEmplacement = (TextView)findViewById(R.id.textViewEmplacement);
        textLibre = (TextView)findViewById(R.id.textViewLibre);
        textConfort = (TextView)findViewById(R.id.textViewConfort);
        textSurface = (TextView)findViewById(R.id.textViewSurface);
        textTemperature = (TextView)findViewById(R.id.textViewTemperature);
        textDescription = (TextView)findViewById(R.id.textViewDescription);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
    }

    /**
     * @brief Méthode appelée à la fin de l'activité SalleActivity
     * @return void
     */
    @Override
    public void finish()
    {
        Log.d(TAG, "finish()");

        Intent intent = new Intent();

        intent.putExtra("salle", maSalle);

        setResult(RESULT_OK, intent);
        super.finish();
    }
}
