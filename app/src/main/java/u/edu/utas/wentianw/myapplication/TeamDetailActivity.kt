package u.edu.utas.wentianw.myapplication

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class TeamDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_detail)

        val teamName = intent.getStringExtra("teamName") ?: "IG"
        title = "$teamName Performance"

        val kdaContainer = findViewById<LinearLayout>(R.id.kdaContainer)

        // 假数据
        val data = listOf(
            Triple("Q1", 12.6, 10.5),
            Triple("Q2", 10.6, 7.5),
            Triple("Q3", 7.5, 13.2)
        )

        // 加载每个季度的数据
        for ((quarter, igScore, rngScore) in data) {
            val textView = TextView(this)
            textView.text = "$teamName $quarter: $igScore  vs  RNG: $rngScore"
            textView.textSize = 16f
            kdaContainer.addView(textView)
        }
    }
}
