package ani.saikou.anilist

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ani.saikou.MainActivity
import java.io.File

var anilist : Anilist = Anilist()

class Anilist {
    var token : String? = null

    fun loginIntent(context: Context){
        val clientID = 6818
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://anilist.co/api/v2/oauth/authorize?client_id=$clientID&response_type=code"))
        context.startActivity(browserIntent)
    }

    fun getSavedToken(context: Context){
        if ("anilistToken" in context.fileList())
            token = File(context.filesDir, "anilistToken").readText()
    }
}

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data: Uri? = intent?.data
        anilist.token = data.toString().replace("saikou://anilist?code=","")
        val filename = "anilistToken"
        this.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(anilist.token!!.toByteArray())
        }
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        this.startActivity(intent)
    }
}