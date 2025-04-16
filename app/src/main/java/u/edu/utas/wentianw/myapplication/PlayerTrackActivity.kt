package u.edu.utas.wentianw.myapplication
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class PlayerTrackActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var currentMatchId: String
    private var selectedTeam = "teamA"
    private var selectedPlayerId: String? = null
    private var selectedSkill: String? = null

    private lateinit var etKills: EditText
    private lateinit var etDeaths: EditText
    private lateinit var etAssists: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_track)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        etKills = findViewById(R.id.etKills)
        etDeaths = findViewById(R.id.etDeaths)
        etAssists = findViewById(R.id.etAssists)

        db = FirebaseFirestore.getInstance()
        currentMatchId = intent.getStringExtra("MATCH_ID") ?: "worlds2024_final"

        setupTeamSelection()
        setupSkillSelection()
        setupSaveButton()
    }

    private fun setupTeamSelection() {
        val rgTeam = findViewById<RadioGroup>(R.id.rgTeam)
        rgTeam.setOnCheckedChangeListener { _, checkedId ->
            selectedTeam = when (checkedId) {
                R.id.rbTeamA -> "teamA"
                R.id.rbTeamB -> "teamB"
                else -> "teamA"
            }
            loadPlayers()
        }
        rgTeam.check(R.id.rbTeamA)
    }

    private fun loadPlayers() {
        val llPlayers = findViewById<LinearLayout>(R.id.llPlayers)
        llPlayers.removeAllViews()

        db.collection("live_matches").document(currentMatchId)
            .get()
            .addOnSuccessListener { document ->
                val team = document.get(selectedTeam) as? Map<*, *>
                val players = team?.get("players") as? Map<*, *>

                players?.keys?.forEach { playerId ->
                    val player = players?.get(playerId) as? Map<*, *> ?: return@forEach
                    val imageButton = ImageButton(this).apply {
                        layoutParams = LinearLayout.LayoutParams(150, 150).apply {
                            marginEnd = 16
                        }
                        val playerName = player["name"] as? String ?: "Unknown Player"
                        setImageResource(getPlayerAvatar(playerName))
                        scaleType = ImageView.ScaleType.FIT_CENTER
                        background = null // 移除默认按钮背景
                        setPadding(8, 8, 8, 8)
                        setOnClickListener {
                            selectPlayer(playerId as String, player)
                        }
                    }
                    llPlayers.addView(imageButton)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "player loading failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun selectPlayer(playerId: String, playerData: Map<*, *>) {
        selectedPlayerId = playerId
        findViewById<TextView>(R.id.tvSelectedPlayer).text =
            "Selected: ${playerData["name"]} (${playerData["kills"]}/${playerData["deaths"]}/${playerData["assists"]})"

        etKills.setText(playerData["kills"]?.toString() ?: "0")
        etDeaths.setText(playerData["deaths"]?.toString() ?: "0")
        etAssists.setText(playerData["assists"]?.toString() ?: "0")
    }

    private fun setupSkillSelection() {
        findViewById<RadioGroup>(R.id.rgSkills).setOnCheckedChangeListener { _, checkedId ->
            selectedSkill = when (checkedId) {
                R.id.rbQ -> "Q"
                R.id.rbW -> "W"
                R.id.rbE -> "E"
                R.id.rbR -> "R"
                else -> null
            }
        }
    }

    private fun setupSaveButton() {
        findViewById<Button>(R.id.btnSave).setOnClickListener {
            if (selectedPlayerId == null || selectedSkill == null) {
                Toast.makeText(this, "please select skill", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 安全转换输入值
            val kills = etKills.text.toString().toIntOrNull() ?: 0
            val deaths = etDeaths.text.toString().toIntOrNull() ?: 0
            val assists = etAssists.text.toString().toIntOrNull() ?: 0

            val updates = hashMapOf<String, Any>(
                "$selectedTeam.players.$selectedPlayerId.kills" to kills,
                "$selectedTeam.players.$selectedPlayerId.deaths" to deaths,
                "$selectedTeam.players.$selectedPlayerId.assists" to assists,
                "$selectedTeam.players.$selectedPlayerId.last_skill" to selectedSkill!!,
                "$selectedTeam.players.$selectedPlayerId.update_time" to FieldValue.serverTimestamp()
            )

            db.collection("live_matches").document(currentMatchId)
                .update(updates)
                .addOnSuccessListener {
                    Toast.makeText(this, "update successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "update failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
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