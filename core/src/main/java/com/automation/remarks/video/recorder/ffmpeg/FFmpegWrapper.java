package com.automation.remarks.video.recorder.ffmpeg;

import com.automation.remarks.video.DateUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.automation.remarks.video.SystemUtils.*;
import static com.automation.remarks.video.recorder.VideoRecorder.conf;
import static java.util.Arrays.asList;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class FFmpegWrapper {

    private static final Logger log = LoggerFactory.getLogger(FFMpegRecorder.class);

    private static final String RECORDING_TOOL = "ffmpeg";
    private static final String TEMP_FILE_NAME = "temporary";
    private static final String EXTENSION = ".mkv";
    private static final String SEND_CTRL_C_TOOL_NAME = "SendSignalCtrlC.exe";
    private CompletableFuture<String> future;
    private File temporaryFile;

    public void startFFmpeg(String... extraArgs) {
        File videoFolder = new File(conf().folder());
        if (!videoFolder.exists()) {
            videoFolder.mkdirs();
        }

        temporaryFile = getTemporaryFile();
        List<String> commandsSequence = asList(
                FFmpegWrapper.RECORDING_TOOL,
                "-y",
                "-video_size", getScreenSize(),
                "-f", conf().ffmpegFormat(),
                "-i", conf().ffmpegDisplay(),
                "-an",
                "-framerate", String.valueOf(conf().frameRate()),
                "-pix_fmt", conf().ffmpegPixelFormat(),
                temporaryFile.getAbsolutePath()
        );

        List<String> command = new ArrayList<>();
        command.addAll(commandsSequence);
        command.addAll(asList(extraArgs));
        this.future = CompletableFuture.supplyAsync(() -> runCommand(command));
    }

    public File stopFFmpegAndSave(String filename) {
        killFFmpeg();

        File destFile = getResultFile(filename);
        this.future.whenCompleteAsync((out, exception) -> {
            temporaryFile.renameTo(destFile);
            log.debug("Recording output log: " + out);
            if (exception != null) {
                log.error("Recording error: ", exception);
            }
            log.info("Recording output file: " + destFile.getAbsolutePath());
        });
        return destFile;
    }

    private void killFFmpeg() {
        log.info("Killing ffmpeg process...");
        String killLog = SystemUtils.IS_OS_WINDOWS ?
                runCommand(SEND_CTRL_C_TOOL_NAME, getPidOf(RECORDING_TOOL)) :
                runCommand("pkill", "-INT", RECORDING_TOOL);

        log.info("Kill output: " + killLog);
    }

    public File getTemporaryFile() {
        return getFile(TEMP_FILE_NAME);
    }

    public File getResultFile(String name) {
        return getFile(name);
    }

    private File getFile(final String filename) {
        File movieFolder = new File(conf().folder());
        String name = filename + "_recording_" + DateUtils.formatDate(new Date(), "yyyy_dd_MM_HH_mm_ss");
        return new File(movieFolder + File.separator + name + EXTENSION);
    }

    private String getScreenSize() {
        Dimension dimension = conf().screenSize();
        return trimToEvenNumber(dimension.width) + "x" + trimToEvenNumber(dimension.height);
    }

    /**
     * ffmpeg crashes if screen dimensions aren't even numbers, this can happen on VMs.
     */
    private static int trimToEvenNumber(int number) {
        return (number % 2 == 0) ? number : (number - 1);
    }
}
