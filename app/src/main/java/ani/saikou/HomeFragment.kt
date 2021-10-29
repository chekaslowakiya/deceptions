package ani.saikou

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import ani.saikou.anilist.anilist
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class HomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewInset(view.findViewById(R.id.fragment_home))
        GlobalScope.async {
            val lol = anilist.getQuery(anilist.queries.userData)
            requireActivity().runOnUiThread{
                view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                view.findViewById<TextView>(R.id.textView).text = lol
            }
        }
    }
}