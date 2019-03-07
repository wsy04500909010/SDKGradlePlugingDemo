package com.wsy.testgroovy.util

class PageUtil {
    private static final HashSet<String> activityPages = new HashSet<>()
    private static final HashSet<String> fragmentPages = new HashSet<>()
    static {

        //add activity
        activityPages.add("android/app/Activity")
        activityPages.add("android/support/v7/app/AppCompatActivity")
        activityPages.add("android/support/v4/app/FragmentActivity")

        //add androidx activity
        activityPages.add("androidx/appcompat/app/AppCompatActivity")
        activityPages.add("androidx/fragment/app/FragmentActivity")

        //add fragment
        fragmentPages.add('android/support/v4/app/Fragment')
        fragmentPages.add('android/support/v4/app/ListFragment')
        fragmentPages.add('android/support/v4/app/DialogFragment')

        //add androidx fragment
        fragmentPages.add('androidx/fragment/app/Fragment')
        fragmentPages.add('androidx/fragment/app/ListFragment')
        fragmentPages.add('androidx/fragment/app/DialogFragment')

    }

    static boolean isActivityPage(String superName) {
        return activityPages.contains(superName)
    }

    static boolean isFragmentPage(String superName) {
        return fragmentPages.contains(superName)
    }

    static boolean isPage(String superName) {
        return activityPages.contains(superName) || fragmentPages.contains(superName)
    }
}
