package ani.saikou

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import nl.joery.animatedbottombar.AnimatedBottomBar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initActivity(window,findViewById(R.id.navbar_container))

        val navbar = findViewById<AnimatedBottomBar>(R.id.navbar)
        val viewPager = findViewById<ViewPager2>(R.id.viewpager)

        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        navbar.setupWithViewPager2(viewPager)
        navbar.selectTabAt(1)
        viewPager.post { viewPager.setCurrentItem(1, false) }
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