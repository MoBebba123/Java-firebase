package com.example.hochschule_koblenz_chat_app;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hochschule_koblenz_chat_app.adapter.SearchUserRecyclerAdapter;
import com.example.hochschule_koblenz_chat_app.model.UserModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hochschule_koblenz_chat_app.utils.FirebaseUtil;

/**
 * Die SearchUserActivity-Klasse ermöglicht es dem Benutzer, nach anderen Benutzern in der Anwendung zu suchen.
 * Sie bietet eine Benutzeroberfläche für die Sucheingabe und zeigt die Suchergebnisse in einer RecyclerView an.
 * Autor: Mohamed Bebba
 */
public class SearchUserActivity extends AppCompatActivity {

    // Eingabefeld für den Benutzernamen zur Suche.
    EditText searchInput;
    // Such-Button, um die Suche auszulösen.
    ImageButton searchButton;
    // Zurück-Button, um zur vorherigen Aktivität zurückzukehren.
    ImageButton backButton;
    // RecyclerView zur Anzeige der Suchergebnisse.
    RecyclerView recyclerView;

    // Adapter für die RecyclerView, um Benutzerergebnisse darzustellen.
    SearchUserRecyclerAdapter adapter;

    /**
     * Diese Methode wird aufgerufen, wenn die Aktivität erstellt wird.
     * Sie initialisiert die UI-Komponenten und setzt Klick-Listener für die Schaltflächen.
     *
     * @param savedInstanceState Das Bundle-Objekt, das den gespeicherten Zustand der Aktivität enthält.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        // Initialisierung der UI-Komponenten.
        searchInput = findViewById(R.id.seach_username_input);
        searchButton = findViewById(R.id.search_user_btn);
        backButton = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.search_user_recycler_view);

        // Setzen des Fokus auf das Suchfeld beim Starten der Aktivität.
        searchInput.requestFocus();

        // Klick-Listener für den Zurück-Button, um zur vorherigen Seite zu navigieren.
        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // Klick-Listener für den Such-Button, um die Suche basierend auf dem eingegebenen Benutzernamen auszuführen.
        searchButton.setOnClickListener(v -> {
            String searchTerm = searchInput.getText().toString();
            if (searchTerm.isEmpty()) {
                setupSearchRecyclerView("");
            } else if (searchTerm.length() < 3) {
                // Fehleranzeige, wenn der eingegebene Benutzername zu kurz ist.
                searchInput.setError("Invalid Username");
                return;
            }
            setupSearchRecyclerView(searchTerm);
        });

        // Initialisierung der RecyclerView mit allen Benutzern, wenn die Aktivität gestartet wird.
        setupSearchRecyclerView("");
    }

    /**
     * Diese Methode initialisiert die RecyclerView mit einer Abfrage, um Benutzer basierend auf dem Suchbegriff zu filtern.
     *
     * @param searchTerm Der Suchbegriff, der verwendet wird, um Benutzer zu filtern.
     */
    void setupSearchRecyclerView(String searchTerm) {
        // Erstellen einer Abfrage, um Benutzer aus der Firebase-Datenbank zu filtern.
        Query query = FirebaseUtil.allUserCollectionReference()
                .whereGreaterThanOrEqualTo("username", searchTerm)
                .whereLessThanOrEqualTo("username", searchTerm + '\uf8ff');

        // Erstellen von FirestoreRecyclerOptions mit der Abfrage und dem UserModel.
        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class).build();

        // Initialisieren des Adapters mit den Optionen und Festlegen auf die RecyclerView.
        adapter = new SearchUserRecyclerAdapter(options, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
