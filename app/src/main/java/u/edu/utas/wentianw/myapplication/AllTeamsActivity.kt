package u.edu.utas.wentianw.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


import android.widget.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlin.jvm.java


class AllTeamsActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private lateinit var teamListLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_teams)

        teamListLayout = findViewById(R.id.teamListLayout)
        loadTeams()

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_teams
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_teams -> true
                R.id.nav_matches -> {
                    startActivity(Intent(this, AllMatchesActivity::class.java))
                    true
                }
                R.id.nav_me -> {
                    startActivity(Intent(this, LiveMatchActivity::class.java))
                    true
                }
                else -> false
            }
        }

    }

    private fun loadTeams() {
        db.collection("teams").get().addOnSuccessListener { result ->
            for (doc in result) {
                val teamName = doc.id
                val bannerId = getTeamBannerResId(teamName)

                val teamView = layoutInflater.inflate(R.layout.team_card_view, teamListLayout, false)
                val teamImage = teamView.findViewById<ImageView>(R.id.teamBanner)
                val teamText = teamView.findViewById<TextView>(R.id.teamName)

                teamImage.setImageResource(bannerId)
                teamText.text = teamName

                teamView.setOnClickListener {
                    val intent = Intent(this, TeamDetailActivity::class.java)
                    intent.putExtra("teamName", teamName)
                    startActivity(intent)
                }

                teamListLayout.addView(teamView)
            }
        }
    }

    private fun getTeamBannerResId(name: String): Int = when (name.lowercase()) {
        "rng" -> R.drawable.rng
        "edg" -> R.drawable.edg
        "we" -> R.drawable.we
        "lgd" -> R.drawable.lgd
        "ig" -> R.drawable.ig
        else -> R.drawable.ic_team
    }


}
