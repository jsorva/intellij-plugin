package fi.aalto.cs.apluscourses.presentation;

import com.intellij.notification.Notification;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import fi.aalto.cs.apluscourses.dal.APlusTokenAuthentication;
import fi.aalto.cs.apluscourses.dal.PasswordStorage;
import fi.aalto.cs.apluscourses.dal.TokenAuthentication;
import fi.aalto.cs.apluscourses.intellij.model.IntelliJModelFactory;
import fi.aalto.cs.apluscourses.intellij.notifications.NewModulesVersionsNotification;
import fi.aalto.cs.apluscourses.intellij.notifications.Notifier;
import fi.aalto.cs.apluscourses.intellij.utils.CourseFileManager;
import fi.aalto.cs.apluscourses.model.Authentication;
import fi.aalto.cs.apluscourses.model.Component;
import fi.aalto.cs.apluscourses.model.Course;
import fi.aalto.cs.apluscourses.model.Module;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MainViewModelUpdater {

  @NotNull
  private final MainViewModel mainViewModel;

  @NotNull
  private final Project project;

  private final long updateInterval;

  @NotNull
  private final Notifier notifier;
  @NotNull
  private final PasswordStorage.Factory passwordStorageFactory;

  private Thread thread;

  private Notification newModulesVersionsNotification = null;

  /**
   * Construct a {@link MainViewModelUpdater} with the given {@link MainViewModel}, project, and
   * update interval.
   *
   * @param mainViewModel  The {@link MainViewModel} that gets updated.
   * @param project        The project to which this updater is tied.
   * @param updateInterval The interval at which the updater performs the update.
   */
  public MainViewModelUpdater(@NotNull MainViewModel mainViewModel,
                              @NotNull Project project,
                              long updateInterval,
                              @NotNull Notifier notifier,
                              @NotNull PasswordStorage.Factory passwordStorageFactory) {
    this.mainViewModel = mainViewModel;
    this.project = project;
    this.updateInterval = updateInterval;
    this.notifier = notifier;
    this.passwordStorageFactory = passwordStorageFactory;
    this.thread = new Thread(this::run);
  }

  @NotNull
  private URL getCourseUrl() {
    return ReadAction.compute(() -> {
      if (project.isDisposed() || !project.isOpen()) {
        return null;
      }
      return CourseFileManager.getInstance().getCourseUrl();
    });
  }

  @Nullable
  private Course getCourse(@Nullable URL courseUrl) {
    if (courseUrl == null) {
      return null;
    }

    try {
      return ReadAction.compute(
          () -> Course.fromUrl(courseUrl, new IntelliJModelFactory(project)));
    } catch (Exception e) {
      return null;
    }
  }

  private void updateMainViewModel(@Nullable Course newCourse) {
    if (newCourse == null) {
      return;
    }

    CourseViewModel courseViewModel = mainViewModel.courseViewModel.get();
    if (courseViewModel != null) {
      Course current = courseViewModel.getModel();
      /*
       * Updating the course view model while modules are installing leads to "Error in
       * dependencies" in the modules tool window. The installations actually still work, and the
       * error state disappears on the next update, but it's still bad UI/UX. Therefore we check if
       * the currently loaded course has any "active" components, and only update the course view
       * model if it doesn't.
       */
      if (current.getComponents().stream().anyMatch(Component::isActive)) {
        return;
      }

      current.unregister();
    }

    mainViewModel.courseViewModel.set(new CourseViewModel(newCourse));

    PasswordStorage passwordStorage = passwordStorageFactory.create(newCourse.getApiUrl());
    TokenAuthentication.Factory authenticationFactory =
        APlusTokenAuthentication.getFactoryFor(passwordStorage);
    mainViewModel.readAuthenticationFromStorage(passwordStorage, authenticationFactory);

    notifyNewVersions(newCourse);

    newCourse.register();
  }

  private void notifyNewVersions(Course newCourse) {
    if (newModulesVersionsNotification != null) {
      newModulesVersionsNotification.expire();
      newModulesVersionsNotification = null;
    }
    List<Module> updatableModules = newCourse.getUpdatableModules();
    if (!updatableModules.isEmpty()) {
      newModulesVersionsNotification = new NewModulesVersionsNotification(updatableModules);
      notifier.notify(newModulesVersionsNotification, project);
    }
  }

  /**
   * This method attempts to update the main view model repeatedly, sleeping a given update interval
   * time between attempts. If the project of this updater is closed, then this method returns. This
   * method is intended to be run on a background thread.
   */
  private void run() {
    try {
      // Sonar dislikes infinite loops...
      while (true) { //NOSONAR
        URL courseUrl = getCourseUrl();
        Course course = getCourse(courseUrl);
        // If parsing the course configuration file fails, then we just silently go back to sleep.
        // For an example, if the internet connection was down, then we may succeed when we try
        // again after sleeping.
        updateMainViewModel(course);
        Thread.sleep(updateInterval); // Good night :)

      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Starts this updater on a separate thread. The updater repeatedly updates its main view model,
   * sleeping the given amount of time between update attempts.
   */
  public void start() {
    thread.start();
  }

  /**
   * Interrupts the updater and starts it again. This can be used to trigger an instant main view
   * model update, skipping the sleep interval.
   */
  public void restart() {
    thread.interrupt();
    thread = new Thread(this::run);
    thread.start();
  }

  /**
   * Interrupts this updater.
   */
  public void interrupt() {
    thread.interrupt();
  }
}
