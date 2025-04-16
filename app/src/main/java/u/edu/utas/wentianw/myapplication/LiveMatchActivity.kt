package u.edu.utas.wentianw.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import au.edu.utas.kit305.tutorial05.CreateMatchActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class LiveMatchActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var tblLiveData: TableLayout
    private var currentMatchId: String = "worlds2024_final" // 示例比赛ID

    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_match)

        db = FirebaseFirestore.getInstance()
        tblLiveData = findViewById(R.id.tblLiveData)
        val btnRefresh = findViewById<Button>(R.id.btnRefresh)

        setupTabButtons()

        loadLiveData()

        btnRefresh.setOnClickListener {
            tblLiveData.removeAllViews()
            loadLiveData()
        }


        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_me
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_teams -> {
                    startActivity(Intent(this, AllTeamsActivity::class.java))
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
    }

    private fun setupTabButtons() {
        val btnLiveboard = findViewById<Button>(R.id.btnLiveboard)
        val btnPlayertrack = findViewById<Button>(R.id.btnPlayertrack)

        btnLiveboard.setOnClickListener { showLiveboard() }
        btnPlayertrack.setOnClickListener { showPlayertrack() }
        showLiveboard()
    }

    private fun showLiveboard() {
        loadLiveData()
    }

    private fun showPlayertrack() {
        val intent = Intent(this, PlayerTrackActivity::class.java).apply {
            putExtra("MATCH_ID", currentMatchId)
        }
        startActivity(intent)
    }

    private fun loadLiveData() {
        db.collection("live_matches").document(currentMatchId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this, "实时数据加载失败", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                snapshot?.let { doc ->
                    tblLiveData.removeAllViews()

                    val headerRow = TableRow(this).apply {
                        addView(createHeaderCell("Stat"))
                        addView(createHeaderCell("Team A"))
                        addView(createHeaderCell("Team B"))
                    }
                    tblLiveData.addView(headerRow)

                    listOf(
                        Triple("Gold", "gold", "gold"),
                        Triple("Kills", "kills", "kills"),
                        Triple("Deaths", "deaths", "deaths"),
                        Triple("Assists", "assists", "assists"),
                        Triple("Towers", "towers", "towers")
                    ).forEach { (label, fieldA, fieldB) ->
                        val row = TableRow(this).apply {
                            addView(createCell(label))
                            addView(createCell(doc.getString("teamA.$fieldA") ?: "-"))
                            addView(createCell(doc.getString("teamB.$fieldB") ?: "-"))
                        }
                        tblLiveData.addView(row)
                    }
                }
            }
    }

    private fun createHeaderCell(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(8, 8, 8, 8)
        }
    }

    private fun createCell(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 14f
            setPadding(8, 8, 8, 8)
        }
    }
}