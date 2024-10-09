package com.example.hochschule_koblenz_chat_app.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.hochschule_koblenz_chat_app.model.UserModel;

/**
 * Die AndroidUtil-Klasse stellt Hilfsfunktionen für allgemeine
 * Android-Operationen bereit,
 * wie das Anzeigen von Toast-Nachrichten, das Übergeben und Abrufen von
 * UserModel-Daten
 * über Intents und das Setzen von Profilbildern in ImageViews.
 * 
 * @autor: Mohamed Bebba
 */
public class AndroidUtil {

    /**
     * Zeigt eine Toast-Nachricht mit der angegebenen Nachricht an.
     *
     * @param context Der Kontext, in dem der Toast angezeigt wird.
     * @param message Die anzuzeigende Nachricht.
     */
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Übergibt die Daten eines UserModels über einen Intent.
     *
     * @param intent Der Intent, in dem die UserModel-Daten gesetzt werden.
     * @param model  Das UserModel, dessen Daten übergeben werden sollen.
     */
    public static void passUserModelAsIntent(Intent intent, UserModel model) {
        intent.putExtra("username", model.getUsername());
        intent.putExtra("phone", model.getPhone());
        intent.putExtra("userId", model.getUserId());
        intent.putExtra("fcmToken", model.getFcmToken());
    }

    /**
     * Holt die UserModel-Daten aus einem Intent und erstellt ein UserModel-Objekt.
     *
     * @param intent Der Intent, aus dem die UserModel-Daten abgerufen werden.
     * @return Ein UserModel-Objekt mit den Daten aus dem Intent.
     */
    public static UserModel getUserModelFromIntent(Intent intent) {
        UserModel userModel = new UserModel();
        userModel.setUsername(intent.getStringExtra("username"));
        userModel.setPhone(intent.getStringExtra("phone"));
        userModel.setUserId(intent.getStringExtra("userId"));
        userModel.setFcmToken(intent.getStringExtra("fcmToken"));
        return userModel;
    }

    /**
     * Setzt ein Profilbild in ein ImageView mithilfe der Glide-Bibliothek.
     * Das Bild wird als Kreis zugeschnitten.
     *
     * @param context   Der Kontext, in dem die Operation ausgeführt wird.
     * @param imageUri  Die URI des Bildes, das gesetzt werden soll.
     * @param imageView Das ImageView, in dem das Bild angezeigt wird.
     */
    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView) {
        Glide.with(context)
                .load(imageUri)
                .apply(RequestOptions.circleCropTransform()) // Anwenden der Kreisform auf das Bild
                .into(imageView);
    }
}
