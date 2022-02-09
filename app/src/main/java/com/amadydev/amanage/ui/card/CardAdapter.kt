package com.amadydev.amanage.ui.card

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amadydev.amanage.data.model.Card
import com.amadydev.amanage.databinding.ItemCardBinding

class CardAdapter(
    private val context: Context,
    private val cardsList: List<Card>,
    private val onCardClickListener: OnCardClickListener
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder =
        CardViewHolder(
            ItemCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) =
        holder.render(cardsList[position])

    override fun getItemCount() =
        cardsList.size

    inner class CardViewHolder(
        private val binding: ItemCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun render(card: Card){
            with(binding) {
                tvCardName.text = card.name

                //Listener
                root.setOnClickListener {
                    onCardClickListener.onCardClicked(card)
                }
            }
        }
    }

    interface OnCardClickListener {
        fun onCardClicked(card: Card)
    }
}