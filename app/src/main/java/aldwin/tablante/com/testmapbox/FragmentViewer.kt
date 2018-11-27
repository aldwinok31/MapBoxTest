package aldwin.tablante.com.testmapbox

import android.app.FragmentManager
import android.database.DataSetObserver
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.viewpager_res.*

class FragmentViewer : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       setContentView(R.layout.viewpager_res)
      /*  val adapter = ViewPageAdapter(supportFragmentManager)
        adapter.addnFragment(FragmentMap())
        adapter.addnFragment(FragmentNavigation())
        vPager.adapter = adapter*/

openFragmentMapbox()
//openFragmentNavigation()

    }


    fun openFragmentMapbox(){
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = FragmentMap()
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        transaction.replace(R.id.frag, fragment, "tagname")
        transaction.addToBackStack("tagname")
        transaction.commit()


    }



    fun openFragmentNavigation(){
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = FragmentNavigation()
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        transaction.replace(R.id.frag, fragment, "tagname")
        transaction.addToBackStack("tagname")
        transaction.commit()


    }

/*    inner class ViewPageAdapter(manage: android.support.v4.app.FragmentManager) : FragmentPagerAdapter(manage) {

        private val fragmentlist: MutableList<Fragment> = ArrayList()


        override fun getItem(position: Int): Fragment {
            return fragmentlist[position]
        }

        override fun getCount(): Int {

            return fragmentlist.size
        }

        fun addnFragment(fragment: Fragment) {
            fragmentlist.add(fragment)
        }


    }*/
}