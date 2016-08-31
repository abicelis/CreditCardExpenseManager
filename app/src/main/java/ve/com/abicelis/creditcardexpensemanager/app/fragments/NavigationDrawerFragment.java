package ve.com.abicelis.creditcardexpensemanager.app.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.adapters.NavigationDrawerAdapter;
import ve.com.abicelis.creditcardexpensemanager.app.dialogs.SelectCreditCardDialogFragment;
import ve.com.abicelis.creditcardexpensemanager.app.holders.CreditCardViewHolder;
import ve.com.abicelis.creditcardexpensemanager.database.ExpenseManagerDAO;
import ve.com.abicelis.creditcardexpensemanager.model.CreditCard;
import ve.com.abicelis.creditcardexpensemanager.model.NavigationDrawerItem;


/**
 * Created by Alex on 5/8/2016.
 */
public class NavigationDrawerFragment extends Fragment implements View.OnClickListener{

    public static final String ACTIVE_CREDIT_CARD = "active_cc";

    //DATA
    private CreditCard mActiveCreditCard = null;
    private ExpenseManagerDAO mDao;

    //UI
    CreditCardViewHolder holder;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private RelativeLayout mContainer;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container);

        mContainer = (RelativeLayout) view.findViewById(R.id.nav_drawer_header_container);
        mContainer.setOnClickListener(this);

        View creditCardView = view.findViewById(R.id.nav_drawer_credit_card);
        View creditCardListItem = creditCardView.findViewById(R.id.list_item_credit_card_container);
        holder = new CreditCardViewHolder(creditCardListItem);

        setUpRecyclerView(view);

        return view;
    }

    private void setUpRecyclerView(View view) {

        List<NavigationDrawerItem> data = new ArrayList<>();
        data.add(new NavigationDrawerItem("Home", android.R.drawable.ic_dialog_alert));
        data.add(new NavigationDrawerItem("Map", android.R.drawable.ic_dialog_map));
        data.add(new NavigationDrawerItem("Info", android.R.drawable.ic_dialog_info));
        data.add(new NavigationDrawerItem("Dialer", android.R.drawable.ic_dialog_dialer));
        data.add(new NavigationDrawerItem("Email", android.R.drawable.ic_dialog_email));


        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.nad_drawer_recyclerview_list);

        NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(getActivity(), data);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


    }


    public void setUpDrawer(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar, CreditCard activeCreditCard, ExpenseManagerDAO dao) {

        mActiveCreditCard = activeCreditCard;
        holder.setData(getContext(), mActiveCreditCard, 0);

        mDrawerLayout = drawerLayout;
        mDao = dao;

        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout,toolbar, R.string.drawer_open, R.string.drawer_closed) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();

            }
        });
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        switch (i) {
            case R.id.nav_drawer_header_container:
                List<CreditCard> creditCardList = mDao.getCreditCardList();

                FragmentManager fm = getFragmentManager();
                SelectCreditCardDialogFragment dialog = SelectCreditCardDialogFragment.newInstance(creditCardList);
                dialog.show(fm, "fragment_dialog_create_expense");

        }
    }
}
