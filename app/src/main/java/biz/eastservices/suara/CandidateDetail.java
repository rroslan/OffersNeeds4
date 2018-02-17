package biz.eastservices.suara;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;

import biz.eastservices.suara.Common.Common;
import biz.eastservices.suara.Model.Candidate;
import biz.eastservices.suara.Model.Rating;
import de.hdodenhof.circleimageview.CircleImageView;

public class CandidateDetail extends AppCompatActivity implements RatingDialogListener {

    Button btnWhatsApp,btnWaze,btnRating;
    RatingBar ratingBar;
    TextView txt_name,txt_description;
    CircleImageView circleImageView;

    String uri ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_detail);


        ratingBar = (RatingBar)findViewById(R.id.ratingBar);

        circleImageView = (CircleImageView)findViewById(R.id.profile_image);

        txt_description = (TextView)findViewById(R.id.txt_description);
        txt_name = (TextView)findViewById(R.id.txt_name);

        btnRating = (Button)findViewById(R.id.btn_rating);
        btnWaze = (Button)findViewById(R.id.btn_waze);
        btnWhatsApp = (Button)findViewById(R.id.btn_whats_app);

        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });

        loadDetail(Common.selected_uid_people);

        btnWaze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
            }
        });

        btnWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hello ! I need your help");
                sendIntent.setType("text/plain");
                sendIntent.setPackage("com.whatsapp");
                startActivity(sendIntent);
            }
        });
    }

    private void loadDetail(String uid) {
        FirebaseDatabase.getInstance()
                .getReference(Common.USER_TABLE_CANDIDATE)
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Candidate candidate = dataSnapshot.getValue(Candidate.class);

                        Picasso.with(getBaseContext())
                                .load(candidate.getProfileImage())
                                .error(R.drawable.ic_terrain_black_24dp)
                                .placeholder(R.drawable.ic_terrain_black_24dp)
                                .into(circleImageView);

                        txt_description.setText(candidate.getDescription());
                        txt_name.setText(candidate.getName());

                        uri = "waze://?ll="+candidate.getLat()+", "+candidate.getLng()+"&navigate=yes";
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        getRatingOfPeople(uid);
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad","Not Good","Quite Ok","Very Good","Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this person")
                .setDescription("Please select some stars and give your feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please write your comment here...")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(CandidateDetail.this)
                .show();
    }

    @Override
    public void onPositiveButtonClicked(int i, String s) {
        final Rating rating = new Rating(Float.valueOf(String.valueOf(i)),
                s);
        FirebaseDatabase.getInstance()
                .getReference(Common.USER_RATING)
                .child(Common.selected_uid_people)
                .child(FirebaseAuth.getInstance().getUid())
                .setValue(rating)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(CandidateDetail.this, "Rating Succeed", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CandidateDetail.this, "Rating Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }


    private void getRatingOfPeople(String uid) {

        FirebaseDatabase
                .getInstance()
                .getReference(Common.USER_RATING)
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    int count=0,sum=0;
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                        {
                            Rating item = postSnapshot.getValue(Rating.class);
                            sum+=item.getRatingValue();
                            count++;
                        }
                        if(count != 0)
                        {
                            float average = sum/count;
                            ratingBar.setRating(average);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



    }
}
