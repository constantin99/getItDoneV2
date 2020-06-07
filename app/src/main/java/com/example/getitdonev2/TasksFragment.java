package com.example.getitdonev2;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import Methods.Methods;
import Models.TasksModel;

public class TasksFragment extends Fragment {
    private View view1;
    private FirebaseFirestore firestoreDB;
    private CollectionReference tasksRef;
    private TaskAdapter adapter;
    private Methods methods = new Methods();
    private Spinner spinnerDates;
    private String userEmail, string;
    private RecyclerView recyclerView;




    public TasksFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view1 = inflater.inflate(R.layout.fragment_tasks, container, false);
        firestoreDB = FirebaseFirestore.getInstance();
        spinnerDates = view1.findViewById(R.id.spinnerDates);
         userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
         String currentDate = methods.getCurrentDate();
        //Toast.makeText(getActivity(), currentDate, Toast.LENGTH_SHORT).show();

        firestoreDB.collection("Tasks").document(userEmail).collection("Tasks").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<String> listOfDates = new ArrayList<>();
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
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




        //setUpRecyclerView(userEmail, currentDate);



        // Inflate the layout for this fragment
        //startActivity(new Intent(getActivity(), TasksActivity.class));

        return view1;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        spinnerDates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                string = spinnerDates.getSelectedItem().toString();
                //recyclerView.setAdapter(adapter);

                String currentDate = methods.getCurrentDate();
//                if(currentDate == currentDate){
//                    Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
//                }
//                Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
               setUpRecyclerView(userEmail, string);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void setUpRecyclerView(String userEmail, String selectedDate){


            tasksRef = firestoreDB.collection("Tasks").document(userEmail).collection("Tasks").document(selectedDate).collection(selectedDate);

            // Query query = tasksRef.orderBy("numberOfPomodoros", Query.Direction.DESCENDING);
            Query query = tasksRef;


            FirestoreRecyclerOptions<TasksModel> options = new FirestoreRecyclerOptions.Builder<TasksModel>().setQuery(query, TasksModel.class).build();
            adapter = new TaskAdapter(options);
            adapter.startListening();
            recyclerView = view1.findViewById(R.id.tasksRecyclerView);

            recyclerView.setItemViewCacheSize(0);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);

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

    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        adapter.startListening();
//    }
//
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


}
