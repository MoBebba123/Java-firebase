package com.example.hochschule_koblenz_chat_app;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hochschule_koblenz_chat_app.adapter.ChatRecyclerAdapter;
import com.example.hochschule_koblenz_chat_app.model.ChatMessageModel;
import com.example.hochschule_koblenz_chat_app.model.ChatroomModel;
import com.example.hochschule_koblenz_chat_app.model.UserModel;
import com.example.hochschule_koblenz_chat_app.utils.AndroidUtil;
import com.example.hochschule_koblenz_chat_app.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Die ChatActivity verwaltet die Chat-Funktionalität zwischen zwei Benutzern.
 * Sie zeigt die Nachrichten an, ermöglicht das Senden von Nachrichten und Benachrichtigungen.
 * Autor: Mohamed Bebba
 */
public class ChatActivity extends AppCompatActivity {

    // Variablen für den anderen Benutzer, die Chatraum-ID und das ChatroomModel
    UserModel otherUser;
    String chatroomId;
    ChatroomModel chatroomModel;
    ChatRecyclerAdapter adapter;

    // UI-Elemente
    EditText messageInput;
    ImageButton sendMessageBtn;
    ImageButton backBtn;
    TextView otherUsername;
    RecyclerView recyclerView;
    ImageView imageView;

    /**
     * Initialisiert die Aktivität.
     *
     * @param savedInstanceState gespeicherter Zustand der Aktivität
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // UserModel aus dem Intent abrufen
        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        // Chatraum-ID generieren basierend auf den Benutzer-IDs
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(), otherUser.getUserId());

        // UI-Elemente initialisieren
        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);
        imageView = findViewById(R.id.profile_pic_image_view);

        // Profilbild des anderen Benutzers abrufen und anzeigen
        FirebaseUtil.getOtherProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if (t.isSuccessful()) {
                        Uri uri = t.getResult();
                        AndroidUtil.setProfilePic(this, uri, imageView);
                    }
                });

        // Klick-Listener für den Zurück-Button
        backBtn.setOnClickListener((v) -> {
            getOnBackPressedDispatcher().onBackPressed();
        });
        // Anzeigen des Benutzernamens des anderen Benutzers
        otherUsername.setText(otherUser.getUsername());

        // Klick-Listener für den Senden-Button
        sendMessageBtn.setOnClickListener((v -> {
            String message = messageInput.getText().toString().trim();
            if (message.isEmpty())
                return;
            sendMessageToUser(message);
        }));

        // Initialisieren oder Abrufen des ChatroomModels
        getOrCreateChatroomModel();
        // Setup der RecyclerView für den Chat
        setupChatRecyclerView();
    }

    /**
     * Richtet die RecyclerView für die Anzeige der Chat-Nachrichten ein.
     */
    void setupChatRecyclerView() {
        // Abfrage für alle Nachrichten im Chatraum, sortiert nach Zeitstempel in absteigender Reihenfolge
        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        // FirestoreRecyclerOptions erstellen, um die Abfrageergebnisse in die RecyclerView zu laden
        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();

        // Initialisieren des Adapters mit den Optionen und Setzen auf die RecyclerView
        adapter = new ChatRecyclerAdapter(options, getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true); // Setzen der Layout-Richtung auf umgekehrt, um die neuesten Nachrichten oben anzuzeigen
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening(); // Startet das Anhören von Änderungen in der Firestore-Abfrage

        // Automatisches Scrollen zur neuesten Nachricht, wenn neue Nachrichten eingefügt werden
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    /**
     * Sendet eine Nachricht an den anderen Benutzer und aktualisiert das ChatroomModel.
     *
     * @param message Die zu sendende Nachricht
     */
    void sendMessageToUser(String message) {
        // Aktualisieren der letzten Nachricht und des Zeitstempels im ChatroomModel
        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatroomModel.setLastMessage(message);
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

        // Erstellen und Senden des ChatMessageModels
        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtil.currentUserId(), Timestamp.now());
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            messageInput.setText(""); // Eingabefeld zurücksetzen
                            sendNotification(message); // Benachrichtigung senden
                        }
                    }
                });
    }

    /**
     * Holt das bestehende ChatroomModel oder erstellt ein neues, falls es noch nicht existiert.
     */
    void getOrCreateChatroomModel() {
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if (chatroomModel == null) {
                    // Erstmaliger Chat
                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserId(), otherUser.getUserId()),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                }
            }
        });
    }

    /**
     * Sendet eine Benachrichtigung an den anderen Benutzer.
     *
     * @param message Die Nachricht, die in der Benachrichtigung angezeigt wird
     */
    void sendNotification(String message) {
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                UserModel currentUser = task.getResult().toObject(UserModel.class);
                try {
                    JSONObject jsonObject = new JSONObject();

                    // Erstellen des Benachrichtigungs-Objekts
                    JSONObject notificationObj = new JSONObject();
                    assert currentUser != null;
                    notificationObj.put("title", currentUser.getUsername());
                    notificationObj.put("body", message);

                    // Erstellen des Daten-Objekts
                    JSONObject dataObj = new JSONObject();
                    dataObj.put("userId", currentUser.getUserId());

                    // Zusammenstellen des gesamten JSON-Objekts
                    jsonObject.put("notification", notificationObj);
                    jsonObject.put("data", dataObj);
                    jsonObject.put("to", otherUser.getFcmToken());

                    // API-Aufruf zur FCM zum Senden der Benachrichtigung
                    callApi(jsonObject);

                } catch (Exception ignored) {
                }

            }
        });

    }

    /**
     * Führt einen API-Aufruf zur Firebase Cloud Messaging API aus, um die Benachrichtigung zu senden.
     *
     * @param jsonObject Das JSON-Objekt, das die Benachrichtigungsdaten enthält
     */
    void callApi(JSONObject jsonObject) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer YOUR_API_KEY")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Fehlerbehandlung für den API-Aufruf
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // Behandlung der erfolgreichen Antwort des API-Aufrufs
            }
        });

    }
}
