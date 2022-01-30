package com.lasalle.meeting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static java.lang.Boolean.TRUE;

/**
 * @file MainActivity.java
 * @brief Déclaration de la classe MainActivity
 * @author Vincent DEVINE
 */

/**
 * @class MainActivity
 * @brief Déclaration de la classe MainActivity
 */
public class MainActivity extends AppCompatActivity  implements RechercherNomBoiteDialogue.rechercheNomBoiteDialogueListener
{
    /**
     * Constantes
     */
    private static final String TAG = "MainActivity";   //!< TAG utilisé pour les logs
    private final String SEPARATEUR = ";";              //!< séparateur utilisé dans le protocole meeting
    private static final int FROID = -3;                //!< Constant niveau de confort FROID
    private static final int FRAIS = -2;                //!< Constant niveau de confort FRAIS
    private static final int LEGEREMENTFRAIS = -1;      //!< Constant niveau de confort LEGEREMENTFRAIS
    private static final int NEUTRE = 0;                //!< Constant niveau de confort NEUTRE
    private static final int LEGEREMENTTIEDE = 1;       //!< Constant niveau de confort LEGEREMENTTIEDE
    private static final int TIEDE = 2;                 //!< Constant niveau de confort TIEDE
    private static final int CHAUD = 3;                 //!< Constant niveau de confort CHAUD

    /**
     * Ressources layout activity_main
     */
    private Spinner filtre;                             //!< la vue
    private ArrayAdapter<String> adapterList;           //!< l'adaptateur
    private SwipeRefreshLayout swipeRefreshLayout;      //!< layout permettant de rafraichir
    private RecyclerView recyclerView;                  //!< la vue des salles
    private RecyclerView.Adapter adapter;               //!< l'adaptateur pour la vue des salles
    private RecyclerView.LayoutManager layoutManager;   //!< le gestionnaire de mise en page
    private BottomNavigationView bottomNavigationView;  //!< layout permettant d'avoir un menu de navigation (en haut)
    private AlertDialog.Builder boiteConfort;           //!< boite de dialogue pour le filtre confort
    private AlertDialog.Builder boiteDisponibilite;     //!< boite de dialogue pour le filtre disponibilité
    private AlertDialog.Builder boiteNom;               //!< boite de dialogue pour le filtre nom

    /**
     * Attributs
     */
    private static Vector<Salle> mesSalles;             //!< Vecteur contenant mes salles (moyen de stockage)
    private static Vector<Salle> sallesFilter;          //!< Vecteur contenant mes salles aprés le filtre appliquer par l'utilisateur
    private static Communication communication = null;  //!< attribut permetant d'envoyer des requêtes
    private Salle salle = null;                         //!< attribut salle
    private WifiManager wm = null;                      //!< attribut permetant de voir la connection au wi-fi

    /**
     * @brief Méthode appelée à la création de l'activité MainActivity
     * @param savedInstanceState
     * @return void
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mesSalles = new Vector<Salle>();

        initialiserRessourcesLayout();
        connecterWiFi();
        initialiserSalles();
    }

    /**
     * @brief Méthode appelée au démarrage de l'activité MainActivity
     * @return void
     */
    @Override
    protected void onStart()
    {
        super.onStart();
        Log.d(TAG, "onStart()");

        rafraichir(mesSalles);
    }

    /**
     * @brief Méthode appelée au démarrage de l'activité MainActivity
     * @return void
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * @brief Méthode appelée quand on appuye sur boutons du menu
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        final Intent intent = new Intent(MainActivity.this, ConfigurationSalleActivity.class);

        switch (item.getItemId())
        {
            case R.id.configurerSalle:
                startActivity(intent);
                return true;
            case R.id.aPropos:
                Toast.makeText(getApplicationContext(), "La fonctionnalité à propos de l'application n'est pas encore disponible !", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * @brief Récupère et initialise les widgets du layout activity_main
     * @return void
     */
    public void initialiserRessourcesLayout()
    {
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView)findViewById(R.id.listeSalle);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        filtre = (Spinner)findViewById(R.id.filtre);

        setListenBouton();
        initialiserBoiteDialogue();
        initialiserSpinner();
    }

    /**
     * @brief applique les listener sur les layouts approprié
     * @return void
     */
    public void setListenBouton()
    {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                rafraichir(mesSalles);
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
                        rafraichir(mesSalles);
                        Toast.makeText(getApplicationContext(), "Rafraichisement", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.Favoris:
                        Toast.makeText(getApplicationContext(), "La fonctionnalité favoris n'est pas encore disponible !", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.Rechercher:
                        /*final Intent intent = new Intent(MainActivity.this, RechercheActivity.class);
                        intent.putExtra("mesSalles", mesSalles);
                        startActivity(intent);*/
                        Toast.makeText(getApplicationContext(), "La fonctionnalité rechercher n'est pas encore disponible !", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });
    }

    /**
     * @brief méthode permettant de se connecter au wi-fi
     * @return void
     */
    private void connecterWiFi()
    {
        wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wm.isWifiEnabled())
        {
            Log.d(TAG, "connecterWiFi() WiFi indisponible !");
            wm.setWifiEnabled(true);
        }
        else
        {
            Log.d(TAG, "connecterWiFi() WiFi disponible");
        }

        WifiInfo wi = wm.getConnectionInfo();
        Log.d(TAG, "connecterWiFi() " + wi.toString() + " " + wi.getIpAddress() + " " + wi.getMacAddress());

        DhcpInfo di = wm.getDhcpInfo();
        Log.d(TAG, "connecterWiFi() " + di.toString());

        communication = new Communication(handlerUI);
        Thread tCommunicationUDP = new Thread(communication, "Communication");
        tCommunicationUDP.start();
    }

    /**
     * @brief initialise le vecteur, les afficheurs pour les salles
     * @return void
     */
    private void initialiserSalles()
    {
        Log.d(TAG, "initialiserSalles()");

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);

        adapter = new SalleAdapter(mesSalles);
        recyclerView.setAdapter(adapter);

        // cf. appel à rafraichir() dans onStart()
        /*if(communication != null)
        {
            communication.envoyer("$GET;1\r\n"); // voir protocole
        }*/
    }

    /**
     * @brief initialise le vecteur, les afficheurs pour les salles
     * @return void
     */
    private void reInitialiserSalles()
    {
        Log.d(TAG, "reInitialiserSalles()");

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);

        adapter = new SalleAdapter(sallesFilter);
        recyclerView.setAdapter(adapter);
    }

    /**
     * @brief ajoute une salle au vecteur (mesSalles)
     * @param maSalle Salle
     * @return void
     */
    public void ajouterSalle(Salle maSalle)
    {
        int positionMemeSalle = verifierExistenceSalle(maSalle);

        if(positionMemeSalle == -1)
        {
            mesSalles.add(maSalle);
            adapter.notifyDataSetChanged();
        }
        else if(verifierChangementSalle(maSalle, positionMemeSalle))
        {
            mesSalles.removeElementAt(positionMemeSalle);
            mesSalles.add(maSalle);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * @brief verifie que la salle n'exisite pas déjà dans le vecteur, r'envoye -1 si il n'en existe pas, r'envoye la position de la salle dans le vecteur si il existe
     * @param maSalle Salle
     * @return int
     */
    public int verifierExistenceSalle(Salle maSalle)
    {
        for(int i = 0; i < mesSalles.size(); ++i)
        {
            if(maSalle.getAdresseIP().equals(mesSalles.elementAt(i).getAdresseIP()))
            {
                return i;
            }
        }
        return -1;
    }

    //TODO a regarder
    public int verifierExistenceSalle(String adresseIP)
    {
        for(int i = 0; i < mesSalles.size(); ++i)
        {
            if(mesSalles.elementAt(i).getAdresseIP().equals(adresseIP))
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * @brief Vérifie que la salle n'est pas different que dans le vecteur, r'envoye true si il y a eu une modifiaction, r'envoye false si il y en a pas eux
     * @param maSalle Salle, positionMemeSalle int
     * @return boolean
     */
    public boolean verifierChangementSalle(Salle maSalle, int positionMemeSalle)
    {
        if(maSalle.getLibre() == mesSalles.elementAt(positionMemeSalle).getLibre())
        {
            return true;
        }
        else if(maSalle.getTemperature() == mesSalles.elementAt(positionMemeSalle).getTemperature())
        {
            return true;
        }
        else if(maSalle.getConfort() == mesSalles.elementAt(positionMemeSalle).getConfort())
        {
            return true;
        }
        else if(maSalle.getNom().equals(mesSalles.elementAt(positionMemeSalle).getNom()))
        {
            return true;
        }
        else if(maSalle.getEmplacement().equals(mesSalles.elementAt(positionMemeSalle).getEmplacement()))
        {
            return true;
        }
        else if(maSalle.getDescription().equals(mesSalles.elementAt(positionMemeSalle).getDescription()))
        {
            return true;
        }
        else if(maSalle.getSurface() == mesSalles.elementAt(positionMemeSalle).getSurface())
        {
            return true;
        }

        return false;
    }

    /**
     * @brief rafraichis mon affichage
     * @param mesSalles Vector<Salle>
     * @return void
     */
    public void rafraichir(Vector<Salle> mesSalles)
    {
        Log.d(TAG, "rafraichir()");
        if(communication != null)
            communication.envoyer("$GET;1\r\n"); // voir protocole

        swipeRefreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged();
    }

    /**
     * @brief permet de récuperer les trames
     * @param Message msg
     * @return void
     */
    private Handler handlerUI = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            Bundle b = msg.getData();

            switch(b.getInt("etat"))
            {
                case Communication.CODE_RECEPTION:
                    String trame = b.getString("trame");
                    if(!trame.startsWith(Communication.DELIMITEUR_EN_TETE))
                        return;
                    if(!trame.endsWith(Communication.DELIMITEUR_FIN))
                        return;
                    Log.d(TAG,"handleMessage() trame reçue de " + b.getString("adresseIP") + ":" + b.getInt("port"));
                    Log.d(TAG,"handleMessage() trame = " + trame.replace("\r\n", ""));
                    /**
                      * @todo Créer une méthode traiterTrame et améliorer le filtrage des trames reçues (cf. protocole)
                      */
                    String nouvelleTrame = trame.replace(Communication.DELIMITEUR_EN_TETE, "");
                    nouvelleTrame = nouvelleTrame.replace(Communication.DELIMITEUR_FIN, "");
                    int nbDelimiteurs = getNbDelimiteurs(trame);
                    String[] tramesDecompose = decomposerTrame(nouvelleTrame);
                    if(nbDelimiteurs == Communication.NB_DELIMITEURS_GET_1)
                    {
                        Salle salle = creerSalle(tramesDecompose, b.getString("adresseIP"));
                        if (salle != null)
                        {
                            ajouterSalle(salle);
                        }
                    }
                    else if(nbDelimiteurs == Communication.NB_DELIMITEURS_GET_2)
                    {

                    }
                    else if(nbDelimiteurs == Communication.NB_DELIMITEURS_GET_3)
                    {

                    }
                    break;
                default:
                    Log.d(TAG,"handleMessage() code état inconnu ! ");
            }
        }
    };

    /**
     * @brief créer une salle a partir de la trame
     * @param trameDecompose String[] , adresseIP String
     * @return salle
     */
    public Salle creerSalle(String[] trameDecompose, String adresseIP)
    {
        Log.d(TAG, "creerSalle() adresseIP = " + adresseIP);
        if(!trameDecompose[0].isEmpty())
        {
            Log.d(TAG, "creerSalle() trameDecompose[0] : " + trameDecompose[0] + " (nomSalle)");
            Log.d(TAG, "creerSalle() trameDecompose[1] : " + trameDecompose[1] + " (description)");
            Log.d(TAG, "creerSalle() trameDecompose[2] : " + trameDecompose[2] + " (emplacement)");
            Log.d(TAG, "creerSalle() trameDecompose[3] : " + trameDecompose[3] + " (surface)");
            Log.d(TAG, "creerSalle() trameDecompose[4] : " + trameDecompose[4] + " (disponibilité)");
            Log.d(TAG, "creerSalle() trameDecompose[5] : " + trameDecompose[5] + " (niveauDeConfort)");
            Log.d(TAG, "creerSalle() trameDecompose[6] : " + trameDecompose[6] + " (température)");
            int surface = 0;
            if(estEntier(trameDecompose[3]))
                surface = Integer.parseInt(trameDecompose[3]);
            int disponible = 0;
            if(estEntier(trameDecompose[4]))
                disponible = Integer.parseInt(trameDecompose[4]);
            int niveauConfort = 0;
            if(estEntier(trameDecompose[5]))
                niveauConfort = Integer.parseInt(trameDecompose[5]);
            float temperature = 0;
            if(estReel(trameDecompose[6]))
                temperature = Float.parseFloat(trameDecompose[6]);

            salle = new Salle(trameDecompose[0], trameDecompose[1], trameDecompose[2], disponible, surface, niveauConfort, temperature, adresseIP);
            return salle;
        }
        else
        {
            Log.d(TAG, "creerSalle() pas de nom de salle");
            String inconnuString = "???";
            int inconnuInt = 1;

            salle = new Salle(inconnuString, inconnuString , inconnuString , Integer.parseInt(trameDecompose[4]), inconnuInt,  Integer.parseInt(trameDecompose[5]), Float.parseFloat(trameDecompose[6]), adresseIP);
            return salle;
        }
    }

    /**
     * @brief découpe la trame
     * @param trame String
     * @return un tableau de string (String[])
     */
    public String[] decomposerTrame(String trame)
    {
        String[] tramesDecompose = trame.split(SEPARATEUR);
        return tramesDecompose;
    }

    /**
     * @brief retourne le nombre de limiteurs dans la trame
     * @param trame String
     * @return int
     */
    private int getNbDelimiteurs(String trame)
    {
        int nb = 0;
        for (int i = 0; i < trame.length(); i++)
        {
            if (trame.charAt(i) == ';')
            {
                nb++;
            }
        }
        return nb;
    }

    /**
     * @brief retourne le vecteur de salle
     * @return Vector<Salle>
     */
    public static Vector<Salle> getMesSalles()
    {
        return mesSalles;
    }

    /**
     * @brief retourne mon attribut communication
     * @return communication
     */
    public static Communication getCommunication()
    {
        return communication;
    }

    private boolean estEntier(String donnee)
    {
        try
        {
            Integer.parseInt(donnee);
        }
        catch(NumberFormatException e)
        {
            return false;
        }
        catch(NullPointerException e)
        {
            return false;
        }

        return true;
    }

    private boolean estReel(String donnee)
    {
        try
        {
            Float.parseFloat(donnee);
        }
        catch(NumberFormatException e)
        {
            return false;
        }
        catch(NullPointerException e)
        {
            return false;
        }

        return true;
    }

    /**
     * @brief initialise les boites de dialogue
     * @return void
     */
    public void initialiserBoiteDialogue()
    {
        boiteDisponibilite = new AlertDialog.Builder(this);
        boiteDisponibilite.setMessage("Voulez vous voir seulement les salles : ");
        boiteDisponibilite.setPositiveButton("Disponible", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                filtreDisponible();
            }
        });
        boiteDisponibilite.setNegativeButton("Occupé", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                filtreNonDisponible();
            }
        });

        boiteConfort = new AlertDialog.Builder(this);
        boiteConfort.setTitle("Choisir le niveau de confort");
        String[] niveauConfort = {"Chaud","Tiède","Légèrement tiède","Neutre","Légèrement fraiche","Fraiche","Froid"};
        boiteConfort.setItems(niveauConfort, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        filtreChaud();
                        break;
                    case 1:
                        filtreTiede();
                        break;
                    case 2:
                        filtreLegerementTiede();
                        break;
                    case 3:
                        filtreNeutre();
                        break;
                    case 4:
                        filtreLegerementFrais();
                        break;
                    case 5:
                        filtreFrais();
                        break;
                    case 6:
                        filtreFroid();
                        break;
                }
            }
        });
    }

    /**
     * @brief initialise la vue
     * @return void
     */
    public void initialiserSpinner()
    {
        Log.d(TAG, "initialiserSpinner()");

        List choixFiltre;
        choixFiltre = new ArrayList<String>();

        choixFiltre.add("Aucun");
        choixFiltre.add("Disponibilité");
        choixFiltre.add("Confort");
        choixFiltre.add("Nom");

        adapterList = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, choixFiltre);
        adapterList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        filtre.setAdapter(adapterList);

        filtre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id)
            {
                //Toast.makeText(getApplicationContext(), "Le choix du filtre est : " + position, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "position : " + position);
                initialiserFiltre(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {
            }
        });
    }

    public void initialiserFiltre(int positionFiltre)
    {
        if(positionFiltre == 0)
        {
            initialiserSalles();
        }
        else if(positionFiltre == 1)
        {
            boiteDisponibilite.show();
        }
        else if(positionFiltre == 2)
        {
            boiteConfort.show();
        }
        else if(positionFiltre == 3)
        {
            RechercherNomBoiteDialogue rechercherNomBoiteDialogue = new RechercherNomBoiteDialogue();
            rechercherNomBoiteDialogue.show(getSupportFragmentManager(), "rechercher une salle par son nom");
        }
    }

    public void filtreDisponible()
    {
        Log.d(TAG, "filtreDisponible()");
        sallesFilter = new Vector<Salle>();

        for(int i = 0; i < mesSalles.size(); i++)
        {
            if(mesSalles.elementAt(i).getLibre())
            {
                sallesFilter.add(mesSalles.get(i));
            }
        }
        reInitialiserSalles();
    }

    public void filtreNonDisponible()
    {
        Log.d(TAG, "filtreNonDisponible()");
        sallesFilter = new Vector<Salle>();

        for(int i = 0; i < mesSalles.size(); i++)
        {
            if(!mesSalles.elementAt(i).getLibre())
            {
                sallesFilter.add(mesSalles.elementAt(i));
            }
        }
        reInitialiserSalles();
    }

    public void filtreTiede()
    {
        Log.d(TAG, "filtreTiede()");
        sallesFilter = new Vector<Salle>();

        for(int i = 0; i < mesSalles.size(); i++)
        {
            if(mesSalles.elementAt(i).getConfort() == TIEDE)
            {
                sallesFilter.add(mesSalles.elementAt(i));
            }
        }
        reInitialiserSalles();
    }

    public void filtreLegerementTiede()
    {
        Log.d(TAG, "filtreLegerementTiede()");
        sallesFilter = new Vector<Salle>();

        for(int i = 0; i < mesSalles.size(); i++)
        {
            if(mesSalles.elementAt(i).getConfort() == LEGEREMENTTIEDE)
            {
                sallesFilter.add(mesSalles.elementAt(i));
            }
        }
        reInitialiserSalles();
    }

    public void filtreNeutre()
    {
        Log.d(TAG, "filtreNeutre()");
        sallesFilter = new Vector<Salle>();

        for(int i = 0; i < mesSalles.size(); i++)
        {
            if(mesSalles.elementAt(i).getConfort() == NEUTRE)
            {
                sallesFilter.add(mesSalles.elementAt(i));
            }
        }
        reInitialiserSalles();
    }

    public void filtreLegerementFrais()
    {
        Log.d(TAG, "filtreLegerementFrais()");
        sallesFilter = new Vector<Salle>();

        for(int i = 0; i < mesSalles.size(); i++)
        {
            if(mesSalles.elementAt(i).getConfort() == LEGEREMENTFRAIS)
            {
                sallesFilter.add(mesSalles.elementAt(i));
            }
        }
        reInitialiserSalles();
    }

    public void filtreFrais()
    {
        Log.d(TAG, "filtreFrais()");
        sallesFilter = new Vector<Salle>();

        for(int i = 0; i < mesSalles.size(); i++)
        {
            if(mesSalles.elementAt(i).getConfort() == FRAIS)
            {
                sallesFilter.add(mesSalles.elementAt(i));
            }
        }
        reInitialiserSalles();
    }

    public void filtreChaud()
    {
        Log.d(TAG, "filtreChaud()");
        sallesFilter = new Vector<Salle>();

        for(int i = 0; i < mesSalles.size(); i++)
        {
            if(mesSalles.elementAt(i).getConfort() == CHAUD)
            {
                sallesFilter.add(mesSalles.elementAt(i));
            }
        }
        reInitialiserSalles();
    }

    public void filtreFroid()
    {
        Log.d(TAG, "filtreFroid()");
        sallesFilter = new Vector<Salle>();

        for(int i = 0; i < mesSalles.size(); i++)
        {
            if(mesSalles.elementAt(i).getConfort() == FROID)
            {
                sallesFilter.add(mesSalles.elementAt(i));
            }
        }
        reInitialiserSalles();
    }

    @Override
    public void applyTexts(String nomSalleRechercher) {
        filtreNom(nomSalleRechercher);
    }

    public void filtreNom(String nomSalleRechercher)
    {
        Log.d(TAG, "filtreNom()");
        sallesFilter = new Vector<Salle>();

        for(int i = 0; i < mesSalles.size(); i++)
        {
            if(mesSalles.elementAt(i).getNom().equals(nomSalleRechercher))
            {
                sallesFilter.add(mesSalles.elementAt(i));
            }
        }
        reInitialiserSalles();
    }
}
