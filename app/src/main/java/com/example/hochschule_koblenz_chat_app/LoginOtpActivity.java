package com.example.hochschule_koblenz_chat_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hochschule_koblenz_chat_app.utils.AndroidUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Die LoginOtpActivity verwaltet die OTP (One-Time Password) Verifizierung für
 * die Benutzeranmeldung.
 * Sie bietet die Möglichkeit, ein OTP zu senden, das OTP einzugeben und den
 * Benutzer nach erfolgreicher Verifizierung anzumelden.
 * 
 * @autor: Mohamed Bebba
 */
public class LoginOtpActivity extends AppCompatActivity {

    // Variablen für die Telefonnummer, Timeout, Verifizierungscode und Resending
    // Token
    String phoneNumber;
    Long timeoutSeconds = 60L; // Timeout in Sekunden für das OTP
    String verificationCode; // Der vom Server gesendete Verifizierungscode
    PhoneAuthProvider.ForceResendingToken resendingToken; // Token zum erneuten Senden des OTP

    // UI-Elemente
    EditText otpInput; // Eingabefeld für das OTP
    Button nextBtn; // Button zum Fortfahren nach Eingabe des OTP
    ProgressBar progressBar; // Fortschrittsanzeige
    TextView resendOtpTextView; // TextView zum erneuten Senden des OTP

    // Firebase-Authentifizierung
    FirebaseAuth mAuth = FirebaseAuth.getInstance(); // FirebaseAuth-Instanz zur Verwaltung der Benutzeranmeldung

    /**
     * Initialisiert die Aktivität.
     *
     * @param savedInstanceState gespeicherter Zustand der Aktivität
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);

        // UI-Elemente initialisieren
        otpInput = findViewById(R.id.login_otp);
        nextBtn = findViewById(R.id.login_next_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        resendOtpTextView = findViewById(R.id.resend_otp_textview);

        // Telefonnummer aus den übergebenen Intents abrufen
        phoneNumber = Objects.requireNonNull(getIntent().getExtras()).getString("phone");

        // OTP senden
        sendOtp(phoneNumber, false);

        // Klick-Listener für den "Weiter" Button
        nextBtn.setOnClickListener(v -> {
            // Eingegebenes OTP abrufen und die Anmeldung starten
            String enteredOtp = otpInput.getText().toString();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, enteredOtp);
            signIn(credential);
        });

        // Klick-Listener für den "OTP erneut senden" TextView
        resendOtpTextView.setOnClickListener((v) -> {
            sendOtp(phoneNumber, true);
        });
    }

    /**
     * Sendet ein OTP an die angegebene Telefonnummer.
     *
     * @param phoneNumber Telefonnummer, an die das OTP gesendet wird
     * @param isResend    Flag, ob es sich um eine erneute Sendung handelt
     */
    void sendOtp(String phoneNumber, boolean isResend) {
        startResendTimer(); // Timer für das erneute Senden des OTP starten
        setInProgress(true); // Zeigt die Fortschrittsanzeige an
        PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber) // Telefonnummer für die Verifizierung
                .setTimeout(timeoutSeconds, TimeUnit.SECONDS) // Timeout-Einstellung
                .setActivity(this) // Die aktuelle Aktivität für den Callback
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        // Bei erfolgreicher Verifizierung sofort anmelden
                        signIn(phoneAuthCredential);
                        setInProgress(false);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        // Fehler bei der Verifizierung
                        AndroidUtil.showToast(getApplicationContext(), "OTP-Verifizierung fehlgeschlagen");
                        setInProgress(false);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s,
                            @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        // OTP wurde erfolgreich gesendet
                        super.onCodeSent(s, forceResendingToken);
                        verificationCode = s; // Speichern des Verifizierungscodes
                        resendingToken = forceResendingToken; // Speichern des Resending Tokens
                        AndroidUtil.showToast(getApplicationContext(), "OTP erfolgreich gesendet");
                        setInProgress(false);
                    }
                });
        // Unterscheidung zwischen erstmaligem Senden und erneutem Senden des OTP
        if (isResend) {
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        } else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }

    /**
     * Meldet den Benutzer mit den angegebenen Anmeldeinformationen an.
     *
     * @param phoneAuthCredential Anmeldeinformationen für die
     *                            Telefonauthentifizierung
     */
    void signIn(PhoneAuthCredential phoneAuthCredential) {
        setInProgress(true); // Zeigt die Fortschrittsanzeige an
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                setInProgress(false);
                if (task.isSuccessful()) {
                    // Anmeldung erfolgreich, Weiterleitung zur LoginUsernameActivity
                    Intent intent = new Intent(LoginOtpActivity.this, LoginUsernameActivity.class);
                    intent.putExtra("phone", phoneNumber);
                    startActivity(intent);
                } else {
                    // Fehler bei der Anmeldung
                    AndroidUtil.showToast(getApplicationContext(), "OTP-Verifizierung fehlgeschlagen");
                }
            }
        });
    }

    /**
     * Setzt den Fortschrittszustand der UI.
     *
     * @param inProgress Flag, ob ein Fortschritt angezeigt werden soll
     */
    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            nextBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            nextBtn.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Startet einen Timer für das erneute Senden des OTP.
     */
    void startResendTimer() {
        resendOtpTextView.setEnabled(false); // Deaktivieren des TextView während des Timers
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                timeoutSeconds--; // Verringern des Timeouts
                resendOtpTextView.setText("OTP erneut senden in " + timeoutSeconds + " Sekunden");
                if (timeoutSeconds <= 0) {
                    // Timer beenden und TextView wieder aktivieren
                    timeoutSeconds = 60L;
                    timer.cancel();
                    runOnUiThread(() -> resendOtpTextView.setEnabled(true));
                }
            }
        }, 0, 1000); // Ausführung jede Sekunde
    }
}
