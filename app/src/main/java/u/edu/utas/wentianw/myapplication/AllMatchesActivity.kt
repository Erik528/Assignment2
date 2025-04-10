package u.edu.utas.wentianw.myapplication
import android.content.Intent
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_matches)

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
            for (doc in result) {
                val tournament = doc.getString("tournament") ?: ""
                val teamA = doc.getString("teamA") ?: "Team A"
                val teamB = doc.getString("teamB") ?: "Team B"
                val status = doc.getString("status") ?: "unknown"
                val playersA = doc.get("playersA") as? List<String> ?: listOf()
                val playersB = doc.get("playersB") as? List<String> ?: listOf()

                val matchView = TextView(this)
                matchView.textSize = 16f
                matchView.setPadding(0, 12, 0, 12)
                matchView.text = """
                $tournament
                $teamA (${playersA.joinToString(", ")})
                vs
                $teamB (${playersB.joinToString(", ")})
                Status: $status
            """.trimIndent()

                matchListLayout.addView(matchView)
            }
        }
    }

}