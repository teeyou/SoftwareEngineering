package bokhakwang.softwareengineering.firestore;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FireStorage {
    private static FireStorage instance;
    private final String TAG = "FireStorage";

    private FirebaseStorage storage;

    private FireStorage() {
        this.storage = FirebaseStorage.getInstance();
    }

    public static FireStorage getInstance() {
        if (instance == null) {
            instance = new FireStorage();
        }
        return instance;
    }

    public void uploadImages(List<Uri> fileUris, FirebaseListener<List<String>> urlCallback) {
        List<String> httpUrls = new ArrayList<>();

        OnCompleteListener<Uri> completeListener = task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Uri downloadUri = task.getResult();

                Log.d(TAG, "onComplete:" + downloadUri);
                httpUrls.add(downloadUri.toString());
            } else {
                Log.d(TAG, "err:");
                urlCallback.onComplete(null);
            }

            if (httpUrls.size() == fileUris.size()) {
                urlCallback.onComplete(httpUrls);
            }
        };

        for (Uri uri : fileUris) {
            uploadImage(uri, completeListener);
        }
    }

    private void uploadImage(Uri fileUri, OnCompleteListener<Uri> completeCallback) {
        UUID uuid = UUID.randomUUID();
        final StorageReference ref = storage.getReference().child("images/" + uuid.toString());
        UploadTask uploadTask = ref.putFile(fileUri);

        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }

            // Continue with the task to get the download URL
            return ref.getDownloadUrl();
        }).addOnCompleteListener(completeCallback);
    }

    public void deleteImage(String url) {
        FireStorage.getInstance().storage.getReferenceFromUrl(url).delete()
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG,"Deleted!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error deleting a Image");
            }
        });
    }
}
