package com.tidal.refactoring.playlist;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.tidal.refactoring.playlist.PlaylistBusinessBean;
import com.tidal.refactoring.playlist.dao.PlaylistDaoBean;

public class TestBusinessModule extends AbstractModule {

	@Override
	protected void configure() {
		bindConstant().annotatedWith(Names.named("defaultPlaylistTrackSize")).to(376);
		bind(PlaylistDaoBean.class).in(Singleton.class);
		bind(PlaylistBusinessBean.class).in(Singleton.class);
	}
}
