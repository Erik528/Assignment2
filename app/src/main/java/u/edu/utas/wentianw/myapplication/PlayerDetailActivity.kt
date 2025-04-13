package u.edu.utas.wentianw.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class PlayerDetailActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var spinnerYear: Spinner
    private lateinit var spinnerTournament: Spinner

    private lateinit var nameText: TextView
    private lateinit var roleText: TextView
    private lateinit var teamText: TextView
    private lateinit var positionText: TextView

    private lateinit var killsText: TextView
    private lateinit var deathsText: TextView
    private lateinit var assistsText: TextView
    private lateinit var kpgText: TextView
    private lateinit var dpgText: TextView
    private lateinit var apgText: TextView

    private lateinit var playerImage: ImageView
    private lateinit var selectedPlayerName: String

    private var selectedYear: String = "2022"
    private var selectedTournament: String = "Demacia Cup"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_detail)

        selectedPlayerName = intent.getStringExtra("playerName") ?: return

        // 绑定视图组件
        spinnerYear = findViewById(R.id.spinnerYear)
        spinnerTournament = findViewById(R.id.spinnerTournament)
        nameText = findViewById(R.id.txtPlayerName)
        roleText = findViewById(R.id.txtPlayerRole)
        teamText = findViewById(R.id.txtTeam)
        positionText = findViewById(R.id.txtPosition)

        killsText = findViewById(R.id.txtKills)
        deathsText = findViewById(R.id.txtDeaths)
        assistsText = findViewById(R.id.txtAssists)
        kpgText = findViewById(R.id.txtKPG)
        dpgText = findViewById(R.id.txtDPG)
        apgText = findViewById(R.id.txtAPG)

        playerImage = findViewById(R.id.playerImage)
        val editIcon = findViewById<ImageView>(R.id.imgEdit)
        val backButton = findViewById<ImageView>(R.id.imgBack)

        nameText.text = selectedPlayerName
        playerImage.setImageResource(getPlayerAvatar(selectedPlayerName))

        // 编辑按钮
        editIcon.setOnClickListener {
            val intent = Intent(this, EditPlayerActivity::class.java)
            intent.putExtra("playerName", selectedPlayerName)
            startActivity(intent)
        }

        backButton.setOnClickListener {
            finish()
        }

        // Spinner监听
        spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                selectedYear = parent?.getItemAtPosition(pos).toString()
                loadPlayerStats()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerTournament.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                selectedTournament = parent?.getItemAtPosition(pos).toString()
                loadPlayerStats()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // 初始化展示一次
        loadPlayerStats()
    }

    private fun loadPlayerStats() {
        db.collection("players")
            .document(selectedYear)
            .collection(selectedTournament)
            .document(selectedPlayerName.lowercase())
            .get()
            .addOnSuccessListener {
                val team = it.getString("team") ?: "Unknown Team"
                val position = it.getString("position") ?: "Unknown Role"
                val kills = it.getLong("kills") ?: 0
                val deaths = it.getLong("deaths") ?: 0
                val assists = it.getLong("assists") ?: 0
                val kpg = it.getDouble("kpg") ?: 0.0
                val dpg = it.getDouble("dpg") ?: 0.0
                val apg = it.getDouble("apg") ?: 0.0

                // 设置界面文字
                teamText.text = "Team: $team"
                positionText.text = "Role: $position"
                roleText.text = "$team / $position"
                killsText.text = "Kills: $kills"
                deathsText.text = "Deaths: $deaths"
                assistsText.text = "Assists: $assists"
                kpgText.text = "KPG: ${format(kpg)}"
                dpgText.text = "DPG: ${format(dpg)}"
                apgText.text = "APG: ${format(apg)}"
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load player data", Toast.LENGTH_SHORT).show()
                teamText.text = "Team: -"
                positionText.text = "Role: -"
                killsText.text = "Kills: -"
                deathsText.text = "Deaths: -"
                assistsText.text = "Assists: -"
                kpgText.text = "KPG: -"
                dpgText.text = "DPG: -"
                apgText.text = "APG: -"
            }
    }

    private fun format(value: Double): String {
        return String.format("%.1f", value)
    }

    private fun getPlayerAvatar(name: String): Int = when (name.lowercase()) {
        "theshy" -> R.drawable.theshy_avatar
        "meiko" -> R.drawable.meiko_avatar
        "gala" -> R.drawable.gala_avatar
        "jiejie" -> R.drawable.jiejie_avatar
        "rookie" -> R.drawable.rookie_avatar
        "keria" -> R.drawable.keria_avatar
        "faker" -> R.drawable.faker_avatar
        "oner" -> R.drawable.oner_avatar
        "zeus" -> R.drawable.zeus_avatar
        "gumayusi" -> R.drawable.gumayusi_avatar
        else -> R.drawable.ic_team
    }
}
