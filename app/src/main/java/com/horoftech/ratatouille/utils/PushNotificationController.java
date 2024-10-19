package com.horoftech.ratatouille.utils;

import static com.horoftech.ratatouille.utils.Configurations.AVAILABLE;
import static com.horoftech.ratatouille.utils.Configurations.INVITATION_ACCEPTED;
import static com.horoftech.ratatouille.utils.Configurations.INVITE;
import static com.horoftech.ratatouille.utils.Configurations.ONLINE_USERS;
import static com.horoftech.ratatouille.utils.Configurations.REFERENCE_USERS;
import static com.horoftech.ratatouille.utils.Configurations.RETRIVE;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.horoftech.ratatouille.models.ProfileModel;
import com.horoftech.ratatouille.viewModels.ActivityHomeViewModel;

import org.json.JSONObject;
public class PushNotificationController {
    ActivityHomeViewModel model;
    public void setUpListener(ActivityHomeViewModel model){
        this.model = model;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(ONLINE_USERS).child(Configurations.profileModel.getUniqueID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String change = snapshot.getValue(String.class);
                        if(change!=null && !change.equals(AVAILABLE)){
                            if(change.equals(RETRIVE)){
                                model.retriveData.setValue(RETRIVE);
                            }else {
                                String[]changes = change.split("_");
                                pickUpAction(changes[0],changes[1]);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


}
    public static void sendNotification(String id) {
        FirebaseDatabase.getInstance().getReference().child(ONLINE_USERS).child(id).setValue(INVITE+"_"+Configurations.profileModel.getUniqueID());

    }
    void pickUpAction(String action,String key){

        switch (action){
            case INVITE:{
                FirebaseDatabase.getInstance().getReference().child(REFERENCE_USERS).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ProfileModel profileModel = snapshot.getValue(ProfileModel.class);
                        model.onInvitationReceived(profileModel);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            }case INVITATION_ACCEPTED:{
                model.onInvitationAccepted(key);
                break;
            }
        }



    }

}
