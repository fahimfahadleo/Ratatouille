package com.horoftech.ratatouille.fragments;

import static androidx.core.content.ContextCompat.getSystemService;
import static com.horoftech.ratatouille.utils.Configurations.ONLINE_USERS;
import static com.horoftech.ratatouille.utils.Configurations.PLAYER1;
import static com.horoftech.ratatouille.utils.Configurations.REFERENCE_USERS;
import static com.horoftech.ratatouille.utils.Configurations.ROOMS;
import static com.horoftech.ratatouille.utils.Configurations.profileModel;
import static com.horoftech.ratatouille.viewModels.HomeFragmentViewModel.ACTION_ADD;
import static com.horoftech.ratatouille.viewModels.HomeFragmentViewModel.ACTION_COPY;
import static com.horoftech.ratatouille.viewModels.HomeFragmentViewModel.RESET;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.horoftech.ratatouille.R;
import com.horoftech.ratatouille.databinding.FragmentHomeBinding;
import com.horoftech.ratatouille.models.ProfileModel;
import com.horoftech.ratatouille.utils.Configurations;
import com.horoftech.ratatouille.utils.Functions;
import com.horoftech.ratatouille.utils.PushNotificationController;
import com.horoftech.ratatouille.viewModels.HomeFragmentViewModel;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {
    public HomeFragment() {

    }

    LifecycleOwner owner;
    ProfileModel userProfileModel;
    ProfileModel user2ProfileModel;
    static HomeFragment homeFragment;


    public static HomeFragment getInstance() {
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }
        return homeFragment;
    }

    public HomeFragmentViewModel model;
    FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        return binding.getRoot();
    }

    int player1win = 0;
    int player2win = 0;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        model = new ViewModelProvider(this).get(HomeFragmentViewModel.class);
        binding.setViewModel(model);
        owner = this;
        binding.setLifecycleOwner(owner);
        userProfileModel = Configurations.profileModel;
        binding.player1.setText(userProfileModel.getName());
        Picasso.get().load(Functions.getHighResPhoto(userProfileModel.getProfile())).into(binding.profilePicture1);
        setWinRate();
        initModel();
        binding.reference.setText(profileModel.getUniqueID());
    }

    @SuppressLint("SetTextI18n")
    void setWinRate() {
        binding.winrate1.setText("Win: "+ player1win);
        binding.winrate2.setText("Win: "+ player2win);
    }

    private void initModel() {
        model.init();
        model.action.observe(owner,action->{
            if(action.equals(ACTION_ADD)){
                String id = binding.edittext.getText().toString();
                if(!TextUtils.isEmpty(id)){
                    FirebaseDatabase.getInstance().getReference().child(ONLINE_USERS).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                String s = snapshot.getValue(String.class);
                                if(s!=null){
                                    if(s.equals(Configurations.AVAILABLE)){
                                        FirebaseDatabase.getInstance().getReference().child(REFERENCE_USERS).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                user2ProfileModel = snapshot.getValue(ProfileModel.class);

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });


                                        sendRequest(id);
                                    }else {
                                        Toast.makeText(getContext(), "Player currently unavailable!", Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Toast.makeText(getContext(), "Wrong ID!", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(getContext(), "Wrong ID!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }else if(action.equals(ACTION_COPY)){
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("nothing", binding.reference.getText().toString());
                clipboard.setPrimaryClip(clip);
            }else if(action.equals(RESET)){
                reset();
                if(model.getIsOnline()){
                    FirebaseDatabase.getInstance().getReference().child(ROOMS).child(model.getRoomID()).setValue(RESET);
                }
            }
        });
        model.map.observe(owner, p -> {
            String tag = String.valueOf(p.getTag());
            String data = p.getData();
            TextView tv = binding.getRoot().findViewWithTag(tag);
            tv.setText(data);
        });
        model.draw.observe(owner,p->{
            reset();
        });
        model.winner.observe(owner, p -> {
            String player = p.split("_")[0];

            String win = p.split("_")[1];
            switch (Integer.parseInt(win)) {
                case 0: {
                    binding.win1.setVisibility(View.VISIBLE);
                    break;
                }
                case 1: {
                    binding.win2.setVisibility(View.VISIBLE);
                    break;
                }
                case 2: {
                    binding.win3.setVisibility(View.VISIBLE);
                    break;
                }
                case 3: {
                    binding.win4.setVisibility(View.VISIBLE);
                    break;
                }
                case 4: {
                    binding.win5.setVisibility(View.VISIBLE);
                    break;
                }
                case 5: {
                    binding.win6.setVisibility(View.VISIBLE);
                    break;
                }
                case 6: {
                    binding.win7.setVisibility(View.VISIBLE);
                    break;
                }
                case 7: {
                    binding.win8.setVisibility(View.VISIBLE);
                    break;
                }
            }

            if(Integer.parseInt(player) == 1){
                player1win++;
            }else {
                player2win++;
            }
            Toast.makeText(getContext(), player.equals("1")?userProfileModel.getName():user2ProfileModel == null?"Player 2":user2ProfileModel.getName() +" Won", Toast.LENGTH_SHORT).show();
            setWinRate();
            disableView();

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                reset();
                enableView();
            }, 3000);
        });
    }

    private void sendRequest(String id) {
        PushNotificationController.sendNotification(id);
    }

    private void reset() {
        model.reset();
        for (int i = 1; i < 10; i++) {
            TextView textView = binding.getRoot().findViewWithTag(String.valueOf(i));
            textView.setText("");
        }
        binding.win1.setVisibility(View.GONE);
        binding.win2.setVisibility(View.GONE);
        binding.win3.setVisibility(View.GONE);
        binding.win4.setVisibility(View.GONE);
        binding.win5.setVisibility(View.GONE);
        binding.win6.setVisibility(View.GONE);
        binding.win7.setVisibility(View.GONE);
        binding.win8.setVisibility(View.GONE);
        model.isEven = true;
    }

    void enableView(){
        for (int i = 1; i < 10; i++) {
            TextView textView = binding.getRoot().findViewWithTag(String.valueOf(i));
            textView.setEnabled(true);
        }
    }
    void disableView(){
        for (int i = 1; i < 10; i++) {
            TextView textView = binding.getRoot().findViewWithTag(String.valueOf(i));
            textView.setEnabled(false);
        }
    }

    public void setUpOnlineGame(String s, ProfileModel userProfileModel,String player) {
        Log.e("checking",s);
        Log.e("player",player);

        model.setRoomID(s);
        model.setIsOnline(true);
        model.setMyPlayerID(player);
        if(!player.equals(PLAYER1)){
            user2ProfileModel = userProfileModel;

            Log.e("profile",userProfileModel.toString());
        }
        setUpPlayer2();

        FirebaseDatabase.getInstance().getReference().child(ROOMS).child(s).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String s = snapshot.getValue(String.class);
                    if(s!=null){
                        setUpGamePlaye(s);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setUpPlayer2() {
        if(user2ProfileModel == null){
            return;
        }
        binding.player2.setText(user2ProfileModel.getName());
        binding.winrate2.setText("Win: "+user2ProfileModel.getWins());
        Picasso.get().load(Functions.getHighResPhoto(user2ProfileModel.getProfile())).into(binding.profilePicture2);
        reset();
    }

    private void setUpGamePlaye(String s) {
        if(s.equals(RESET)){
            reset();
            return;
        }
        String[]position = s.split("_");
        binding.getRoot().findViewWithTag(position[1]).performClick();
        Log.e("myPleyaer",model.getMyPlayerID()+"move player id"+position[0]);
        if(!model.getMyPlayerID().equals(position[0])){
            disableView();
        }else {
            enableView();
        }
    }


}