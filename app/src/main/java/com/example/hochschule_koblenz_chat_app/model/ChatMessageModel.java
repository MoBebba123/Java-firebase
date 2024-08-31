package com.example.hochschule_koblenz_chat_app.model;

import com.google.firebase.Timestamp;

/**
 * Die ChatMessageModel-Klasse repräsentiert eine einzelne Chat-Nachricht in der Anwendung.
 * Sie enthält Informationen über den Nachrichtentext, die ID des Absenders und den Zeitstempel der Nachricht.
 * Autor: Mohamed Bebba
 */
public class ChatMessageModel {

    // Felder für die Nachrichtendaten
    private String message; // Inhalt der Nachricht
    private String senderId; // Benutzer-ID des Absenders der Nachricht
    private Timestamp timestamp; // Zeitstempel, wann die Nachricht gesendet wurde

    /**
     * Standardkonstruktor für ChatMessageModel.
     */
    public ChatMessageModel() {
    }

    /**
     * Konstruktor für ChatMessageModel mit Parametern.
     *
     * @param message   Der Inhalt der Nachricht.
     * @param senderId  Die Benutzer-ID des Absenders der Nachricht.
     * @param timestamp Der Zeitstempel der Nachricht.
     */
    public ChatMessageModel(String message, String senderId, Timestamp timestamp) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }

    /**
     * Gibt den Inhalt der Nachricht zurück.
     *
     * @return Der Nachrichtentext.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Setzt den Inhalt der Nachricht.
     *
     * @param message Der Nachrichtentext, der gesetzt werden soll.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gibt die Benutzer-ID des Absenders der Nachricht zurück.
     *
     * @return Die Benutzer-ID des Absenders.
     */
    public String getSenderId() {
        return senderId;
    }

    /**
     * Setzt die Benutzer-ID des Absenders der Nachricht.
     *
     * @param senderId Die Benutzer-ID, die gesetzt werden soll.
     */
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    /**
     * Gibt den Zeitstempel der Nachricht zurück.
     *
     * @return Der Zeitstempel der Nachricht.
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * Setzt den Zeitstempel der Nachricht.
     *
     * @param timestamp Der Zeitstempel, der gesetzt werden soll.
     */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
