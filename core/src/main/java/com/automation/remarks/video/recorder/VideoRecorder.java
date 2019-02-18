package com.automation.remarks.video.recorder;

import com.automation.remarks.video.enums.RecorderType;
import com.automation.remarks.video.enums.RecordingMode;
import com.automation.remarks.video.enums.VideoSaveMode;
import org.aeonbits.owner.ConfigFactory;

import java.awt.*;
import java.io.File;

import static com.automation.remarks.video.SystemUtils.getOsType;

/**
 * Created by sepi on 19.07.16.
 */
public abstract class VideoRecorder implements IVideoRecorder {
    public static VideoConfiguration conf() {
        ConfigFactory.setProperty("os.type", getOsType());
        VideoConfiguration videoConfiguration = ConfigFactory.create(VideoConfiguration.class, System.getProperties());
        return new VideoConfiguration() {
            @Override
            public String folder() {
                return videoConfiguration.folder() != null ? videoConfiguration.folder() : VideoConfiguration.defaultFolder();
            }

            @Override
            public Boolean videoEnabled() {
                return videoConfiguration.videoEnabled();
            }

            @Override
            public RecordingMode mode() {
                return videoConfiguration.mode();
            }

            @Override
            public String remoteUrl() {
                return videoConfiguration.remoteUrl();
            }

            @Override
            public Boolean isRemote() {
                return videoConfiguration.isRemote();
            }

            @Override
            public String fileName() {
                return videoConfiguration.fileName();
            }

            @Override
            public RecorderType recorderType() {
                return videoConfiguration.recorderType();
            }

            @Override
            public VideoSaveMode saveMode() {
                return videoConfiguration.saveMode();
            }

            @Override
            public int frameRate() {
                return videoConfiguration.frameRate();
            }

            @Override
            public Dimension screenSize() {
                return videoConfiguration.screenSize() != null ? videoConfiguration.screenSize() : VideoConfiguration.defaultScreenSize();
            }

            @Override
            public String ffmpegFormat() {
                return videoConfiguration.ffmpegFormat();
            }

            @Override
            public String ffmpegDisplay() {
                return videoConfiguration.ffmpegDisplay();
            }

            @Override
            public String ffmpegPixelFormat() {
                return videoConfiguration.ffmpegPixelFormat();
            }
        };
    }

    private static File lastVideo;

    protected void setLastVideo(File video) {
        lastVideo = video;
    }

    public static File getLastRecording() {
        return lastVideo;
    }
}
