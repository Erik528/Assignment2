package u.edu.utas.wentianw.myapplication
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import au.edu.utas.kit305.tutorial05.CreateMatchActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.jvm.java


class AllMatchesActivity : AppCompatActivity() {
    private lateinit var matchListLayout: LinearLayout
    private lateinit var btnCreateMatch: Button
    private val db = Firebase.firestore
    private val matches = mutableListOf<Pair<String, MatchData>>()

    data class MatchData(
        val tournament: String,
        val teamA: String,
        val teamB: String,
        val status: String,
        val playersA: List<String>,
        val playersB: List<String>,
        val scoreA: Map<String, String> = mapOf(),
        val scoreB: Map<String, String> = mapOf(),
        val winner: String = ""
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_matches)

        val btnShare = findViewById<Button>(R.id.btnShare)
        btnShare.setOnClickListener {
            shareMatches()
        }

        matchListLayout = findViewById(R.id.matchListLayout)
        btnCreateMatch = findViewById(R.id.btnCreateMatch)

        btnCreateMatch.setOnClickListener {
            startActivity(Intent(this, CreateMatchActivity::class.java))
        }

        loadMatches()

        // 底部导航栏逻辑
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_matches
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_teams -> {
                    startActivity(Intent(this, TeamDetailActivity::class.java))
                    true
                }
                R.id.nav_matches -> {
                    startActivity(Intent(this, AllMatchesActivity::class.java))
                    true
                }
                R.id.nav_me -> {
                    val intent = Intent(this, LiveMatchActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        // 返回按钮功能
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    private fun loadMatches() {
        db.collection("matches").get().addOnSuccessListener { result ->
            matchListLayout.removeAllViews()
            matches.clear()

            for (doc in result) {
                val matchId = doc.id
                // 解析数据并保存到列表
                val matchData = MatchData(
                    tournament = doc.getString("tournament") ?: "",
                    teamA = doc.getString("teamA") ?: "Team A",
                    teamB = doc.getString("teamB") ?: "Team B",
                    status = doc.getString("status") ?: "unknown",
                    playersA = doc.get("playersA") as? List<String> ?: listOf(),
                    playersB = doc.get("playersB") as? List<String> ?: listOf(),
                    scoreA = doc.get("scoreA") as? Map<String, String> ?: mapOf(),
                    scoreB = doc.get("scoreB") as? Map<String, String> ?: mapOf(),
                    winner = doc.getString("winner") ?: ""
                )
                matches.add(matchId to matchData)

                // 创建带胜负标记的视图
                val matchView = TextView(this).apply {
                    textSize = 16f
                    setPadding(0, 12, 0, 12)
                    text = buildMatchText(matchData)
                }

                val btnEnd = Button(this).apply {
                    text = "End Match"
                    isEnabled = matchData.status != "ended"
                    setOnClickListener {
                        db.collection("matches").document(matchId)
                            .update("status", "ended")
                            .addOnSuccessListener {
                                Toast.makeText(this@AllMatchesActivity, "Match ended successfully", Toast.LENGTH_SHORT).show()
                                loadMatches()
                            }
                    }
                }
                matchListLayout.addView(matchView)
                matchListLayout.addView(btnEnd)
            }
        }
    }

    private fun buildMatchText(data: MatchData): Spanned {
        val winnerLine = if (data.winner == "teamA") {
            "Winner: <b>${data.teamA}</b>"
        } else if (data.winner == "teamB") {
            "Winner: <b>${data.teamB}</b>"
        } else {
            "Winner: In progress"
        }

        val scoreLine = if (data.status == "ended") {
            """
        Final Score:<br>
        ${data.teamA}: ${data.scoreA["goals"] ?: "0"} . ${data.scoreA["behinds"] ?: "0"} (${data.scoreA["total"] ?: "0"})<br>
        ${data.teamB}: ${data.scoreB["goals"] ?: "0"} . ${data.scoreB["behinds"] ?: "0"} (${data.scoreB["total"] ?: "0"})<br>
        """.trimIndent()
        } else ""

        return Html.fromHtml("""
        ${data.tournament}<br>
        ${data.teamA} (${data.playersA.joinToString(", ")})<br>
        vs<br>
        ${data.teamB} (${data.playersB.joinToString(", ")})<br>
        Status: ${data.status.capitalize()}<br>
        $scoreLine
        $winnerLine<hr>
    """.trimIndent(), Html.FROM_HTML_MODE_LEGACY)
    }

    private fun shareMatches() {
        val shareText = buildString {
            append("All current match data:\n\n")
            matches.forEach { (_, match) ->
                append("${match.tournament}\n")
                append("${match.teamA} (${match.playersA.joinToString(", ")})\n")
                append("vs\n")
                append("${match.teamB} (${match.playersB.joinToString(", ")})\n")

                // 加上比分展示（可选）
                if (match.status == "ended") {
                    append("Final Score:\n")
                    append("${match.teamA}: ${match.scoreA}\n")
                    append("${match.teamB}: ${match.scoreB}\n")
                }

                val winner = when (match.status) {
                    "teamA_win" -> "**${match.teamA}**"
                    "teamB_win" -> "**${match.teamB}**"
                    "ended" -> "Match ended"
                    else -> "In progress"
                }

                append("Status: $winner\n")
                append("-------------------------\n")
            }
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_SUBJECT, "Match data sharing")
        }
        startActivity(Intent.createChooser(shareIntent, "Match data sharing"))
    }
}
