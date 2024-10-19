package com.horoftech.ratatouille.viewModels;

import static com.horoftech.ratatouille.utils.Configurations.ONLINE_USERS;
import static com.horoftech.ratatouille.utils.Configurations.RETRIVE;
import static com.horoftech.ratatouille.utils.Configurations.profileModel;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.horoftech.ratatouille.models.ProfileModel;

import kotlin.internal.OnlyInputTypes;

public class ActivityHomeViewModel extends ViewModel {
    private final MutableLiveData<Integer> onFragmentChange = new MutableLiveData<>();
    public MutableLiveData<ProfileModel>invitationListener = new MutableLiveData<>();
    public MutableLiveData<String>invitationAccepted = new MutableLiveData<>();
    public LiveData<Integer> getOnFragmentChange(){
        return onFragmentChange;
    }
    public MutableLiveData<String>retriveData = new MutableLiveData<>();
    public void setFragmentChanged(int position){
        onFragmentChange.setValue(position);
    }
    public void onInvitationReceived(ProfileModel profileModel){
        invitationListener.setValue(profileModel);
    }
    public void onInvitationAccepted(String roomID){
        invitationAccepted.setValue(roomID);
    }


}
