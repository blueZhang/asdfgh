package com.bluezhang.picasso_1010_1930_work.adadapterutil;

public interface MultiItemTypeSupport<T> {
	int getLayoutId(int position, T t);

	int getViewTypeCount();

	int getItemViewType(int postion, T t);
}