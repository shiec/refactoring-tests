package com.tidal.refactoring.playlist.dao;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.tidal.refactoring.playlist.data.PlayList;
import com.tidal.refactoring.playlist.data.PlayListTrack;
import com.tidal.refactoring.playlist.data.Track;
import com.tidal.refactoring.playlist.util.Utils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class faking the data layer, and returning fake playlists
 */
public class PlaylistDaoBean {
	// makes it flexible to change the default playlist track size
	@Inject
	@Named("defaultPlaylistTrackSize")
	private int defaultPlaylistTrackSize;

	private final Map<String, PlayList> playlists = new HashMap<String, PlayList>();

	public PlayList getPlaylistByUUID(String uuid) {
		if (!playlists.containsKey(uuid)) {
			createPlayList(uuid);
		}

		return playlists.get(uuid);
	}

	private void createPlayList(String uuid) {
		PlayList trackPlayList = new PlayList(uuid);

		trackPlayList.setDeleted(false);
		trackPlayList.setDuration((float) (60 * 60 * 2));
		trackPlayList.setId(49834);
		trackPlayList.setLastUpdated(new Date());
		trackPlayList.setPlayListName("Collection of great songs");
		trackPlayList.setPlayListTracks(getPlaylistTracks(uuid, defaultPlaylistTrackSize));

		playlists.put(uuid, trackPlayList);
	}

	private static Set<PlayListTrack> getPlaylistTracks(String playlistUuid, int playlistTrackSize) {
		Set<PlayListTrack> playListTracks = new HashSet<PlayListTrack>();

		for (int i = 0; i < playlistTrackSize; i++) {
			PlayListTrack playListTrack = new PlayListTrack(i + 1, playlistUuid, i, new Date(), getTrack());

			playListTracks.add(playListTrack);
		}

		return playListTracks;
	}

	public static Track getTrack() {
		Random randomGenerator = new Random();

		Track track = new Track();
		track.setArtistId(randomGenerator.nextInt(10000));
		track.setDuration(60 * 3);

		int trackNumber = randomGenerator.nextInt(15);
		track.setTitle("Track no: " + trackNumber);
		track.setId(trackNumber);

		return track;
	}

	// extract functionality from PlaylistBusineesBean to keep it's logic clean
	public List<PlayListTrack> addTracksToPlaylist(String uuid, int toIndex, List<Track> tracksToAdd) {
		PlayList playlist = getPlaylistByUUID(uuid);

		List<PlayListTrack> playlistTracksToUpdate = getPlaylistTracksToUpdate(playlist.getPlayListTracks());

		// This method can also be avoided depending on how to define that two playlist tracks are equal.
		tracksToAdd = removeExistingTracks(tracksToAdd, playlistTracksToUpdate);

		// update indexes of tracks from toIndex, everything before stays the same
		for (int i = toIndex; i < playlistTracksToUpdate.size(); i++) {
			playlistTracksToUpdate.get(i).setIndex(i + tracksToAdd.size());
		}

		List<PlayListTrack> added = new ArrayList<>(tracksToAdd.size());
		int originalSize = playlistTracksToUpdate.size();

		for (int i = 0; i < tracksToAdd.size(); i++) {
			PlayListTrack playlistTrack = new PlayListTrack(originalSize + i, uuid, toIndex + i, new Date(),
					tracksToAdd.get(i));
			playlist.addDuration(tracksToAdd.get(i).getDuration());
			added.add(playlistTrack);
		}

		// this can be moved up into the for loop if the equals() method is properly defined, so we can avoid
		// adding duplicate/existing tracks based on the return value of add()
		playlistTracksToUpdate.addAll(toIndex, added);

		updatePlaylist(playlist, playlistTracksToUpdate);

		return added;
	}

	// remove tracks at the given indexes
	public List<PlayListTrack> removeTracksFromPlaylist(String uuid, Set<Integer> indexes) {
		PlayList playlist = getPlaylistByUUID(uuid);

		List<PlayListTrack> playlistTracksToUpdate = getPlaylistTracksToUpdate(playlist.getPlayListTracks());

		List<PlayListTrack> removed = new ArrayList<>();
		for (int index : indexes) {
			removed.add(playlistTracksToUpdate.remove(index));
		}

		// start updating the indexes of tracks only after the first removed index, since everything before stays the same
		int indexStart = indexes.iterator().next();
		for (int i = indexStart; i < playlistTracksToUpdate.size(); i++) {
			playlistTracksToUpdate.get(i).setIndex(i);
		}

		updatePlaylist(playlist, playlistTracksToUpdate);

		return removed;
	}

	// update all related attributes of the playlist
	private void updatePlaylist(PlayList playlist, List<PlayListTrack> playlistTracksToUpdate) {
		playlist.getPlayListTracks().clear();
		playlist.getPlayListTracks().addAll(playlistTracksToUpdate);
		playlist.setNrOfTracks(playlistTracksToUpdate.size());
		playlist.setLastUpdated(new Date());
	}

	// get sorted playlist tracks to update
	private static List<PlayListTrack> getPlaylistTracksToUpdate(Set<PlayListTrack> originalPlaylistTracks) {
		List<PlayListTrack> updatedPlaylistTracks = new ArrayList<>();

		if (!Utils.isCollectionEmpty(originalPlaylistTracks)) {
			updatedPlaylistTracks.addAll(originalPlaylistTracks);

			Collections.sort(updatedPlaylistTracks);
		}

		return updatedPlaylistTracks;
	}

	// this can be removed if the equals() method of PlayListTrack is properly defined
	private static List<Track> removeExistingTracks(List<Track> tracksToAdd,
			List<PlayListTrack> playlistTracksToUpdate) {

		List<Track> existingTracks = playlistTracksToUpdate.stream().map(track -> track.getTrack())
				.collect(Collectors.toList());

		return tracksToAdd.stream().filter(track -> !existingTracks.contains(track)).collect(Collectors.toList());
	}
}
