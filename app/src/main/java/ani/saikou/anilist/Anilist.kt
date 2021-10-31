package ani.saikou.anilist

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import ani.saikou.anime.Anime
import ani.saikou.logger
import ani.saikou.manga.Manga
import ani.saikou.startMainActivity
import java.io.File


var anilist : Anilist = Anilist()

data class Media(
    val anime:Anime? = null,
    val manga: Manga? = null,
)

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


class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data: Uri? = intent?.data
        logger(data.toString())
        anilist.token = Regex("""(?<=access_token=).+(?=&token_type)""").find(data.toString())!!.value
        val filename = "anilistToken"
        this.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(anilist.token!!.toByteArray())
        }
        startMainActivity(this)
    }
}

