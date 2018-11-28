package com.swdave.firestoretest;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";

    private EditText editTextTitle;
    private EditText editTextDescription;
    private TextView textViewData;
    private EditText editTextPriority;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Notebook");
    private DocumentReference noteRef = db.document("Notebook/My First Note");
    private ListenerRegistration noteLisener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        textViewData = findViewById(R.id.text_view_data);
        editTextPriority = findViewById(R.id.edit_text_priority);

    }

    @Override
    protected void onStart() {
        super.onStart();
        notebookRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                String data = "";

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Note note = documentSnapshot.toObject(Note.class);
                    note.setDocumentId(documentSnapshot.getId());


                    String documentId = note.getDocumentId();
                    String title = note.getTitle();
                    String description = note.getDescription();
                    int priority = note.getPriority();

                    data += "ID: " + documentId +
                            "\nTitle: " + title +
                            "\nDescription: " + description +
                            "\nPriority: " + priority +
                            "\n\n";
                }
                textViewData.setText(data);
            }
        });
        // below is code for testing only
//        noteRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
//                if (e != null) {
//                    Toast.makeText(MainActivity.this, "Error While Loading", Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, e.toString());
//                    return;
//                }
//                if (documentSnapshot.exists()) {
//                    Note note = documentSnapshot.toObject(Note.class);
//
//                    String title = note.getTitle();
//                    String description = note.getDescription();
//                    textViewData.setText("Title: " + title + "\n" + "Description: " + description);
//                } else {
//                    textViewData.setText("");
//                }
//            }
//        });
    }

    public void addNote(View view) {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();

        if(editTextPriority.length() == 0){
            editTextPriority.setText("0");
        }

        int priority = Integer.parseInt(editTextPriority.getText().toString());


        Note note = new Note(title, description, priority);

        notebookRef.add(note); // can add onSuccess and onFailure listeners.
    }

    public void loadNotes(View view) {
        notebookRef
                .whereEqualTo("priority", 2)  // Adds a filter
                .orderBy("priority", Query.Direction.DESCENDING)
                .limit(3)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String data = "";

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Note note = documentSnapshot.toObject(Note.class);
                            note.setDocumentId(documentSnapshot.getId());

                            String documentId = note.getDocumentId();
                            String title = note.getTitle();
                            String description = note.getDescription();
                            int priority = note.getPriority();

                            data += "ID: " + documentId +
                                    "\nTitle: " + title +
                                    "\nDescription: " + description +
                                    "\nPriority: " + priority +
                                    "\n\n";

                        }

                        textViewData.setText(data);
                    }
                });
    }

//    public void saveNote(View view) {
//        String title = editTextTitle.getText().toString();
//        String description = editTextDescription.getText().toString();
//
//        Note note = new Note(title, description);
//
//        noteRef.set(note)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Toast.makeText(MainActivity.this, "Note Saved", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
//                        Log.d(TAG, e.toString());
//                    }
//                });
//    }
//
//    public void updateDescription(View view) {
//        String description = editTextDescription.getText().toString();
//
//        /**  Below creates new document if it does not exist **/
//
//        //Map<String, Object> note = new HashMap<>();
//        //note.put(KEY_DESCRIPTION, description);
//        //noteRef.set(note, SetOptions.merge());
//
//        // Below only updates - if no document exists wont create.
//        noteRef.update(KEY_DESCRIPTION, description);
//    }
//
//    public void deleteDescription(View view) {
//
//        //Map<String, Object> note = new HashMap<>();
//        //note.put(KEY_DESCRIPTION,FieldValue.delete());
//        //noteRef.update(note);
//
//        noteRef.update(KEY_DESCRIPTION, FieldValue.delete());
//    }
//
//    public void deleteNote(View view) {
//
//        noteRef.delete();
//    }
//
//    public void loadNote(View view) {
//        noteRef.get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        if (documentSnapshot.exists()) {
//                            Note note = documentSnapshot.toObject(Note.class);
//
//                            String title = note.getTitle();
//                            String description = note.getDescription();
//                            textViewData.setText("Title: " + title + "\n" + "Description: " + description);
//                        } else {
//                            Toast.makeText(MainActivity.this, "Document Does not exist", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
//                        Log.d(TAG, e.toString());
//                    }
//                });
//    }
}
