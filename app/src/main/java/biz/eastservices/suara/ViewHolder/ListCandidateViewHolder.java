package biz.eastservices.suara.ViewHolder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import biz.eastservices.suara.Interface.ItemClickListener;
import biz.eastservices.suara.R;

/**
 * Created by reale on 2/9/2018.
 */

public class ListCandidateViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txt_name,txt_description;

    private CardView cardView;


    public ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ListCandidateViewHolder(View itemView) {
        super(itemView);
        txt_name = (TextView)itemView.findViewById(R.id.txt_name);
        txt_description = (TextView)itemView.findViewById(R.id.txt_description);
        cardView = (CardView)itemView.findViewById(R.id.card_view);


        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition());
    }


    public void hideLayout()
    {
       cardView.setVisibility(View.GONE);
    }

}
