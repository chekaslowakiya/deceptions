package ani.saikou

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import ani.saikou.anilist.anilist
import nl.joery.animatedbottombar.AnimatedBottomBar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initActivity(window,findViewById(R.id.navbar_container))

        if (!isOnline(this)){
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, NoInternet::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
        }

        if (anilist.getSavedToken(this)){

            //Load Data
            val navbar = findViewById<AnimatedBottomBar>(R.id.navbar)
            bottomBar = navbar
            val mainViewPager = findViewById<ViewPager2>(R.id.viewpager)
            mainViewPager.isUserInputEnabled = false
            mainViewPager.adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
            navbar.setupWithViewPager2(mainViewPager)
            navbar.selectTabAt(1)
            mainViewPager.post { mainViewPager.setCurrentItem(1, false) }
        }
        else{
            //Login
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer,LoginFragment()).addToBackStack(null).commit()
        }
    }

    //Double Tap Back
    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

}

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        when (position){
            0-> return AnimeFragment()
            1-> return HomeFragment()
            2-> return MangaFragment()
        }
        return HomeFragment()
    }
}