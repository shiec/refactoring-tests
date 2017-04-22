package com.tidal.refactoring.playlist.util;

import java.util.Collection;

public class Utils {
	public static boolean isCollectionEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}
}
