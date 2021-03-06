package fi.aalto.cs.apluscourses.intellij.notifications;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import fi.aalto.cs.apluscourses.model.Component;
import fi.aalto.cs.apluscourses.model.Module;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link Notification} wrapper to let the user know about some A+ Course modules having the new
 * versions of them in A+ LMS.
 */
public class NewModulesVersionsNotification extends Notification {

  /**
   * Builds the notification.
   *
   * @param modules is a {@link List} {@link Module}s to process.
   */
  public NewModulesVersionsNotification(@NotNull List<Module> modules) {
    super("A+",
        "Updates available for A+ Course Modules",
        "There are newer version(s) for the following A+ Course Modules: "
            + getModuleNameStrings(modules) + ".",
        NotificationType.INFORMATION);
  }

  /**
   * A naive helper method to get a comma separated String of Module names.
   *
   * @param modules a {@link List} {@link Module}s to process.
   * @return a {@link String} of comma separated Module names.
   */
  @NotNull
  public static String getModuleNameStrings(@NotNull List<Module> modules) {
    return modules
        .stream()
        .map(Component::getName)
        .collect(Collectors.joining(", "));
  }
}
