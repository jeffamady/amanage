package com.amadydev.amanage.ui.board

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amadydev.amanage.R
import com.amadydev.amanage.data.model.Board
import com.amadydev.amanage.databinding.ItemBoardBinding
import com.bumptech.glide.Glide

class BoardAdapter(
    private val context: Context,
    private val boardList: List<Board>,
    private val onBoardClickListener: OnBoardClickListener
) : RecyclerView.Adapter<BoardAdapter.BoardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder =
        BoardViewHolder(
            ItemBoardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) =
        holder.render(boardList[position])

    override fun getItemCount() =
        boardList.size

    inner class BoardViewHolder(private val binding: ItemBoardBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun render(board: Board) {
            with(binding) {
                Glide.with(context)
                    .load(board.image)
                    .placeholder(R.drawable.ic_task_image)
                    .centerCrop()
                    .into(ivBoard)

                tvName.text = board.name
                tvCreatedBy.text = context.getString(R.string.created_by)
                    .plus(" ").plus(board.createdBy)

                binding.root.setOnClickListener {
                    onBoardClickListener.onBoardClicked(board)
                }
            }
        }
    }

    interface OnBoardClickListener {
        fun onBoardClicked(board: Board)
    }
}