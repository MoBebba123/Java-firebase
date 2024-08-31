package com.example.hochschule_koblenz_chat_app;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hochschule_koblenz_chat_app.adapter.RecentChatRecyclerAdapter;
import com.example.hochschule_koblenz_chat_app.model.ChatroomModel;
import com.example.hochschule_koblenz_chat_app.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

/**
 * Das ChatFragment zeigt eine Liste der kürzlichen Chats des aktuellen Benutzers an.
 * Es verwendet einen RecyclerView-Adapter, um die Chat-Daten aus Firestore anzuzeigen.
 * Autor: Mohamed Bebba
 */
public class ChatFragment extends Fragment {

    // RecyclerView zur Anzeige der Chat-Liste.
    RecyclerView recyclerView;
    // Adapter für die RecyclerView, um die Chat-Räume anzuzeigen.
    RecentChatRecyclerAdapter adapter;

    /**
     * Konstruktor für ChatFragment.
     */
    public ChatFragment() {
    }

    /**
     * Erstellt und initialisiert die Benutzeroberfläche des Fragments.
     *
     * @param inflater  Inflater zum Erstellen der Ansicht.
     * @param container Das Eltern-View, zu dem die Ansicht hinzugefügt wird.
     * @param savedInstanceState Das Bundle-Objekt, das den gespeicherten Zustand des Fragments enthält.
     * @return Die erstellte View für das Fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating des Fragment-Layouts.
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        // Initialisieren der RecyclerView.
        recyclerView = view.findViewById(R.id.recyler_view);
        // Setup der RecyclerView für die Anzeige der Chat-Räume.
        setupRecyclerView2();  // Es gibt zwei Methoden, setupRecyclerView2 und setupRecyclerView, die den Adapter unterschiedlich konfigurieren.
        setupRecyclerView();

        return view;
    }

    /**
     * Richtet die RecyclerView mit einer Abfrage ein, die die Chat-Räume des aktuellen Benutzers anzeigt.
     * Die Abfrage ist unsortiert.
     */
    void setupRecyclerView2() {
        // Abfrage für alle Chat-Räume, in denen der aktuelle Benutzer enthalten ist.
        Query query = FirebaseUtil.allChatroomCollectionReference()
                .whereArrayContains("userIds", FirebaseUtil.currentUserId());

        // FirestoreRecyclerOptions erstellen, um die Abfrageergebnisse in die RecyclerView zu laden.
        FirestoreRecyclerOptions<ChatroomModel> options = new FirestoreRecyclerOptions.Builder<ChatroomModel>()
                .setQuery(query, ChatroomModel.class).build();
        // Initialisieren des Adapters mit den Optionen und Setzen auf die RecyclerView.
        adapter = new RecentChatRecyclerAdapter(options, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();  // Startet das Anhören von Änderungen in der Firestore-Abfrage.
    }

    /**
     * Richtet die RecyclerView mit einer Abfrage ein, die die Chat-Räume des aktuellen Benutzers anzeigt.
     * Die Abfrage sortiert die Chat-Räume nach dem Zeitstempel der letzten Nachricht in absteigender Reihenfolge.
     */
    void setupRecyclerView() {
        // Abfrage für alle Chat-Räume, in denen der aktuelle Benutzer enthalten ist, sortiert nach dem letzten Nachrichtenzeitstempel.
        Query query = FirebaseUtil.allChatroomCollectionReference()
                .whereArrayContains("userIds", FirebaseUtil.currentUserId())
                .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING);

        // FirestoreRecyclerOptions erstellen, um die sortierten Abfrageergebnisse in die RecyclerView zu laden.
        FirestoreRecyclerOptions<ChatroomModel> options = new FirestoreRecyclerOptions.Builder<ChatroomModel>()
                .setQuery(query, ChatroomModel.class).build();
        // Initialisieren des Adapters mit den Optionen und Setzen auf die RecyclerView.
        adapter = new RecentChatRecyclerAdapter(options, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();  // Startet das Anhören von Änderungen in der Firestore-Abfrage.
    }

    /**
     * Wird aufgerufen, wenn das Fragment sichtbar wird. Startet das Anhören des Adapters.
     */
    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();  // Adapter beginnt, Datenänderungen zu hören.
        }
    }

    /**
     * Wird aufgerufen, wenn das Fragment nicht mehr sichtbar ist. Stoppt das Anhören des Adapters.
     */
    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();  // Adapter hört auf, Datenänderungen zu hören.
        }
    }

    /**
     * Wird aufgerufen, wenn das Fragment wieder in den Vordergrund tritt. Benachrichtigt den Adapter über Datenänderungen.
     */
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();  // Adapter wird über Änderungen benachrichtigt, um die Anzeige zu aktualisieren.
        }
    }
}
