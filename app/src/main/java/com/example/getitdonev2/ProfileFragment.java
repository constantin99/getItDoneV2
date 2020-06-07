package com.example.getitdonev2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    private TextView usernameProfileTextView, nameProfileTextView, surnameProfileTextView, descriptionProfileTextView, email;
    private Button logoutButton;
    private View view1, view2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_profile, container, false);
        //set values for the two views of profile
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


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final NavController navController = Navigation.findNavController(view);
        Button editProfileButton = view2.findViewById(R.id.editProfileButton);
        Button loginProfileButton = view1.findViewById(R.id.loginProfileButton);
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
    }
}
