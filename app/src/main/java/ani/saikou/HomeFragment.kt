package ani.saikou

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ani.saikou.anilist.anilist
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
class HomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewInset(view.findViewById(R.id.fragment_home))

        //UserData

        view.findViewById<View>(R.id.homeUserDataProgressBar).visibility = View.VISIBLE
        view.findViewById<View>(R.id.homeUserDataContainer).visibility = View.GONE

        GlobalScope.launch {
            val lol = anilist.getUserData()
            requireActivity().runOnUiThread{
                view.findViewById<View>(R.id.homeUserDataProgressBar).visibility = View.GONE
                view.findViewById<View>(R.id.homeUserDataContainer).visibility = View.VISIBLE
                logger(lol)
            }
        }

        //Lists
        GlobalScope.launch {
//            TODO(anilist.getImage("ANIME or MANGA"))
//            val animeImage = anilist.getQuery(anilist.queries.coverImage(anilist.userid!!,"ANIME"))
//            val mangaImage = anilist.getQuery(anilist.queries.coverImage(anilist.userid!!,"MANGA"))
//            requireActivity().runOnUiThread{
//                Picasso.get().load(animeImage).into(view.findViewById<ImageView>(R.id.homeAnimeListImage))
//                Picasso.get().load(mangaImage).into(view.findViewById<ImageView>(R.id.homeMangaListImage))
//            }
        }

        //Continue Watching
        view.findViewById<View>(R.id.homeWatchingProgressBar).visibility = View.VISIBLE
        view.findViewById<View>(R.id.homeWatchingRecyclerView).visibility = View.GONE
        GlobalScope.launch {
//            TODO(anilist.getAnimeWatching)
//            val watching:ArrayList<Anime> = anilist.getQuery(anilist.queries.continueWatching(anilist.user.id,"ANIME"))
        }

        //Continue Reading
        view.findViewById<View>(R.id.homeReadingProgressBar).visibility = View.VISIBLE
        view.findViewById<View>(R.id.homeReadingRecyclerView).visibility = View.GONE
        GlobalScope.launch {
//            TODO(anilist.getMangaReading)
//            val reading:ArrayList<Manga> = anilist.getQuery(anilist.queries.continueWatching(anilist.user.id,"MANGA"))
        }
    }
}