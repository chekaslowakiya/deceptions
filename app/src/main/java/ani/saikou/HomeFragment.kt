package ani.saikou

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ani.saikou.anilist.Media
import ani.saikou.anilist.anilist
import com.squareup.picasso.Picasso
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@OptIn(DelicateCoroutinesApi::class)
class HomeFragment : Fragment(),MediaAdaptor.OnMediaClickListener {
    private val model: HomeViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewInset(view.findViewById(R.id.fragment_home))

        view.findViewById<View>(R.id.homeUserDataProgressBar).visibility = View.VISIBLE
        view.findViewById<View>(R.id.homeUserDataContainer).visibility = View.GONE

        model.getListImages().observe(viewLifecycleOwner,{
            if (it.isNotEmpty()){
                Picasso.get().load(it[0]?:"").into(view.findViewById<ImageView>(R.id.homeAnimeListImage))
                Picasso.get().load(it[1]?:"").into(view.findViewById<ImageView>(R.id.homeMangaListImage))
            }
        })

        val continueWatchingRecycler = view.findViewById<RecyclerView>(R.id.homeWatchingRecyclerView)
        view.findViewById<View>(R.id.homeWatchingProgressBar).visibility = View.VISIBLE
        continueWatchingRecycler.visibility = View.GONE

        model.getAnimeContinue().observe(viewLifecycleOwner,{
            continueWatchingRecycler.adapter= MediaAdaptor(it, this)
            continueWatchingRecycler.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        })

        val continueReadingRecycler = view.findViewById<RecyclerView>(R.id.homeReadingRecyclerView)
        view.findViewById<View>(R.id.homeReadingProgressBar).visibility = View.VISIBLE
        continueReadingRecycler.visibility = View.GONE

        model.getMangaContinue().observe(viewLifecycleOwner,{
            continueReadingRecycler.adapter= MediaAdaptor(it, this)
            continueReadingRecycler.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        })

        GlobalScope.launch {
            anilist.query.getUserData()
            requireActivity().runOnUiThread {

                //UserData
                view.findViewById<View>(R.id.homeUserDataProgressBar).visibility = View.GONE
                view.findViewById<View>(R.id.homeUserDataContainer).visibility = View.VISIBLE

                view.findViewById<TextView>(R.id.homeUserName).text = anilist.username
                view.findViewById<TextView>(R.id.homeUserEpisodesWatched).text = anilist.episodesWatched.toString()
                view.findViewById<TextView>(R.id.homeUserChaptersRead).text = anilist.chapterRead.toString()
                Picasso.get().load(anilist.avatar).into(view.findViewById<ImageView>(R.id.homeUserAvatar))

                //Lists Buttons
                GlobalScope.launch { model.setListImages(anilist.query.getCoverImages())
                }

                //Continue Watching
                GlobalScope.launch {
                    model.setAnimeContinue(anilist.query.continueMedia("ANIME"))
                    requireActivity().runOnUiThread {
                        continueWatchingRecycler.visibility = View.VISIBLE
                        view.findViewById<View>(R.id.homeWatchingProgressBar).visibility = View.GONE
                    }
                }

                //Continue Reading
                GlobalScope.launch {
                    model.setMangaContinue(anilist.query.continueMedia("MANGA"))
                    requireActivity().runOnUiThread {
                        continueReadingRecycler.visibility = View.VISIBLE
                        view.findViewById<View>(R.id.homeReadingProgressBar).visibility = View.GONE
                    }
                }

            }
        }
    }

    override fun onMediaClick(media: Media) {
        if (media.anime!=null){
            logger(media.anime.id)
        }
        else if(media.manga!=null){
            logger(media.manga.id)
        }
    }
}

class HomeViewModel : ViewModel() {
    private val listImages : MutableLiveData<ArrayList<String?>> = MutableLiveData<ArrayList<String?>>(arrayListOf())
    fun getListImages(): LiveData<ArrayList<String?>> = listImages
    fun setListImages(arr: ArrayList<String?>) = listImages.postValue(arr)

    private val animeContinue: MutableLiveData<ArrayList<Media>> = MutableLiveData<ArrayList<Media>>(arrayListOf())
    fun getAnimeContinue(): LiveData<ArrayList<Media>> = animeContinue
    fun setAnimeContinue(media: ArrayList<Media>) = animeContinue.postValue(media)

    private val mangaContinue: MutableLiveData<ArrayList<Media>> = MutableLiveData<ArrayList<Media>>(arrayListOf())
    fun getMangaContinue(): LiveData<ArrayList<Media>> = mangaContinue
    fun setMangaContinue(media: ArrayList<Media>) = mangaContinue.postValue(media)

    private val recommendation:MutableLiveData<ArrayList<Media>> = MutableLiveData<ArrayList<Media>>(arrayListOf())
    fun getRecommendation(): LiveData<ArrayList<Media>> = recommendation
    fun setRecommendation(media: ArrayList<Media>) = animeContinue.postValue(media)
}

class MediaAdaptor(
    private val mediaList: ArrayList<Media>,
    private val listener: OnMediaClickListener) :
    RecyclerView.Adapter<MediaAdaptor.MediaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_compact, parent, false)
        return MediaViewHolder(itemView)
    }
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val v = holder.view
        val anime = mediaList[position].anime
        val manga = mediaList[position].manga
        if (anime!=null){
            Picasso.get().load(anime.cover).into(v.findViewById<ImageView>(R.id.itemCompactImage))
            v.findViewById<View>(R.id.itemCompactOngoing).visibility = if (anime.status=="RELEASING")  View.VISIBLE else View.GONE
            v.findViewById<TextView>(R.id.itemCompactTitle).text = anime.userPreferredName
            v.findViewById<TextView>(R.id.itemCompactScore).text = ((if(anime.userScore==0) (anime.meanScore?:0) else anime.userScore?:0)/10.0).toString()
            v.findViewById<View>(R.id.itemCompactScoreBG).background = ContextCompat.getDrawable(v.context,(if (anime.userScore!=0) R.drawable.item_user_score else R.drawable.item_score))
            v.findViewById<TextView>(R.id.itemCompactUserProgress).text = (anime.userProgress?:"~").toString()
            v.findViewById<TextView>(R.id.itemCompactTotal).text = " / ${if (anime.nextAiringEpisode!=null) (anime.nextAiringEpisode.toString()+" / "+(anime.totalEpisodes?:"~").toString()) else (anime.totalEpisodes?:"~").toString()}"
        }
        else if(manga!=null){
            Picasso.get().load(manga.cover).into(v.findViewById<ImageView>(R.id.itemCompactImage))
            v.findViewById<View>(R.id.itemCompactOngoing).visibility = if (manga.status=="RELEASING")  View.VISIBLE else View.GONE
            v.findViewById<TextView>(R.id.itemCompactTitle).text = manga.userPreferredName
            v.findViewById<TextView>(R.id.itemCompactScore).text = ((if(manga.userScore==0) (manga.meanScore?:0) else manga.userScore?:0)/10.0).toString()
            v.findViewById<View>(R.id.itemCompactScoreBG).background = ContextCompat.getDrawable(v.context,(if (manga.userScore!=null) R.drawable.item_user_score else R.drawable.item_score))
            v.findViewById<TextView>(R.id.itemCompactUserProgress).text = (manga.userProgress?:"~").toString()
            v.findViewById<TextView>(R.id.itemCompactTotal).text = " / ${manga.totalChapters?:"~"}"
        }
    }

    override fun getItemCount() = mediaList.size

    inner class MediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val view = itemView
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            listener.onMediaClick(mediaList[bindingAdapterPosition])
        }
    }
    interface OnMediaClickListener{
        fun onMediaClick(media: Media)
    }
}