package org.roylance.yaorm;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class YaormPlugin extends DefaultTask {
    public String packageName;
    public String projectName;

    @TaskAction
    public void defaultExecute() {
        YaormPluginLogic.INSTANCE.execute(packageName, projectName);
    }
}
