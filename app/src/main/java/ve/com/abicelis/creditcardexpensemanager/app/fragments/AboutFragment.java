package ve.com.abicelis.creditcardexpensemanager.app.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

import ve.com.abicelis.creditcardexpensemanager.R;

/**
 * Created by abice on 2/2/2017.
 */

public class AboutFragment extends Fragment {

    public static final String GITHUB_URL = "github.com/abicelis/";
    public static final String WEBSITE_URL = "www.alejandrobicelis.com.ve";


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.fragment_name_about));
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        TextView version = (TextView) rootView.findViewById(R.id.fragment_about_version);
        TextView author = (TextView) rootView.findViewById(R.id.fragment_about_author);
        TextView githubLink = (TextView) rootView.findViewById(R.id.fragment_about_github_link);
        TextView websiteLink = (TextView) rootView.findViewById(R.id.fragment_about_website_link);


        version.setText(String.format(Locale.getDefault(), getResources().getString(R.string.fragment_about_version), getAppVersionAndBuild(getActivity()).first));
        author.setText(String.format(Locale.getDefault(), getResources().getString(R.string.fragment_about_author), Calendar.getInstance().get(Calendar.YEAR)));

        githubLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchWebBrowser(getActivity(), GITHUB_URL);
            }
        });
        websiteLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchWebBrowser(getActivity(), WEBSITE_URL);
            }
        });

        return rootView;
    }


    public static Pair<String, Integer> getAppVersionAndBuild(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return new Pair<>(pInfo.versionName, pInfo.versionCode);
        } catch (Exception e) {
            return new Pair<>("", 0);
        }
    }

    @SuppressLint("DefaultLocale")
    public static boolean launchWebBrowser(Context context, String url) {
        try {
            url = url.toLowerCase();
            if (!url.startsWith("http://") || !url.startsWith("https://")) {
                url = "http://" + url;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            if (null == resolveInfo) {
                Toast.makeText(context, "Could not find a Browser to open link", Toast.LENGTH_SHORT).show();
                return false;
            }
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            Toast.makeText(context, "Could not start web browser", Toast.LENGTH_SHORT).show();

            return false;
        }
    }
}
