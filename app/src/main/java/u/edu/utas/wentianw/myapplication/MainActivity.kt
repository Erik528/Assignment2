package u.edu.utas.wentianw.myapplication

import android.util.Log
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import au.edu.utas.kit305.tutorial05.CreateMatchActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


const val FIREBASE_TAG = "FirebaseLogging"

class MainActivity : AppCompatActivity() {
    data class Player(val name: String, val role: String, val likes: Int)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. 初始化队伍logo
        val teamContainer = findViewById<LinearLayout>(R.id.teamContainer)
        val teams = listOf("RNG", "EDG", "WE", "LGD")
        for (team in teams) {
            val btn = Button(this)
            btn.text = team
            btn.setOnClickListener {
                val intent = Intent(this, TeamDetailActivity::class.java)
                intent.putExtra("teamName", team)
                startActivity(intent)
            }
            teamContainer.addView(btn)
        }

        // 2. 设置热门选手列表
        val players = listOf(
            Player("xiye", "F/A MID", 824556),
            Player("TheShy", "IG / TOP", 572364),
            Player("Scout", "JDG / MID", 235565)
        )

        val playerList = findViewById<ListView>(R.id.playerList)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, players.map { "${it.name} - ${it.role} (${it.likes})" })
        playerList.adapter = adapter
        playerList.setOnItemClickListener { _, _, position, _ ->
            val selectedPlayer = players[position]
            val intent = Intent(this, PlayerInfoActivity::class.java)
            intent.putExtra("playerName", selectedPlayer.name)
            startActivity(intent)
        }

        //get db connection
        val db = Firebase.firestore
        Log.d("FIREBASE", "Firebase connected: ${db.app.name}")


//        // 3. 底部导航跳转
//        findViewById<Button>(R.id.navHome).setOnClickListener {
//            Toast.makeText(this, "Already at Home", Toast.LENGTH_SHORT).show()
//        }
//
//        findViewById<Button>(R.id.navTeams).setOnClickListener {
//            val intent = Intent(this, TeamDetailActivity::class.java)
//            startActivity(intent)
//        }


        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Already at Home", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this, "Me clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }
}
