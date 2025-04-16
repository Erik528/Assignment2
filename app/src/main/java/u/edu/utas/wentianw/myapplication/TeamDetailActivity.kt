package u.edu.utas.wentianw.myapplication

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class TeamDetailActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var currentTeam: String
    private lateinit var yearSpinner: Spinner
    private lateinit var opponentSpinner: Spinner
    private lateinit var statsTable: TableLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_detail)

        db = FirebaseFirestore.getInstance()
        currentTeam = intent.getStringExtra("teamName") ?: "RNG"
        title = "$currentTeam Performance"

        yearSpinner = findViewById(R.id.yearSpinner)
        opponentSpinner = findViewById(R.id.opponentSpinner)
        statsTable = findViewById(R.id.statsTable)

        setupYearSpinner()
        setupOpponentSpinner()
        setupSpinnerListeners()


        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }


        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_teams
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


    private fun setupYearSpinner() {
        val years = arrayOf("2024", "2023", "2022")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinner.adapter = adapter
    }

    private fun setupOpponentSpinner() {
        db.collection("teams")
            .get()
            .addOnSuccessListener { documents ->
                val teams = documents.map { it.id }.filter { it != currentTeam }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, teams)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                opponentSpinner.adapter = adapter
            }
    }

    private fun setupSpinnerListeners() {
        val listener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                loadComparisonData()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        yearSpinner.onItemSelectedListener = listener
        opponentSpinner.onItemSelectedListener = listener
    }

    private fun createHeaderCell(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 16f
            setTypeface(null, Typeface.BOLD)
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

    private fun loadComparisonData() {
        val selectedYear = yearSpinner.selectedItem?.toString() ?: return
        val opponent = opponentSpinner.selectedItem?.toString() ?: return

        statsTable.removeAllViews()

        val headerRow = TableRow(this).apply {
            addView(createHeaderCell("Quarter"))
            addView(createHeaderCell("KDA"))
            addView(createHeaderCell("MVP"))
            addView(createHeaderCell("Players"))
        }
        statsTable.addView(headerRow)

        val currentDocId = "$selectedYear-$currentTeam"
        val opponentDocId = "$selectedYear-$opponent"

        db.collection("team_stats").document(currentDocId).get().addOnSuccessListener { currentDoc ->
            db.collection("team_stats").document(opponentDocId).get().addOnSuccessListener { opponentDoc ->
                listOf("Q1", "Q2", "Q3", "Q4").forEach { quarter ->
                    val currentData = currentDoc.get(quarter) as? Map<*, *>
                    val opponentData = opponentDoc.get(quarter) as? Map<*, *>

                    val row = TableRow(this).apply {
                        addView(createCell(quarter))
                        addView(createCell("${currentData?.get("kda") ?: "-"} vs ${opponentData?.get("kda") ?: "-"}"))
                        addView(createCell("${currentData?.get("mvp") ?: "-"} vs ${opponentData?.get("mvp") ?: "-"}"))
                        addView(createCell("${currentData?.get("players") ?: "-"} vs ${opponentData?.get("players") ?: "-"}"))
                    }
                    statsTable.addView(row)
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to load opponent data", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load current team data", Toast.LENGTH_SHORT).show()
        }
    }
}