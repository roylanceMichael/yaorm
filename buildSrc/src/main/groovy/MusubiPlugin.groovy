import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete

class MusubiPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        // first, we need to process any protobufs at configuration time
        // we need certain things from the user
        // what languages to process
        // what repositories to publish to
    }
}
