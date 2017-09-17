package com.campustechng.aminu.idpenrollment.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.campustechng.aminu.idpenrollment.fragments.EnrollmentFragment;
import com.campustechng.aminu.idpenrollment.R;
import com.campustechng.aminu.idpenrollment.fragments.ReliefItemsFragment;
import com.campustechng.aminu.idpenrollment.fragments.SearchFragment;
import com.campustechng.aminu.idpenrollment.fragments.VerificationFragment;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    NavigationView navigationView;
    FloatingActionButton fab;
    Toolbar toolbar;
    private String[] activityTitles;
    public static int navItemIndex = 0;
    private Handler mHandler;
    private static final String TAG_HOME = "home";
    private static final String TAG_ENROLLMENT = "enroll";
    private static final String TAG_VERIFICATION = "verify";
    private static final String TAG_SEARCH = "search";
    private static final String TAG_RELIEF = "relief";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_HOME;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mHandler = new Handler();

       /* save = (FloatingActionButton) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });*/

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        // initializing navigation menu
        setUpNavigationView();

       // toggleFab();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
        }
        if(getIntent().getExtras() != null){
            CURRENT_TAG = getIntent().getExtras().getString("CURRENT_TAG");
            navItemIndex = getIntent().getExtras().getInt("navIndex");
        }

        loadHomeFragment();
    }

    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar description
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the save button
           // toggleFab();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect.
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }


        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }


    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                EnrollmentFragment enrollFragment = new EnrollmentFragment();
                return enrollFragment;
            case 1:
                VerificationFragment verifyFragment = new VerificationFragment();
                return verifyFragment;
            case 2:
                SearchFragment reportFragment = new SearchFragment();
                return reportFragment;
            case 3:
                ReliefItemsFragment reliefFragment = new ReliefItemsFragment();
                return reliefFragment;
            default:
                return new Fragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }


    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_enroll:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_ENROLLMENT;
                        break;
                    case R.id.nav_verify:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_VERIFICATION;
                        break;
                    case R.id.nav_search:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_SEARCH;
                        break;
                    case R.id.nav_relief:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_RELIEF;
                        break;
                    case R.id.nav_settings:
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        drawer.closeDrawers();
                    case R.id.nav_logout:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, LoginActivity.class ));
                        drawer.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    // show or hide the save
    private void toggleFab() {
        if(navItemIndex == 0)
            fab.show();
        else
            fab.hide();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

            // checking if user is on other navigation menu
            // rather than home
            /*if (navItemIndex != 0) {
                if(navItemIndex == 2) {
                    startActivity(new Intent(MainActivity.this,HomeActivity.class));
                    finish();
                }
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }else {
                startActivity(new Intent(MainActivity.this,HomeActivity.class));
                finish();
            }*/

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.empty_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

       /* if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void save() {

    }
}
