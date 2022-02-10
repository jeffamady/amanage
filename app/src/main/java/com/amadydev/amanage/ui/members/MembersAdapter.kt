package com.amadydev.amanage.ui.members

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amadydev.amanage.R
import com.amadydev.amanage.data.model.User
import com.amadydev.amanage.databinding.ItemMemberBinding
import com.bumptech.glide.Glide

class MembersAdapter(
    private val context: Context,
    private val membersList: List<User>,
    private val onMemberClickListener: OnMemberClickListener
) : RecyclerView.Adapter<MembersAdapter.MemberViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembersAdapter.MemberViewHolder =
        MemberViewHolder(
            ItemMemberBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: MembersAdapter.MemberViewHolder, position: Int) =
        holder.render(membersList[position])

    override fun getItemCount() =
        membersList.size


    inner class MemberViewHolder(
        private val binding: ItemMemberBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun render(user: User) {
            with(binding) {
                Glide.with(context)
                    .load(user.image)
                    .error(R.drawable.ic_user_place_holder)
                    .centerCrop()
                    .into(binding.ivMemberImage)

                binding.tvMemberName.text = user.name
                binding.tvMemberEmail.text = user.email


                root.setOnClickListener {
                    onMemberClickListener.onMemberClicked(user)
                }
            }
        }
    }

    interface OnMemberClickListener {
        fun onMemberClicked(user: User)
    }
}