package com.tidal.refactoring.playlist.data;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * A very simplified version of TrackPlaylist
 */
public class PlayList {

	private Integer id;
	private String playListName;
	private Set<PlayListTrack> playListTracks = new HashSet<PlayListTrack>();
	private Date registeredDate;
	private Date lastUpdated;
	private final String uuid; // a playlist is created with a uuid, and cannot be modified
	private int nrOfTracks;
	private boolean deleted;
	private float duration;

	public PlayList(String uuid) {
		this.uuid = uuid;
		Date d = new Date();
		this.registeredDate = d;
		this.lastUpdated = d;
		this.playListTracks = new HashSet<PlayListTrack>();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPlayListName() {
		return playListName;
	}

	public void setPlayListName(String playListName) {
		this.playListName = playListName;
	}

	public Set<PlayListTrack> getPlayListTracks() {
		return playListTracks;
	}

	public void setPlayListTracks(Set<PlayListTrack> playListTracks) {
		this.playListTracks = playListTracks;

		// keep the nrOfTracks in sync when changing the track list
		if (playListTracks != null) {
			this.nrOfTracks = playListTracks.size();
		}
	}

	public Date getRegisteredDate() {
		return registeredDate;
	}

	public void setRegisteredDate(Date registeredDate) {
		this.registeredDate = registeredDate;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getUuid() {
		return uuid;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public int getNrOfTracks() {
		return nrOfTracks;
	}

	public void setNrOfTracks(int nrOfTracks) {
		this.nrOfTracks = nrOfTracks;
	}

	public float getDuration() {
		return duration;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	public void addDuration(float duration) {
		setDuration(getDuration() + duration);
	}
}