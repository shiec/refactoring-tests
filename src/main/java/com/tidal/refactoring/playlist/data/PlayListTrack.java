package com.tidal.refactoring.playlist.data;

import java.io.Serializable;
import java.util.Date;

public class PlayListTrack implements Serializable, Comparable<PlayListTrack> {

	private static final long serialVersionUID = 5464240796158432162L;

	private Integer id;
	private String playlistUuid;  // only a reference to the actual playlist is needed
	private int index;
	private Date dateAdded;
	private int trackId; // maybe removed? since it can be fetched from the track below
	private Track track;

	public PlayListTrack(Integer id, String playlistUuid, int index, Date dateAdded, Track track) {
		this.id = id;
		this.playlistUuid = playlistUuid;
		this.index = index;
		this.dateAdded = dateAdded == null ? new Date() : dateAdded;
		this.trackId = track.getId();
		this.track = track;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getTrackId() {
		return trackId;
	}

	public void setTrackId(int trackId) {
		this.trackId = trackId;
	}

	public String getTrackPlayListUuid() {
		return playlistUuid;
	}

	public void setTrackPlaylistUuid(String playlistUuid) {
		this.playlistUuid = playlistUuid;
	}

	public Track getTrack() {
		return track;
	}

	public void setTrack(Track track) {
		this.track = track;
		
		// keep the trackid in sync when updating the track
		this.trackId = track.getId();
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Date getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}

	public int compareTo(PlayListTrack o) {
		return this.getIndex() - o.getIndex();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dateAdded == null) ? 0 : dateAdded.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + index;
		result = prime * result + ((playlistUuid == null) ? 0 : playlistUuid.hashCode());
		result = prime * result + ((track == null) ? 0 : track.hashCode());
		result = prime * result + trackId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlayListTrack other = (PlayListTrack) obj;
		if (dateAdded == null) {
			if (other.dateAdded != null)
				return false;
		} else if (!dateAdded.equals(other.dateAdded))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (index != other.index)
			return false;
		if (playlistUuid == null) {
			if (other.playlistUuid != null)
				return false;
		} else if (!playlistUuid.equals(other.playlistUuid))
			return false;
		if (track == null) {
			if (other.track != null)
				return false;
		} else if (!track.equals(other.track))
			return false;
		if (trackId != other.trackId)
			return false;
		return true;
	}

	public String toString() {
		return "PlayListTrack id[" + getId() + "], trackId[" + getTrackId() + "]";
	}
}
