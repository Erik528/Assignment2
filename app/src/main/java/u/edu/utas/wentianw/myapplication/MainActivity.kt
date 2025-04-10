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
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 检查是否首次登录，显示 banner
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

        // 设置 RecyclerView 显示玩家卡片
        val players = listOf(
            Player("xiye", "F/A MID", 824556),
            Player("TheShy", "IG / TOP", 572364),
            Player("Scout", "JDG / MID", 235565)
        )

        val recyclerView = findViewById<RecyclerView>(R.id.playerRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = PlayerAdapter(players)

        // 设置队伍按钮和跳转逻辑
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

        // Firebase debug
        val db = Firebase.firestore
        Log.d(FIREBASE_TAG, "Firebase connected: ${db.app.name}")

        // 底部导航栏逻辑
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
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


        }

}
