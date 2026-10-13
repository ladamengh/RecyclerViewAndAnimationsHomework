package ru.otus.cryptosample.coins.feature.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.otus.cryptosample.R
import ru.otus.cryptosample.coins.feature.CoinState
import ru.otus.cryptosample.coins.feature.adapter.holders.CoinViewHolder
import ru.otus.cryptosample.databinding.ItemCoinBinding

internal const val COIN_HIGHLIGHT_PAYLOAD = "highlight_changed"

private object CoinDiffCallback : DiffUtil.ItemCallback<CoinState>() {
    override fun areItemsTheSame(oldItem: CoinState, newItem: CoinState): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CoinState, newItem: CoinState): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: CoinState, newItem: CoinState): Any? {
        return if (oldItem.copy(highlight = newItem.highlight) == newItem) {
            COIN_HIGHLIGHT_PAYLOAD
        } else {
            null
        }
    }
}

class CoinItemsAdapter(
    private val horizontal: Boolean,
) : ListAdapter<CoinState, CoinViewHolder>(CoinDiffCallback) {

    override fun getItemViewType(position: Int): Int = CoinsAdapter.VIEW_TYPE_COIN

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinViewHolder {
        val holder = CoinViewHolder(
            ItemCoinBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

        if (horizontal) {
            val margin = parent.resources.getDimensionPixelSize(R.dimen.horizontal_coin_spacing)
            holder.itemView.layoutParams = RecyclerView.LayoutParams(
                parent.resources.getDimensionPixelSize(R.dimen.horizontal_coin_width),
                parent.resources.getDimensionPixelSize(R.dimen.horizontal_coin_height),
            ).apply {
                setMargins(margin, margin, margin, margin)
            }
        }

        return holder
    }

    override fun onBindViewHolder(holder: CoinViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: CoinViewHolder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        val coin = getItem(position)
        if (payloads.contains(COIN_HIGHLIGHT_PAYLOAD)) {
            holder.bindHighlight(coin.highlight)
        } else {
            holder.bind(coin)
        }
    }

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = getItem(position).id.hashCode().toLong()
}
