package com.abhishek.dividekharcha;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fltact;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    private int totalx=0;
    Button splitit;
    RecyclerView recyclerView;
    FirestoreRecyclerAdapter<model, viewholder> noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fltact = findViewById(R.id.flt);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        splitit=findViewById(R.id.split);


        splitit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Friends.class);
                intent.putExtra("totalx",totalx);
                startActivity(intent);
                totalx=0;
            }
        });

        fltact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateNotes.class);
                startActivity(intent);
                totalx=0;
            }
        });

        Query query = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").orderBy("title", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<model> allusers = new FirestoreRecyclerOptions.Builder<model>().setQuery(query, model.class).build();
        noteAdapter = new FirestoreRecyclerAdapter<model, viewholder>(allusers) {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull viewholder holder, int position, @NonNull model model) {

                int randomColor = getRandomColor();
                ImageView popmenubtn=holder.itemView.findViewById(R.id.popbtn);
                holder.mlayout.setBackgroundColor(holder.itemView.getResources().getColor(randomColor, null));
                holder.givenTitle.setText(model.getTitle());
                holder.givenContent.setText("Rs. "+model.getContent());
                holder.givenPhone.setText("Desc. "+model.getPhone());
                int each=Integer.parseInt(model.getContent());
                totalx+=each;
                splitit.setText("Split: Rs "+ totalx);
                Toast.makeText(MainActivity.this, "Total Expenditure has become: "+totalx, Toast.LENGTH_LONG).show();
                String docId=noteAdapter.getSnapshots().getSnapshot(position).getId();

                popmenubtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        PopupMenu popupMenu=new PopupMenu(v.getContext(),v);
                        popupMenu.setGravity(Gravity.END);


                        popupMenu.getMenu().add("delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                DocumentReference documentreference=firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(docId);
                                int each=Integer.parseInt(model.getContent());
                                totalx-=each;
                                Toast.makeText(getApplicationContext(),"Total Expenditure has reduced to: "+totalx,Toast.LENGTH_LONG).show();
                                splitit.setText("Split :"+ totalx);

                                documentreference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        int x=0;
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
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.noteslayout, parent, false);
                return new viewholder(view);
            }
        };

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(noteAdapter);

    }

    private class viewholder extends RecyclerView.ViewHolder {
        private TextView givenTitle;
        private TextView givenContent;
        private  TextView givenPhone;
        LinearLayout mlayout;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            givenTitle = itemView.findViewById(R.id.tvtitle);
            givenContent = itemView.findViewById(R.id.tvcontent);
            givenPhone=itemView.findViewById(R.id.tvphone);
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
                startActivity(new Intent(MainActivity.this, SignIn.class));
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