package ani.saikou.anilist

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import ani.saikou.logger
import ani.saikou.startMainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.File


var anilist : Anilist = Anilist()

class Anilist {
    val queries : AnilistQueries = AnilistQueries()
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

    fun getQuery(query:String): String = runBlocking{
        return@runBlocking withContext(Dispatchers.Default) {
            Jsoup.connect("https://graphql.anilist.co/")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer $token")
                .requestBody("""{"query":"$query"}""")
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .post().body().text()
        }
    }

    fun getUserData():Boolean{
        val a = getQuery(queries.userData)
        //TODO(JSON DEPRESSION)
        return true
    }

}

class AnilistQueries{
    val userData ="""{Viewer {name avatar{medium}bannerImage id statistics{anime{episodesWatched}manga{chaptersRead}}}}"""

    fun continueWatching(userId:Int,type:String): String {
        return """ { MediaListCollection(userId: $userId, type: $type, status: CURRENT) { lists { entries { progress status media { id status chapters episodes nextAiringEpisode {episode} meanScore coverImage{large} title { romaji userPreferred } } } } } } """
    }
    fun animeList(userId:Int,type:String): String {
        return """{ MediaListCollection(userId: $userId, type: $type) { lists { name entries { score(format:POINT_100) media { id status chapters episodes nextAiringEpisode {episode} meanScore coverImage{large} title { romaji userPreferred } } } } } }"""
    }

    val recommendations = """ { Page(page: 1, perPage:25) { pageInfo { total currentPage hasNextPage } recommendations(sort: RATING_DESC, onList: true) { rating userRating mediaRecommendation { id title { romaji userPreferred } format type status(version: 2) coverImage { large } } } } } """

    fun coverImage(userId: Int,type: String): String {
        return """ query ($userId: Int){ MediaListCollection(userId: $userId, type: $type, sort:[SCORE_DESC,UPDATED_TIME_DESC],chunk:1,perChunk:1) { lists { entries{ media { coverImage{large} } } } } } """
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

