package com.example.hochschule_koblenz_chat_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hochschule_koblenz_chat_app.model.UserModel;
import com.example.hochschule_koblenz_chat_app.utils.AndroidUtil;
import com.example.hochschule_koblenz_chat_app.utils.FirebaseUtil;

/**
 * Die SplashActivity-Klasse dient als Startbildschirm der Anwendung.
 * Sie zeigt das Splash-Layout an, überprüft den Anmeldestatus des Benutzers und
 * leitet entsprechend weiter.
 * Falls eine Benachrichtigung vorhanden ist, wird der Benutzer direkt zur
 * Chat-Aktivität geleitet.
 * 
 * @autor: Mohamed Bebba
 */
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    /**
     * Diese Methode wird aufgerufen, wenn die Aktivität erstellt wird.
     * Sie zeigt das Splash-Layout an und leitet den Benutzer basierend auf
     * Anmeldestatus oder Benachrichtigungsdaten weiter.
     *
     * @param savedInstanceState Das Bundle-Objekt, das den gespeicherten Zustand
     *                           der Aktivität enthält.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Überprüfen, ob die Aktivität durch eine Benachrichtigung gestartet wurde.
        if (getIntent().getExtras() != null) {
            // Holen der Benutzer-ID aus den Benachrichtigungsdaten.
            String userId = getIntent().getExtras().getString("userId");
            assert userId != null;

            // Abrufen der Benutzerdaten aus der Firebase-Datenbank.
            FirebaseUtil.allUserCollectionReference().document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Konvertieren des Datenbankdokuments in ein UserModel-Objekt.
                            UserModel model = task.getResult().toObject(UserModel.class);

                            // Starten der MainActivity ohne Animation.
                            Intent mainIntent = new Intent(this, MainActivity.class);
                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(mainIntent);

                            // Starten der ChatActivity und Übergeben des UserModels per Intent.
                            Intent intent = new Intent(this, ChatActivity.class);
                            assert model != null;
                            AndroidUtil.passUserModelAsIntent(intent, model);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                            // Beenden der SplashActivity.
                            finish();
                        }
                    });

        } else {
            // Verzögerter Start der nächsten Aktivität, abhängig vom Anmeldestatus des
            // Benutzers.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Überprüfen, ob der Benutzer eingeloggt ist.
                    if (FirebaseUtil.isLoggedIn()) {
                        // Starten der MainActivity, wenn der Benutzer eingeloggt ist.
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    } else {
                        // Starten der LoginPhoneNumberActivity, wenn der Benutzer nicht eingeloggt ist.
                        startActivity(new Intent(SplashActivity.this, LoginPhoneNumberActivity.class));
                    }
                    // Beenden der SplashActivity.
                    finish();
                }
            }, 1000); // Verzögerung von 1 Sekunde.
        }
    }
}
