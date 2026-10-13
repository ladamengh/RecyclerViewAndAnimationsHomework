package ru.otus.cryptosample.coins.feature.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.otus.cryptosample.coins.feature.CoinCategoryState
import ru.otus.cryptosample.coins.feature.adapter.holders.CategoryCarouselViewHolder
import ru.otus.cryptosample.coins.feature.adapter.holders.CategoryHeaderViewHolder
import ru.otus.cryptosample.coins.feature.adapter.holders.CoinViewHolder
import ru.otus.cryptosample.coins.feature.adapter.items.CoinsAdapterItem
import ru.otus.cryptosample.databinding.ItemCategoryCarouselBinding
import ru.otus.cryptosample.databinding.ItemCategoryHeaderBinding
import ru.otus.cryptosample.databinding.ItemCoinBinding

class CoinsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_CATEGORY = 0
        const val VIEW_TYPE_COIN = 1
        const val VIEW_TYPE_CATEGORY_CAROUSEL = 2
        private const val MAX_COINS_IN_HORIZONTAL_LIST = 10
    }

    private val sharedViewPool = RecyclerView.RecycledViewPool()
    private var items: List<CoinsAdapterItem> = emptyList()

    init {
        setHasStableIds(true)
        sharedViewPool.setMaxRecycledViews(VIEW_TYPE_COIN, 30)
    }

    fun setData(categories: List<CoinCategoryState>, showAll: Boolean) {
        val adapterItems = buildList<CoinsAdapterItem> {
            categories.forEach { category ->
                if (showAll && category.coins.size > MAX_COINS_IN_HORIZONTAL_LIST) {
                    add(
                        CoinsAdapterItem.CategoryCarousel(
                            categoryId = category.id,
                            categoryName = category.name,
                            coins = category.coins,
                        )
                    )
                } else {
                    add(
                        CoinsAdapterItem.CategoryHeader(
                            categoryId = category.id,
                            categoryName = category.name,
                        )
                    )
                    category.coins.forEach { coin ->
                        add(CoinsAdapterItem.CoinItem(coin))
                    }
                }
            }
        }
        val oldItems = items
        val diffResult = DiffUtil.calculateDiff(ItemDiffCallback(oldItems, adapterItems))
        items = adapterItems
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is CoinsAdapterItem.CategoryHeader -> VIEW_TYPE_CATEGORY
            is CoinsAdapterItem.CoinItem -> VIEW_TYPE_COIN
            is CoinsAdapterItem.CategoryCarousel -> VIEW_TYPE_CATEGORY_CAROUSEL
        }
    }

    override fun getItemId(position: Int): Long {
        return when (val item = items[position]) {
            is CoinsAdapterItem.CategoryHeader -> stableId(item.categoryId, 0L)
            is CoinsAdapterItem.CategoryCarousel -> stableId(item.categoryId, 1L)
            is CoinsAdapterItem.CoinItem -> stableId(item.coin.id, 2L)
        }
    }

    private fun stableId(value: String, type: Long): Long {
        return value.hashCode().toLong() * 3L + type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_CATEGORY -> CategoryHeaderViewHolder(
                ItemCategoryHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            VIEW_TYPE_COIN -> CoinViewHolder(
                ItemCoinBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            VIEW_TYPE_CATEGORY_CAROUSEL -> CategoryCarouselViewHolder(
                binding = ItemCategoryCarouselBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),
                sharedViewPool = sharedViewPool,
            )
            else -> error("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is CoinsAdapterItem.CategoryHeader -> {
                (holder as CategoryHeaderViewHolder).bind(item.categoryName)
            }
            is CoinsAdapterItem.CoinItem -> {
                (holder as CoinViewHolder).bind(item.coin)
            }
            is CoinsAdapterItem.CategoryCarousel -> {
                (holder as CategoryCarouselViewHolder).bind(
                    CoinCategoryState(item.categoryId, item.categoryName, item.coins)
                )
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }

        val item = items[position]
        if (item is CoinsAdapterItem.CoinItem && holder is CoinViewHolder) {
            if (payloads.contains(COIN_HIGHLIGHT_PAYLOAD)) {
                holder.bindHighlight(item.coin.highlight)
            } else {
                holder.bind(item.coin)
            }
        } else {
            onBindViewHolder(holder, position)
        }
    }
}

private class ItemDiffCallback(
    private val oldItems: List<CoinsAdapterItem>,
    private val newItems: List<CoinsAdapterItem>,
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldItems.size

    override fun getNewListSize(): Int = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]
        return when {
            oldItem is CoinsAdapterItem.CategoryHeader && newItem is CoinsAdapterItem.CategoryHeader ->
                oldItem.categoryId == newItem.categoryId
            oldItem is CoinsAdapterItem.CategoryCarousel && newItem is CoinsAdapterItem.CategoryCarousel ->
                oldItem.categoryId == newItem.categoryId
            oldItem is CoinsAdapterItem.CoinItem && newItem is CoinsAdapterItem.CoinItem ->
                oldItem.coin.id == newItem.coin.id
            else -> false
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition] == newItems[newItemPosition]
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]
        if (oldItem is CoinsAdapterItem.CoinItem && newItem is CoinsAdapterItem.CoinItem) {
            return if (oldItem.coin.copy(highlight = newItem.coin.highlight) == newItem.coin) {
                COIN_HIGHLIGHT_PAYLOAD
            } else {
                null
            }
        }
        return null
    }
}
