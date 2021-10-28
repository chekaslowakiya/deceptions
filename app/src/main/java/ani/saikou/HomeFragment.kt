package ani.saikou

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import ani.saikou.anilist.anilist

class HomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewInset(view.findViewById(R.id.fragment_home))
        val button = view.findViewById<Button>(R.id.button)
        if (anilist.token==null)
            button.setOnClickListener { anilist.loginIntent(requireContext()) }
        else
            button.visibility = View.GONE
    }
}