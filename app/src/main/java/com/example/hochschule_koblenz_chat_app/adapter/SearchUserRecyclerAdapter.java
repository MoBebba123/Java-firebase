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

import com.example.hochschule_koblenz_chat_app.R;
import com.example.hochschule_koblenz_chat_app.model.UserModel;
import com.example.hochschule_koblenz_chat_app.utils.AndroidUtil;
import com.example.hochschule_koblenz_chat_app.ChatActivity;
import com.example.hochschule_koblenz_chat_app.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

/**
 * Der SearchUserRecyclerAdapter ist ein Adapter für eine RecyclerView, die
 * Benutzerprofile anzeigt,
 * basierend auf einer Firestore-Abfrage. Er ermöglicht das Durchsuchen und
 * Anklicken von Benutzerprofilen,
 * um eine Chat-Aktivität zu starten.
 * 
 * @autor: Mohamed Bebba
 */
public class SearchUserRecyclerAdapter
        extends FirestoreRecyclerAdapter<UserModel, SearchUserRecyclerAdapter.UserModelViewHolder> {

    // Kontext der Anwendung
    Context context;

    /**
     * Konstruktor für den SearchUserRecyclerAdapter.
     *
     * @param options Die FirestoreRecyclerOptions, die die Abfrageergebnisse
     *                enthalten.
     * @param context Der Kontext, in dem der Adapter verwendet wird.
     */
    public SearchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context) {
        super(options);
        this.context = context;
    }

    /**
     * Bindet die Daten eines UserModel an die ViewHolder-Komponenten.
     *
     * @param holder   Der ViewHolder, der die Ansichtselemente hält.
     * @param position Die Position des Elements im Adapter.
     * @param model    Das UserModel-Objekt, das an die Ansicht gebunden wird.
     */
    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull UserModel model) {
        // Setzen des Benutzernamens und der Telefonnummer auf die entsprechenden
        // TextViews
        holder.usernameText.setText(model.getUsername());
        holder.phoneText.setText(model.getPhone());

        // Markiert den aktuellen Benutzer als "(Me)"
        if (model.getUserId().equals(FirebaseUtil.currentUserId())) {
            holder.usernameText.setText(model.getUsername() + " (Me)");
        }

        // Laden und Anzeigen des Profilbilds mithilfe der Glide-Bibliothek
        FirebaseUtil.getOtherProfilePicStorageRef(model.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if (t.isSuccessful()) {
                        Uri uri = t.getResult();
                        AndroidUtil.setProfilePic(context, uri, holder.profilePic);
                    }
                });

        // Klick-Listener für das Listenelement, um zur ChatActivity zu navigieren
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            AndroidUtil.passUserModelAsIntent(intent, model); // Übergibt die Benutzerdaten an die ChatActivity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    /**
     * Erstellt einen neuen ViewHolder, wenn keine vorhandenen ViewHolder mehr für
     * das Recycling zur Verfügung stehen.
     *
     * @param parent   Die übergeordnete ViewGroup, zu der diese Ansicht hinzugefügt
     *                 wird.
     * @param viewType Der Typ der neuen Ansicht.
     * @return Ein neues UserModelViewHolder-Objekt.
     */
    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Erstellen der Ansicht für jedes Listenelement
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row, parent, false);
        return new UserModelViewHolder(view);
    }

    /**
     * Der UserModelViewHolder hält die UI-Komponenten für jedes Listenelement.
     * Er enthält die TextViews für den Benutzernamen, die Telefonnummer und das
     * ImageView für das Profilbild.
     */
    class UserModelViewHolder extends RecyclerView.ViewHolder {
        // UI-Komponenten für jedes Listenelement
        TextView usernameText; // TextView für den Benutzernamen
        TextView phoneText; // TextView für die Telefonnummer
        ImageView profilePic; // ImageView für das Profilbild

        /**
         * Konstruktor für UserModelViewHolder.
         *
         * @param itemView Die Ansicht des Listenelements.
         */
        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialisieren der UI-Komponenten
            usernameText = itemView.findViewById(R.id.user_name_text);
            phoneText = itemView.findViewById(R.id.phone_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}
