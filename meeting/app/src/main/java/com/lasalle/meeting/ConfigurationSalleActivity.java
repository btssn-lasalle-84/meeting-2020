package com.lasalle.meeting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * @file ConfigurationSalleActivity.java
 * @brief Déclaration de la classe ConfigurationSalleActivity
 * @author Vincent DEVINE
 */

/**
 * @class ConfigurationSalleActivity
 * @brief Déclaration de la classe ConfigurationSalleActivity
 */
public class ConfigurationSalleActivity extends AppCompatActivity
{
    /**
     * Constantes
     */
    private final static String TAG = "ConfigurationSalleActivity"; //!< TAG utilisé pour les logs
    /**
     * Attributs
     */
    private Spinner listeSalles;                    //!< la vue
    private List<String> IPSalle;                   //!< les données traité
    private ArrayAdapter<String> adapter;           //!< l'adaptateur
    private static Vector<Salle> salles;            //!< les données non traité
    private Communication communication = null;     //!< attribut permetant d'envoyer une requête
    private int positionSalleList = 0;              //!< position dans la vue
    private String nom ="";                         //!< attribut du nom de la salle
    private String emplacement ="";                 //!< attribut de l'emplacement de la salle
    private String description ="";                 //!< attribut de la description de la salle
    private String surface ="";                     //!< attribut de la surface de la salle
    /**
     * Ressources layout activity_main
     */
    private EditText editNom;                       //!< layout récuperant le nom donné
    private EditText editEmplacement;               //!< layout récuperant l'emplacement donné
    private EditText editDescription;               //!< layout récuperant la description donné
    private EditText editSurface;                   //!< layout récuperant la surface donné
    private Button buttonEnvoie;                    //!< layout du bouton envoie

    /**
     * @brief Méthode appelée à la création de l'activité ConfigurationSalleActivity
     * @param savedInstanceState
     * @return void
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_salle);

        communication = MainActivity.getCommunication();

        initialiserRessourcesLayout();
        initialiserSpinner();
        setListener();
    }

    /**
     * @brief Récupère et initialise les widgets du layout activity_configuration_salle
     * @return void
     */
    public void initialiserRessourcesLayout()
    {
        Log.d(TAG, "initialiserRessourcesLayout()");

        listeSalles = (Spinner)findViewById(R.id.listeSalles);
        editNom = (EditText)findViewById(R.id.EditNom);
        editEmplacement = (EditText)findViewById(R.id.EditEmplacement);
        editDescription = (EditText)findViewById(R.id.EditDescription);
        editSurface = (EditText)findViewById(R.id.EditSurface);
        buttonEnvoie = (Button)findViewById(R.id.buttonEnvoie);
    }

    /**
     * @brief initialise la vue
     * @return void
     */
    public void initialiserSpinner()
    {
        Log.d(TAG, "initialiserSpinner()");

        IPSalle = new ArrayList<String>();

        salles = MainActivity.getMesSalles();
        String label = "";

        for(int i = 0; i < salles.size(); ++i)
        {
            if(!salles.elementAt(i).getNom().isEmpty())
            {
                label = salles.elementAt(i).getEmplacement() + " - " + salles.elementAt(i).getNom() + " (" + salles.elementAt(i).getAdresseIP() + ")";
            }
            else
            {
                label = salles.elementAt(i).getAdresseIP();
            }
            Log.d(TAG, "Ajout : " + label);
            IPSalle.add(label);
        }

        if(salles.size() > 0)
        {
            editNom.setText(salles.elementAt(0).getNom());
            editDescription.setText(salles.elementAt(0).getDescription());
            editEmplacement.setText(salles.elementAt(0).getEmplacement());
            editSurface.setText(Integer.toString(salles.elementAt(0).getSurface()));
        }

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, IPSalle);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        listeSalles.setAdapter(adapter);

        listeSalles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id)
            {
                positionSalleList = position;
                Log.d(TAG, "position : " + position + " - " + "nom : " + IPSalle.get(position));
                editNom.setText(salles.elementAt(position).getNom());
                editDescription.setText(salles.elementAt(position).getDescription());
                editEmplacement.setText(salles.elementAt(position).getEmplacement());
                editSurface.setText(Integer.toString(salles.elementAt(position).getSurface()));
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {
            }
        });
    }

    /**
     * @brief applique les listener sur les layouts approprié
     * @return void
     */
    public void setListener()
    {
        Log.d(TAG, "setListener()");

        buttonEnvoie.setOnClickListener(
            new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    recupererInformation();

                    Log.d(TAG,"trame : $SET;1;" + nom + ";" + description + ";" + emplacement + ";" + surface + "\r\n" + IPSalle.get(positionSalleList));
                    if(communication != null)
                    {
                        communication.envoyer("$SET;1;" + nom + ";" + description + ";" + emplacement + ";" + surface + "\r\n", salles.elementAt(positionSalleList).getAdresseIP());
                    }
                }
            }
        );
    }

    /**
     * @brief recuepe et applique les informations mis dans les layouts
     * @return void
     */
    public void recupererInformation()
    {
        Log.d(TAG, "recupererInformation()");

        nom = editNom.getText().toString();
        description = editDescription.getText().toString();
        emplacement = editEmplacement.getText().toString();
        surface = editSurface.getText().toString();
    }
}