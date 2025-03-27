package u.edu.utas.wentianw.myapplication


import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PlayerInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_info)

        val name = intent.getStringExtra("playerName") ?: "Unknown"
        val imageView = findViewById<ImageView>(R.id.playerImage)
        val nameView = findViewById<TextView>(R.id.playerName)
        val roleView = findViewById<TextView>(R.id.playerRole)

        nameView.text = name
        roleView.text = if (name == "xiye") "F/A MID" else "Unknown Role"

//        // 根据名字加载图片
//        val imageRes = when (name) {
//            "xiye" -> R.drawable.xiye
//            "TheShy" -> R.drawable.theshy
//            "Scout" -> R.drawable.scout
//            else -> R.drawable.player_default
//        }
//        imageView.setImageResource(imageRes)
    }
}