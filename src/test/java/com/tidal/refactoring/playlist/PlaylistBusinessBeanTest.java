package com.tidal.refactoring.playlist;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.tidal.refactoring.playlist.dao.PlaylistDaoBean;
import com.tidal.refactoring.playlist.data.PlayList;
import com.tidal.refactoring.playlist.data.PlayListTrack;
import com.tidal.refactoring.playlist.data.Track;
import com.tidal.refactoring.playlist.exception.PlaylistException;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.testng.Assert.assertEquals;

@Guice(modules = TestBusinessModule.class)
public class PlaylistBusinessBeanTest {

	@Inject
	PlaylistBusinessBean playlistBusinessBean;

	@Inject
	PlaylistDaoBean playlistDaoBean;

	@Inject
	@Named("defaultPlaylistTrackSize")
	int defaultPlaylistTrackSize;

	private String uuid;
	private List<Track> trackList;

	@BeforeMethod
	public void setUp() throws Exception {
		uuid = UUID.randomUUID().toString();
		trackList = new ArrayList<Track>();

		Track track = createTrack(4, "A brand new track", 76868);

		trackList.add(track);
	}

	private Track createTrack(int artistId, String title, int id) {
		Track track = new Track();
		track.setArtistId(artistId);
		track.setTitle(title);
		track.setId(id);

		return track;
	}

	@AfterMethod
	public void tearDown() throws Exception {

	}

	@Test
	public void testAddSingleTrackInMiddle() throws Exception {
		List<PlayListTrack> playListTracksAdded = playlistBusinessBean.addTracks(uuid, trackList, 5);

		assertEquals(1, playListTracksAdded.size());
		assertEquals(76868, playListTracksAdded.get(0).getTrackId());
		assertEquals(uuid, playListTracksAdded.get(0).getTrackPlayListUuid());

		PlayList playlist = playlistDaoBean.getPlaylistByUUID(uuid);
		List<PlayListTrack> playlistTracks = new ArrayList<>(playlist.getPlayListTracks());
		assertEquals(defaultPlaylistTrackSize + trackList.size(), playlist.getNrOfTracks());
		assertEquals(defaultPlaylistTrackSize + trackList.size(), playlistTracks.size());

		Collections.sort(playlistTracks);
		PlayListTrack playListTrackAdded = playlistTracks.get(5);

		assertEquals(trackList.get(0), playListTrackAdded.getTrack());
		assertEquals(5, playListTrackAdded.getIndex());
	}

	@Test
	public void testAddMultipleTracksAtEnd() throws Exception {
		trackList.add(createTrack(5, "Another new track", 76869));

		List<PlayListTrack> playListTracksAdded = playlistBusinessBean.addTracks(uuid, trackList,
				defaultPlaylistTrackSize + 10);

		assertEquals(2, playListTracksAdded.size());
		assertEquals(76868, playListTracksAdded.get(0).getTrackId());
		assertEquals(uuid, playListTracksAdded.get(0).getTrackPlayListUuid());

		assertEquals(76869, playListTracksAdded.get(1).getTrackId());
		assertEquals(uuid, playListTracksAdded.get(1).getTrackPlayListUuid());

		PlayList playlist = playlistDaoBean.getPlaylistByUUID(uuid);
		List<PlayListTrack> playlistTracks = new ArrayList<>(playlist.getPlayListTracks());

		assertEquals(defaultPlaylistTrackSize + trackList.size(), playlist.getNrOfTracks());
		assertEquals(defaultPlaylistTrackSize + trackList.size(), playlistTracks.size());

		Collections.sort(playlistTracks);
		PlayListTrack playListTrackAdded1 = playlistTracks.get(defaultPlaylistTrackSize);
		PlayListTrack playListTrackAdded2 = playlistTracks.get(defaultPlaylistTrackSize + 1);

		assertEquals(trackList.get(0), playListTrackAdded1.getTrack());
		assertEquals(defaultPlaylistTrackSize, playListTrackAdded1.getIndex());

		assertEquals(trackList.get(1), playListTrackAdded2.getTrack());
		assertEquals(defaultPlaylistTrackSize + 1, playListTrackAdded2.getIndex());
	}

	@Test
	public void testAddNullTracks() {
		List<PlayListTrack> playListTracksAdded = playlistBusinessBean.addTracks(uuid, null, defaultPlaylistTrackSize);

		assertEquals(0, playListTracksAdded.size());
	}

	@Test
	public void testAddNoTracks() {
		List<PlayListTrack> playListTracksAdded = playlistBusinessBean.addTracks(uuid, Collections.emptyList(),
				defaultPlaylistTrackSize);

		assertEquals(0, playListTracksAdded.size());
	}

	@Test(expectedExceptions = PlaylistException.class)
	public void testAddingOutOfBoundTracks() {
		for (int i = 0; i < 150; i++) {
			trackList.add(createTrack(i, "new track " + i, i));
		}

		playlistBusinessBean.addTracks(uuid, trackList, defaultPlaylistTrackSize);
	}

	@Test
	public void testAddDuplicateTracks() {
		trackList.add(trackList.get(0));

		List<PlayListTrack> playListTracksAdded = playlistBusinessBean.addTracks(uuid, trackList, 2);

		assertEquals(1, playListTracksAdded.size());
		assertEquals(76868, playListTracksAdded.get(0).getTrackId());
		assertEquals(uuid, playListTracksAdded.get(0).getTrackPlayListUuid());
	}

	@Test
	public void testAddExistingTracks() {
		playlistBusinessBean.addTracks(uuid, null, defaultPlaylistTrackSize);

		PlayListTrack playlistTrack = playlistDaoBean.getPlaylistByUUID(uuid).getPlayListTracks().iterator().next();
		trackList.add(playlistTrack.getTrack());
		List<PlayListTrack> playListTracksAdded = playlistBusinessBean.addTracks(uuid, trackList,
				defaultPlaylistTrackSize);

		assertEquals(1, playListTracksAdded.size());

		assertEquals(76868, playListTracksAdded.get(0).getTrackId());
		assertEquals(uuid, playListTracksAdded.get(0).getTrackPlayListUuid());
	}

	@Test
	public void testRemoveIndex() {
		playlistBusinessBean.addTracks(uuid, null, defaultPlaylistTrackSize);
		List<PlayListTrack> removedTracks = playlistBusinessBean.removeTracks(uuid, Lists.newArrayList(22));

		assertEquals(1, removedTracks.size());
		assertEquals(22, removedTracks.get(0).getIndex());

		PlayList playlist = playlistDaoBean.getPlaylistByUUID(uuid);
		List<PlayListTrack> playlistTracks = new ArrayList<>(playlist.getPlayListTracks());
		assertEquals(defaultPlaylistTrackSize - removedTracks.size(), playlist.getNrOfTracks());
		assertEquals(defaultPlaylistTrackSize - removedTracks.size(), playlistTracks.size());

		Collections.sort(playlistTracks);

		assertEquals(defaultPlaylistTrackSize - removedTracks.size() - 1,
				playlistTracks.get(playlistTracks.size() - 1).getIndex());
	}

	@Test
	public void testRemoveNullTracks() {
		playlistBusinessBean.addTracks(uuid, null, defaultPlaylistTrackSize);
		List<PlayListTrack> removedTracks = playlistBusinessBean.removeTracks(uuid, null);

		assertEquals(0, removedTracks.size());
	}

	@Test
	public void testRemoveNoTracks() {
		playlistBusinessBean.addTracks(uuid, null, defaultPlaylistTrackSize);
		List<PlayListTrack> removedTracks = playlistBusinessBean.removeTracks(uuid, Collections.emptyList());

		assertEquals(0, removedTracks.size());
	}

	@Test(expectedExceptions = PlaylistException.class)
	public void testRemoveInvalidIndexes() {
		playlistBusinessBean.addTracks(uuid, null, defaultPlaylistTrackSize);
		playlistBusinessBean.removeTracks(uuid, Lists.newArrayList(1000));
	}

	@Test
	public void testRemoveDuplicateIndexes() {
		playlistBusinessBean.addTracks(uuid, null, defaultPlaylistTrackSize);

		List<PlayListTrack> removedTracks = playlistBusinessBean.removeTracks(uuid, Lists.newArrayList(0, 0));

		assertEquals(1, removedTracks.size());
		assertEquals(0, removedTracks.get(0).getIndex());

		PlayList playlist = playlistDaoBean.getPlaylistByUUID(uuid);

		assertEquals(defaultPlaylistTrackSize - 1, playlist.getNrOfTracks());
		assertEquals(defaultPlaylistTrackSize - 1, playlist.getPlayListTracks().size());
	}
}