package com.example.hochschule_koblenz_chat_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.hbb20.CountryCodePicker;

/**
 * Die LoginPhoneNumberActivity ermöglicht es dem Benutzer, sich mit seiner
 * Telefonnummer anzumelden.
 * Sie stellt eine Benutzeroberfläche zur Eingabe der Telefonnummer bereit und
 * leitet den Benutzer
 * zur OTP-Überprüfungsseite weiter.
 * 
 * @autor: Mohamed Bebba
 */
public class LoginPhoneNumberActivity extends AppCompatActivity {

    // UI-Komponenten zur Eingabe der Telefonnummer und Auswahl des Ländercodes.
    CountryCodePicker countryCodePicker;
    EditText phoneInput;
    // Button zum Absenden der Telefonnummer zur OTP-Überprüfung.
    Button sendOtpBtn;
    // Fortschrittsanzeige, die während der Verarbeitungszeit angezeigt wird.
    ProgressBar progressBar;

    /**
     * Diese Methode wird aufgerufen, wenn die Aktivität erstellt wird.
     * Sie initialisiert die UI-Komponenten und setzt einen Klick-Listener für den
     * Button.
     *
     * @param savedInstanceState Das Bundle-Objekt, das den gespeicherten Zustand
     *                           der Aktivität enthält.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Aktivieren des Edge-to-Edge-Designs für eine nahtlose Benutzeroberfläche.
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_phone_number);

        // Initialisierung der UI-Komponenten.
        countryCodePicker = findViewById(R.id.login_countrycode);
        phoneInput = findViewById(R.id.login_mobile_number);
        sendOtpBtn = findViewById(R.id.send_otp_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        progressBar.setVisibility(View.GONE);

        // Verknüpfen des CountryCodePickers mit dem Eingabefeld für die Telefonnummer.
        countryCodePicker.registerCarrierNumberEditText(phoneInput);

        // Klick-Listener für den Button, um die Telefonnummer zu überprüfen und zur
        // OTP-Überprüfungsseite zu wechseln.
        sendOtpBtn.setOnClickListener((v) -> {
            // Überprüfen, ob die eingegebene Telefonnummer gültig ist.
            if (!countryCodePicker.isValidFullNumber()) {
                phoneInput.setError("Phone number not valid");
                return;
            }
            // Starten der LoginOtpActivity und Übergeben der Telefonnummer.
            Intent intent = new Intent(LoginPhoneNumberActivity.this, LoginOtpActivity.class);
            intent.putExtra("phone", countryCodePicker.getFullNumberWithPlus());
            startActivity(intent);
        });
    }
}
