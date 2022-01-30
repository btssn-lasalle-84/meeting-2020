package com.lasalle.meeting;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.net.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @file Communication.java
 * @brief Déclaration de la classe Communication
 * @author Vincent DEVINE
 */

/**
 * @class Communication
 * @brief Déclaration de la classe Communication
 */
public class Communication implements Runnable
{
    /**
     * Constantes
     */
    private final static String TAG = "Communication";              //!< TAG utilisé dans les log
    private final static String adresseMulticast = "239.0.0.42";    //!< adresse de multicast des portiers
    private final static int PORT = 5000;                           //!< port d'ecoute des portiers
    public static final int TIMEOUT_RECEPTION_REPONSE = 30000;      //!< temps maximum d'une réponse d'un portier
    public final static int CODE_RECEPTION = 1;                     //!< code de reception correcte pour le portiers
    private final ReentrantLock mutex = new ReentrantLock();
    /**
     * Attributs
     */
    private DatagramSocket socket;                  //!< attribut récuperant les informations de la socket
    private InetAddress adresseIP = null;           //!< attribut récuperant l'adresse IP du portier
    private Handler handler;                        //!< attribut permetant d'envoyer une requête par rapport a une autre activity
    private String trame;                           //!< attribut récuperant la trame
    /**
     * Protocole
     */
    public static final String DELIMITEUR_EN_TETE = "$";
    public static final String DELIMITEUR_CHAMP = ";";
    public static final String DELIMITEUR_FIN = "\r\n";
    public static final int NB_DELIMITEURS_GET_1 = 6; // $GET;1\r\n
    public static final int NB_DELIMITEURS_GET_2 = 3; // $GET;2\r\n
    public static final int NB_DELIMITEURS_GET_3 = 1; // $GET;3\r\n

    /**
     * @brief constructeur de communication
     * @param handler Handler
     * @return void
     */
    public Communication(Handler handler)
    {
        this.handler = handler;
        try
        {
            socket = new DatagramSocket(PORT);
            socket.setSoTimeout(Communication.TIMEOUT_RECEPTION_REPONSE);
        }
        catch (SocketException se)
        {
            se.printStackTrace();
        }

        try
        {
            this.adresseIP = InetAddress.getByName(adresseMulticast);
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * @brief constructeur de communication
     * @param handler Handler, port int
     * @return void
     */
    public Communication(int port, Handler handler)
    {
        this.handler = handler;
        try
        {
            socket = new DatagramSocket(port);
            socket.setSoTimeout(Communication.TIMEOUT_RECEPTION_REPONSE);
        }
        catch (SocketException se)
        {
            se.printStackTrace();
        }

        try
        {
            this.adresseIP = InetAddress.getByName(adresseMulticast);
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * @brief change le handler par celui mis en paramètre
     * @param handler Handler
     * @return void
     */
    public void setHandler(Handler handler)
    {
        mutex.lock();
        this.handler = handler;
        mutex.unlock();
    }

    /**
     * @brief méthode recevant les trames des portiers
     * @return void
     */
    public void recevoir()
    {
        byte[] reception = new byte[1024];

        while (socket != null && !socket.isClosed())
        {
            try
            {
                final DatagramPacket paquetRecu = new DatagramPacket(reception, reception.length);
                socket.receive(paquetRecu);

                trame = new String(paquetRecu.getData(), paquetRecu.getOffset(), paquetRecu.getLength());
                Log.d(TAG, "Réception de " + paquetRecu.getAddress().getHostAddress() + ":" + paquetRecu.getPort() + " -> " + trame);

                Message msg = Message.obtain();
                Bundle b = new Bundle();
                b.putString("adresseIP", paquetRecu.getAddress().getHostAddress());
                b.putInt("port", paquetRecu.getPort());
                b.putInt("etat", Communication.CODE_RECEPTION);
                b.putString("trame", trame);
                msg.setData(b);
                mutex.lock();
                handler.sendMessage(msg);
                mutex.unlock();
            }
            catch (Exception e)
            {
                Log.d(TAG, "Erreur recevoir() [socket.isClosed = " + socket.isClosed() + "]");
                e.printStackTrace();
            }
        }
    }

    /**
     * @brief méthode envoyant une requête à l'adresse de multicast
     * @param requete String
     * @return void
     */
    public void envoyer(final String requete)
    {
        if(socket == null)
            return;

        new Thread()
        {
            @Override public void run()
            {
                byte[] emission = new byte[1024];

                try
                {
                    emission = requete.getBytes();
                    DatagramPacket paquetRetour = new DatagramPacket(emission, emission.length, adresseIP, PORT);
                    socket.send(paquetRetour);
                    Log.d(TAG, "send() = " + requete);
                }
                catch (IOException e)
                {
                    Log.d(TAG, "Erreur send() [socket.isClosed = " + socket.isClosed() + "]");
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * @brief méthode envoyant une requête à l'adresse de indiqué en paramètre
     * @param requete String, adresseIP String
     * @return void
     */
    public void envoyer(final String requete, final String adresseIP)
    {
        if(socket == null)
            return;

        final InetAddress adresseIPDistante;
        try
        {
            adresseIPDistante = InetAddress.getByName(adresseIP);
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
            return;
        }

        new Thread()
        {
            @Override public void run()
            {
                byte[] emission = new byte[1024];

                try
                {
                    emission = requete.getBytes();
                    DatagramPacket paquetRetour = new DatagramPacket(emission, emission.length, adresseIPDistante, PORT);
                    socket.send(paquetRetour);
                    Log.d(TAG, "send() " + adresseIP + " = " + requete);
                }
                catch (IOException e)
                {
                    Log.d(TAG, "Erreur send() [socket.isClosed = " + socket.isClosed() + "]");
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * @brief méthode arrétant la socket, donc la communication avec les portiers
     * @return void
     */
    public void arreter()
    {
        if(socket == null)
            return;
        socket.close();
    }

    /**
     * @brief méthode appelée automatiquement quand le socket reçois quelque chose
     * @return void
     */
    @Override
    public void run()
    {
        recevoir();
    }
}
