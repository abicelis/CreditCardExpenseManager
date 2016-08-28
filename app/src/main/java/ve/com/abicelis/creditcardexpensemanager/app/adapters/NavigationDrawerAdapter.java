package ve.com.abicelis.creditcardexpensemanager.app.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.holders.NavigationDrawerViewHolder;
import ve.com.abicelis.creditcardexpensemanager.model.NavigationDrawerItem;


/**
 * Created by Alex on 5/8/2016.
 */
public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerViewHolder> {

    private List<NavigationDrawerItem> mDataList = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;

    public NavigationDrawerAdapter(Context context, List<NavigationDrawerItem> data) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.mDataList = data;
    }

    @Override
    public NavigationDrawerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_nav_drawer, parent, false);
        NavigationDrawerViewHolder holder = new NavigationDrawerViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(NavigationDrawerViewHolder holder, int position) {
        NavigationDrawerItem current = mDataList.get(position);

        holder.setData(context, current, position);
        holder.setListeners();
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }
}
