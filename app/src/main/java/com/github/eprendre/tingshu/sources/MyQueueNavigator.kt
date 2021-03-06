package com.github.eprendre.tingshu.sources

import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.github.eprendre.tingshu.App
import com.github.eprendre.tingshu.TingShuService
import com.github.eprendre.tingshu.utils.Prefs
import com.google.android.exoplayer2.ControlDispatcher
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator

class MyQueueNavigator(private val mediaSessionCompat: MediaSessionCompat, val service: TingShuService) :
    TimelineQueueNavigator(mediaSessionCompat) {
    private val window = Timeline.Window()

    override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
        val discription = player.currentTimeline.getWindow(windowIndex, window, true).tag as MediaDescriptionCompat
        return discription
    }

    /**
     * 返回能否跳转上一首、下一首的action
     */
    override fun getSupportedQueueNavigatorActions(player: Player?): Long {
        if (player == null) {
            return 0
        }
        var actions = 0L
        if (App.currentEpisodeIndex() < Prefs.playList.size - 1) {
            actions = actions or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
        }
        actions = actions or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
        return actions
    }

    /**
     * 处理跳转上一首的逻辑
     */
    override fun onSkipToPrevious(player: Player, controlDispatcher: ControlDispatcher) {
        Prefs.currentBook = Prefs.currentBook?.apply {
            this.currentEpisodePosition = 0
        }
        if (App.currentEpisodeIndex() < 1) {
            player.seekTo(0)
        } else {
            service.getAudioUrl(Prefs.playList[App.currentEpisodeIndex() - 1].url)
        }
    }

    /**
     * 处理跳转下一首的逻辑, 不必担心越界 getSupportedQueueNavigatorActions 里面已经检查了
     */
    override fun onSkipToNext(player: Player, controlDispatcher: ControlDispatcher) {
        Prefs.currentBook = Prefs.currentBook?.apply {
            this.currentEpisodePosition = 0
        }
        service.getAudioUrl(Prefs.playList[App.currentEpisodeIndex() + 1].url)
    }
}