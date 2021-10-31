package ani.saikou.anilist

import ani.saikou.anime.Anime
import ani.saikou.manga.Manga
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.jsoup.Jsoup

class AnilistQueries{

    private fun getQuery(query:String): String = runBlocking{
        return@runBlocking withContext(Dispatchers.Default) {
            Jsoup.connect("https://graphql.anilist.co/")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer ${anilist.token}")
                .requestBody("""{"query":"$query"}""")
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .post().body().text()
        }
    }

    fun getUserData(){
        val response = Json.decodeFromString<JsonObject>(
            getQuery("""{Viewer {name avatar{medium}id statistics{anime{episodesWatched}manga{chaptersRead}}}}""")
        )["data"]!!.jsonObject["Viewer"]!!

        anilist.userid = response.jsonObject["id"].toString().toInt()
        anilist.username = response.jsonObject["name"].toString().trim('"')
        anilist.avatar = response.jsonObject["avatar"]!!.jsonObject["medium"].toString().trim('"')
        anilist.episodesWatched = response.jsonObject["statistics"]!!.jsonObject["anime"]!!.jsonObject["episodesWatched"].toString().toInt()
        anilist.chapterRead = response.jsonObject["statistics"]!!.jsonObject["manga"]!!.jsonObject["chaptersRead"].toString().toInt()
    }


    fun continueMedia(type:String): ArrayList<Media> {
        val response = getQuery(""" { MediaListCollection(userId: ${anilist.userid}, type: $type, status: CURRENT) { lists { entries { progress score(format:POINT_100) status media { id status chapters episodes nextAiringEpisode {episode} meanScore coverImage{large} title { romaji userPreferred } } } } } } """)
        val returnArray = arrayListOf<Media>()
        println(response)
        Json.decodeFromString<JsonObject>(response)["data"]!!.jsonObject["MediaListCollection"]!!.jsonObject["lists"]!!.jsonArray[0].jsonObject["entries"]!!.jsonArray.reversed().forEach {
            if (type == "ANIME"){
                returnArray.add(
                    Media(anime=Anime(
                    name = it.jsonObject["media"]!!.jsonObject["title"]!!.jsonObject["romaji"].toString().trim('"'),
                    id = it.jsonObject["media"]!!.jsonObject["id"].toString().toInt(),

                    cover = it.jsonObject["media"]!!.jsonObject["coverImage"]!!.jsonObject["large"].toString().trim('"'),

                    status = it.jsonObject["media"]!!.jsonObject["status"].toString().trim('"'),
                    meanScore = it.jsonObject["media"]!!.jsonObject["meanScore"].toString().toInt(),

                    totalEpisodes = if (it.jsonObject["media"]!!.jsonObject["episodes"].toString() != "null") it.jsonObject["media"]!!.jsonObject["episodes"].toString().toInt() else null,
                    nextAiringEpisode = if(it.jsonObject["media"]!!.jsonObject["nextAiringEpisode"].toString() != "null") it.jsonObject["media"]!!.jsonObject["nextAiringEpisode"]!!.jsonObject["episode"].toString().toInt() else null,

                    userPreferredName = it.jsonObject["media"]!!.jsonObject["title"]!!.jsonObject["userPreferred"].toString().trim('"'),
                    userProgress = it.jsonObject["progress"].toString().toInt(),
                    userScore = it.jsonObject["score"].toString().toInt(),
                    userStatus = it.jsonObject["status"].toString()
                )))
            }
            else{
                returnArray.add(
                    Media(manga= Manga(
                    name = it.jsonObject["media"]!!.jsonObject["title"]!!.jsonObject["romaji"].toString().trim('"'),
                    id = it.jsonObject["media"]!!.jsonObject["id"].toString().toInt(),

                    cover = it.jsonObject["media"]!!.jsonObject["coverImage"]!!.jsonObject["large"].toString().trim('"'),

                    status = it.jsonObject["media"]!!.jsonObject["status"].toString().trim('"'),
                    meanScore = it.jsonObject["media"]!!.jsonObject["meanScore"].toString().toInt(),

                    totalChapters = if (it.jsonObject["media"]!!.jsonObject["chapters"]!!.toString() != "null") it.jsonObject["media"]!!.jsonObject["chapters"].toString().toInt() else null,

                    userPreferredName = it.jsonObject["media"]!!.jsonObject["title"]!!.jsonObject["userPreferred"].toString().trim('"'),
                    userProgress = it.jsonObject["progress"].toString().toInt(),
                    userScore = it.jsonObject["score"].toString().toInt(),
                    userStatus = it.jsonObject["status"].toString()
                )))
            }
        }
        return returnArray
    }

    fun mangaList(): MutableList<Manga> {
        val response = getQuery("""{ MediaListCollection(userId: ${anilist.userid}, type: MANGA) { lists { name entries { progress score(format:POINT_100) media { id status chapters episodes nextAiringEpisode {episode} meanScore coverImage{large} title { romaji userPreferred } } } } } }""")
        val returnArray = mutableListOf<Manga>()
        Json.decodeFromString<JsonObject>(response)["data"]!!.jsonObject["MediaListCollection"]!!.jsonObject["lists"]!!.jsonArray.forEach { i ->
            i.jsonObject["entries"]!!.jsonArray.forEach {
                returnArray.add(
                    Manga(
                        name = it.jsonObject["media"]!!.jsonObject["title"]!!.jsonObject["romaji"].toString(),
                        id = it.jsonObject["media"]!!.jsonObject["id"].toString().toInt(),
                        cover = it.jsonObject["media"]!!.jsonObject["coverImage"]!!.jsonObject["large"].toString(),

                        status = it.jsonObject["media"]!!.jsonObject["status"].toString().trim('"'),
                        meanScore = if(it.jsonObject["media"]!!.jsonObject["meanScore"].toString() != "null") it.jsonObject["media"]!!.jsonObject["meanScore"].toString().toInt() else null,

                        totalChapters = if (it.jsonObject["media"]!!.jsonObject["chapters"].toString() != "null") it.jsonObject["media"]!!.jsonObject["chapters"].toString().toInt() else null,

                        userPreferredName = it.jsonObject["media"]!!.jsonObject["title"]!!.jsonObject["userPreferred"].toString(),
                        userScore = it.jsonObject["score"].toString().toInt(),
                        userProgress = it.jsonObject["progress"].toString().toInt(),
                    )
                )
            }
        }
        return returnArray
    }

    fun animeList(): ArrayList<Anime> {
        val response = getQuery("""{ MediaListCollection(userId: ${anilist.userid}, type: ANIME) { lists { name entries { status progress score(format:POINT_100) media { id status chapters episodes nextAiringEpisode {episode} meanScore coverImage{large} title { romaji userPreferred } } } } } }""")
        val returnArray = arrayListOf<Anime>()
        println(response)
        Json.decodeFromString<JsonObject>(response)["data"]!!.jsonObject["MediaListCollection"]!!.jsonObject["lists"]!!.jsonArray.forEach { i ->
            i.jsonObject["entries"]!!.jsonArray.forEach {
                returnArray.add(
                    Anime(
                        name = it.jsonObject["media"]!!.jsonObject["title"]!!.jsonObject["romaji"].toString(),
                        id = it.jsonObject["media"]!!.jsonObject["id"].toString().toInt(),

                        cover = it.jsonObject["media"]!!.jsonObject["coverImage"]!!.jsonObject["large"].toString(),

                        status = it.jsonObject["media"]!!.jsonObject["status"].toString().trim('"'),
                        meanScore = if(it.jsonObject["media"]!!.jsonObject["meanScore"].toString() != "null") it.jsonObject["media"]!!.jsonObject["meanScore"].toString().toInt() else null,


                        totalEpisodes = if(it.jsonObject["media"]!!.jsonObject["episodes"]!!.toString() != "null") it.jsonObject["media"]!!.jsonObject["episodes"].toString().toInt() else null,
                        nextAiringEpisode = if (it.jsonObject["media"]!!.jsonObject["nextAiringEpisode"].toString() != "null") it.jsonObject["media"]!!.jsonObject["nextAiringEpisode"]!!.jsonObject["episode"].toString().toInt() else null,

                        userPreferredName = it.jsonObject["media"]!!.jsonObject["title"]!!.jsonObject["userPreferred"].toString(),
                        userProgress = it.jsonObject["progress"].toString().toInt(),
                        userStatus = it.jsonObject["status"].toString(),
                        userScore = it.jsonObject["score"].toString().toInt()
                    )
                )
            }
        }
        return returnArray
    }

    fun recommendations(): ArrayList<Media> {
        val response = getQuery(""" { Page(page: 1, perPage:30) { pageInfo { total currentPage hasNextPage } recommendations(sort: RATING_DESC, onList: true) { rating userRating mediaRecommendation { id meanScore title { romaji userPreferred } type status(version: 2) coverImage { large } } } } } """)
        val responseArray = arrayListOf<Media>()
        Json.decodeFromString<JsonObject>(response)["data"]!!.jsonObject["Page"]!!.jsonObject["recommendations"]!!.jsonArray.reversed().forEach{
            if(it.jsonObject["mediaRecommendation"]!!.jsonObject["type"].toString() == "\"ANIME\""){
                responseArray.add(Media(anime = Anime(
                    id = it.jsonObject["mediaRecommendation"]!!.jsonObject["id"].toString().toInt(),
                    name = it.jsonObject["mediaRecommendation"]!!.jsonObject["title"]!!.jsonObject["romaji"].toString(),
                    userPreferredName = it.jsonObject["mediaRecommendation"]!!.jsonObject["title"]!!.jsonObject["userPreferred"].toString(),
                    status = it.jsonObject["mediaRecommendation"]!!.jsonObject["status"].toString().trim('"'),
                    cover = it.jsonObject["mediaRecommendation"]!!.jsonObject["coverImage"]!!.jsonObject["large"].toString(),
                    meanScore = it.jsonObject["mediaRecommendation"]!!.jsonObject["meanScore"].toString().toInt(),
                )
                ))
            }
            else {
                responseArray.add(Media(manga = Manga(
                    id = it.jsonObject["mediaRecommendation"]!!.jsonObject["id"].toString().toInt(),
                    name = it.jsonObject["mediaRecommendation"]!!.jsonObject["title"]!!.jsonObject["romaji"].toString(),
                    userPreferredName = it.jsonObject["mediaRecommendation"]!!.jsonObject["title"]!!.jsonObject["userPreferred"].toString(),
                    status = it.jsonObject["mediaRecommendation"]!!.jsonObject["status"].toString().trim('"'),
                    cover = it.jsonObject["mediaRecommendation"]!!.jsonObject["coverImage"]!!.jsonObject["large"].toString(),
                    meanScore = it.jsonObject["mediaRecommendation"]!!.jsonObject["meanScore"].toString().toInt(),
                )
                ))
            }

        }
        return responseArray
    }


    private fun coverImage(type: String): String? {
        val response = getQuery("""{ MediaListCollection(userId: ${anilist.userid}, type: $type, sort:[SCORE_DESC,UPDATED_TIME_DESC],chunk:1,perChunk:1) { lists { entries{ media { coverImage{large} } } } } } """)
        println(response)
        val list = Json.decodeFromString<JsonObject>(response)["data"]!!.jsonObject["MediaListCollection"]!!.jsonObject["lists"]!!.jsonArray
        if (list.isNotEmpty()){
            return list[0].jsonObject["entries"]!!.jsonArray[0].jsonObject["media"]!!.jsonObject["coverImage"]!!
                .jsonObject["large"].toString().trim('"')
        }
        return null
    }

    fun getCoverImages(): ArrayList<String?> {
        val default = arrayListOf<String?>(null,null)
        default[0]=coverImage("ANIME")
        default[1]=coverImage("MANGA")
        return default
    }
}
