package ru.otus.cryptosample.coins.feature.adapter.items

import ru.otus.cryptosample.coins.feature.CoinState

sealed class CoinsAdapterItem {
    data class CategoryHeader(
        val categoryId: String,
        val categoryName: String,
    ) : CoinsAdapterItem()

    data class CategoryCarousel(
        val categoryId: String,
        val categoryName: String,
        val coins: List<CoinState>,
    ) : CoinsAdapterItem()

    data class CoinItem(val coin: CoinState) : CoinsAdapterItem()
}
