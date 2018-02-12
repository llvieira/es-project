package com.github.ghcli.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.ghcli.R;
import com.github.ghcli.fragments.FollowersFragment;
import com.github.ghcli.fragments.ProfileFragment;
import com.github.ghcli.fragments.ReposFragment;
import com.github.ghcli.models.GitHubOrganization;
import com.github.ghcli.models.GitHubUser;
import com.github.ghcli.util.Authentication;

import java.util.ArrayList;


public class HomePage extends AppCompatActivity implements ProfileFragment.OnFragmentInteractionListener, FollowersFragment.OnFragmentInteractionListener, ReposFragment.OnFragmentInteractionListener {
    private static final String SELECTED_ITEM = "arg_selected_item";
    private static final String KEY_USER = "user";
    private static final String KEY_USER_ORGANIZATIONS = "organizations";

    //private BottomNavigationView navBar;
    private int mSelectedItem;

    private DrawerLayout drawerLayout;
    private String[] actions = {"Profile", "Repositories", "Followers", "Logout"};
    private ListView leftDrawer;
    private ActionBarDrawerToggle drawerToggle;

    private GitHubUser user;
    private ArrayList<GitHubOrganization> userOrganizations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_home_page);
        drawerLayout = findViewById(R.id.activity_home_page);
        leftDrawer = findViewById(R.id.left_drawer);

        // get user from login page
        Intent intent = getIntent();
        this.user = intent.getParcelableExtra(KEY_USER);
        this.userOrganizations = intent.getParcelableArrayListExtra(KEY_USER_ORGANIZATIONS);

        Log.d("TOKEN", Authentication.getToken(getApplicationContext()));

        final MenuItem selectedItem;

        leftDrawer.setAdapter(new ArrayAdapter<String>(
                this,
                R.layout.actions_list_drawer,
                actions));
        leftDrawer.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                selectFragment(position);
            }
        });

        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                leftDrawer.bringToFront();
                drawerLayout.requestLayout();
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        drawerLayout.addDrawerListener(drawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        selectFragment(0);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    private void selectFragment(int item) {
        Fragment frag = null;
        // init corresponding fragment
        switch (item) {
            case 0:
                frag = ProfileFragment.newInstance(user, userOrganizations);
                break;
            case 1:
                frag = ReposFragment.newInstance("teste1","teste2");
                break;
            case 2:
                frag = FollowersFragment.newInstance("teste1", "teste2");
                break;
            case 3:
                logout();
                break;
        }

        if (frag != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.activity_home_page, frag, frag.getTag());
            ft.commit();
        }


        Log.d("TESTE", "Deu certo");
        leftDrawer.setItemChecked(item, true);
        drawerLayout.closeDrawer(leftDrawer);
        getSupportActionBar().setTitle(actions[item]);
    }

    public void logout() {
        Authentication.removeToken(getApplicationContext());
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {
    }
    //heavily based on https://github.com/segunfamisa/bottom-navigation-demo/blob/master/app/src/main/java/com/segunfamisa/sample/bottomnav/MainActivity.java
}
