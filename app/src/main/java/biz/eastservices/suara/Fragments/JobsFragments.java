package biz.eastservices.suara.Fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import biz.eastservices.suara.CandidateDetail;
import biz.eastservices.suara.Common.Common;
import biz.eastservices.suara.Interface.ItemClickListener;
import biz.eastservices.suara.Model.Candidate;
import biz.eastservices.suara.R;
import biz.eastservices.suara.ViewHolder.ListCandidateViewHolder;

public class JobsFragments extends Fragment {

    private static JobsFragments INSTANCE=null;

    private static Location mLocation;

    FirebaseDatabase database;
    DatabaseReference candidates;

    FirebaseRecyclerOptions<Candidate> options;
    FirebaseRecyclerAdapter<Candidate,ListCandidateViewHolder> adapter;
    //View
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    public JobsFragments() {
        database = FirebaseDatabase.getInstance();
        candidates = database.getReference(Common.USER_TABLE_CANDIDATE);

        Query query = candidates.orderByChild("category").equalTo("Jobs");
        options = new FirebaseRecyclerOptions.Builder<Candidate>()
                .setQuery(query,Candidate.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Candidate, ListCandidateViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ListCandidateViewHolder holder, int position, @NonNull Candidate model) {




                //Check range
                Location candidateLocation = new Location(LocationManager.NETWORK_PROVIDER);
                candidateLocation.setLatitude(model.getLat());
                candidateLocation.setLongitude(model.getLng());
                double distanceInKm = (mLocation.distanceTo(candidateLocation))/1000;

                if(distanceInKm <= 20) // 20km
                {
                    holder.txt_description.setText(model.getDescription());
                    holder.txt_name.setText(model.getName());

                    holder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position) {
                            Common.selected_uid_people = adapter.getRef(position).getKey();
                            startActivity(new Intent(getActivity(), CandidateDetail.class));

                        }
                    });
                }
                else
                    holder.hideLayout();
            }

            @Override
            public ListCandidateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_item,parent,false);
                return new ListCandidateViewHolder(itemView);
            }
        };
    }



    public static JobsFragments getInstance(Location location)
    {
        if(INSTANCE == null)
            INSTANCE = new JobsFragments();
        mLocation = location;
        //Log.d("MYLO",""+location.getLatitude());
        return INSTANCE;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_jobs, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_jobs);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                loadData();
            }
        });


        return view;
    }

    private void loadData() {
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }
    @Override
    public void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }

    @Override
    public void onStop() {
        if(adapter!=null)
            adapter.stopListening();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter != null)
            adapter.startListening();
    }

}
