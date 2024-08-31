package com.example.hochschule_koblenz_chat_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hochschule_koblenz_chat_app.model.UserModel;
import com.example.hochschule_koblenz_chat_app.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Objects;

/**
 * Die LoginUsernameActivity ermöglicht es dem Benutzer, einen Benutzernamen einzugeben und
 * sich mit diesem in die Anwendung einzuloggen.
 * Sie verwaltet die Eingabe, Überprüfung und Speicherung des Benutzernamens.
 * Autor: Mohamed Bebba
 */
public class LoginUsernameActivity extends AppCompatActivity {

    // Eingabefeld für den Benutzernamen.
    EditText usernameInput;
    // Button, um die Anmeldung abzuschließen.
    Button letMeInBtn;
    // Fortschrittsanzeige, die während der Datenverarbeitung angezeigt wird.
    ProgressBar progressBar;
    // Telefonnummer des Benutzers.
    String phoneNumber;
    // Das UserModel-Objekt, das die Benutzerdaten enthält.
    UserModel userModel;
    // Tag für die Protokollierung von Fehlern und Informationen.
    private static final String TAG = "LoginUsernameActivity";

    /**
     * Diese Methode wird aufgerufen, wenn die Aktivität erstellt wird.
     * Sie initialisiert die UI-Komponenten und ruft den aktuellen Benutzernamen ab,
     * falls dieser bereits existiert.
     *
     * @param savedInstanceState Das Bundle-Objekt, das den gespeicherten Zustand der Aktivität enthält.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_username);

        // Initialisierung der UI-Komponenten.
        usernameInput = findViewById(R.id.login_username);
        letMeInBtn = findViewById(R.id.login_let_me_in_btn);
        progressBar = findViewById(R.id.login_progress_bar);

        // Abrufen der Telefonnummer aus den übergebenen Intents.
        phoneNumber = Objects.requireNonNull(getIntent().getExtras()).getString("phone");
        // Abrufen des Benutzernamens, falls bereits vorhanden.
        getUsername();

        // Klick-Listener für den Button, um den Benutzernamen zu setzen.
        letMeInBtn.setOnClickListener((v -> setUsername()));
    }

    /**
     * Verarbeitet den Klick auf den "Let Me In"-Button.
     * Überprüft die Eingaben, erstellt oder aktualisiert das UserModel und speichert die Daten in Firestore.
     */
    void setUsername() {
        String username = usernameInput.getText().toString();
        // Überprüfen, ob der Benutzername gültig ist.
        if (username.isEmpty() || username.length() < 3) {
            usernameInput.setError("Username length should be at least 3 chars");
            return;
        }
        setInProgress(true);
        // Aktualisieren des UserModels mit dem neuen Benutzernamen.
        if (userModel != null) {
            userModel.setUsername(username);
        } else {
            // Erstellen eines neuen UserModels, wenn es noch nicht existiert.
            userModel = new UserModel(phoneNumber, username, Timestamp.now(), FirebaseUtil.currentUserId());
        }

        // Speichern der Benutzerdaten in Firestore.
        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setInProgress(false);
                if (task.isSuccessful()) {
                    // Bei Erfolg Start der MainActivity und Abschluss der aktuellen Aktivität.
                    Intent intent = new Intent(LoginUsernameActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    // Protokollieren von Fehlern, falls das Speichern fehlschlägt.
                    Log.e(TAG, "Failed to set username: " + task.getException().getMessage(), task.getException());
                }
            }
        });
    }

    /**
     * Ruft den aktuellen Benutzernamen aus Firestore ab und zeigt ihn in der Eingabemaske an, falls vorhanden.
     */
    void getUsername() {
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setInProgress(false);
                if (task.isSuccessful()) {
                    // Abrufen des UserModels aus der Firestore-Datenbank.
                    userModel = task.getResult().toObject(UserModel.class);
                    if (userModel != null) {
                        // Setzen des Benutzernamens in das Eingabefeld, falls er existiert.
                        usernameInput.setText(userModel.getUsername());
                    }
                } else {
                    // Protokollieren von Fehlern, falls das Abrufen fehlschlägt.
                    Log.e(TAG, "Failed to get username: " + task.getException().getMessage(), task.getException());
                }
            }
        });
    }

    /**
     * Setzt den Fortschrittsstatus und aktualisiert die Sichtbarkeit der UI-Komponenten entsprechend.
     *
     * @param inProgress Gibt an, ob die Aktion im Gange ist.
     */
    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            letMeInBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            letMeInBtn.setVisibility(View.VISIBLE);
        }
    }
}
