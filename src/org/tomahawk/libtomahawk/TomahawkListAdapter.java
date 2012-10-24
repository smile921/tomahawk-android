/* == This file is part of Tomahawk Player - <http://tomahawk-player.org> ===
 *
 *   Copyright 2012, Enno Gottschalk <mrmaffen@googlemail.com>
 *
 *   Tomahawk is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Tomahawk is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Tomahawk. If not, see <http://www.gnu.org/licenses/>.
 */
package org.tomahawk.libtomahawk;

import java.util.ArrayList;
import java.util.List;

import org.tomahawk.tomahawk_android.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Enno Gottschalk <mrmaffen@googlemail.com>
 *
 */
public class TomahawkListAdapter extends BaseAdapter {

    private List<List<TomahawkListItem>> mListArray;
    private List<List<TomahawkListItem>> mFilteredListArray;
    private List<String> mHeaderArray;
    private boolean mShowHeaders;
    private boolean mFiltered;
    private LayoutInflater mInflater;
    private int mResourceListItem;
    private int mTextViewResourceListItemId1;
    private int mTextViewResourceListItemId2;
    private int mResourceListHeader;
    private int mTextViewResourceListHeaderId;

    /**
     * This interface represents an item displayed in our {@link Collection} list.
     */
    public interface TomahawkListItem {

        /** @return the corresponding name/title */
        public String getName();

        /** @return the corresponding {@link Artist} */
        public Artist getArtist();

        /** @return the corresponding {@link Album} */
        public Album getAlbum();
    }

    /**
     * Constructs a new {@link TomahawkListAdapter}
     * 
     * @param activity the activity, which uses the {@link TomahawkListAdapter}. Used to get the {@link LayoutInflater}
     * @param resourceListItem the resource id for the view, that represents a listItem
     * @param textViewResourceListItemId1 the resource id for the textView inside resourceListItem that displays the
     * first line of text
     * @param textViewResourceListItemId2 the resource id for the textView inside resourceListItem that displays the
     * second line of text
     * @param list contains a list of TomahawkListItems.
     */
    public TomahawkListAdapter(Activity activity, int resourceListItem, int textViewResourceListItemId1,
            int textViewResourceListItemId2, List<TomahawkListItem> list) {
        mInflater = activity.getLayoutInflater();
        mResourceListItem = resourceListItem;
        mTextViewResourceListItemId1 = textViewResourceListItemId1;
        mTextViewResourceListItemId2 = textViewResourceListItemId2;
        mListArray = new ArrayList<List<TomahawkListItem>>();
        mListArray.add(list);
        mShowHeaders = false;
        setFiltered(false);
        fillHeaderArray();
    }

    /**
     * Constructs a new {@link TomahawkListAdapter}
     * 
     * @param activity the activity, which uses the {@link TomahawkListAdapter}. Used to get the {@link LayoutInflater}
     * @param resourceListItem the resource id for the view, that represents a listItem
     * @param textViewResourceListItemId1 the resource id for the textView inside resourceListItem that displays the
     * first line of text
     * @param list contains a list of TomahawkListItems.
     */
    public TomahawkListAdapter(Activity activity, int resourceListItem, int textViewResourceListItemId1,
            List<TomahawkListItem> list) {
        mInflater = activity.getLayoutInflater();
        mResourceListItem = resourceListItem;
        mTextViewResourceListItemId1 = textViewResourceListItemId1;
        mListArray = new ArrayList<List<TomahawkListItem>>();
        mListArray.add(list);
        mShowHeaders = false;
        setFiltered(false);
        fillHeaderArray();
    }

    /**
     * Constructs a new {@link TomahawkListAdapter}
     * 
     * @param activity the activity, which uses the {@link TomahawkListAdapter}. Used to get the {@link LayoutInflater}
     * @param resourceListHeader the resource id for the view, that represents a listHeader
     * @param textViewResourceListHeaderId the resource id for the textView inside resourceListHeader that displays the
     * text
     * @param resourceListItem the resource id for the view, that represents a listItem
     * @param textViewResourceListItemId1 the resource id for the textView inside resourceListItem that displays the
     * first line of text
     * @param textViewResourceListItemId2 the resource id for the textView inside resourceListItem that displays the
     * second line of text
     * @param listArray contains a list of lists of TomahawkListItems. Every list within the list can be shown in
     * different categories with headers specified in headerArray
     * @param headerArray contains the headers of the lists. if no headerArray is given, the headers won't be shown
     */
    public TomahawkListAdapter(Activity activity, int resourceListHeader, int textViewResourceListHeaderId,
            int resourceListItem, int textViewResourceListItemId1, int textViewResourceListItemId2,
            List<List<TomahawkListItem>> listArray, List<String> headerArray) {
        mInflater = activity.getLayoutInflater();
        mResourceListHeader = resourceListHeader;
        mTextViewResourceListHeaderId = textViewResourceListHeaderId;
        mResourceListItem = resourceListItem;
        mTextViewResourceListItemId1 = textViewResourceListItemId1;
        mTextViewResourceListItemId2 = textViewResourceListItemId2;
        mListArray = listArray;
        mHeaderArray = headerArray;
        mShowHeaders = true;
        setFiltered(true);
        fillHeaderArray();

    }

    /**
     * Constructs a new {@link TomahawkListAdapter}
     * 
     * @param activity the activity, which uses the {@link TomahawkListAdapter}. Used to get the {@link LayoutInflater}
     * @param resourceListHeader the resource id for the view, that represents a listHeader
     * @param textViewResourceListHeaderId the resource id for the textView inside resourceListHeader that displays the
     * text
     * @param resourceListItem the resource id for the view, that represents a listItem
     * @param textViewResourceListItemId1 the resource id for the textView inside resourceListItem that displays the
     * first line of text
     * @param listArray contains a list of lists of TomahawkListItems. Every list within the list can be shown in
     * different categories with headers specified in headerArray
     * @param headerArray contains the headers of the lists. if no headerArray is given, the headers won't be shown
     */
    public TomahawkListAdapter(Activity activity, int resourceListHeader, int textViewResourceListHeaderId,
            int resourceListItem, int textViewResourceListItemId1, List<List<TomahawkListItem>> listArray,
            List<String> headerArray) {
        mInflater = activity.getLayoutInflater();
        mResourceListHeader = resourceListHeader;
        mTextViewResourceListHeaderId = textViewResourceListHeaderId;
        mResourceListItem = resourceListItem;
        mTextViewResourceListItemId1 = textViewResourceListItemId1;
        mListArray = listArray;
        mHeaderArray = headerArray;
        mShowHeaders = true;
        setFiltered(true);
        fillHeaderArray();
    }

    /**  fill mHeaderArray with "No header" strings  */
    public void fillHeaderArray() {
        if (mHeaderArray == null) {
            mShowHeaders = false;
            mHeaderArray = new ArrayList<String>();
            for (int i = 0; i < mListArray.size(); i++) {
                mHeaderArray.add("No header");
            }
        }
        while (mHeaderArray.size() < mListArray.size()) {
            mHeaderArray.add("No header");
        }
    }

    /** Add a list to the {@link TomahawkListAdapter}.
     *  @param title the title of the list, which will be displayed as a header, if the list is not empty
     * @return the index of the just added list*/
    public int addList(String title) {
        mListArray.add(new ArrayList<TomahawkListItem>());
        mHeaderArray.add(title);
        return mListArray.size() - 1;
    }

    /** Add an item to the list with the given index
     *  @param index the index which specifies which list the item should be added to
     *  @param item the item to add
     *  @return true if successful, otherwise false*/
    public boolean addItemToList(int index, TomahawkListItem item) {
        if (hasListWithIndex(index)) {
            mListArray.get(index).add(item);
            return true;
        }
        return false;
    }

    /** Add an item to the list with the given title
     *  @param title the title, which specifies which list the item should be added to
     *  @param item the item to add
     *  @return true if successful, otherwise false*/
    public boolean addItemToList(String title, TomahawkListItem item) {
        int i = hasListWithTitle(title);
        if (i >= 0) {
            mListArray.get(i).add(item);
            return true;
        }
        return false;
    }

    /** test if the list with the given index exists
     *  @param index
     *  @return true if list exists, false otherwise*/
    public boolean hasListWithIndex(int index) {
        if (mListArray.get(index) != null) {
            return true;
        }
        return false;
    }

    /** test if the list with the given title exists
     *  @param title
     *  @return if the list was found, the method returns the index of the list, otherwise -1*/
    public int hasListWithTitle(String title) {
        for (int i = 0; i < mHeaderArray.size(); i++)
            if (mHeaderArray.get(i) == title) {
                return i;
            }
        return -1;
    }

    /** Removes every element from every list there is  */
    public void clearAllLists() {
        for (int i = 0; i < mHeaderArray.size(); i++)
            mListArray.get(i).clear();
    }

    /** @return the {@link Filter}, which allows to filter the items inside the custom {@link ListView} fed by {@link TomahawkListAdapter}*/
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilteredListArray = (List<List<TomahawkListItem>>) results.values;
                TomahawkListAdapter.this.notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                constraint = constraint.toString().toLowerCase();
                constraint = constraint.toString().trim();
                List<List<TomahawkListItem>> filteredResults = (List<List<TomahawkListItem>>) getFilteredResults(constraint);

                FilterResults results = new FilterResults();
                synchronized (this) {
                    results.values = filteredResults;
                }

                return results;
            }

            protected List<List<TomahawkListItem>> getFilteredResults(CharSequence constraint) {
                List<List<TomahawkListItem>> filteredResults = new ArrayList<List<TomahawkListItem>>();
                if (constraint == null || constraint.toString().length() <= 1)
                    return filteredResults;

                for (int i = 0; i < mListArray.size(); i++) {
                    filteredResults.add(new ArrayList<TomahawkListItem>());
                    for (int j = 0; j < mListArray.get(i).size(); j++) {
                        TomahawkListItem item = mListArray.get(i).get(j);
                        if (item.getName().toLowerCase().contains(constraint))
                            filteredResults.get(i).add(item);
                    }
                }
                return filteredResults;
            }
        };
    }

    /**
     * @param filtered true if the list is being filtered, else false
     */
    public void setFiltered(boolean filtered) {
        this.mFiltered = filtered;
    }

    static class ViewHolder {
        protected int viewType;
        protected TextView textFirstLine;
        protected TextView textSecondLine;
    }

    /* 
     * (non-Javadoc)
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        Object item = getItem(position);

        if (item != null) {
            ViewHolder viewHolder = new ViewHolder();

            if ((item instanceof TomahawkListItem && convertView == null)
                    || (item instanceof TomahawkListItem && convertView != null && ((ViewHolder) convertView.getTag()).viewType != R.id.tomahawklistadapter_viewtype_item)) {
                view = mInflater.inflate(mResourceListItem, null);
                viewHolder.viewType = R.id.tomahawklistadapter_viewtype_item;
                viewHolder.textFirstLine = (TextView) view.findViewById(mTextViewResourceListItemId1);
                viewHolder.textSecondLine = (TextView) view.findViewById(mTextViewResourceListItemId2);
                view.setTag(viewHolder);
            } else if ((item instanceof String && convertView == null)
                    || (item instanceof String && convertView != null && ((ViewHolder) convertView.getTag()).viewType != R.id.tomahawklistadapter_viewtype_header)) {
                view = mInflater.inflate(mResourceListHeader, null);
                viewHolder.viewType = R.id.tomahawklistadapter_viewtype_header;
                viewHolder.textFirstLine = (TextView) view.findViewById(mTextViewResourceListHeaderId);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            if (viewHolder.textFirstLine != null) {
                if (item instanceof String)
                    viewHolder.textFirstLine.setText((String) item);
                else if (item instanceof TomahawkListItem)
                    viewHolder.textFirstLine.setText(((TomahawkListItem) item).getName());
            }
            if (viewHolder.textSecondLine != null)
                viewHolder.textSecondLine.setText(((TomahawkListItem) item).getArtist().getName());

        }
        return view;
    }

    /* 
     * (non-Javadoc)
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        if ((mFiltered ? mFilteredListArray : mListArray) == null)
            return 0;
        int displayedListArrayItemsCount = 0;
        int displayedHeaderArrayItemsCount = 0;
        for (List<TomahawkListItem> list : (mFiltered ? mFilteredListArray : mListArray)) {
            displayedListArrayItemsCount += list.size();
            if (list.size() > 0 && mShowHeaders)
                displayedHeaderArrayItemsCount++;
        }
        return displayedListArrayItemsCount + displayedHeaderArrayItemsCount;
    }

    /* 
     * (non-Javadoc)
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        Object item = null;
        int offsetCounter = 0;
        for (int i = 0; i < (mFiltered ? mFilteredListArray : mListArray).size(); i++) {
            List<TomahawkListItem> list = (mFiltered ? mFilteredListArray : mListArray).get(i);
            if (mShowHeaders) {
                if (!list.isEmpty())
                    offsetCounter++;
                if (position - offsetCounter == -1) {
                    item = mHeaderArray.get(i);
                    break;
                }
            }
            if (position - offsetCounter < list.size()) {
                item = list.get(position - offsetCounter);
                break;
            }
            offsetCounter += list.size();
        }
        return item;
    }

    /* 
     * (non-Javadoc)
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return position;
    }
}