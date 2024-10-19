package com.horoftech.ratatouille.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.horoftech.ratatouille.databinding.FragmentProfileBinding;
import com.horoftech.ratatouille.models.ProfileModel;
import com.horoftech.ratatouille.utils.Configurations;
import com.horoftech.ratatouille.utils.Functions;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {
    public ProfileFragment() {

    }
    static ProfileFragment profileFragment;
    public static ProfileFragment getInstance() {
        if (profileFragment == null) {
            profileFragment = new ProfileFragment();
        }
        return profileFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentProfileBinding binding = FragmentProfileBinding.inflate(inflater,container,false);
        ProfileModel profileModel = Configurations.profileModel;
        Picasso.get().load(Functions.getHighResPhoto(profileModel.getProfile())).into(binding.profile);
        binding.namelayout.setText(profileModel.getName());
        binding.wins.setText("Win: "+profileModel.getWins());
        binding.lose.setText("Lose: "+profileModel.getLoses());
        binding.emaillayout.setText(profileModel.getEmail());
        binding.id.setText(profileModel.getUniqueID());
        return binding.getRoot();
    }
}