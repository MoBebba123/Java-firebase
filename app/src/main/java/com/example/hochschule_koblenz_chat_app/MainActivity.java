package com.example.hochschule_koblenz_chat_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hochschule_koblenz_chat_app.utils.FirebaseUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Die MainActivity-Klasse ist die Hauptaktivität der Anwendung, die als Einstiegspunkt dient.
 * Sie verwaltet die Navigation zwischen verschiedenen Fragmenten (Chat und Profil) und
 * abonniert den FCM-Token (Firebase Cloud Messaging).
 * Autor: Mohamed Bebba
 */
public class MainActivity extends AppCompatActivity {

    // BottomNavigationView zur Verwaltung der unteren Navigationsleiste.
    BottomNavigationView bottomNavigationView;
    // ImageButton für die Suchfunktion.
    ImageButton searchButton;

    // Fragment für den Chat-Bereich der Anwendung.
    ChatFragment chatFragment;
    // Fragment für den Profil-Bereich der Anwendung.
    ProfileFragment profileFragment;

    /**
     * Diese Methode wird aufgerufen, wenn die Aktivität erstellt wird.
     * Sie initialisiert die Benutzeroberfläche und die Fragmente,
     * setzt Listener für die Navigation und den Such-Button und holt den FCM-Token.
     *
     * @param savedInstanceState Das Bundle-Objekt, das den gespeicherten Zustand der Aktivität enthält.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisieren der Fragmente.
        chatFragment = new ChatFragment();
        profileFragment = new ProfileFragment();

        // Verknüpfen der BottomNavigationView mit der Layout-Datei.
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        // Verknüpfen des Such-Buttons mit der Layout-Datei.
        searchButton = findViewById(R.id.main_search_btn);

        // Setzen eines OnClickListeners auf den Such-Button, um zur Suchaktivität zu navigieren.
        searchButton.setOnClickListener((v) -> {
            startActivity(new Intent(MainActivity.this, SearchUserActivity.class));
        });

        // Setzen eines OnItemSelectedListener auf die BottomNavigationView für die Navigation zwischen Fragmenten.
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Überprüfen, ob das ausgewählte Menüelement der Chat-Bereich ist.
                if (item.getItemId() == R.id.menu_chat) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, chatFragment).commit();
                }
                // Überprüfen, ob das ausgewählte Menüelement der Profil-Bereich ist.
                if (item.getItemId() == R.id.menu_profile) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, profileFragment).commit();
                }
                return true;
            }
        });

        // Setzen des Standard ausgewählten Menüelements auf den Chat-Bereich.
        bottomNavigationView.setSelectedItemId(R.id.menu_chat);

        // Abrufen des Firebase Cloud Messaging Tokens.
        getFCMToken();
    }

    /**
     * Diese Methode holt den FCM-Token (Firebase Cloud Messaging) und speichert ihn in der
     * Benutzerdatenbank mittels FirebaseUtil.
     */
    void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Abrufen des FCM-Tokens.
                String token = task.getResult();
                // Ausgabe des Tokens im Logcat.
                Log.i("fcmToken", token);
                // Aktualisieren der Benutzerdetails mit dem neuen FCM-Token.
                FirebaseUtil.currentUserDetails().update("fcmToken", token);
            }
        });
    }
}
