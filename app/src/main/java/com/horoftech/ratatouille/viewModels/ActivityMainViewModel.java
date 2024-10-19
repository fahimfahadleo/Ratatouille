package com.horoftech.ratatouille.viewModels;

import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.horoftech.ratatouille.R;

public class ActivityMainViewModel extends ViewModel {
    public MutableLiveData<String>performRegistration = new MutableLiveData<>();
    public static final String REGISTRATION = "registration";
    public void onRegisterClick(View vi){
        int id = vi.getId();
        if(id == R.id.button || id == R.id.google){
            performRegistration.setValue(REGISTRATION);
        }
    }
}
