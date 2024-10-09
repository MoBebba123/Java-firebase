package com.example.hochschule_koblenz_chat_app.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hochschule_koblenz_chat_app.ChatActivity;
import com.example.hochschule_koblenz_chat_app.R;
import com.example.hochschule_koblenz_chat_app.model.ChatroomModel;
import com.example.hochschule_koblenz_chat_app.model.UserModel;
import com.example.hochschule_koblenz_chat_app.utils.AndroidUtil;
import com.example.hochschule_koblenz_chat_app.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

/**
 * Der RecentChatRecyclerAdapter ist ein Adapter für eine RecyclerView, die die
 * letzten Chats des Benutzers anzeigt.
 * Er ermöglicht das Durchsuchen der Chatliste und den Übergang zur
 * Chat-Aktivität durch Anklicken eines Chat-Elements.
 * 
 * @autor: Mohamed Bebba
 */
public class RecentChatRecyclerAdapter
        extends FirestoreRecyclerAdapter<ChatroomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder> {

    // Kontext der Anwendung
    Context context;

    /**
     * Konstruktor für den RecentChatRecyclerAdapter.
     *
     * @param options Die FirestoreRecyclerOptions, die die Abfrageergebnisse
     *                enthalten.
     * @param context Der Kontext, in dem der Adapter verwendet wird.
     */
    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    /**
     * Bindet die Daten eines ChatroomModels an die ViewHolder-Komponenten.
     *
     * @param holder   Der ViewHolder, der die Ansichtselemente hält.
     * @param position Die Position des Elements im Adapter.
     * @param model    Das ChatroomModel-Objekt, das an die Ansicht gebunden wird.
     */
    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position,
            @NonNull ChatroomModel model) {
        // Abrufen der anderen Benutzerinformationen im Chatraum
        FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Überprüfen, ob die letzte Nachricht vom aktuellen Benutzer gesendet wurde
                        boolean lastMessageSentByMe = model.getLastMessageSenderId()
                                .equals(FirebaseUtil.currentUserId());

                        // Konvertieren des Firestore-Dokuments in ein UserModel-Objekt
                        UserModel otherUserModel = task.getResult().toObject(UserModel.class);

                        // Profilbild des anderen Benutzers laden und anzeigen
                        FirebaseUtil.getOtherProfilePicStorageRef(otherUserModel.getUserId()).getDownloadUrl()
                                .addOnCompleteListener(t -> {
                                    if (t.isSuccessful()) {
                                        Uri uri = t.getResult();
                                        AndroidUtil.setProfilePic(context, uri, holder.profilePic);
                                    }
                                });

                        // Setzen des Benutzernamens und der letzten Nachricht
                        holder.usernameText.setText(otherUserModel.getUsername());
                        if (lastMessageSentByMe)
                            holder.lastMessageText.setText("Du : " + model.getLastMessage());
                        else
                            holder.lastMessageText.setText(model.getLastMessage());

                        // Setzen des Zeitstempels der letzten Nachricht
                        holder.lastMessageTime.setText(FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));

                        // Klick-Listener für das Listenelement, um zur ChatActivity zu navigieren
                        holder.itemView.setOnClickListener(v -> {
                            Intent intent = new Intent(context, ChatActivity.class);
                            AndroidUtil.passUserModelAsIntent(intent, otherUserModel); // Übergibt die Benutzerdaten an
                                                                                       // die ChatActivity
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        });

                    }
                });
    }

    /**
     * Erstellt einen neuen ViewHolder, wenn keine vorhandenen ViewHolder mehr für
     * das Recycling zur Verfügung stehen.
     *
     * @param parent   Die übergeordnete ViewGroup, zu der diese Ansicht hinzugefügt
     *                 wird.
     * @param viewType Der Typ der neuen Ansicht.
     * @return Ein neues ChatroomModelViewHolder-Objekt.
     */
    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Erstellen der Ansicht für jedes Listenelement
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row, parent, false);
        return new ChatroomModelViewHolder(view);
    }

    /**
     * Der ChatroomModelViewHolder hält die UI-Komponenten für jedes Listenelement.
     * Er enthält die TextViews für den Benutzernamen, die letzte Nachricht, die
     * Zeit der letzten Nachricht und das ImageView für das Profilbild.
     */
    class ChatroomModelViewHolder extends RecyclerView.ViewHolder {
        // UI-Komponenten für jedes Listenelement
        TextView usernameText; // TextView für den Benutzernamen
        TextView lastMessageText; // TextView für die letzte Nachricht
        TextView lastMessageTime; // TextView für die Zeit der letzten Nachricht
        ImageView profilePic; // ImageView für das Profilbild

        /**
         * Konstruktor für ChatroomModelViewHolder.
         *
         * @param itemView Die Ansicht des Listenelements.
         */
        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialisieren der UI-Komponenten
            usernameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}
