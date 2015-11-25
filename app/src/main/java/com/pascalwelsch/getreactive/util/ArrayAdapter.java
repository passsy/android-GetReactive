package com.pascalwelsch.getreactive.util;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by pascalwelsch on 04.07.14.
 */
public abstract class ArrayAdapter<T, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    private List<T> mObjects;

    public ArrayAdapter(@Nullable final List<T> objects) {
        mObjects = objects != null ? objects : new ArrayList<T>();
    }

    /**
     * Adds the specified object at the end of the array.
     *
     * @param object The object to add at the end of the array.
     */
    public void add(final T object) {
        mObjects.add(object);
        notifyItemInserted(getItemCount() - 1);
    }

    /**
     * Adds the specified list of objects at the end of the array.
     *
     * @param objects The objects to add at the end of the array.
     */
    public void addAll(final List<T> objects) {
        if (objects == null) {
            mObjects.clear();
        } else {
            mObjects.addAll(objects);
        }
        notifyDataSetChanged();
    }

    /**
     * Remove all elements from the list.
     */
    public void clear() {
        final int size = getItemCount();
        mObjects.clear();
        notifyItemRangeRemoved(0, size);
    }

    public T getItem(final int position) {
        try {
            return mObjects.get(position);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return mObjects.size();
    }

    public long getItemId(final int position) {
        return position;
    }

    /**
     * Returns the position of the specified item in the array.
     *
     * @param item The item to retrieve the position of.
     * @return The position of the specified item.
     */
    public int getPosition(final T item) {
        return mObjects.indexOf(item);
    }

    /**
     * Inserts the specified object at the specified index in the array.
     *
     * @param object The object to insert into the array.
     * @param index  The index at which the object must be inserted.
     */
    public void insert(final T object, int index) {
        mObjects.add(index, object);
        notifyItemInserted(index);
    }

    /**
     * Removes the specified object from the array.
     *
     * @param object The object to remove.
     */
    public void remove(T object) {
        final int position = getPosition(object);
        mObjects.remove(object);
        notifyItemRemoved(position);
    }

    public void removeLastObject() {
        mObjects.remove(mObjects.size() - 1);
        notifyItemRemoved(mObjects.size() - 1);
    }

    public void replaceItem(final T oldObject, final T newObject) {
        final int position = getPosition(oldObject);
        mObjects.remove(position);
        mObjects.add(position, newObject);
        notifyItemChanged(position);
    }

    public void replaceItemWithHeader(final T oldObject, final T newObject) {
        final int position = getPosition(oldObject);
        mObjects.remove(position);
        mObjects.add(position, newObject);
        notifyItemChanged(position + 1);
    }

    /**
     * Removes all elements and replaces them with the given guys from the list, all done with an
     * animation.
     *
     * @param objects The new Objects to display
     */
    public void replaceObjectsWithAnimations(final List<T> objects) {
        animateRemoveObjects(objects);
        animateAddObjects(objects);
    }

    /**
     * Sorts the content of this adapter using the specified comparator.
     *
     * @param comparator The comparator used to sort the objects contained in this adapter.
     */
    public void sort(Comparator<? super T> comparator) {
        Collections.sort(mObjects, comparator);
        notifyItemRangeChanged(0, getItemCount());
    }

    /**
     * replaces the data with the given list
     *
     * @param objects new data
     */
    public void swap(final List<T> objects) {
        mObjects = objects;
        notifyDataSetChanged();
    }

    protected T removeItem(final int position) {
        final T object = mObjects.remove(position);
        notifyItemRemoved(position);
        return object;
    }

    protected T removeItemWithHeader(final int position) {
        final T object = mObjects.remove(position);
        notifyItemRemoved(position + 1);
        return object;
    }

    private void addItem(final int postion, final T object) {
        mObjects.add(postion, object);
        notifyItemInserted(postion);
    }

    private void animateAddObjects(final List<T> objects) {
        for (int i = 0; i < objects.size(); i++) {
            final T object = objects.get(i);
            if (!mObjects.contains(object)) {
                addItem(i, object);
            }
        }
    }

    private void animateRemoveObjects(final List<T> objects) {
        for (int i = mObjects.size() - 1; i >= 0; i--) {
            final T object = mObjects.get(i);
            if (!objects.contains(object)) {
                removeItem(i);
            }
        }
    }

    private void moveItem(final int fromPosition, final int toPosition) {
        final T object = mObjects.remove(fromPosition);
        mObjects.add(toPosition, object);
        notifyItemMoved(fromPosition, toPosition);
    }
}
