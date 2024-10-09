package com.example.hochschule_koblenz_chat_app.model;

import com.google.firebase.Timestamp;

import java.util.List;

/**
 * Die ChatroomModel-Klasse repräsentiert einen Chatraum in der Anwendung.
 * Sie enthält Informationen über die Chatraum-ID, die Benutzer-IDs der
 * Teilnehmer,
 * den Zeitstempel der letzten Nachricht, die ID des Absenders der letzten
 * Nachricht und den Inhalt der letzten Nachricht.
 * 
 * @autor: Mohamed Bebba
 */
public class ChatroomModel {

    // Felder für die Chatroom-Informationen
    private String chatroomId; // Eindeutige ID des Chatrooms
    private List<String> userIds; // Liste der Benutzer-IDs der Teilnehmer im Chatroom
    private Timestamp lastMessageTimestamp; // Zeitstempel der letzten Nachricht im Chatroom
    private String lastMessageSenderId; // Benutzer-ID des Absenders der letzten Nachricht
    private String lastMessage; // Inhalt der letzten Nachricht

    /**
     * Standardkonstruktor für ChatroomModel.
     */
    public ChatroomModel() {
    }

    /**
     * Konstruktor für ChatroomModel mit Parametern.
     *
     * @param chatroomId           Die ID des Chatrooms.
     * @param userIds              Die Liste der Benutzer-IDs der Teilnehmer im
     *                             Chatroom.
     * @param lastMessageTimestamp Der Zeitstempel der letzten Nachricht im
     *                             Chatroom.
     * @param lastMessageSenderId  Die Benutzer-ID des Absenders der letzten
     *                             Nachricht.
     */
    public ChatroomModel(String chatroomId, List<String> userIds, Timestamp lastMessageTimestamp,
            String lastMessageSenderId) {
        this.chatroomId = chatroomId;
        this.userIds = userIds;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.lastMessageSenderId = lastMessageSenderId;
    }

    /**
     * Gibt die ID des Chatrooms zurück.
     *
     * @return Die Chatroom-ID.
     */
    public String getChatroomId() {
        return chatroomId;
    }

    /**
     * Setzt die ID des Chatrooms.
     *
     * @param chatroomId Die Chatroom-ID, die gesetzt werden soll.
     */
    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    /**
     * Gibt die Liste der Benutzer-IDs der Teilnehmer im Chatroom zurück.
     *
     * @return Die Liste der Benutzer-IDs.
     */
    public List<String> getUserIds() {
        return userIds;
    }

    /**
     * Setzt die Liste der Benutzer-IDs der Teilnehmer im Chatroom.
     *
     * @param userIds Die Liste der Benutzer-IDs, die gesetzt werden soll.
     */
    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    /**
     * Gibt den Zeitstempel der letzten Nachricht im Chatroom zurück.
     *
     * @return Der Zeitstempel der letzten Nachricht.
     */
    public Timestamp getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    /**
     * Setzt den Zeitstempel der letzten Nachricht im Chatroom.
     *
     * @param lastMessageTimestamp Der Zeitstempel, der gesetzt werden soll.
     */
    public void setLastMessageTimestamp(Timestamp lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    /**
     * Gibt die Benutzer-ID des Absenders der letzten Nachricht zurück.
     *
     * @return Die Benutzer-ID des letzten Nachrichtensenders.
     */
    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    /**
     * Setzt die Benutzer-ID des Absenders der letzten Nachricht.
     *
     * @param lastMessageSenderId Die Benutzer-ID, die gesetzt werden soll.
     */
    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }

    /**
     * Gibt den Inhalt der letzten Nachricht im Chatroom zurück.
     *
     * @return Der Inhalt der letzten Nachricht.
     */
    public String getLastMessage() {
        return lastMessage;
    }

    /**
     * Setzt den Inhalt der letzten Nachricht im Chatroom.
     *
     * @param lastMessage Die Nachricht, die gesetzt werden soll.
     */
    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
