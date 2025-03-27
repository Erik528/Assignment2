package u.edu.utas.wentianw.myapplication

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class PlayerAdapter(private val players: List<MainActivity.Player>) :
    RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    class PlayerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.playerImage)
        val name: TextView = view.findViewById(R.id.playerName)
        val role: TextView = view.findViewById(R.id.playerRole)
        val likes: TextView = view.findViewById(R.id.playerLikes)
        val fav: ImageView = view.findViewById(R.id.favIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.player_card, parent, false)
        return PlayerViewHolder(view)
    }

    override fun getItemCount() = players.size

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = players[position]
        holder.name.text = player.name
        holder.role.text = player.role
        holder.likes.text = player.likes.toString()
        holder.image.setImageResource(R.drawable.player_default) // 可根据需求替换

        // ✅ 设置点击事件，跳转到 PlayerDetailActivity
        val context = holder.itemView.context
        val clickListener = View.OnClickListener {
            val intent = Intent(context, PlayerDetailActivity::class.java)
            intent.putExtra("playerName", player.name)
            context.startActivity(intent)
        }

        holder.image.setOnClickListener(clickListener)
        holder.name.setOnClickListener(clickListener)
    }
}
