package com.lasalle.meeting;

/**
 * @file Salle.java
 * @brief Déclaration de la classe Salle
 * @author Vincent DEVINE
 */

import android.util.Log;

import java.io.Serializable;

/**
 * @class Salle
 * @brief Déclaration de la classe Salle
 */
public class Salle implements Serializable
{
    /**
     * Constantes
     */
    private static final String TAG = "Salle";          //!< TAG utilisé pour les logs
    private static final int FROID = -3;                //!< Constant niveau de confort FROID
    private static final int FRAIS = -2;                //!< Constant niveau de confort FRAIS
    private static final int LEGEREMENTFRAIS = -1;      //!< Constant niveau de confort LEGEREMENTFRAIS
    private static final int NEUTRE = 0;                //!< Constant niveau de confort NEUTRE
    private static final int LEGEREMENTTIEDE = 1;       //!< Constant niveau de confort LEGEREMENTTIEDE
    private static final int TIEDE = 2;                 //!< Constant niveau de confort TIEDE
    private static final int CHAUD = 3;                 //!< Constant niveau de confort CHAUD

    /**
     * Attributs
     */
    private String nom = "";            //!< Le nom de la salle
    private String description = "";    //!< La description de la salle
    private String emplacement = "";    //!< L'emplacement de la salle
    private boolean libre;              //!< L'état booléen d'occupation Libre de la salle
    private int surface;                //!< la surface de la salle
    private int confort;                //!< le niveau de confort de la salle
    private float temperature;          //!< la température de la salle
    private String adresseIP;           //!< l'adresse IP de la salle

    /**
     * @brief Constructeur de la classe Salle
     *
     * @param nom
     * @param description
     * @param emplacement
     * @param libre
     * @param surface
     * @param temperature
     * @param adresseIP
     */
    public Salle(String nom, String description, String emplacement, int libre, int surface, int confort, float temperature, String adresseIP)
    {
        this.nom = nom;
        this.description = description;
        this.emplacement = emplacement;
        setLibre(libre);
        this.surface = surface;
        this.confort = confort;
        this.temperature = temperature;
        this.adresseIP = adresseIP;
        Log.d(TAG, "Salle : nom = " + nom + " - description = " + description + " - emplacement " + emplacement + " - libre = " + libre + " - surface = " + surface + " - confort = " + confort + " - température = " + temperature + " - adresseIP = " + adresseIP);
    }

    /**
     * @brief Accesseur set de l'emplacement de la salle
     * @param nouvelleEmplacement l'emplacement de la salle
     */
    public void setEmplacement(String nouvelleEmplacement)
    {
        emplacement = nouvelleEmplacement;
    }

    /**
     * @brief Accesseur set du nom de la salle
     * @param nouveauNom le nom de la salle
     */
    public void setNom(String nouveauNom)
    {
        nom = nouveauNom;
    }

    /**
     * @brief Accesseur set la disponibilité de la salle
     * @param libre le nouvelle état de la salle
     */
    public void setLibre(int libre)
    {
        if (libre == 0)
        {
            this.libre = false;
        }
        else
        {
            this.libre = true;
        }
    }

    /**
     * @brief Accesseur set la disponibilité de la salle, change l'état de la salle
     */
    public void setLibre()
    {
        if (libre == true)
        {
            this.libre = false;
        }
        else
        {
            this.libre = true;
        }
    }

    /**
     * @brief Accesseur set la surface de la salle
     * @param nouvelleSurface le nouvelle surface de la salle
     */
    public void setSurface(int nouvelleSurface)
    {
        this.surface = nouvelleSurface;
    }

    /**
     * @brief Accesseur set du confort de la salle
     * @param nouveauConfort le nouveau confort de la salle
     */
    public void setConfort(int nouveauConfort)
    {
        this.confort = nouveauConfort;
    }

    /**
     * @brief Accesseur set la température de la salle
     * @param temperature la nouvelle température de la salle
     */
    public void setTemperature(float temperature)
    {
        this.temperature = temperature;
    }

    /**
     * @brief Accesseur set l'adresse IP de la salle
     * @param adresseIP la nouvelle adresse IP de la salle
     */
    public void setAdresseIP (String adresseIP)
    {
        this.adresseIP = adresseIP;
    }

    /**
     * @brief Accesseur get de l'emplacement de la salle
     * @return String l'emplacement de la salle
     */
    public final String getEmplacement()
    {
        return emplacement;
    }

    /**
     * @brief Accesseur get du nom de la salle
     * @return String le nom de la salle
     */
    public final String getNom()
    {
        return nom;
    }

    /**
     * @brief Accesseur get de libre de la salle
     * @return boolean la disponibilité de la salle
     */
    public final boolean getLibre()
    {
        return libre;
    }

    /**
     * @brief Accesseur get de libre de la salle
     * @return String la disponibilité de la salle, pour la trame
     */
    public final String getLibreTrame()
    {
        if(libre == false)
        {
            return "0";
        }
        else
        {
            return "1";
        }
    }

    /**
     * @brief Accesseur get de libre de la salle
     * @return String la disponibilité de la salle, pour l'affichage
     */
    public final String getLibreIHM()
    {
        if(libre == false)
        {
            return "occupée";
        }
        else
        {
            return "disponible";
        }
    }

    /**
     * @brief Accesseur get la surface de la salle
     * @return int la surface de la salle
     */
    public final int getSurface()
    {
        return surface;
    }

    /**
     * @brief Accesseur get le confort de la salle
     * @return int le niveau de confort de la salle
     */
    public final int getConfort()
    {
        return confort;
    }

    /**
     * @brief Accesseur get le confort de la salle
     * @return String le niveau de confort de la salle, pour l'affichage
     */
    public final String getConfortIHM()
    {
        String message = "";
        switch (confort)
        {
            case FROID:
                message = "Confort : Froid";
                break;
            case FRAIS:
                message = "Confort : Frais";
                break;
            case LEGEREMENTFRAIS:
                message = "Confort : Légèrement frais";
                break;
            case NEUTRE:
                message = "Confort : Neutre";
                break;
            case LEGEREMENTTIEDE:
                message = "Confort : Légèrement tiède";
                break;
            case TIEDE:
                message = "Confort : Tiède";
                break;
            case CHAUD:
                message = "Confort : Chaud";
                break;
        }
        return message;
    }

    /**
     * @brief Accesseur get la température de la salle
     * @return int la température de la salle
     */
    public final float getTemperature()
    {
        return temperature;
    }

    /**
     * @brief Accesseur get la description de la salle
     * @return String la description de la salle
     */
    public final String getDescription()
    {
        return description;
    }

    /**
     * @brief Accesseur get l'adresse IP de la salle
     * @return String l'adresse IP de la salle
     */
    public final String getAdresseIP()
    {
        return adresseIP;
    }
}
