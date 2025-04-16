package u.edu.utas.wentianw.myapplication
import u.edu.utas.wentianw.myapplication.PlayerAdapter
import android.util.Log
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import au.edu.utas.kit305.tutorial05.CreateMatchActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.content.SharedPreferences
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.os.Build
import android.view.WindowInsetsController
import android.view.WindowManager
import android.graphics.Color
import com.google.firebase.firestore.FirebaseFirestore

const val FIREBASE_TAG = "FirebaseLogging"

class MainActivity : AppCompatActivity() {
    data class Player(val name: String, val role: String, val likes: Int)

    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPrefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val isFirstLogin = sharedPrefs.getBoolean("first_login", true)
        val banner = findViewById<ImageView>(R.id.videoBanner)

        if (isFirstLogin) {
            banner.visibility = View.VISIBLE
//            banner.visibility = View.VISIBLE
//            sharedPrefs.edit().putBoolean("first_login", false).apply()
        } else {
            banner.visibility = View.VISIBLE
        }

        val players = listOf(
            Player("TheShy", "IG / TOP", 879134),
            Player("Rookie", "IG / MID", 752120),
            Player("Meiko", "IG / SUP", 692341),
            Player("Jiejie", "IG / JUNGLE", 633220),
            Player("Gala", "IG / ADC", 612888),

            Player("Keria", "T1 / SUP", 903211),
            Player("Faker", "T1 / MID", 1599999),
            Player("Oner", "T1 / JUNGLE", 778421),
            Player("Zeus", "T1 / TOP", 842777),
            Player("Gumayusi", "T1 / ADC", 755521)
        )

        val recyclerView = findViewById<RecyclerView>(R.id.playerRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = PlayerAdapter(players)

        val teamContainer = findViewById<LinearLayout>(R.id.teamContainer)
        val teams = listOf("RNG", "EDG", "WE", "LGD","IG")
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

//        // Firebase debug
//        val db = Firebase.firestore
//        Log.d(FIREBASE_TAG, "Firebase connected: ${db.app.name}")


        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true

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
//    fun insertStructuredPlayerData() {
//        val db = FirebaseFirestore.getInstance()
//
//        val tournaments = listOf("Demacia Cup", "LPL Spring", "LPL Summer", "MSI", "Worlds")
//        val years = listOf("2022", "2023", "2024")
//
//        val players = listOf(
//            mapOf("name" to "theshy", "team" to "IG", "position" to "TOP", "kills" to 321, "deaths" to 120, "assists" to 510, "kpg" to 4.5, "dpg" to 1.8, "apg" to 7.1),
//            mapOf("name" to "rookie", "team" to "IG", "position" to "MID", "kills" to 289, "deaths" to 100, "assists" to 440, "kpg" to 4.0, "dpg" to 1.6, "apg" to 6.0),
//            mapOf("name" to "meiko", "team" to "IG", "position" to "SUPPORT", "kills" to 123, "deaths" to 90, "assists" to 830, "kpg" to 1.2, "dpg" to 1.0, "apg" to 8.3),
//            mapOf("name" to "jiejie", "team" to "IG", "position" to "JUNGLE", "kills" to 205, "deaths" to 85, "assists" to 390, "kpg" to 3.5, "dpg" to 1.2, "apg" to 6.4),
//            mapOf("name" to "gala", "team" to "IG", "position" to "ADC", "kills" to 377, "deaths" to 130, "assists" to 480, "kpg" to 5.0, "dpg" to 2.0, "apg" to 6.5),
//            mapOf("name" to "faker", "team" to "T1", "position" to "MID", "kills" to 500, "deaths" to 150, "assists" to 700, "kpg" to 6.0, "dpg" to 1.5, "apg" to 7.7),
//            mapOf("name" to "keria", "team" to "T1", "position" to "SUPPORT", "kills" to 110, "deaths" to 95, "assists" to 880, "kpg" to 1.1, "dpg" to 1.2, "apg" to 8.8),
//            mapOf("name" to "oner", "team" to "T1", "position" to "JUNGLE", "kills" to 220, "deaths" to 100, "assists" to 420, "kpg" to 3.8, "dpg" to 1.4, "apg" to 6.5),
//            mapOf("name" to "zeus", "team" to "T1", "position" to "TOP", "kills" to 300, "deaths" to 110, "assists" to 450, "kpg" to 4.2, "dpg" to 1.6, "apg" to 6.7),
//            mapOf("name" to "gumayusi", "team" to "T1", "position" to "ADC", "kills" to 410, "deaths" to 140, "assists" to 510, "kpg" to 5.2, "dpg" to 1.9, "apg" to 6.3)
//        )
//
//        for (year in years) {
//            for (tournament in tournaments) {
//                for (player in players) {
//                    val playerName = player["name"] as String
//                    db.collection("players")
//                        .document(year)
//                        .collection(tournament)
//                        .document(playerName)
//                        .set(player)
//                        .addOnSuccessListener {
//                            println("✅ Inserted $playerName in $year/$tournament")
//                        }
//                        .addOnFailureListener {
//                            println("❌ Failed to insert $playerName: ${it.message}")
//                        }
//                }
//            }
//        }
    }



