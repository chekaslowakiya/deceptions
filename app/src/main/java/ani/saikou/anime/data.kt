package ani.saikou.anime

import ani.saikou.UserMediaStatus

data class Anime (
    val name: String,
    val id: Int,
    var description: String? = null,

    val cover: String,
    val banner: String,

    val ongoing: Boolean,
    val meanScore: Int,

    var totalEpisodes: Int? = null,
    var nextAiringEpisode: Int? = null,

    val userPreferredName: String,
    var userProgress: Int? = null,
    var userStatus: UserMediaStatus? = null,
    var userScore: Int? = null,
    )