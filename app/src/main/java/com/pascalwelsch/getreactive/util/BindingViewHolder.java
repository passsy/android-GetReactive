package com.pascalwelsch.getreactive.util;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

/**
 * Helper for RecyclerView ViewHolders baked with databinding
 *
 * Created by pascalwelsch on 10/24/15.
 */
public class BindingViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {

    private final T mBinding;

    public BindingViewHolder(T viewBinding) {
        super(viewBinding.getRoot());
        //noinspection unchecked
        mBinding = DataBindingUtil.bind(itemView);
    }

    public T getBinding() {
        return mBinding;
    }
}
