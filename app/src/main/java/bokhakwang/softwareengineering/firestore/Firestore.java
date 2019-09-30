package bokhakwang.softwareengineering.firestore;

import android.util.Log;

import androidx.annotation.NonNull;

import bokhakwang.softwareengineering.model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;



public class Firestore {
    private static Firestore instance;
    private final String TAG = "Firestore";
    private FirebaseFirestore db;

    private Firestore() {
        this.db = FirebaseFirestore.getInstance();
    }

    public static Firestore getInstance() {
        if (instance == null) {
            instance = new Firestore();
        }
        return instance;
    }

    public void fetchAllPosts(FirebaseListener<List<Post>> callback) {
        List<Post> posts = new ArrayList<>();

        db.collection("posts")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Map<String, Object> data = document.getData();
                            Log.d(TAG, document.getId() + " => " + data);

                            GeoPoint geoPoint = (GeoPoint) data.get("geoPoint");
                            String author = (String) data.get("author");
                            List<String> images = (ArrayList<String>) data.get("images");
                            String time = (String) data.get("time");
                            String district = (String) data.get("district");
                            String detail_location = (String) data.get("detail_location");
                            String contents = (String) data.get("contents");
                            String password = (String) data.get("password");

                            posts.add(new Post(document.getId(), author, geoPoint, images, time, district, detail_location, contents, password));
                        }

                        callback.onComplete(posts);
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        callback.onComplete(null);
                    }
                });
    }

    public void deletePost(Post post, FirebaseListener<Boolean> callback) {
        db.collection("posts").document(post.getId()).delete()
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                //decrementCollectionCount("posts");
                callback.onComplete(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error deleting document", e);
                callback.onComplete(false);
            }
        });
    }

    public void updatePost(Post post, FirebaseListener<Boolean> callback) {
        HashMap<String, Object> updatePost = new HashMap<>();
        updatePost.put("geoPoint", post.getLatLng());
        updatePost.put("author", post.getAuthor());
        updatePost.put("images", post.getImages());
        updatePost.put("time", post.getTime());
        updatePost.put("district", post.getDistrict());
        updatePost.put("detail_location", post.getDetail_location());
        updatePost.put("contents", post.getContents());
        updatePost.put("password", post.getPassword());

        db.collection("posts").document(post.getId()).update(updatePost)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            callback.onComplete(true);
                        } else {
                            callback.onComplete(false);
                        }
                    }
                });
    }

    public void addNewPost(Post post, FirebaseListener<Boolean> callback) {
        Map<String, Object> newPost = new HashMap<>();
        newPost.put("geoPoint", post.getLatLng());
        newPost.put("author", post.getAuthor());
        newPost.put("images", post.getImages());
        newPost.put("time", post.getTime());
        newPost.put("district", post.getDistrict());
        newPost.put("detail_location", post.getDetail_location());
        newPost.put("contents", post.getContents());
        newPost.put("password", post.getPassword());

        // Add a new document with a generated ID
        db.collection("posts")
                .add(newPost)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    //incrementCollectionCount("posts");
                    callback.onComplete(true);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                    callback.onComplete(false);
                });
    }
}
