package com.abhishek.dividekharcha;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Friends extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FloatingActionButton floatingActionButton;
    FirebaseFirestore firebaseFirestore;
    RecyclerView recyclerView;
    TextView textView;
    Button divideit;
    private int count=0;
    FirestoreRecyclerAdapter<friendmodel, viewholder> noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        divideit=findViewById(R.id.divide);
        floatingActionButton=findViewById(R.id.flt);
        Intent data=getIntent();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateFriends.class);
                startActivity(intent);
                count=0;
            }
        });

        divideit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Display.class);
                intent.putExtra("totalx",data.getIntExtra("totalx",0));
                intent.putExtra("count",count);
                startActivity(intent);
                count=0;
            }
        });


        Query query = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("names").orderBy("name", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<friendmodel> allusers = new FirestoreRecyclerOptions.Builder<friendmodel>().setQuery(query, friendmodel.class).build();
        noteAdapter = new FirestoreRecyclerAdapter<friendmodel, viewholder>(allusers) {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull viewholder holder, int position, @NonNull friendmodel friendmodel) {
                int randomColor = getRandomColor();
                holder.mlayout.setBackgroundColor(holder.itemView.getResources().getColor(randomColor, null));
                holder.givenName.setText(friendmodel.getName());
                holder.givenMail.setText(friendmodel.getEmail());
                ImageView popmenubtn=holder.itemView.findViewById(R.id.popbtn);
                ++count;
                divideit.setText("Divide among "+count);

                String docId=noteAdapter.getSnapshots().getSnapshot(position).getId();

                popmenubtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        PopupMenu popupMenu=new PopupMenu(v.getContext(),v);
                        popupMenu.setGravity(Gravity.END);


                        popupMenu.getMenu().add("delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                DocumentReference documentreference=firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("names").document(docId);
                                --count;
                                divideit.setText("Divide among "+count);

                                documentreference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Friends.this, "Friend Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(v.getContext(),"Record is not Deleted",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return  false;
                            }
                        });
                        popupMenu.show();
                    }
                });

            }

            @NonNull
            @Override
            public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.namelist, parent, false);
                return new viewholder(view);
            }
        };

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(noteAdapter);

    }

    private class viewholder extends RecyclerView.ViewHolder {
        private TextView givenName;
        private TextView givenMail;
        LinearLayout mlayout;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            givenName = itemView.findViewById(R.id.tvtitle);
            givenMail = itemView.findViewById(R.id.tvcontent);
            mlayout = itemView.findViewById(R.id.mnote);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menubar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(Friends.this, SignIn.class));
                return true;

            case R.id.exit:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (noteAdapter != null) {
            noteAdapter.stopListening();
        }
    }

    private int getRandomColor()
    {
        List<Integer> color=new ArrayList<>();
//        color.add(R.color.color1);
//        color.add(R.color.color2);
//        color.add(R.color.color3);
//        color.add(R.color.color4);
//        color.add(R.color.color5);
//        color.add(R.color.color6);
//        color.add(R.color.color7);
//        color.add(R.color.color8);
//        color.add(R.color.color9);
//        color.add(R.color.color10);


        color.add(R.color.color1);
        color.add(R.color.color2);
        color.add(R.color.color3);
        color.add(R.color.color4);
        color.add(R.color.color5);


        Random random=new Random();
        int number=random.nextInt(color.size());
        return color.get(number);

    }

}