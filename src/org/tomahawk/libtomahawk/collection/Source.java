/* == This file is part of Tomahawk Player - <http://tomahawk-player.org> ===
 *
 *   Copyright 2012, Christopher Reichert <creichert07@gmail.com>
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
package org.tomahawk.libtomahawk.collection;

/**
 * This class is a wrapper around a {@link Collection} with an id and a name
 */
public class Source {

    private int mId;

    private String mName;

    private Collection mCollection;

    /**
     * Constructs a new {@link Source} from the given id.
     */
    public Source(Collection coll, int id, String name) {
        mId = id;
        mName = name;
        mCollection = coll;
    }

    /**
     * Returns whether or not this {@link Source} is local.
     */
    public boolean isLocal() {
        return mCollection.isLocal();
    }

    /**
     * Returns the name of this {@link Source}.
     */
    public String getName() {
        return mName;
    }

    /**
     * @return this {@link Source}'s id
     */
    public int getId() {
        return mId;
    }

    /**
     * @return the {@link Collection} associated with this {@link Source}
     */
    public Collection getCollection() {
        return mCollection;
    }
}
