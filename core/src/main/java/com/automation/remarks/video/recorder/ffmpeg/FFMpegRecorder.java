package com.automation.remarks.video.recorder.ffmpeg;

import com.automation.remarks.video.exception.RecordingException;
import com.automation.remarks.video.recorder.VideoRecorder;
import org.awaitility.core.ConditionTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

/**
 * Created by sepi on 19.07.16.
 */
public abstract class FFMpegRecorder extends VideoRecorder {

    private static final int VIDEO_PROCESSING_TIMEOUT = 30;
    private static final int VIDEO_PROCESSING_POLL_DELAY = 1;

    private FFmpegWrapper ffmpegWrapper;

    private static final Logger log = LoggerFactory.getLogger(FFMpegRecorder.class);

    public FFMpegRecorder() {
        this.ffmpegWrapper = new FFmpegWrapper();
    }

    public FFmpegWrapper getFfmpegWrapper() {
        return ffmpegWrapper;
    }

    @Override
    public File stopAndSave(final String filename) {
        File file = getFfmpegWrapper().stopFFmpegAndSave(filename);
        waitForVideoCompleted(file);
        setLastVideo(file);
        return file;
    }

    private void waitForVideoCompleted(File video) {
        log.info(MessageFormat.format(
                "Waiting for completion of video processing, file {0} ... ",
                video.getName()
        ));

        try {
            await().atMost(VIDEO_PROCESSING_TIMEOUT, TimeUnit.SECONDS)
                    .pollDelay(VIDEO_PROCESSING_POLL_DELAY, TimeUnit.SECONDS)
                    .ignoreExceptions()
                    .until(() -> {
                        boolean exists = video.exists();
                        log.info(exists ? "Video created." : "Video not yet there...");
                        return exists;
                    });
        } catch (ConditionTimeoutException ex) {
            throw new RecordingException(ex.getMessage());
        }
    }
}
