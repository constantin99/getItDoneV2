package com.example.getitdonev2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import Models.UsersInfo;


public class ProfileFragment extends Fragment {
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private FirebaseDatabase  firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private ImageView profilePictureImageView;
    private TextView usernameProfileTextView, nameProfileTextView, surnameProfileTextView, descriptionProfileTextView, email, userStatus;
    private Button logoutButton;
    private View view1, view2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view1 = inflater.inflate(R.layout.fragment_profile, container, false);
        view2 = inflater.inflate(R.layout.fragment_profile2, container, false);


        //get Firebase instance
        auth = FirebaseAuth.getInstance();
       //databaseReference = FirebaseDatabase.getInstance().getReference(auth.getUid());
        if(auth.getCurrentUser() != null) {


            //get Instance of database Firebase
            databaseReference = FirebaseDatabase.getInstance().getReference();
            DatabaseReference member = databaseReference.child(auth.getUid());
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()){
                        Intent intent = new Intent(getActivity().getApplicationContext(), EditProfile.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(),"Exista user", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            member.addListenerForSingleValueEvent(valueEventListener);

            databaseReference = FirebaseDatabase.getInstance().getReference();
            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseStorage = FirebaseStorage.getInstance();


            DatabaseReference databaseReference = firebaseDatabase.getReference(auth.getUid());
            StorageReference storageReference = firebaseStorage.getReference();

            //set values to variables
            profilePictureImageView = view2.findViewById(R.id.profile_pic_profile2);
            usernameProfileTextView = view2.findViewById(R.id.userNameProfileTextView);
            nameProfileTextView = view2.findViewById(R.id.nameProfileTextView);
            surnameProfileTextView = view2.findViewById(R.id.surNameProfileTextView);
            descriptionProfileTextView = view2.findViewById(R.id.descriptionProfileTextView);
            email = view2.findViewById(R.id.emailProfileTextView);
            logoutButton = view2.findViewById(R.id.logoutProfileButton);
            userStatus = view2.findViewById(R.id.statusProfileTextView);

            //set up the profile image

            storageReference.child(auth.getUid()).child("Images").child("Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).fit().centerInside().into(profilePictureImageView);
                }
            });

            //setting text variables

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UsersInfo userInfo = dataSnapshot.getValue(UsersInfo.class);

                    //avoid producing NullPointerException
                    if(userInfo.getUsername() != null) {
                        usernameProfileTextView.setText(userInfo.getUsername());
                    }
                    nameProfileTextView.setText(userInfo.getName());
                    surnameProfileTextView.setText(userInfo.getSurname());
                    descriptionProfileTextView.setText(userInfo.getDescription());
                    email.setText(auth.getCurrentUser().getEmail());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(), "The retrieving data failed with error:" + databaseError.getCode(), Toast.LENGTH_SHORT).show();

                }
            });

            setUpUserStatus(userStatus);

            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    auth.signOut();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(getActivity(), "You have been logged out", Toast.LENGTH_SHORT).show();
                }
            });
            return view2;
        } else {
            return view1;
        }
    }

    private void setUpUserStatus(final TextView textView){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        String email = auth.getCurrentUser().getEmail();
        final CollectionReference documentReference = firebaseFirestore.collection("Statistics").document(email).collection("Stats");
         final Long[] done = new Long[1];
         done[0] = (long) 0;
         final Long[] notDone = new Long[1];
         notDone[0] = (long) 0;

        documentReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Long stats;
                //inner class
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        done[0] = done[0] + documentSnapshot.getLong("done");
                        notDone[0] = notDone[0] + documentSnapshot.getLong("notDone");

                        stats = done[0] - notDone[0];

                        if(stats < 0){
                            textView.setText("Critic");
                        } else if(stats <= 30){
                            textView.setText("Beginner");
                        } else if(stats <= 120){
                            textView.setText("Intermidiate");
                        } else if(stats > 120){
                            textView.setText("Advanced");
                        }
                    }
                }
            }
        });



    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final NavController navController = Navigation.findNavController(view);
        Button editProfileButton = view2.findViewById(R.id.editProfileButton);
        Button loginProfileButton = view1.findViewById(R.id.loginProfileButton);
        FloatingActionButton floatingActionButton = view1.findViewById(R.id.floatingTimer);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_profileFragment_to_editProfile);

            }
        });

        loginProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_profileFragment_to_loginActivity);

            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_profileFragment_to_pomodoroTimer);
            }
        });
    }
}
