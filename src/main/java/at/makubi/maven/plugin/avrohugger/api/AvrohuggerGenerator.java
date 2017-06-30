package at.makubi.maven.plugin.avrohugger.api;

import org.apache.maven.plugin.logging.Log;

import java.io.File;

public interface AvrohuggerGenerator {

    void generateScalaFiles(File inputDirectory, String outputDirectory, Log log, boolean recursive);
}
