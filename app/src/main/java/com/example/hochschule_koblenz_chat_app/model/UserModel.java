package com.example.hochschule_koblenz_chat_app.model;

import com.google.firebase.Timestamp;

/**
 * Die UserModel-Klasse repräsentiert die Benutzerdaten in der Anwendung.
 * Sie enthält Informationen wie die Telefonnummer, den Benutzernamen, den
 * Erstellungszeitpunkt,
 * die Benutzer-ID und den FCM-Token (Firebase Cloud Messaging Token) des
 * Benutzers.
 * 
 * @autor: Mohamed Bebba
 */
public class UserModel {

    // Benutzerinformationen
    private String phone; // Telefonnummer des Benutzers
    private String username; // Benutzername
    private Timestamp createdTimestamp; // Zeitstempel der Erstellung des Benutzerkontos
    private String userId; // Eindeutige Benutzer-ID
    private String fcmToken; // FCM-Token für Benachrichtigungen

    /**
     * Standardkonstruktor für UserModel.
     */
    public UserModel() {
    }

    /**
     * Konstruktor für UserModel mit Parametern.
     *
     * @param phone            Die Telefonnummer des Benutzers.
     * @param username         Der Benutzername.
     * @param createdTimestamp Der Zeitstempel der Erstellung des Benutzerkontos.
     * @param userId           Die eindeutige Benutzer-ID.
     */
    public UserModel(String phone, String username, Timestamp createdTimestamp, String userId) {
        this.phone = phone;
        this.username = username;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
    }

    /**
     * Gibt die Telefonnummer des Benutzers zurück.
     *
     * @return Die Telefonnummer des Benutzers.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Setzt die Telefonnummer des Benutzers.
     *
     * @param phone Die Telefonnummer, die gesetzt werden soll.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gibt den Benutzernamen zurück.
     *
     * @return Der Benutzername.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setzt den Benutzernamen.
     *
     * @param username Der Benutzername, der gesetzt werden soll.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gibt den Zeitstempel der Erstellung des Benutzerkontos zurück.
     *
     * @return Der Zeitstempel der Erstellung des Benutzerkontos.
     */
    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    /**
     * Setzt den Zeitstempel der Erstellung des Benutzerkontos.
     *
     * @param createdTimestamp Der Zeitstempel, der gesetzt werden soll.
     */
    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    /**
     * Gibt die eindeutige Benutzer-ID zurück.
     *
     * @return Die Benutzer-ID.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Setzt die Benutzer-ID.
     *
     * @param userId Die Benutzer-ID, die gesetzt werden soll.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gibt den FCM-Token des Benutzers zurück.
     *
     * @return Der FCM-Token des Benutzers.
     */
    public String getFcmToken() {
        return fcmToken;
    }

    /**
     * Setzt den FCM-Token des Benutzers.
     *
     * @param fcmToken Der FCM-Token, der gesetzt werden soll.
     */
    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
