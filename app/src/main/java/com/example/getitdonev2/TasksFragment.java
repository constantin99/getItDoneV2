package com.example.getitdonev2;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Methods.Methods;
import Models.DoneModel;
import Models.TasksModel;

public class TasksFragment extends Fragment {
    private View view1, view2;
    private FirebaseFirestore firestoreDB;
    private CollectionReference tasksRef;
    private TaskAdapter adapter;
    private Methods methods = new Methods();
    private Spinner spinnerDates;
    private String userEmail, string;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButtonTimer, floatingActionButtonGraph;
    private int checkingStart;
    private LottieAnimationView lottieAnimationView;
    private ProgressBar progressBar;





    public TasksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view2 = inflater.inflate(R.layout.fragment_profile, container, false);
        view1 = inflater.inflate(R.layout.fragment_tasks, container, false);

        lottieAnimationView = view1.findViewById(R.id.animationView);

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            firestoreDB = FirebaseFirestore.getInstance();
            spinnerDates = view1.findViewById(R.id.spinnerDates);
            userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            String currentDate = methods.getCurrentDate();
            floatingActionButtonTimer = view1.findViewById(R.id.floatingTimer);
            floatingActionButtonGraph = view1.findViewById(R.id.floatingGraph);
            progressBar = view1.findViewById(R.id.progressBar);

            firestoreDB.collection("Tasks").document(userEmail).collection("Tasks").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<String> listOfDates = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            listOfDates.add(documentSnapshot.getId());
                            ArrayAdapter<String> adapterString = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_spinner_dropdown_item, listOfDates);
                            spinnerDates.setAdapter(adapterString);
                            string = spinnerDates.getSelectedItem().toString();
                            // Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();

//                        Toast.makeText(getActivity(), documentSnapshot.getId(), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            floatingActionButtonTimer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), PomodoroTimer.class));

                }
            });

            floatingActionButtonGraph.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), StatisticsActivity.class));
                }
            });


            return view1;
        } else {
            return view2;
        }


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    if(FirebaseAuth.getInstance().getCurrentUser() != null) {
        spinnerDates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                string = spinnerDates.getSelectedItem().toString();
                //recyclerView.setAdapter(adapter);

                setUpRecyclerView(userEmail, string);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

        //navigation
        final NavController navController = Navigation.findNavController(view);

        Button loginProfileButton = view2.findViewById(R.id.loginProfileButton);
        FloatingActionButton floatingActionButton = view2.findViewById(R.id.floatingTimer);
        loginProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_tasksFragment_to_loginActivity);

            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_tasksFragment_to_pomodoroTimer);
            }
        });

    }

    public void setUpRecyclerView(String userEmail, final String selectedDate){
            lottieAnimationView.setAnimation("startupApp.json");
            lottieAnimationView.playAnimation();
            progressBar.setVisibility(View.VISIBLE);
            tasksRef = firestoreDB.collection("Tasks").document(userEmail).collection("Tasks").document(selectedDate).collection(selectedDate);

            // Query query = tasksRef.orderBy("numberOfPomodoros", Query.Direction.DESCENDING);
            Query query = tasksRef;


            FirestoreRecyclerOptions<TasksModel> options = new FirestoreRecyclerOptions.Builder<TasksModel>().setQuery(query, TasksModel.class).build();
            adapter = new TaskAdapter(options);
            adapter.startListening();
            checkingStart = 1;
            recyclerView = view1.findViewById(R.id.tasksRecyclerView);

            recyclerView.setItemViewCacheSize(0);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);

            lottieAnimationView.cancelAnimation();
            progressBar.setVisibility(View.INVISIBLE);
        //swipe to delete
            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.deleteItem(viewHolder.getAdapterPosition());
                }
            }).attachToRecyclerView(recyclerView);

         adapter.setOnItemClickListener(new TaskAdapter.OnItemClickListener() {
             @Override
             public void onDoneCLick(int position) {
                String  id = adapter.getTitle(position);

                   tasksRef.document(id).update("done", 1);
                   updateDoneStats(selectedDate);
             }

             @Override
             public void onNotDoneClick(int position) {
                 String id = adapter.getTitle(position);

                 tasksRef.document(id).update("done", 0);
                 updateNotDoneStats(selectedDate);
             }
         });



    }

    public void updateDoneStats(String selectedDate){
       final DocumentReference doneReference = firestoreDB.collection("Statistics").document(userEmail).collection("Stats").document(selectedDate);

        doneReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                DoneModel doneModel = documentSnapshot.toObject(DoneModel.class);
                doneReference.update("done", doneModel.getDone() + 1);
            }

        });
    }

    public void updateNotDoneStats(String selectedDate){
        final DocumentReference doneReference = firestoreDB.collection("Statistics").document(userEmail).collection("Stats").document(selectedDate);

        doneReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                DoneModel doneModel = documentSnapshot.toObject(DoneModel.class);
                doneReference.update("notDone", doneModel.getNotDone() + 1);
            }

        });    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        adapter.startListening();
//    }
//
    @Override
    public void onStop() {
        super.onStop();
        if(checkingStart == 1) {
            adapter.stopListening();
        }
    }


}
