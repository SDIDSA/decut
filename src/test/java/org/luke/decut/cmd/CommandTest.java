package org.luke.decut.cmd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.luke.decut.crossplatform.Os;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class CommandTest {

    Os os;

    @BeforeEach
    void setUp() {
        os = Os.fromSystem();
    }

    @Test
    void testStandardOutput() {
        String expectedOutcome = os.name();
        Command command;
        if(os.isWindows()) {
            command = new Command("echo " + expectedOutcome);
        } else if(os.isLinux()) {
            command = new Command("echo '" + expectedOutcome + "'");
        } else if(os.isOsx()) {
            command = new Command("echo '" + expectedOutcome + "'");
        } else {
            command = new Command("echo '" + expectedOutcome + "'");
        }

        StringBuilder stdOut = new StringBuilder();
        StringBuilder errOut = new StringBuilder();
        AtomicInteger exitCode = new AtomicInteger(-1);
        command
                .addInputHandler(stdOut::append)
                .addErrorHandler(errOut::append)
                .addOnExit(exitCode::set)
                .executeAndJoin(new File("/"));

        assertEquals(expectedOutcome, stdOut.toString().trim());
        assertEquals("", errOut.toString().trim());
        assertEquals(0, exitCode.get());
    }

    @Test
    void testErrorOutput() {
        StringBuilder stdOut = new StringBuilder();
        StringBuilder errOut = new StringBuilder();
        AtomicInteger exitCode = new AtomicInteger(-1);

        Command errorCommand;
        if(os.isWindows()) {
            errorCommand = new Command("type nonexistent_file.txt");
        } else {
            errorCommand = new Command("ls /nonexistent_directory_12345");
        }

        errorCommand
                .addInputHandler(stdOut::append)
                .addErrorHandler(errOut::append)
                .addOnExit(exitCode::set)
                .executeAndJoin(new File("/"));

        assertEquals("", stdOut.toString().trim());
        assertFalse(errOut.toString().trim().isEmpty());
        assertNotEquals(0, exitCode.get());
    }
}