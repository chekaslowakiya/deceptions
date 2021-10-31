package ani.saikou.anime

//import ani.saikou.UserMediaStatus

data class Anime(
    val name: String,
    val id: Int,
    var description: String? = null,

    val cover: String,
    val banner: String? = null,

    val status : String? = null,
    val meanScore: Int?,

    var totalEpisodes: Int? = null,
    var nextAiringEpisode: Int? = null,

    val userPreferredName: String,
    var userProgress: Int? = null,
    var userStatus: String? = null,
    var userScore: Int? = null,
)