package com.krishna.navbar.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.krishna.navbar.MainActivity;
import com.krishna.navbar.R;
import com.krishna.navbar.fragments.FindExpertFragment;
import com.krishna.navbar.fragments.LandingFragment;

/**
 * Helper class to standardize navigation throughout the app
 */
public class NavigationHelper {

    /**
     * Navigate to a fragment within the therapist flow
     * @param fragment Current fragment
     * @param destination Destination fragment
     * @param addToBackStack Whether to add to back stack
     */
    public static void navigateToFragment(Fragment fragment, Fragment destination, boolean addToBackStack) {
        FragmentTransaction transaction = fragment.getParentFragmentManager().beginTransaction();
        
        // Add animations
        transaction.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out_right
        );
        
        transaction.replace(R.id.con, destination);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    /**
     * Navigate back to the main flow and show the bottom navigation bar
     * @param fragment Current fragment
     */
    public static void navigateBackToMainFlow(Fragment fragment) {
        // Clear back stack to return to main flow
        FragmentManager fragmentManager = fragment.getParentFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        
        // Navigate to FindExpertFragment and ensure bottom navigation is visible
        FindExpertFragment findExpertFragment = new FindExpertFragment();
        fragmentManager.beginTransaction()
                .setCustomAnimations(
                        R.anim.fade_in,
                        R.anim.fade_out
                )
                .replace(R.id.con, findExpertFragment)
                .commit();
        
        // Show the therapy tab in bottom navigation
        if (fragment.getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity) fragment.getActivity();
            activity.bottomNavigation.show(MainActivity.ID_THERAPY, true);
            activity.showBottomNavigation();
        }
    }

    /**
     * Navigate back to home and show the bottom navigation bar
     * @param fragment Current fragment
     */
    public static void navigateToHome(Fragment fragment) {
        // Clear back stack to return to main flow
        FragmentManager fragmentManager = fragment.getParentFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        
        // Navigate to LandingFragment and ensure bottom navigation is visible
        LandingFragment landingFragment = new LandingFragment();
        fragmentManager.beginTransaction()
                .setCustomAnimations(
                        R.anim.fade_in,
                        R.anim.fade_out
                )
                .replace(R.id.con, landingFragment)
                .commit();
        
        // Show the home tab in bottom navigation
        if (fragment.getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity) fragment.getActivity();
            activity.bottomNavigation.show(MainActivity.ID_HOME, true);
            activity.showBottomNavigation();
        }
    }

    /**
     * Handle back navigation with proper stack management
     * @param fragment Current fragment
     * @return true if navigation was handled, false otherwise
     */
    public static boolean handleBackNavigation(Fragment fragment) {
        FragmentManager fragmentManager = fragment.getParentFragmentManager();
        
        // If there are fragments in the back stack, pop one
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
            return true;
        }
        
        // If we're at the root of a flow, navigate back to main flow
        navigateBackToMainFlow(fragment);
        return true;
    }
} 