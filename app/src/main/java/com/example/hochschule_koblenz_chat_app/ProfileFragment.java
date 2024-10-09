package com.example.hochschule_koblenz_chat_app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.hochschule_koblenz_chat_app.model.UserModel;
import com.example.hochschule_koblenz_chat_app.utils.AndroidUtil;
import com.example.hochschule_koblenz_chat_app.utils.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * Das ProfileFragment ermöglicht dem Benutzer das Anzeigen und Bearbeiten
 * seines Profils.
 * Es bietet Funktionen zum Aktualisieren des Benutzernamens und Profilbildes
 * sowie zum Abmelden.
 * 
 * @autor: Mohamed Bebba
 */
public class ProfileFragment extends Fragment {

    // UI-Komponenten
    ImageView profilePic;
    EditText usernameInput;
    EditText phoneInput;
    Button updateProfileBtn;
    ProgressBar progressBar;
    TextView logoutBtn;

    // Modell für die aktuellen Benutzerdaten.
    UserModel currentUserModel;
    // Launcher zum Auswählen eines Profilbilds.
    ActivityResultLauncher<Intent> imagePickLauncher;
    // URI des ausgewählten Profilbilds.
    Uri selectedImageUri;

    /**
     * Konstruktor für ProfileFragment.
     */
    public ProfileFragment() {

    }

    /**
     * Wird aufgerufen, wenn das Fragment erstellt wird.
     * Hier wird der Launcher für die Bildauswahl initialisiert.
     *
     * @param savedInstanceState Das Bundle-Objekt, das den gespeicherten Zustand
     *                           des Fragments enthält.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            AndroidUtil.setProfilePic(getContext(), selectedImageUri, profilePic);
                        }
                    }
                });
    }

    /**
     * Erstellt und initialisiert die Benutzeroberfläche des Fragments.
     *
     * @param inflater           Inflater zum Erstellen der Ansicht.
     * @param container          Das Eltern-View, zu dem die Ansicht hinzugefügt
     *                           wird.
     * @param savedInstanceState Das Bundle-Objekt, das den gespeicherten Zustand
     *                           des Fragments enthält.
     * @return Die erstellte View für das Fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profilePic = view.findViewById(R.id.profile_image_view);
        usernameInput = view.findViewById(R.id.profile_username);
        phoneInput = view.findViewById(R.id.profile_phone);
        updateProfileBtn = view.findViewById(R.id.profle_update_btn);
        progressBar = view.findViewById(R.id.profile_progress_bar);
        logoutBtn = view.findViewById(R.id.logout_btn);

        // Benutzerinformationen abrufen und anzeigen.
        getUserData();

        // Klick-Listener für den Update-Button.
        updateProfileBtn.setOnClickListener((v -> updateBtnClick()));

        // Klick-Listener für den Logout-Button.
        logoutBtn.setOnClickListener((v) -> {
            // Löscht den FCM-Token und meldet den Benutzer ab.
            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        FirebaseUtil.logout();
                        Intent intent = new Intent(getContext(), SplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            });
        });

        // Klick-Listener für das Profilbild, um ein neues Bild auszuwählen.
        profilePic.setOnClickListener((v) -> {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512, 512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });
        });

        return view;
    }

    /**
     * Verarbeitet den Klick auf den Update-Button. Überprüft die Eingaben und lädt
     * ggf. ein neues Profilbild hoch.
     */
    void updateBtnClick() {
        String newUsername = usernameInput.getText().toString();
        if (newUsername.isEmpty() || newUsername.length() < 3) {
            usernameInput.setError("Username length should be at least 3 chars");
            return;
        }
        currentUserModel.setUsername(newUsername);
        setInProgress(true);

        // Überprüft, ob ein neues Profilbild ausgewählt wurde und lädt es hoch.
        if (selectedImageUri != null) {
            FirebaseUtil.getCurrentProfilePicStorageRef().putFile(selectedImageUri)
                    .addOnCompleteListener(task -> updateToFirestore());
        } else {
            updateToFirestore();
        }
    }

    /**
     * Aktualisiert die Benutzerdaten in Firestore.
     */
    void updateToFirestore() {
        FirebaseUtil.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        AndroidUtil.showToast(getContext(), "Updated successfully");
                    } else {
                        AndroidUtil.showToast(getContext(), "Update failed");
                    }
                });
    }

    /**
     * Ruft die aktuellen Benutzerdaten und das Profilbild ab und zeigt sie in den
     * UI-Komponenten an.
     */
    void getUserData() {
        setInProgress(true);

        // Profilbild aus dem Storage abrufen und anzeigen.
        FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult();
                        AndroidUtil.setProfilePic(getContext(), uri, profilePic);
                    }
                });

        // Benutzerdaten aus Firestore abrufen.
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProgress(false);
            currentUserModel = task.getResult().toObject(UserModel.class);
            usernameInput.setText(currentUserModel.getUsername());
            phoneInput.setText(currentUserModel.getPhone());
        });
    }

    /**
     * Setzt den Fortschrittsstatus und aktualisiert die Sichtbarkeit der
     * UI-Komponenten entsprechend.
     *
     * @param inProgress Gibt an, ob die Aktion im Gange ist.
     */
    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            updateProfileBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            updateProfileBtn.setVisibility(View.VISIBLE);
        }
    }
}
