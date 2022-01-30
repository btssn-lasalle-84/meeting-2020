package com.lasalle.meeting;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;

public class RechercherNomBoiteDialogue extends AppCompatDialogFragment
{
    private EditText nomSalleRechercherBoiteDialogue;
    private rechercheNomBoiteDialogueListener listener;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_nom, null);

        builder.setView(view)
                .setTitle("Recherche par nom")
                .setNegativeButton("Annulé", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    String nomSalleRechercher = nomSalleRechercherBoiteDialogue.getText().toString();
                    listener.applyTexts(nomSalleRechercher);
                    }
                });

        nomSalleRechercherBoiteDialogue = view.findViewById(R.id.nomSalleRechercher);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try
        {
            listener = (rechercheNomBoiteDialogueListener) context;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "doit implementer rechercheNomBoiteDiallogueListener");
        }
    }

    public interface rechercheNomBoiteDialogueListener
    {
        void applyTexts(String nomSalleRechercher);
    }
}
