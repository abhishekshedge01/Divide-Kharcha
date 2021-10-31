package com.abhishek.dividekharcha;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static android.content.ContentValues.TAG;

public class Display extends AppCompatActivity
{
    FirebaseAuth firebaseAuth;
    Button Done,sendmale;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    RecyclerView recyclerView;
    DocumentReference reference;
    FirestoreRecyclerAdapter<finalmodel, viewholder> noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        Done=findViewById(R.id.done);
        sendmale=findViewById(R.id.sendMail);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        Intent data=getIntent();
        int no1=data.getIntExtra("totalx",0);
        int no2=data.getIntExtra("count",0);
        double no3=no1/no2;
        String str=Double.toString(no3);

        String currid=firebaseUser.getUid();
        reference=firebaseFirestore.collection("notes").document(currid);



        sendmale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{ "asdf@gmail.com","jfjbdn@gmail.com", "fejjkfg@gmail.com","hfghnfj@gmail.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, "You owe me Rs. "+no3);
                email.putExtra(Intent.EXTRA_TEXT,"We were a group of "+ no2+" and the total expenditure was "+no1);
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
            }
        });


        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void aVoid) {
                       finishAffinity();
                   }
               });

            }
        });


        Query query = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("names").orderBy("name", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<finalmodel> allusers = new FirestoreRecyclerOptions.Builder<finalmodel>().setQuery(query, finalmodel.class).build();
        noteAdapter = new FirestoreRecyclerAdapter<finalmodel, viewholder>(allusers) {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull viewholder holder, int position, @NonNull finalmodel finalmodel) {

                int i=0;
                int randomColor = getRandomColor();
                holder.mlayout.setBackgroundColor(holder.itemView.getResources().getColor(randomColor, null));
                holder.givenName.setText(finalmodel.getName());
                holder.givenMoney.setText("Rs. "+no3);

            }

            @NonNull
            @Override
            public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.maillayout, parent, false);
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
        private TextView givenMoney;
        private  TextView givenButton;
        LinearLayout mlayout;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            givenName = itemView.findViewById(R.id.mailname);
//            givenButton=itemView.findViewById(R.id.sendmailbtn);
            givenMoney=itemView.findViewById(R.id.mailamount);
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
                startActivity(new Intent(getApplicationContext(), SignIn.class));
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