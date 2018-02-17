package biz.eastservices.suara;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import biz.eastservices.suara.Common.Common;
import biz.eastservices.suara.Model.Candidate;
import de.hdodenhof.circleimageview.CircleImageView;

public class CandidateSettings extends AppCompatActivity {
    CircleImageView circleImageView;
    MaterialEditText txtName, txtDescription, txtPhone, txtWhatsApp, txtWaze;
    RadioButton rdiJobs, rdiHelp, rdiService, rdiTransport;
    Button btnSave, btnViewList;

    private Uri filePath;

    int selectCategory = -1;

    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseDatabase database;
    DatabaseReference candidates;

    Candidate candidate = new Candidate();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_settings);
        //Init FireStorage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Init Firebase Realtime Database
        database = FirebaseDatabase.getInstance();
        candidates = database.getReference(Common.USER_TABLE_CANDIDATE);

        //Init view
        circleImageView = (CircleImageView) findViewById(R.id.profile_image);

        txtName = (MaterialEditText) findViewById(R.id.edt_name);
        txtDescription = (MaterialEditText) findViewById(R.id.edt_description);
        txtPhone = (MaterialEditText) findViewById(R.id.edt_phone);
        txtWhatsApp = (MaterialEditText) findViewById(R.id.edt_whats_app);
        txtWaze = (MaterialEditText) findViewById(R.id.edt_waze);

        rdiHelp = (RadioButton) findViewById(R.id.rdi_help);
        rdiJobs = (RadioButton) findViewById(R.id.rdi_job);
        rdiService = (RadioButton) findViewById(R.id.rdi_services);
        rdiTransport = (RadioButton) findViewById(R.id.rdi_transport);


        btnSave = (Button) findViewById(R.id.btn_save);
        btnViewList = (Button) findViewById(R.id.btn_view_list);


        //Event

        rdiJobs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    selectCategory = 0;
            }
        });
        rdiHelp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    selectCategory = 1;
            }
        });
        rdiService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    selectCategory = 2;
            }
        });
        rdiTransport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    selectCategory = 3;
            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Set select image when click to avatar
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), Common.PICK_IMAGE_REQUEST);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create new object user information
                candidate.setName(txtName.getText().toString());
                candidate.setDescription(txtDescription.getText().toString());
                candidate.setPhone(txtPhone.getText().toString());
                candidate.setCategory(Common.convertTypeToCategory(selectCategory));
                candidate.setWhatsapp(txtWhatsApp.getText().toString());
                candidate.setWaze(txtWaze.getText().toString());


                candidates.child(FirebaseAuth.getInstance().getUid())
                        .setValue(candidate)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(CandidateSettings.this, "Information updated !", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });

            }
        });


        //Load Information
        candidates.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            Candidate candidate = dataSnapshot.getValue(Candidate.class);

                            //Set image
                            Picasso.with(getBaseContext())
                                    .load(candidate.getProfileImage())
                                    .error(R.drawable.ic_terrain_black_24dp)
                                    .placeholder(R.drawable.ic_terrain_black_24dp)
                                    .into(circleImageView);

                            txtName.setText(candidate.getName());
                            txtDescription.setText(candidate.description);
                            txtPhone.setText(candidate.getPhone());
                            txtWaze.setText(candidate.getWaze());
                            txtWhatsApp.setText(candidate.getWhatsapp());

                            if (Common.convertCategoryToType(candidate.getCategory()) == 0)
                                rdiJobs.setChecked(true);
                            else if (Common.convertCategoryToType(candidate.getCategory()) == 1)
                                rdiHelp.setChecked(true);
                            else if (Common.convertCategoryToType(candidate.getCategory()) == 2)
                                rdiService.setChecked(true);
                            else if (Common.convertCategoryToType(candidate.getCategory()) == 3)
                                rdiTransport.setChecked(true);


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    private void uploadImageAndGetUrl() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(CandidateSettings.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference ref = storageReference.child("images/" + imageName);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(CandidateSettings.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    candidate.setProfileImage(uri.toString());


                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(CandidateSettings.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            circleImageView.setImageURI(data.getData());

            uploadImageAndGetUrl();
        }
    }
}