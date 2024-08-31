package com.example.hochschule_koblenz_chat_app.utils;

import android.annotation.SuppressLint;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Die FirebaseUtil-Klasse stellt Hilfsfunktionen für die Interaktion mit Firebase Auth, Firestore und Firebase Storage bereit.
 * Sie bietet Methoden zur Verwaltung von Benutzerdaten, Chatrooms, Nachrichten und Profilbildern.
 * Autor: Mohamed Bebba
 */
public class FirebaseUtil {

    /**
     * Gibt die aktuelle Benutzer-ID des angemeldeten Benutzers zurück.
     *
     * @return Die Benutzer-ID des aktuell angemeldeten Benutzers oder null, wenn nicht angemeldet.
     */
    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    /**
     * Überprüft, ob ein Benutzer eingeloggt ist.
     *
     * @return true, wenn ein Benutzer eingeloggt ist, sonst false.
     */
    public static boolean isLoggedIn() {
        return currentUserId() != null;
    }

    /**
     * Gibt die DocumentReference des aktuellen Benutzers zurück.
     *
     * @return Die DocumentReference des angemeldeten Benutzers.
     */
    public static DocumentReference currentUserDetails() {
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    /**
     * Gibt die CollectionReference für alle Benutzer zurück.
     *
     * @return Die CollectionReference der "users"-Sammlung in Firestore.
     */
    public static CollectionReference allUserCollectionReference() {
        return FirebaseFirestore.getInstance().collection("users");
    }

    /**
     * Gibt die DocumentReference für einen bestimmten Chatroom zurück.
     *
     * @param chatroomId Die ID des Chatrooms.
     * @return Die DocumentReference des angegebenen Chatrooms.
     */
    public static DocumentReference getChatroomReference(String chatroomId) {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    /**
     * Gibt die CollectionReference für die Nachrichten eines bestimmten Chatrooms zurück.
     *
     * @param chatroomId Die ID des Chatrooms.
     * @return Die CollectionReference der Nachrichten innerhalb des angegebenen Chatrooms.
     */
    public static CollectionReference getChatroomMessageReference(String chatroomId) {
        return getChatroomReference(chatroomId).collection("chats");
    }

    /**
     * Generiert eine Chatroom-ID basierend auf den Benutzer-IDs der Teilnehmer.
     * Die IDs werden lexikografisch sortiert, um eine konsistente Chatroom-ID zu erzeugen.
     *
     * @param userId1 Die Benutzer-ID des ersten Teilnehmers.
     * @param userId2 Die Benutzer-ID des zweiten Teilnehmers.
     * @return Die generierte Chatroom-ID.
     */
    public static String getChatroomId(String userId1, String userId2) {
        if (userId1.hashCode() < userId2.hashCode()) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }

    /**
     * Gibt die CollectionReference für alle Chatrooms zurück.
     *
     * @return Die CollectionReference der "chatrooms"-Sammlung in Firestore.
     */
    public static CollectionReference allChatroomCollectionReference() {
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    /**
     * Gibt die DocumentReference des anderen Benutzers im Chatroom zurück, basierend auf den Benutzer-IDs.
     *
     * @param userIds Die Liste der Benutzer-IDs im Chatroom.
     * @return Die DocumentReference des anderen Benutzers im Chatroom.
     */
    public static DocumentReference getOtherUserFromChatroom(List<String> userIds) {
        if (userIds.get(0).equals(FirebaseUtil.currentUserId())) {
            return allUserCollectionReference().document(userIds.get(1));
        } else {
            return allUserCollectionReference().document(userIds.get(0));
        }
    }

    /**
     * Konvertiert einen Firestore-Timestamp in eine Zeit-String im Format "HH:mm".
     *
     * @param timestamp Der Timestamp, der konvertiert werden soll.
     * @return Der Zeit-String im Format "HH:mm".
     */
    @SuppressLint("SimpleDateFormat")
    public static String timestampToString(Timestamp timestamp) {
        return new SimpleDateFormat("HH:mm").format(timestamp.toDate());
    }

    /**
     * Meldet den aktuell angemeldeten Benutzer von Firebase ab.
     */
    public static void logout() {
        FirebaseAuth.getInstance().signOut();
    }

    /**
     * Gibt die StorageReference für das Profilbild des aktuellen Benutzers zurück.
     *
     * @return Die StorageReference des Profilbilds des angemeldeten Benutzers.
     */
    public static StorageReference getCurrentProfilePicStorageRef() {
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(FirebaseUtil.currentUserId());
    }

    /**
     * Gibt die StorageReference für das Profilbild eines anderen Benutzers zurück.
     *
     * @param otherUserId Die Benutzer-ID des anderen Benutzers.
     * @return Die StorageReference des Profilbilds des anderen Benutzers.
     */
    public static StorageReference getOtherProfilePicStorageRef(String otherUserId) {
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(otherUserId);
    }
}
