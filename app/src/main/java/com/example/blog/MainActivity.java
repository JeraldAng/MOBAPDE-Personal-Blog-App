package com.example.blog;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String POST_TITLE = "POST_TITLE";
    public static final String POST_ID = "POST_ID";

    EditText txtPostTitle, txtPostDescription, txtCategory;
    Button btnAddPost, btnSort;
    Spinner spinnerViewBy;
    ListView listViewPosts;

    List<Post> posts;
    ArrayList<String> arrCategories = new ArrayList<>();
    PostList postAdapter;
    ///// FIREBASE DATABASE INSTANCE ////
    DatabaseReference databasePosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Getting "posts". If it does not exist, creates it//
        databasePosts = FirebaseDatabase.getInstance().getReference("posts");

        posts = new ArrayList<>();

        txtPostTitle = findViewById(R.id.txtPostTitle);
        txtPostDescription = findViewById(R.id.txtPostDescription);
        btnAddPost = findViewById(R.id.btnAddPost);
        btnSort = findViewById(R.id.btnSort);
        txtCategory = findViewById(R.id.txtCategory);
        spinnerViewBy = findViewById(R.id.spinnerViewBy);
        listViewPosts = findViewById(R.id.listViewPosts);

        btnAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPost();
            }
        });

        /*listViewPosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //getting the selected artist
                Post post = posts.get(i);

                Intent intent = new Intent(getApplicationContext(), ArtistActivity.class);
                intent.putExtra(POST_ID, post.getPostId());
                intent.putExtra(POST_TITLE, post.getTitle());
                startActivity(intent);
            }
        });*/

        listViewPosts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Post post = posts.get(i);
                showDeleteDialog(post.getPostId(), post.getTitle());
                return true;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //attaching value event listener to our DB
        databasePosts.addValueEventListener(new ValueEventListener() {
            // Database entries are all DataSnapshots
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                posts.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting post
                    Post post = postSnapshot.getValue(Post.class);
                    //adding post to the list
                    posts.add(0, post);
                }

                //creating adapter
                postAdapter = new PostList(MainActivity.this, posts);
                //attaching adapter to the listview
                listViewPosts.setAdapter(postAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    /*
     * This method is for saving a new post to the
     * Firebase Realtime Database
     * */
    private void addPost() {
        //getting the values to save
        String title = txtPostTitle.getText().toString().trim();
        String description = txtPostDescription.getText().toString().trim();
        String categories = txtCategory.getText().toString().trim();

        arrCategories.add(categories);
        String timestamp = getCurrentTimeStamp();

       /* SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp timestamp = Timestamp.valueOf(dateFormat.format(new Date()));*/

        //checking if the value is provided
        if (!TextUtils.isEmpty(title)) {

            //getting a unique id using push().getKey() method
            //it will create a unique id & use it as the Primary Key for our Post
            String id = databasePosts.push().getKey();

            //creating a Post Object
            Post post = new Post(id, title, description, timestamp, categories);

            //Saving the Post
            databasePosts.child(id).setValue(post);

            //setting edittext to blank again
            txtPostTitle.setText("");
            txtPostDescription.setText("");
            txtCategory.setText("");

            //displaying a success toast
            Toast.makeText(this, "Post added", Toast.LENGTH_LONG).show();
        } else {
            //if the value is not given displaying a toast
            Toast.makeText(this, "Please provide all details to proceed", Toast.LENGTH_LONG).show();
        }
    }

    public static String getCurrentTimeStamp(){
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date()); // Find today's date

            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }


    public void sort(View V)
    {
        String choice = spinnerViewBy.getSelectedItem().toString();

        switch (choice){
            case "Creation Date":
                sortByCreationDate();
                break;
            case "Category":
                sortByCategory();
                break;
            default:
                break;
        }
    }

    public void sortByCreationDate(){
       Collections.sort(posts, new Comparator<Post>() {
            public int compare(Post first, Post second)  {
                return second.getTimeStamp().compareTo(first.getTimeStamp());
            }
        });

        postAdapter.notifyDataSetChanged();
    }

    public void sortByCategory(){
        ArrayList list = new ArrayList();


        Collections.sort(posts, new Comparator<Post>() {
            public int compare(Post first, Post second)  {
                return first.getCategory().compareTo(second.getCategory());
            }
        });

        postAdapter.notifyDataSetChanged();

    }

    private void showDeleteDialog(final String postId, String postTitle) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.delete_dialog, null);
        dialogBuilder.setView(dialogView);

        final Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);
        final Button btnDeletePost = (Button) dialogView.findViewById(R.id.btnDeletePost);

        dialogBuilder.setTitle(postTitle);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.dismiss();
            }
        });

        btnDeletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deletePost(postId);
                b.dismiss();
            }
        });
    }

    private boolean deletePost(String id) {
        //getting the specified delete reference
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("posts").child(id);

        //removing post
        dR.removeValue();

        Toast.makeText(getApplicationContext(), "Post Deleted", Toast.LENGTH_LONG).show();

        return true;
    }
}
