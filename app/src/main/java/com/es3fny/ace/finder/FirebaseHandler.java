package com.es3fny.ace.finder;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Random;

public class FirebaseHandler {

    FirebaseStorage firebaseStorage;
    private StorageReference mstorageRef ;

    interface FirebaseCallback{
        void get_download_link(String link);
    }

    public FirebaseHandler()
    {
        firebaseStorage = FirebaseStorage.getInstance();
        mstorageRef = firebaseStorage.getReference();
    }
    public void UploadRecord(final Uri file, final FirebaseCallback firebaseCallback){
        String filename = "";
        Random ran = new Random();
        for(int i = 0;i <20;i++)
        {
            filename += (char)(97+(ran.nextInt(26)));
        }
        final StorageReference mountainsRef = mstorageRef.child(filename + ".3gp");
        mountainsRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mountainsRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        firebaseCallback.get_download_link(uri.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        firebaseCallback.get_download_link(null);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                firebaseCallback.get_download_link(null);
            }
        });
    }
}
