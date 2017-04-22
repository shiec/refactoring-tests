package com.tidal.refactoring.playlist;

import com.google.inject.Inject;
import com.tidal.refactoring.playlist.dao.PlaylistDaoBean;
import com.tidal.refactoring.playlist.data.PlayListTrack;
import com.tidal.refactoring.playlist.data.Track;
import com.tidal.refactoring.playlist.exception.PlaylistException;
import com.tidal.refactoring.playlist.util.Utils;

import java.util.*;

public class PlaylistBusinessBean {

	private PlaylistDaoBean playlistDaoBean;

	@Inject
	public PlaylistBusinessBean(PlaylistDaoBean playlistDaoBean) {
		this.playlistDaoBean = playlistDaoBean;
	}

	/**
	 * Add tracks to the index
	 */
	List<PlayListTrack> addTracks(String uuid, List<Track> tracksToAdd, int toIndex) throws PlaylistException {
		try {
			int originalSize = playlistDaoBean.getPlaylistByUUID(uuid).getNrOfTracks();
			
			// check if nothing to update, then no need to go through the rest
			// or warning/error can be thrown
			if (Utils.isCollectionEmpty(tracksToAdd)) {
				return Collections.emptyList();
			}
			
			// Remove duplicate tracks before updating playlist. This depends on how the equals() method is defined,
			// and it's possible to avoid this method and do the check in 'addTracksToPlaylist' method in the dao.
			tracksToAdd = removeDuplicateTracks(tracksToAdd);

			// We do not allow > 500 tracks in new playlists
			if (originalSize + tracksToAdd.size() > 500) {
				throw new PlaylistException("Playlist cannot have more than " + 500 + " tracks");
			}

			// The index is out of bounds, put it in the end of the list.
			toIndex = (toIndex > originalSize || toIndex == -1) ? originalSize : toIndex;

			if (!isValidIndex(toIndex, originalSize)) {
				return Collections.emptyList();
			}

			return playlistDaoBean.addTracksToPlaylist(uuid, toIndex, tracksToAdd);

		} catch (Exception e) {
			e.printStackTrace();
			throw new PlaylistException("Generic error");
		}
	}

	/**
	 * Remove the tracks from the playlist located at the sent indexes
	 */
	List<PlayListTrack> removeTracks(String uuid, List<Integer> indexes) throws PlaylistException {
		// avoid going through the rest of the logic if nothing to update
		// or warning/error can be thrown
		if (Utils.isCollectionEmpty(indexes)) {
			return Collections.emptyList();
		}

		try {
			int originalSize = playlistDaoBean.getPlaylistByUUID(uuid).getNrOfTracks();

			// throw exception if trying to delete track(s) from empty playlist or delete an out-of-bound index
			boolean isAnyIndexInvalid = indexes.stream()
					.filter(index -> !isValidIndex(index, originalSize - 1))
					.findAny()
					.isPresent();
			
			if (originalSize == 0 || isAnyIndexInvalid) {
				throw new PlaylistException("Cannot remove tracks at invalid index(es)");
			}

			return playlistDaoBean.removeTracksFromPlaylist(uuid, new HashSet<>(indexes));
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new PlaylistException("Generic error");
		}
	}

	private boolean isValidIndex(int toIndex, int length) {
		return toIndex >= 0 && toIndex <= length;
	}

	// can be removed if the equals() method is properly defined
	private List<Track> removeDuplicateTracks(List<Track> originalTracks) {
		assert (!Utils.isCollectionEmpty(originalTracks));

		List<Track> tracksToAdd = new ArrayList<>();
		for (Track track : originalTracks) {
			if (!tracksToAdd.contains(track)) {
				tracksToAdd.add(track);
			}
		}

		return tracksToAdd;
	}
}
