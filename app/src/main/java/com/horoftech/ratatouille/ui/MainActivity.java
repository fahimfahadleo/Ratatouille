package com.horoftech.ratatouille.ui;

import static com.horoftech.ratatouille.utils.Configurations.REFERENCE_PAIR;
import static com.horoftech.ratatouille.utils.Configurations.REFERENCE_USERS;
import static com.horoftech.ratatouille.utils.Configurations.profileModel;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.horoftech.ratatouille.models.ProfileModel;
import com.horoftech.ratatouille.R;
import com.horoftech.ratatouille.databinding.ActivityMainBinding;
import com.horoftech.ratatouille.utils.Configurations;
import com.horoftech.ratatouille.utils.Functions;
import com.horoftech.ratatouille.utils.OpenProgress;
import com.horoftech.ratatouille.viewModels.ActivityMainViewModel;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    ActivityMainViewModel model;
    private FirebaseAuth mAuth;
    GoogleSignInClient client;
    GoogleSignInOptions gso;
    FirebaseUser currentUser;
    OpenProgress openProgress;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        model = new ViewModelProvider(this).get(ActivityMainViewModel.class);
        binding.setViewModel(model);
        binding.setLifecycleOwner(this);
        Functions.init(this);
        initObserver();
        mAuth = FirebaseAuth.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1041284880884-8qmohgf164dadp4gp9q7gp1e6bsskgka.apps.googleusercontent.com")
                .requestEmail().build();
        client = GoogleSignIn.getClient(this, gso);
        Configurations.client = client;
        reference = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    protected void onStart() {
        super.onStart();
        openProgress = new OpenProgress(this).showProgress();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Functions.checkIfNodeExists(reference.child(REFERENCE_PAIR), Functions.removeAllSign(currentUser.getEmail()), b -> reference.child(REFERENCE_USERS).child(b).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Configurations.profileModel = snapshot.getValue(ProfileModel.class);
                    FirebaseMessaging.getInstance().getToken().addOnSuccessListener(s -> {
                        Configurations.TOKEN = s;
                        profileModel.setId(s);
                        FirebaseDatabase.getInstance().getReference().child(REFERENCE_USERS).child(profileModel.getUniqueID()).setValue(profileModel);
                    });
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    openProgress.dismissProgress();
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                    openProgress.dismissProgress();
                }
            }));
        } else {
            openProgress.dismissProgress();
        }
    }


    private void initObserver() {
        model.performRegistration.observe(this, e -> {
            if (e.equals(ActivityMainViewModel.REGISTRATION)) {
                performGoogleLogin();
            }
        });
    }

    void performGoogleLogin() {
        Intent signInIntent = client.getSignInIntent();
        someActivityResultLauncher.launch(signInIntent);
    }

    void afterRegister() {
        profileModel.setId(Configurations.TOKEN);
        reference.child(REFERENCE_USERS).child(profileModel.getUniqueID()).setValue(profileModel);
        startActivity(new Intent(MainActivity.this, HomeActivity.class));
        finish();
    }

//    AlertDialog dialog;

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        firebaseAuthWithGoogle(task.getResult(ApiException.class));
                    } catch (ApiException e) {
                        Log.e("GoogleSignIn", "Google sign in failed", e);
                    }
                }
            });

    private void firebaseAuthWithGoogle(GoogleSignInAccount acc) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            Functions.checkIfNodeExists(reference, Functions.removeAllSign(currentUser.getEmail()), b -> {
                                if (TextUtils.isEmpty(b)) {
                                    ProfileModel profileModel = new ProfileModel();
                                    profileModel.setEmail(currentUser.getEmail());
                                    profileModel.setName(currentUser.getDisplayName());
                                    profileModel.setProfile(currentUser.getPhotoUrl() == null ? "" : currentUser.getPhotoUrl().toString());
                                    profileModel.setId(Configurations.TOKEN);
                                    profileModel.setWins("0");
                                    profileModel.setLoses("0");
                                    profileModel.setUniqueID(Functions.getRandomId());
                                    Configurations.profileModel = profileModel;
                                    reference.child(REFERENCE_USERS).child(profileModel.getUniqueID()).setValue(profileModel);
                                    reference.child(REFERENCE_PAIR).child(Functions.removeAllSign(profileModel.getEmail())).setValue(profileModel.getUniqueID());
                                    afterRegister();
                                } else {
                                    reference.child(REFERENCE_USERS).child(b).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Configurations.profileModel = snapshot.getValue(ProfileModel.class);
                                            afterRegister();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(MainActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });

                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}