package ru.otus.cryptosample.coins.feature.adapter.holders

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.otus.cryptosample.coins.feature.CoinCategoryState
import ru.otus.cryptosample.coins.feature.adapter.CoinItemsAdapter
import ru.otus.cryptosample.databinding.ItemCategoryCarouselBinding

class CategoryCarouselViewHolder(
    private val binding: ItemCategoryCarouselBinding,
    sharedViewPool: RecyclerView.RecycledViewPool,
) : RecyclerView.ViewHolder(binding.root) {

    private val coinsAdapter = CoinItemsAdapter(horizontal = true)

    init {
        binding.coinsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = coinsAdapter
            setRecycledViewPool(sharedViewPool)
            isNestedScrollingEnabled = false
            itemAnimator = null
        }
    }

    fun bind(category: CoinCategoryState) {
        binding.categoryTitle.text = category.name
        coinsAdapter.submitList(category.coins)
    }
}
