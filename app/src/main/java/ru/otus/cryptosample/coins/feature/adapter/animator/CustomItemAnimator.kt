package ru.otus.cryptosample.coins.feature.adapter.animator

import android.animation.AnimatorInflater
import android.content.Context
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import ru.otus.cryptosample.R

class CustomItemAnimator(
    private val context: Context,
) : DefaultItemAnimator() {

    init {
        supportsChangeAnimations = false
    }

    override fun animateAdd(holder: RecyclerView.ViewHolder): Boolean {
        val set = AnimatorInflater.loadAnimator(context, R.animator.coin_add)
        set.interpolator = DecelerateInterpolator()
        set.setTarget(holder.itemView)
        set.start()
        return true
    }

    override fun animateRemove(holder: RecyclerView.ViewHolder): Boolean {
        val set = AnimatorInflater.loadAnimator(context, R.animator.coin_remove)
        set.interpolator = DecelerateInterpolator()
        set.setTarget(holder.itemView)
        set.start()
        return true
    }
}
