package com.horoftech.ratatouille.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.horoftech.ratatouille.databinding.FragmentHomeBinding;
import com.horoftech.ratatouille.databinding.FragmentSettingsBinding;
import com.horoftech.ratatouille.ui.MainActivity;
import com.horoftech.ratatouille.utils.Configurations;

public class SettingsFragment extends Fragment {
    public SettingsFragment() {

    }
    static SettingsFragment settingsFragment;
    public static SettingsFragment getInstance() {
        if (settingsFragment == null) {
            settingsFragment = new SettingsFragment();
        }
        return settingsFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentSettingsBinding binding = FragmentSettingsBinding.inflate(inflater,container,false);
        binding.signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth m = FirebaseAuth.getInstance();
                m.signOut();
                Configurations.client.signOut();
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });
        return binding.getRoot();
    }
}