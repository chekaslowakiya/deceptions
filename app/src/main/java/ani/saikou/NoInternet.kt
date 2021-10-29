package ani.saikou

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class NoInternet : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_internet)

        viewInset(findViewById(R.id.refreshButtonContainer))
        findViewById<Button>(R.id.refreshButton).setOnClickListener {
            if (isOnline(this)){
                startMainActivity(this)
            }
        }
    }
}