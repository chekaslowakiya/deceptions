package ani.saikou.anilist

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ani.saikou.R
import ani.saikou.anime.Anime
import ani.saikou.databinding.ItemCompactBinding
import ani.saikou.logger
import ani.saikou.manga.Manga
import com.squareup.picasso.Picasso
import java.io.File


var anilist : Anilist = Anilist()

data class Media(
    val anime:Anime? = null,
    val manga: Manga? = null,
)

class MediaAdaptor(
    private val mediaList: ArrayList<Media>) :
    RecyclerView.Adapter<MediaAdaptor.MediaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val binding = ItemCompactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MediaViewHolder(binding)
    }
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val b = holder.binding
        val anime = mediaList[position].anime
        val manga = mediaList[position].manga
        if (anime!=null){
            Picasso.get().load(anime.cover).into(b.itemCompactImage)
            b.itemCompactOngoing.visibility = if (anime.status=="RELEASING")  View.VISIBLE else View.GONE
            b.itemCompactTitle.text = anime.userPreferredName
            b.itemCompactScore.text = ((if(anime.userScore==0) (anime.meanScore?:0) else anime.userScore)/10.0).toString()
            b.itemCompactScoreBG.background = ContextCompat.getDrawable(b.root.context,(if (anime.userScore!=0) R.drawable.item_user_score else R.drawable.item_score))
            b.itemCompactUserProgress.text = (anime.userProgress?:"~").toString()
            b.itemCompactTotal.text = " / ${if (anime.nextAiringEpisode!=null) (anime.nextAiringEpisode.toString()+" / "+(anime.totalEpisodes?:"~").toString()) else (anime.totalEpisodes?:"~").toString()}"
        }
        else if(manga!=null){
            Picasso.get().load(manga.cover).into(b.itemCompactImage)
            b.itemCompactOngoing.visibility = if (manga.status=="RELEASING")  View.VISIBLE else View.GONE
            b.itemCompactTitle.text = manga.userPreferredName
            b.itemCompactScore.text = ((if(manga.userScore==0) (manga.meanScore?:0) else manga.userScore)/10.0).toString()
            b.itemCompactScoreBG.background = ContextCompat.getDrawable(b.root.context,(if (manga.userScore!=0) R.drawable.item_user_score else R.drawable.item_score))
            b.itemCompactUserProgress.text = (manga.userProgress?:"~").toString()
            b.itemCompactTotal.text = " / ${manga.totalChapters?:"~"}"
        }
    }

    override fun getItemCount() = mediaList.size

    inner class MediaViewHolder(val binding: ItemCompactBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val media = mediaList[bindingAdapterPosition]
                if (media.anime != null) {
                    logger(media.anime.id)
                } else if (media.manga != null) {
                    logger(media.manga.id)
                }
            }
        }
    }
}

class Anilist {
    val query : AnilistQueries = AnilistQueries()

    var token : String? = null
    var username : String? = null
    var userid : Int? = null
    var avatar : String? = null
    var episodesWatched : Int? = null
    var chapterRead : Int? = null

    fun loginIntent(context: Context){
        val clientID = 6818

        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, Uri.parse("https://anilist.co/api/v2/oauth/authorize?client_id=$clientID&response_type=token"))
    }

    fun getSavedToken(context: Context):Boolean{
        if ("anilistToken" in context.fileList()){
            token = File(context.filesDir, "anilistToken").readText()
            return true
        }
        return false
    }

}
