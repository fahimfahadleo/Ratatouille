package com.horoftech.ratatouille.utils;

import static com.horoftech.ratatouille.utils.Configurations.REFERENCE_USERS;
import static com.horoftech.ratatouille.utils.Configurations.profileModel;

import androidx.annotation.NonNull;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCM extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Configurations.TOKEN = token;
        if(profileModel!=null) {
            profileModel.setId(token);
            FirebaseDatabase.getInstance().getReference().child(REFERENCE_USERS).child(profileModel.getUniqueID()).setValue(profileModel);
        }
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
    }
}
