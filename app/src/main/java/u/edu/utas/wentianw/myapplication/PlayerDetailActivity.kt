package u.edu.utas.wentianw.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class PlayerDetailActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_detail)

        val playerName = intent.getStringExtra("playerName") ?: return

        // 找到编辑按钮
        val editButton = findViewById<ImageView>(R.id.imgEdit)

        // 设置点击监听器：跳转到 EditPlayerActivity
        editButton.setOnClickListener {
            val intent = Intent(this, EditPlayerActivity::class.java)
            intent.putExtra("playerName", playerName)
            startActivity(intent)
        }

        val nameText = findViewById<TextView>(R.id.txtPlayerName)
        val roleText = findViewById<TextView>(R.id.txtPlayerRole)
        val killsText = findViewById<TextView>(R.id.txtKills)
        val deathsText = findViewById<TextView>(R.id.txtDeaths)
        val assistsText = findViewById<TextView>(R.id.txtAssists)
        val editIcon = findViewById<ImageView>(R.id.imgEdit)

        db.collection("players").document(playerName).get().addOnSuccessListener {
            nameText.text = playerName
            roleText.text = it.getString("role")
            killsText.text = "Kills: ${it.getLong("kills")} (${it.getDouble("kpg")})"
            deathsText.text = "Deaths: ${it.getLong("deaths")} (${it.getDouble("dpg")})"
            assistsText.text = "Assists: ${it.getLong("assists")} (${it.getDouble("apg")})"
        }

        editIcon.setOnClickListener {
            val intent = Intent(this, EditPlayerActivity::class.java)
            intent.putExtra("playerName", playerName)
            startActivity(intent)
        }

        val backButton = findViewById<ImageView>(R.id.imgBack)
        backButton.setOnClickListener {
            finish() // 关闭当前页面，回到上一个页面
        }
    }
}
