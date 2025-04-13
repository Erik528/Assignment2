package au.edu.utas.kit305.tutorial05

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import u.edu.utas.wentianw.myapplication.LiveMatchActivity
import u.edu.utas.wentianw.myapplication.MainActivity
import u.edu.utas.wentianw.myapplication.R
import u.edu.utas.wentianw.myapplication.TeamDetailActivity

class CreateMatchActivity : AppCompatActivity() {

    private lateinit var editTeamNameA: EditText
    private lateinit var editTeamNameB: EditText
    private lateinit var btnAddPlayerA: Button
    private lateinit var btnAddPlayerB: Button
    private lateinit var playerListA: LinearLayout
    private lateinit var playerListB: LinearLayout
    private lateinit var tournamentSpinner: Spinner
    private lateinit var btnStartMatch: Button
    private lateinit var btnEndMatch: Button

    private val playersA = mutableListOf<String>()
    private val playersB = mutableListOf<String>()
    private val db = Firebase.firestore
    val matchRef = db.collection("matches").document()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_match)

        // 初始化视图
        editTeamNameA = findViewById(R.id.editTeamNameA)
        editTeamNameB = findViewById(R.id.editTeamNameB)
        btnAddPlayerA = findViewById(R.id.btnAddPlayerA)
        btnAddPlayerB = findViewById(R.id.btnAddPlayerB)
        playerListA = findViewById(R.id.playerListA)
        playerListB = findViewById(R.id.playerListB)
        tournamentSpinner = findViewById(R.id.tournamentSpinner)
        btnStartMatch = findViewById(R.id.btnStartMatch)
        btnEndMatch = findViewById(R.id.btnEndMatch)

        // 设置比赛名称下拉菜单
        val tournaments = listOf("2024 Demacia Cup", "2023 Demacia Cup")
        tournamentSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tournaments)

        // 添加队员按钮
        btnAddPlayerA.setOnClickListener {
            showAddPlayerDialog(playersA, playerListA)
        }

        btnAddPlayerB.setOnClickListener {
            showAddPlayerDialog(playersB, playerListB)
        }

        // 开始比赛
        btnStartMatch.setOnClickListener {
            val teamAName = editTeamNameA.text.toString().trim()
            val teamBName = editTeamNameB.text.toString().trim()

            if (teamAName.isEmpty() || teamBName.isEmpty()) {
                Toast.makeText(this, "Please enter both team names", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (playersA.size < 2 || playersB.size < 2) {
                Toast.makeText(this, "Each team must have at least two players", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val matchData = hashMapOf(
                "id" to matchRef.id,
                "tournament" to tournamentSpinner.selectedItem.toString(),
                "teamA" to teamAName,
                "teamB" to teamBName,
                "playersA" to playersA,
                "playersB" to playersB,
                "status" to "ongoing"
            )

            matchRef.set(matchData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Match created successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to create match", Toast.LENGTH_SHORT).show()
                }
        }

        // 结束比赛
        btnEndMatch.setOnClickListener {
            Toast.makeText(this, "Match ended successfully", Toast.LENGTH_SHORT).show()
            finish()
        }

        // 返回按钮功能
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

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
                    startActivity(Intent(this, CreateMatchActivity::class.java))
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

    // 添加成员对话框
    private fun showAddPlayerDialog(playerList: MutableList<String>, layout: LinearLayout) {
        val input = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("Add Player")
            .setView(input)
            .setPositiveButton("add") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    playerList.add(name)
                    refreshPlayerListUI(playerList, layout)
                }
            }
            .setNegativeButton("cancel", null)
            .show()
    }

    // 刷新成员显示列表
    private fun refreshPlayerListUI(players: MutableList<String>, layout: LinearLayout) {
        layout.removeAllViews()
        players.forEachIndexed { index, name ->
            val row = LinearLayout(this)
            row.orientation = LinearLayout.HORIZONTAL
            row.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            row.setPadding(0, 8, 0, 8)

            val text = TextView(this)
            text.text = name
            text.textSize = 16f
            text.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

            val deleteBtn = Button(this)
            deleteBtn.text = "×"
            deleteBtn.setOnClickListener {
                players.removeAt(index)
                refreshPlayerListUI(players, layout)
            }

            row.addView(text)
            row.addView(deleteBtn)
            layout.addView(row)
        }
    }
}
