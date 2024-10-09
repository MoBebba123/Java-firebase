package com.example.hochschule_koblenz_chat_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hochschule_koblenz_chat_app.R;
import com.example.hochschule_koblenz_chat_app.model.ChatMessageModel;
import com.example.hochschule_koblenz_chat_app.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

/**
 * Der ChatRecyclerAdapter ist ein Adapter für eine RecyclerView, die Chat-Nachrichten in einer Chat-Aktivität anzeigt.
 * Er verwaltet die Anzeige von gesendeten und empfangenen Nachrichten und ordnet sie entsprechend dem Sender an.
 * Autor: Mohamed Bebba
 */
public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.ChatModelViewHolder> {

    // Kontext der Anwendung
    Context context;

    /**
     * Konstruktor für den ChatRecyclerAdapter.
     *
     * @param options Die FirestoreRecyclerOptions, die die Abfrageergebnisse enthalten.
     * @param context Der Kontext, in dem der Adapter verwendet wird.
     */
    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context) {
        super(options);
        this.context = context;
    }

    /**
     * Bindet die Daten eines ChatMessageModel an die ViewHolder-Komponenten und ordnet die Nachrichten entsprechend dem Sender an.
     *
     * @param holder   Der ViewHolder, der die Ansichtselemente hält.
     * @param position Die Position des Elements im Adapter.
     * @param model    Das ChatMessageModel-Objekt, das an die Ansicht gebunden wird.
     */
    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {
        // Überprüfen, ob die Nachricht vom aktuellen Benutzer gesendet wurde
        if (model.getSenderId().equals(FirebaseUtil.currentUserId())) {
            // Wenn der aktuelle Benutzer der Absender ist, wird die Nachricht auf der rechten Seite angezeigt
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatTextview.setText(model.getMessage());
        } else {
            // Wenn die Nachricht von einem anderen Benutzer stammt, wird sie auf der linken Seite angezeigt
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.leftChatTextview.setText(model.getMessage());
        }
    }

    /**
     * Erstellt einen neuen ViewHolder, wenn keine vorhandenen ViewHolder mehr für das Recycling zur Verfügung stehen.
     *
     * @param parent   Die übergeordnete ViewGroup, zu der diese Ansicht hinzugefügt wird.
     * @param viewType Der Typ der neuen Ansicht.
     * @return Ein neues ChatModelViewHolder-Objekt.
     */
    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Erstellen der Ansicht für jedes Listenelement
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row, parent, false);
        return new ChatModelViewHolder(view);
    }

    /**
     * Der ChatModelViewHolder hält die UI-Komponenten für jedes Listenelement.
     * Er enthält die Layouts und TextViews für gesendete und empfangene Nachrichten.
     */
    static class ChatModelViewHolder extends RecyclerView.ViewHolder {

        // Layouts und TextViews für die Anzeige von gesendeten und empfangenen Nachrichten
        LinearLayout leftChatLayout, rightChatLayout; // Layouts für Nachrichten des anderen Benutzers und des aktuellen Benutzers
        TextView leftChatTextview, rightChatTextview; // TextViews für die Nachrichteninhalte

        /**
         * Konstruktor für ChatModelViewHolder.
         *
         * @param itemView Die Ansicht des Listenelements.
         */
        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialisieren der UI-Komponenten
            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextview = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);
        }
    }
}
