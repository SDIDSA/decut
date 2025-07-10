package org.luke.decut.file.project;

import org.luke.decut.app.home.Home;

public interface ProjectPart {
    void save(Home owner);
    void load(Home owner);
}
